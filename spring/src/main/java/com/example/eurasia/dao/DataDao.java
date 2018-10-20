package com.example.eurasia.dao;

import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.DataXMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class DataDao {

    // 属性注入
    // 加入JdbcTemplate作为成员变变量
    @Autowired
    private JdbcTemplate jdbcTemplate;
    // 注意这里要增加get和set方法
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 添加数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void addData(String tableName, Data data) {
        StringBuffer sql = new StringBuffer();
        for (Map.Entry<String, String> entry : data.getKeyValue().entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            sql.append("insert into " + tableName + "(" + entry.getKey() + ") values (?)");
            getJdbcTemplate().update(sql.toString(), entry.getValue());
            sql.setLength(0);
        }
/*
StringBuffer sbf = new  StringBuffer("Hello World!");
sbf .setLength(0);//设置长度 (清楚内容效率最高)
sbf.delete(0, sbf.length());//删除(清楚内容效率最差)
sbf = new StringBuffer("");//重新new
*/
    }

    /**
     * 删除数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void deleteData(String tableName, Data data) {
        //Nothing to do
    }

    /**
     * 删除重复的数据，只保留一行
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void deleteSameData(String tableName) {

        String colsName = queryListForColumnName(tableName);
        String[] name = colsName.split(",");

        StringBuffer sql =  new StringBuffer();
        sql.append("delete " + tableName);
        sql.append(" from " + tableName);
        sql.append(" ( select min(id) id," + colsName);
        sql.append(" from " + tableName);
        sql.append(" group by " + colsName);
        sql.append(" having count(*) > 1)tempSameDataTable");
        sql.append(" where ");
        sql.append(tableName + "." + name[0] + " = tempSameDataTable." + name[0]);
        for (int i=1; i < name.length; i++) {
            sql.append(" and " + tableName + "." + name[i] + " = tempSameDataTable." + name[i]);
        }
        sql.append(" and " + tableName + ".id > tempSameDataTable.id");

        getJdbcTemplate().update(sql.toString());//执行成功返回数据库当中受影响的记录条数，失败则返回-1
    }

    /**
     * 更新数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void updateData(String tableName, Data data) {
        //Nothing to do
    }

    /**
     * 批处理更新数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void batchUpdateData(String tableName, Data[] data) {
        //Nothing to do
    }

    /**
     * 查询表的记录数
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public Long queryTableNumbers(String tableName) {
        // 获得jdbcTemplate对象
        //ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        //JdbcTemplate jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");

        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) from " + tableName);
        Long row = getJdbcTemplate().queryForObject(sql.toString(), Long.class);
        //System.out.println("查询出来的记录数为：" + row);
        return row;
    }

    /**
     * 查询返回对象
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public Object queryForObject(String tableName, Data queryConditions) {
        //Nothing to do
        return null;
    }

    /**
     * 查询并返回List集合
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> queryListForObject(String tableName, Data queryConditions) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName + " where");

        Set<Map.Entry<String, String>> set = queryConditions.getKeyValue().entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            sql.append(entry.getKey() + "is" + entry.getValue());//T.B.D.
        }

        sql.append(" concat_ws(" + queryConditions.getKeys() + ")");//T.B.D.

        return getJdbcTemplate().query(sql.toString(), new DataMapper());
    }

    /**
     * 拼接语句，往表里面插入数据
     * @param tableName
     * @param data
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int saveData(String tableName, Data data) {
        int re = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into " + tableName + " (");
            Map<String,String> map = data.getKeyValue();
            Set<String> set = map.keySet();
            for (String key : set) {
                sql.append(key + ",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" ) values ( ");
            for (String key : set) {
                sql.append("'" + map.get(key) + "',");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" ) ");
            re = getJdbcTemplate().update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 根据表名称创建一张表
     * @param tableName
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int createTable(String tableName) {
        try {
            // 判断数据库是否已经存在这个名称的表，如果有某表，直接返回；否则动态创建表之后再返回
            if (getAllTableName(tableName)) {
                return 1;
            } else {
                ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
                DataXMLReader dataXMLReader = (DataXMLReader) context.getBean("columnsName");

                StringBuffer sb = new StringBuffer();
                sb.append("CREATE TABLE `" + tableName + "` (");
                sb.append(" `id` int(11) NOT NULL AUTO_INCREMENT,");
                Map<String,String> map = dataXMLReader.getKeyValue();
                Set<String> set = map.keySet();
                for (String key : set) {
                    sb.append("`" + key + "` varchar(255) DEFAULT '',");
                }
                sb.append(" `tableName` varchar(255) DEFAULT '',");
                sb.append(" PRIMARY KEY (`id`)");
                sb.append(") ENGINE=InnoDB DEFAULT CHARSET=gbk;");

                getJdbcTemplate().update(sb.toString());
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询数据库是否有某表
     * @param tableName
     * @return
     * @exception Exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean getAllTableName(String tableName) throws Exception {
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
     * 查询某表的列名
     * @param tableName
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-15 23:11:00
     */
    public String queryListForColumnName(String tableName) {
        /*
        Select COLUMN_NAME 列名, DATA_TYPE 字段类型, COLUMN_COMMENT 字段注释
        from INFORMATION_SCHEMA.COLUMNS
        Where table_name = 'companies'  ##表名
        AND table_schema = 'testhuicard'##数据库名
        AND column_name LIKE 'c_name'   ##字段名
        */

        StringBuffer sql = new StringBuffer();
        sql.append("select COLUMN_NAME from information_schema.COLUMNS where table_name = " + tableName);
        sql.append(" and table_schema = (select database())");
        List<Map<String,Object>> colsNameList = getJdbcTemplate().queryForList(sql.toString());

        StringBuffer ret = new StringBuffer();
        for(Map<String,Object> colsName: colsNameList) {

            Set<Map.Entry<String, Object>> set = colsName.entrySet();
            Iterator<Map.Entry<String, Object>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,Object> entry = it.next();
                //System.out.println("Key:" + entry.getKey() + " Value:" + entry.getValue());
                ret.append(entry.getValue());
                ret.append(",");
            }

        }
        ret.deleteCharAt(ret.length() - 1);
        return ret.toString();
    }

}
