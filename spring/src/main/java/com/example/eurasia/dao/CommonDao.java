package com.example.eurasia.dao;

import com.example.eurasia.entity.DataXMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class CommonDao {

    public static final String COMMA = ",";

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
            StringBuffer sql = new StringBuffer();
            sql.append("CREATE DATABASE IF NOT EXISTS " + databaseName);
            stmt = conn.createStatement();
            databases = stmt.executeQuery(sql.toString());

            sql.append("CREATE DATABASE IF NOT EXISTS ?");
            preStatement = conn.prepareStatement(sql.toString());
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
                Map<String,String> map = dataXMLReader.getKeyValue();

                StringBuffer sql = new StringBuffer();
                sql.append("CREATE TABLE `" + tableName + "` (");
                sql.append(" `id` int(11) NOT NULL AUTO_INCREMENT,");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sql.append("`" + entry.getKey() + "` " + entry.getValue() + " NOT NULL,");//key字段名 value字段类型
                }
                sql.append(" PRIMARY KEY (`id`)");
                sql.append(") ENGINE=InnoDB DEFAULT CHARSET=gbk;");
//MyISAM适合：(1)做很多count 的计算；(2)插入不频繁，查询非常频繁；(3)没有事务。
//InnoDB适合：(1)可靠性要求比较高，或者要求事务；(2)表更新和查询都相当的频繁，并且行锁定的机会比较大的情况

                getJdbcTemplate().update(sql.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 给表中某个字段添加唯一属性
     * @param tableName
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    public boolean addUnique(String tableName, String columnName) throws Exception {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("ALTER TABLE " + tableName + " ADD unique(" + columnName + ")");
            getJdbcTemplate().update(sql.toString());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 创建SPRING_SESSION
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createSpringSessionTable() throws Exception {
/*
    CREATE TABLE SPRING_SESSION (
	PRIMARY_ID CHAR(36) NOT NULL,
	SESSION_ID CHAR(36) NOT NULL,
	CREATION_TIME BIGINT NOT NULL,
	LAST_ACCESS_TIME BIGINT NOT NULL,
	MAX_INACTIVE_INTERVAL INT NOT NULL,
	EXPIRY_TIME BIGINT NOT NULL,
	PRINCIPAL_NAME VARCHAR(100),
	CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
	SESSION_PRIMARY_ID CHAR(36) NOT NULL,
	ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
	ATTRIBUTE_BYTES BLOB NOT NULL,
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
*/

        try {
            StringBuffer sql = new StringBuffer();
            sql.append("CREATE TABLE `SPRING_SESSION` (");
            sql.append("`PRIMARY_ID` char(36) NOT NULL,");
            sql.append("`SESSION_ID` char(36) NOT NULL DEFAULT '',");
            sql.append("`CREATION_TIME` BIGINT NOT NULL,");
            sql.append("`LAST_ACCESS_TIME` BIGINT NOT NULL,");
            sql.append("`MAX_INACTIVE_INTERVAL` INT NOT NULL,");
            sql.append("`EXPIRY_TIME` BIGINT NOT NULL,");
            sql.append("`PRINCIPAL_NAME` varchar(100) DEFAULT NULL,");
            sql.append("PRIMARY KEY (`SESSION_ID`) USING BTREE,");
            sql.append("KEY `SPRING_SESSION_IX1` (`LAST_ACCESS_TIME`) USING BTREE");
            sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
            getJdbcTemplate().update(sql.toString());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 创建SPRING_SESSION_ATTRIBUTES
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createSpringSessionAttributesTable() throws Exception {

        try {
            StringBuffer sql = new StringBuffer();
            sql.append("CREATE TABLE `SPRING_SESSION_ATTRIBUTES` (");
            sql.append("`SESSION_ID` char(36) NOT NULL DEFAULT '',");
            sql.append("`ATTRIBUTE_NAME` varchar(200) NOT NULL DEFAULT '',");
            sql.append("`ATTRIBUTE_BYTES` blob NOT NULL,");
            sql.append("PRIMARY KEY (`SESSION_ID`,`ATTRIBUTE_NAME`),");
            sql.append("KEY `SPRING_SESSION_ATTRIBUTES_IX1` (`SESSION_ID`) USING BTREE,");
            sql.append("CONSTRAINT `SPRING_SESSION_ATTRIBUTES_ibfk_1` FOREIGN KEY (`SESSION_ID`) REFERENCES `SPRING_SESSION` (`SESSION_ID`) ON DELETE CASCADE");
            sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
            getJdbcTemplate().update(sql.toString());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    // angular得到的时间格式是 2018/9/11 和数据库2018/09/11里面不一致，所以转换一下
    public String convertDateToNewFormat(String dateFromAngular){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            java.util.Date tempDateEnd= sdf.parse(dateFromAngular);
            return sdf.format(tempDateEnd);
        }catch(ParseException e){
            e.printStackTrace();
            // 格式不正确的时候至少返回原来的时间字符串
            return dateFromAngular;
        }
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

    /**
     * 查询数据库中的最近一个月
     * @param tableName
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-15 23:11:00
     */
    public String[] queryListForTheLastMouth(String tableName, String dateColumnName) throws Exception {

/*
        //近30天
        select 日期 from eurasiaTable where DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(日期);
        //本月
        select 日期 from eurasiaTable where DATE_FORMAT(日期,'%Y-%m') = DATE_FORMAT(CURDATE(),'%Y-%m');
        //上个月
        SELECT * FROM eurasiaTable WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(字段名,'%Y%m'))=1;
        SELECT * FROM eurasiaTable WHERE PERIOD_DIFF(DATE_FORMAT(CURDATE(),'%Y%m'),DATE_FORMAT(字段名,'%Y%m'))=1;
        //最近一个月
        SELECT 日期 FROM eurasiaTable WHERE DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(now());
        //查询距离当前现在6个月的数据
        select 日期 from eurasiaTable where 日期 between date_sub(now(),interval 6 month) and now();
*/
/* 测试用表
select PERIOD_DIFF(DATE_FORMAT(CURDATE(),'%Y%m'),DATE_FORMAT(日期,'%Y%m')) from dual;
 */

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT top 1 " + dateColumnName + " FROM " + tableName);
        sql.append(" WHERE DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(now()) order by " + dateColumnName);

        StringBuffer sqlAsc = new StringBuffer();
        sqlAsc.append(" asc");

        StringBuffer sqlDesc = new StringBuffer();
        sqlDesc.append(" desc");

        List<Map<String, Object>> dateAscList = getJdbcTemplate().queryForList(sql.append(sqlAsc).toString());

        List<Map<String, Object>> dateDescList = getJdbcTemplate().queryForList(sql.append(sqlDesc).toString());

        String[] dateArr =  new String[2];
        dateArr[0] = dateAscList.get(0).get(dateColumnName).toString();
        dateArr[1] = dateDescList.get(0).get(dateColumnName).toString();
        return dateArr;
    }

}
