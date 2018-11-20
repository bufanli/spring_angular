package com.example.eurasia.dao;

import com.example.eurasia.entity.DataXMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

public class CommonDao {

    // 属性注入
    // 加入JdbcTemplate作为成员变变量
    @Autowired
    public JdbcTemplate jdbcTemplate;
    // 注意这里要增加get和set方法
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * 创建数据库
     * @param databaseName
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createDatabase(String databaseName) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement preStatement = null;
        ResultSet databases = null;
        try {
            DataSource ds = getJdbcTemplate().getDataSource();
            conn = getJdbcTemplate().getDataSource().getConnection();
/*
检测数据库是否存在：
SELECT information_schema.SCHEMATA.SCHEMA_NAME FROM information_schema.SCHEMATA where SCHEMA_NAME="database_name"
 */
            StringBuffer sb = new StringBuffer();
            sb.append("CREATE DATABASE IF NOT EXISTS " + databaseName);
            stmt = conn.createStatement();
            databases = stmt.executeQuery(sb.toString());

            sb.append("CREATE DATABASE IF NOT EXISTS ?");
            preStatement = conn.prepareStatement(sb.toString());
            preStatement.setString(1, databaseName);

            return preStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(databases != null) databases.close();
            if(stmt != null) stmt.close();
            if(preStatement != null) preStatement.close();
            if(conn != null) conn.close();
        }

        return false;
    }

    /**
     * 根据表名称创建一张表
     * @param tableName
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createTable(String tableName, String beanName) throws Exception {
        try {
            // 判断数据库是否已经存在这个名称的表，如果有某表，直接返回；否则动态创建表之后再返回
            if (isExistTableName(tableName)) {
                return true;
            } else {
                ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                DataXMLReader dataXMLReader = (DataXMLReader) context.getBean(beanName);

                StringBuffer sb = new StringBuffer();
                sb.append("CREATE TABLE `" + tableName + "` (");
                sb.append(" `id` int(11) NOT NULL AUTO_INCREMENT,");
                Map<String,String> map = dataXMLReader.getKeyValue();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sb.append("`" + entry.getKey() + "` " + entry.getValue() + " NOT NULL,");//key字段名 value字段类型
                }
                sb.append(" PRIMARY KEY (`id`)");
                sb.append(") ENGINE=InnoDB DEFAULT CHARSET=gbk;");
//MyISAM适合：(1)做很多count 的计算；(2)插入不频繁，查询非常频繁；(3)没有事务。
//InnoDB适合：(1)可靠性要求比较高，或者要求事务；(2)表更新和查询都相当的频繁，并且行锁定的机会比较大的情况

                getJdbcTemplate().update(sb.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询数据库是否有某表
     * @param tableName
     * @return
     * @exception Exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean isExistTableName(String tableName) throws Exception {
        Connection conn = getJdbcTemplate().getDataSource().getConnection();
        ResultSet tables = null;
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            String[] types = { "TABLE" };
            tables = dbMetaData.getTables(null, null, tableName, types);
            if (tables.next()) {
                /*将光标从当前位置向前移一行。*/
                /*ResultSet 光标最初位于第一行之前；第一次调用 next 方法使第一行成为当前行；第二次调用使第二行成为当前行，依此类推。*/
                /*当调用 next 方法返回 false 时，光标位于最后一行的后面。*/
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tables.close();
            conn.close();
        }
        return false;
    }


    /**
     * 查询表的记录数
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public Long queryTableRows(String tableName) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) from " + tableName);
        Long row = getJdbcTemplate().queryForObject(sql.toString(), Long.class);
        //System.out.println("查询出来的记录数为：" + row);
        return row;
    }

    /**
     * 查询表的字段数
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public Long queryTableColumns(String tableName) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) from information_schema.COLUMNS where table_name = " + tableName);
        sql.append("' and table_schema = 'eurasia'");
        Long columns = getJdbcTemplate().queryForObject(sql.toString(), Long.class);
        //System.out.println("查询出来的字段数为：" + columns);
        return columns;
    }

}
