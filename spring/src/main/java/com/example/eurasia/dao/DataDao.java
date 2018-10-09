package com.example.eurasia.dao;

import com.example.eurasia.entity.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
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
     * @param data 数据
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void addData(Data data) {
        String sql = "insert into user (username, password) values (?, ?)";
        getJdbcTemplate().update(sql, data.getKeyValue());
    }

    /**
     * 删除数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void deleteData( ) {
        String sql = "delete from user where username= ?";
        getJdbcTemplate().update(sql,"小王");
    }

    /**
     * 更新数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void updateData(Data data) {
        String sql = "update user set username=? where username= ?";
        getJdbcTemplate().update(sql,data.getKeyValue() + "_new", data.getKeyValue());
    }

    /**
     * 批处理更新数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void batchUpdateData() {

    }

    /**
     * 查询表的记录数
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void queryNumForObject() {
        // 获得jdbcTemplate对象
        //ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        //JdbcTemplate jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");

        String sql = "select count(*) from user";
        Long row = getJdbcTemplate().queryForObject(sql, Long.class);
        System.out.println("查询出来的记录数为：" + row);
    }

    /**
     * 查询返回对象
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void queryForObject() {
        String sql = "select username, password from user where username = ?";
        // 设定参数
        Object[] object = {"mary_new"};
        // 进行查询
        Data data = getJdbcTemplate().queryForObject(sql, object, new DataMapper());
        System.out.println(data);
    }

    /**
     * 查询并返回List集合
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void queryListForObject() {
        // sql语句
        String sql = "select * from user";
        List<Data> dataList = getJdbcTemplate().query(sql, new DataMapper());
        for(Data data: dataList) {
            System.out.println(data);
        }
    }


    /**
     * 下面，动态创建表。
     * 通过一个代理对象和数据库进行对应，这个对象除了id和一个tableName属性外和数据库的字段名称都是一致的
     * 通过一个公共方法类来获得代理类有那些属性，用来创建表和新增时进行动态SQL的拼装
     * 核心处理是，先看有么有该表，没有创建插入，有的话直接插入
     */
    /**
     * 创建表，添加记录
     * @param tableName
     * @param obj
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int insertObject(String tableName,Object obj) {
        int re = 0;
        try {
            // 判断数据库是否已经存在这个名称的表，如果有某表，则保存数据；否则动态创建表之后再保存数据
            if (getAllTableName(tableName)) {
                re = saveObject(tableName,obj);
            } else {
                re = createTable(tableName,obj);
                re = saveObject(tableName,obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 拼接语句，往表里面插入数据
     * @param tableName
     * @param obj
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int saveObject(String tableName,Object obj) {
        int re = 0;
        try {
            String sql = " insert into " + tableName + " (";
            Map<String,String> map = ObjectUtil.getProperty(obj);
            Set<String> set = map.keySet();
            for (String key : set) {
                sql += (key + ",");
            }
            sql += " tableName ) ";
            sql += " values ( ";
            for (String key : set) {
                sql += ("'" + map.get(key) + "',");
            }
            sql += ("'" + tableName + "' ) ");
            re = getJdbcTemplate().update(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 根据表名称创建一张表
     * @param tableName
     * @param obj
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int createTable(String tableName,Object obj) {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE `" + tableName + "` (");
        sb.append(" `id` int(11) NOT NULL AUTO_INCREMENT,");
        Map<String,String> map = ObjectUtil.getProperty(obj);
        Set<String> set = map.keySet();
        for (String key : set) {
            sb.append("`" + key + "` varchar(255) DEFAULT '',");
        }
        sb.append(" `tableName` varchar(255) DEFAULT '',");
        sb.append(" PRIMARY KEY (`id`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        try {
            getJdbcTemplate().update(sb.toString());
            return 1;
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
}
