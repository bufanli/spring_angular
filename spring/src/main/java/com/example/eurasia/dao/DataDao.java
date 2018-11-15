package com.example.eurasia.dao;

import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.DataXMLReader;
import com.example.eurasia.entity.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public int addData(String tableName, Data data) throws Exception {

        StringBuffer sql = new StringBuffer();
        int size = data.getKeyValue().size();
        String columnsNames = data.getKeys();
        //String columnsValues = data.getValues();
        //String[] columnsValuesArr = columnsValues.split(",",-1);
        String[] columnsValuesArr = data.getValuesToArray();

        sql.append("insert into " + tableName + "(" + columnsNames + ") values (");
        for (int i=0; i<size; i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - ",".length());
        sql.append(")");
        int num = getJdbcTemplate().update(sql.toString(),(Object[])columnsValuesArr);
        return num;//大于0，插入成功。
/*
StringBuffer sbf = new  StringBuffer("Hello World!");
sbf .setLength(0);//设置长度 (清楚内容效率最高)
sbf.delete(0, sbf.length());//删除(清楚内容效率最差)
sbf = new StringBuffer("");//重新new
*/
    }

    /**
     * 批处理添加数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void batchAddData(String tableName, Data[] data) throws Exception {
        //Nothing to do
    }

    /**
     * 删除数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void deleteData(String tableName, Data data) throws Exception {
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
    public int deleteSameData(String tableName) throws Exception {

        StringBuffer strCcolsName = new StringBuffer();
        List<Map<String,Object>> colsNameList = queryListForColumnName(tableName);
        for(Map<String,Object> colsName: colsNameList) {
            Set<Map.Entry<String, Object>> set = colsName.entrySet();
            Iterator<Map.Entry<String, Object>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,Object> entry = it.next();
                strCcolsName.append(entry.getValue());
                strCcolsName.append(",");
            }
        }
        strCcolsName.deleteCharAt(strCcolsName.length() - ",".length());
        String[] name = strCcolsName.toString().split(",",-1);

        StringBuffer sql =  new StringBuffer();
        sql.append("delete " + tableName);
        sql.append(" from " + tableName);
        sql.append(" ( select min(id) id," + strCcolsName);
        sql.append(" from " + tableName);
        sql.append(" group by " + strCcolsName);
        sql.append(" having count(*) > 1)tempSameDataTable");
        sql.append(" where ");
        sql.append(tableName + "." + name[0] + " = 'tempSameDataTable." + name[0]);
        for (int i=1; i < name.length; i++) {
            sql.append("' and " + tableName + "." + name[i] + " = 'tempSameDataTable." + name[i]);
        }
        sql.append("' and " + tableName + ".id > 'tempSameDataTable.id'");

        int num = getJdbcTemplate().update(sql.toString());//执行成功返回数据库当中受影响的记录条数，失败则返回-1
        return num;
    }

    /**
     * 更新数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void updateData(String tableName, Data data) throws Exception {
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
    public void batchUpdateData(String tableName, Data[] data) throws Exception {
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
    public Long queryTableNumbers(String tableName) throws Exception {
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
    public Object queryForObject(String tableName, Data queryConditions) throws Exception {
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
        String sqlAnd = " and ";
        String sqlOr= " or ";
        String dateStart = "";
        String dateEnd = "";
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName + " where");

        Set<Map.Entry<String, String>> set = queryConditions.getKeyValue().entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey().toString().equals("起始日期") == true) {//T.B.D
                dateStart = entry.getValue().toString();
                continue;
            }
            if (entry.getKey().toString().equals("结束日期") == true) {//T.B.D
                dateEnd = entry.getValue().toString();
                continue;
            }
/*
public static boolean isEmpty(String str)
判断某字符串是否为空，为空的标准是 str==null 或 str.length()==0
下面是 StringUtils 判断是否为空的示例：

StringUtils.isEmpty(null) = true
StringUtils.isEmpty("") = true
StringUtils.isEmpty(" ") = false //注意在 StringUtils 中空格作非空处理
StringUtils.isEmpty("   ") = false
StringUtils.isEmpty("bob") = false
StringUtils.isEmpty(" bob ") = false
*/
            if (!StringUtils.isEmpty(entry.getValue().toString())) {
                if(!entry.getValue().toString().contains("||")) {
                    sql.append(" " + entry.getKey().toString() + " like '%" + entry.getValue().toString() + "%'");
                    sql.append(sqlAnd);
                }else{
                    String values[] = entry.getValue().toString().split("\\|\\|",-1);
                    sql.append("( ");
                    for(String value : values){
                       sql.append(entry.getKey().toString() + " like '%" + value + "%'");
                       sql.append(sqlOr);
                    }
                    if (sql.indexOf(sqlOr) >= 0) {
                        sql.delete((sql.length() - sqlOr.length()),sql.length());
                    }
                    sql.append(" )");
                    sql.append(sqlAnd);
                }
            }
        }

        //T.B.D
        if (dateStart.equals("") == true && dateEnd.equals("") == false) {
            dateStart = "(select min(" + "日期" + ")";
            dateEnd = convertDateToNewFormat(dateEnd);
            sql.append(" (日期" + " between " + dateStart + " and '" + dateEnd + "')");
        } else if (dateStart.equals("") == false && dateEnd.equals("") == true) {
            dateEnd = "(select max(" + "日期" + "))";
            dateStart = convertDateToNewFormat(dateStart);
            sql.append(" (日期" + " between '" + dateStart + "' and " + dateEnd + ")");
        } else if (dateStart.equals("") == false && dateEnd.equals("") == false) {
            dateStart = convertDateToNewFormat(dateStart);
            dateEnd = convertDateToNewFormat(dateEnd);
            sql.append(" (日期" + " between '" + dateStart + "' and '" + dateEnd + "')");
        } else if (dateStart.equals("") == true && dateEnd.equals("") == true)  {
            if (sql.indexOf(sqlAnd) >= 0) {
                sql.delete((sql.length() - sqlAnd.length()),sql.length());
            }
        }

        //sql.append(" concat_ws(" + queryConditions.getKeys() + ")");

        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }
    /**
     * 查询并返回List集合
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> queryListForObject(String tableName, QueryCondition[] queryConditionsArr) {
        String sqlAnd = " and ";
        String sqlOr= " or ";
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName + " where");

        for (QueryCondition queryCondition : queryConditionsArr) {
            String key = queryCondition.getValue();
            switch (queryCondition.getType()) {
                case QueryCondition.QUERY_CONDITION_TYPE_STRING:
                    String value = queryCondition.getValue();
                    sql.append(" " + key + " like '%" + value + "%'");
                    sql.append(sqlAnd);
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_DATE:
                    String dateArr[] = queryCondition.getValue().split(QueryCondition.SPLIT_DATE,-1);
                    String dateStart = dateArr[0];
                    String dateEnd = dateArr[1];
                    if (dateStart.equals("") == true && dateEnd.equals("") == false) {
                        dateStart = "(select min(" + key + ")";
                        dateEnd = convertDateToNewFormat(dateEnd);
                        sql.append(" (" + key + " between " + dateStart + " and '" + dateEnd + "')");
                    } else if (dateStart.equals("") == false && dateEnd.equals("") == true) {
                        dateEnd = "(select max(" + key + "))";
                        dateStart = convertDateToNewFormat(dateStart);
                        sql.append(" (" + key + " between '" + dateStart + "' and " + dateEnd + ")");
                    } else if (dateStart.equals("") == false && dateEnd.equals("") == false) {
                        dateStart = convertDateToNewFormat(dateStart);
                        dateEnd = convertDateToNewFormat(dateEnd);
                        sql.append(" (" + key + " between '" + dateStart + "' and '" + dateEnd + "')");
                    } else if (dateStart.equals("") == true && dateEnd.equals("") == true) {
                        if (sql.indexOf(sqlAnd) >= 0) {
                            sql.delete((sql.length() - sqlAnd.length()),sql.length());
                        }
                    }
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                    String listArr[] = queryCondition.getValue().split(QueryCondition.SPLIT_LIST,-1);
                    sql.append("( ");
                    for (String list : listArr) {
                        sql.append(key + " like '%" + list + "%'");
                        sql.append(sqlOr);
                    }
                    if (sql.indexOf(sqlOr) >= 0) {
                        sql.delete((sql.length() - sqlOr.length()),sql.length());
                    }
                    sql.append(" )");
                    sql.append(sqlAnd);
                    
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
                    String moneyArr[] = queryCondition.getValue().split(QueryCondition.SPLIT_MONEY,-1);
                    String moneyStart = moneyArr[0];
                    String moneyEnd = moneyArr[1];
                    if (moneyStart.equals("") == true && moneyEnd.equals("") == false) {
                        moneyStart = "(select min(" + key + ")";
                        sql.append(" (" + key + " between " + moneyStart + " and '" + moneyEnd + "')");
                    } else if (moneyStart.equals("") == false && moneyEnd.equals("") == true) {
                        moneyEnd = "(select max(" + key + "))";
                        sql.append(" (" + key + " between '" + moneyStart + "' and " + moneyEnd + ")");
                    } else if (moneyStart.equals("") == false && moneyEnd.equals("") == false) {
                        sql.append(" (" + key + " between '" + moneyStart + "' and '" + moneyEnd + "')");
                    } else if (moneyStart.equals("") == true && moneyEnd.equals("") == true) {
                        if (sql.indexOf(sqlAnd) >= 0) {
                            sql.delete((sql.length() - sqlAnd.length()),sql.length());
                        }
                    }
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                    String amountArr[] = queryCondition.getValue().split(QueryCondition.SPLIT_AMOUNT,-1);
                    String amountStart = amountArr[0];
                    String amountEnd = amountArr[1];
                    if (amountStart.equals("") == true && amountEnd.equals("") == false) {
                        amountStart = "(select min(" + key + ")";
                        sql.append(" (" + key + " between " + amountStart + " and '" + amountEnd + "')");
                    } else if (amountStart.equals("") == false && amountEnd.equals("") == true) {
                        amountEnd = "(select max(" + key + "))";
                        sql.append(" (" + key + " between '" + amountStart + "' and " + amountEnd + ")");
                    } else if (amountStart.equals("") == false && amountEnd.equals("") == false) {
                        sql.append(" (" + key + " between '" + amountStart + "' and '" + amountEnd + "')");
                    } else if (amountStart.equals("") == true && amountEnd.equals("") == true) {
                        if (sql.indexOf(sqlAnd) >= 0) {
                            sql.delete((sql.length() - sqlAnd.length()),sql.length());
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }
    // angular得到的时间格式是 2018/9/11 和数据库2018/09/11里面不一致，所以转换一下
    private String convertDateToNewFormat(String dateFromAngular){
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
     * 查询并返回List集合
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Data> queryListForAllObject(String tableName) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName);
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
    public int saveData(String tableName, Data data) throws Exception {
        int re = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into " + tableName + " (");
            Map<String,String> map = data.getKeyValue();
            Set<String> set = map.keySet();
            for (String key : set) {
                sql.append(key + ",");
            }
            sql.deleteCharAt(sql.length() - ",".length());
            sql.append(" ) values ( ");
            for (String key : set) {
                sql.append("'" + map.get(key) + "',");
            }
            sql.deleteCharAt(sql.length() - ",".length());
            sql.append(" ) ");
            re = getJdbcTemplate().update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
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
    public boolean createTable(String tableName) throws Exception {
        try {
            // 判断数据库是否已经存在这个名称的表，如果有某表，直接返回；否则动态创建表之后再返回
            if (isExistTableName(tableName)) {
                return true;
            } else {
                ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                DataXMLReader dataXMLReader = (DataXMLReader) context.getBean("columnsDefaultName");

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
     * 查询某表的列名
     * @param tableName
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-15 23:11:00
     */
    public List<Map<String,Object>> queryListForColumnName(String tableName) throws Exception {
        /*
        Select COLUMN_NAME 列名, DATA_TYPE 字段类型, COLUMN_COMMENT 字段注释
        from INFORMATION_SCHEMA.COLUMNS
        Where table_name = 'companies'  ##表名
        AND table_schema = 'testhuicard'##数据库名
        AND column_name LIKE 'c_name'   ##字段名

        mysql查询表所有列名，并用逗号分隔
        SELECT GROUP_CONCAT(COLUMN_NAME SEPARATOR ",") FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = 'db_name' AND TABLE_NAME = 'table_name'
        */

        StringBuffer sql = new StringBuffer();
        sql.append("select ORDINAL_POSITION,COLUMN_NAME from information_schema.COLUMNS where table_name = '" + tableName);
        sql.append("' and table_schema = 'eurasia' order by ORDINAL_POSITION asc");//在这之前要手动创建完数据库。asc生序，desc降序。
        List<Map<String, Object>> colsNameList = getJdbcTemplate().queryForList(sql.toString());
/*
        for(Map<String,Object> colsName: colsNameList) {
            Set<Map.Entry<String, Object>> set = colsName.entrySet();
            Iterator<Map.Entry<String, Object>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,Object> entry = it.next();
                //System.out.println("Key:" + entry.getKey() + " Value:" + entry.getValue());//Key:COLUMN_NAME Value:xxx
            }
        }
*/
        return colsNameList;
    }

    /**
     * 查询条件
     * @param tableName
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-15 00:00:00
     */
    public QueryCondition[] queryListForQueryConditions(String tableName) throws Exception {
        return null;//T.B.D
    }

    /**
     * T.B.D
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-15 23:11:00
     */
    public List getFirstName(int userID)
    {
        String sql = "select firstname from users where user_id = " + userID;

        SingleColumnRowMapper rowMapper = new SingleColumnRowMapper(String.class);
        List firstNameList = (List) getJdbcTemplate().query(sql, rowMapper);

        for(Object firstName: firstNameList)
            System.out.println(firstName.toString());

        return firstNameList;
    }

    /**
     * T.B.D
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-15 23:11:00
     */
    public List<Map<String, Object>> getUserData(int userID)
    {
        String sql = "select firstname, lastname, dept from users where userID = ? ";

        ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
        List<Map<String, Object>> userDataList =  getJdbcTemplate().query(sql, rowMapper, userID);

        for(Map<String, Object> map: userDataList){
            System.out.println("FirstName = " + map.get("firstname"));
            System.out.println("LastName = " + map.get("lastname"));
            System.out.println("Department = " + map.get("dept"));
        }

        return userDataList;

    }
}
