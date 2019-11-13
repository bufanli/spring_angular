package com.example.eurasia.dao;

import com.example.eurasia.entity.Data.ComputeField;
import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.DataXMLReader;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class CommonDao {

    public static final String COMMA = ",";
    public static final String ID_COMMA = "id,";

/*
1.excute: 可以执行所有的SQL语句，一般用于执行DDL语句。
2.update: 用于执行insert、update、delete等DML语句。
3.QueryXxx: 用于DQL数据查询语句。
*/
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
     *
     * @param databaseName
     * @return
     * @throws
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
            if (databases != null) databases.close();
            if (stmt != null) stmt.close();
            if (preStatement != null) preStatement.close();
            if (conn != null) conn.close();
        }

        return false;
    }

    /**
     * 根据表名称创建一张表
     *
     * @param tableName
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createTable(String tableName, String beanName) throws Exception {
        try {
            // 判断数据库是否已经存在这个名称的表，如果有某表，直接返回；否则动态创建表之后再返回
            if (isExistTableName(tableName)) {
                return false;
            } else {
                ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                DataXMLReader dataXMLReader = (DataXMLReader) context.getBean(beanName);
                Map<String, String> columnNameType = dataXMLReader.getKeyValue();

                StringBuffer sql = convertCreatingTableWithPrimaryKeyToSQL(tableName, columnNameType);
                int count = getJdbcTemplate().update(sql.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据表名称创建一张表
     *
     * @param tableName
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createTable(String tableName, Map<String, String> columnNameType) throws Exception {
        try {
            // 判断数据库是否已经存在这个名称的表，如果有某表，直接返回；否则动态创建表之后再返回
            if (isExistTableName(tableName)) {
                return false;
            } else {

                StringBuffer sql = convertCreatingTableWithPrimaryKeyToSQL(tableName, columnNameType);
                int count = getJdbcTemplate().update(sql.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据表名称删除一张表
     *
     * @param tableName
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-10-18 00:00:00
     */
    public boolean deleteTable(String tableName) throws Exception {
        try {
            // 判断数据库是否已经存在这个名称的表，如果没有某表，直接返回；否则删除之后再返回
            if (!isExistTableName(tableName)) {
                return true;
            } else {

                StringBuffer sql = new StringBuffer();
                sql.append("DROP TABLE " + tableName);
                int count = getJdbcTemplate().update(sql.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 复制旧表结构到新表（包含主键、索引、分区等）
     *
     * @param src 旧表名字
     * @param des 新表名字
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-10-31 00:00:00
     */
    public boolean copyTableStructure(String src, String des) throws Exception {
        try {
            // 判断数据库是否已经存在这个名称的表，如果没有某表，直接创建；否则删除之后再创建
            if (this.deleteTable(des)) {
                StringBuffer sql = new StringBuffer();
                sql.append("CREATE TABLE " + des + " LIKE " + src);
                int count = getJdbcTemplate().update(sql.toString());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 复制旧表数据到新表（俩表的字段名相同）
     *
     * @param src 旧表名字
     * @param des 新表名字
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-10-31 00:00:00
     */
    public int copyTableData(String src, String des) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + des + " SELECT * FROM " + src);
        int num = getJdbcTemplate().update(sql.toString());
        return num;
    }

    /**
     * 给表中某个字段添加唯一属性
     *
     * @param tableName
     * @return
     * @throws
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
     *
     * @param
     * @return
     * @throws
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
            // 判断数据库是否已经存在这个名称的表，如果有某表，直接返回；否则动态创建表之后再返回
            if (isExistTableName("SPRING_SESSION")) {
                return true;
            } else {
                StringBuffer sql = new StringBuffer();
                sql.append("CREATE TABLE `SPRING_SESSION` (");
                sql.append("`PRIMARY_ID` char(36) NOT NULL,");
                sql.append("`SESSION_ID` char(36) NOT NULL,");
                sql.append("`CREATION_TIME` BIGINT NOT NULL,");
                sql.append("`LAST_ACCESS_TIME` BIGINT NOT NULL,");
                sql.append("`MAX_INACTIVE_INTERVAL` INT NOT NULL,");
                sql.append("`EXPIRY_TIME` BIGINT NOT NULL,");
                sql.append("`PRINCIPAL_NAME` varchar(100) DEFAULT NULL,");
                sql.append("CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)");
                //sql.append("PRIMARY KEY (`SESSION_ID`) USING BTREE,");
                //sql.append("KEY `SPRING_SESSION_IX1` (`LAST_ACCESS_TIME`) USING BTREE");
                sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                getJdbcTemplate().update(sql.toString());
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 创建SPRING_SESSION_ATTRIBUTES
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createSpringSessionAttributesTable() throws Exception {

        try {
            if (isExistTableName("SPRING_SESSION_ATTRIBUTES")) {
                return true;
            } else {
                StringBuffer sql = new StringBuffer();
                sql.append("CREATE TABLE `SPRING_SESSION_ATTRIBUTES` (");
                sql.append("`SESSION_PRIMARY_ID` char(36) NOT NULL DEFAULT '',");
                sql.append("`ATTRIBUTE_NAME` varchar(200) NOT NULL DEFAULT '',");
                sql.append("`ATTRIBUTE_BYTES` blob NOT NULL,");
                sql.append("CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),");
                sql.append("CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE");
                //sql.append("PRIMARY KEY (`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME`),");
                //sql.append("KEY `SPRING_SESSION_ATTRIBUTES_IX1` (`SESSION_PRIMARY_ID`) USING BTREE,");
                //sql.append("CONSTRAINT `SPRING_SESSION_ATTRIBUTES_ibfk_1` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `SPRING_SESSION` (`SESSION_ID`) ON DELETE CASCADE");
                sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                getJdbcTemplate().update(sql.toString());
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询数据库是否有某表
     *
     * @param tableName
     * @return
     * @throws Exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean isExistTableName(String tableName) throws Exception {
        Connection conn = null;
        ResultSet tables = null;
        try {
            conn = getJdbcTemplate().getDataSource().getConnection();
            DatabaseMetaData dbMetaData = conn.getMetaData();
            String[] types = {"TABLE"};
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
            if (tables != null) tables.close();
            if (conn != null) conn.close();
        }
        return false;
    }

    // angular得到的时间格式是 2018/9/11 和数据库2018/09/11里面不一致，所以转换一下
    public String convertDateToNewFormat(String dateFromAngular) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_1);
            Date tempDateEnd = sdf.parse(dateFromAngular);
            return sdf.format(tempDateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
            // 格式不正确的时候至少返回原来的时间字符串
            return dateFromAngular;
        }
    }


    /**
     * 查询某表某字段指定值的个数
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 23:11:00
     */
    public Long queryCountOfColumnValue(String tableName, String columnName, String value)
    {
        //String sql = "select 1 from " + tableName +" where userID = '" + userID + "' limit 1";
        String sql = "select count(*) from " + tableName +" where " + columnName + " = '" + value + "'";
        Long count = getJdbcTemplate().queryForObject(sql,Long.class);
        return count;
    }

    /**
     * 查询某表某字段指定值的个数,本条数据以外。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 23:11:00
     */
    public Long queryCountOfColumnValueExcept(String id, String tableName, String columnName, String value)
    {
        String sql = "select count(*) from " + tableName +" where " + columnName + " = '" + value + "' and id not in ('" + id + "')";
        Long count = getJdbcTemplate().queryForObject(sql,Long.class);
        return count;
    }

    /**
     * 查询表的记录数
     *
     * @param
     * @return
     * @throws
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
     * query table row with query conditions
     *
     * @param tableName
     * @return
     * @throws Exception
     */
    public Long queryTableRows(String tableName,
                               QueryCondition[] queryConditions) throws Exception {
        StringBuffer sql = convertQueryConditionsToSQL(tableName, queryConditions, true);
        Long row = getJdbcTemplate().queryForObject(sql.toString(), Long.class);
        //System.out.println("查询出来的记录数为：" + row);
        return row;
    }

    /**
     * 查询表的字段数
     *
     * @param
     * @return
     * @throws
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
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-10-29 00:00:00
     */
    public List<Map<String, Object>> queryListForColumnMinMaxValues(String tableName, String category) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select min(" + category + "),max(" + category + ") from " + tableName);

        List<Map<String, Object>> selectionColsList = getJdbcTemplate().queryForList(sql.toString());

        return selectionColsList;
    }

    /**
     * 查询数据库中的最近一个月
     *
     * @param tableName
     * @return
     * @throws
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
/*
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT " + dateColumnName + " FROM " + tableName);
        sql.append(" WHERE DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(now()) order by " + dateColumnName);

        StringBuffer sqlAsc = new StringBuffer();
        sqlAsc.append(" asc");

        StringBuffer sqlDesc = new StringBuffer();
        sqlDesc.append(" desc");

        StringBuffer sqlLimit = new StringBuffer();
        sqlLimit.append(" limit 0,1");

        sql.append(sqlAsc);
        //sql.append(sqlLimit);
        List<Map<String, Object>> dateAscList = getJdbcTemplate().queryForList(sql.toString());

        //sql.append(sqlDesc);
        //sql.append(sqlLimit);
        //List<Map<String, Object>> dateDescList = getJdbcTemplate().queryForList(sql.toString());

        String[] dateArr = new String[2];
        dateArr[0] = dateAscList.get(0).get(dateColumnName).toString();
        //dateArr[1] = dateDescList.get(0).get(dateColumnName).toString();
        dateArr[1] = dateAscList.get(dateAscList.size()-1).get(dateColumnName).toString();
*/
        StringBuffer sql = new StringBuffer();
        sql.append("select max(" + dateColumnName + ") from " + tableName);
        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        if (dataList.size() != 1) {
            return null;
        }
        if (dataList.get(0).getKeyValue().size() != 1) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_1);
        Date date = null;
        String[] dateArr = new String[2];
        dateArr[1] = dataList.get(0).getKeyValue().get("max(" + dateColumnName + ")");
        Slf4jLogUtil.get().info("从数据库中取出的日期最大值是:" + dateArr[1]);
        if (StringUtils.isEmpty(dateArr[1])) {
            //数据库中没有数据的话，取今天日期。
            date = new Date();
            dateArr[1] = sdf.format(date);
        } else {
            date = formatStringToDate(dateArr[1]);
            if (date == null) {
                date = new Date();
                dateArr[1] = sdf.format(date);
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);//当前时间前去一个月，即一个月前的时间    
        Date dateMouth = calendar.getTime();
        dateArr[0] = sdf.format(dateMouth);
        return dateArr;
    }

    // try different date formats
    private Date formatStringToDate(String dateStr) throws ParseException {
        Date retDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_1);
        try {
            retDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            try {
                sdf = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_2);
                retDate = sdf.parse(dateStr);
            } catch (ParseException e1) {
                try {
                    sdf = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_3);
                    retDate = sdf.parse(dateStr);
                } catch (ParseException e2) {
                    retDate = null;
                }
            }
        }
        return retDate;
    }

    /**
     * convert query conditions to sql
     */
    protected StringBuffer convertQueryConditionsToSQL(String tableName,
                                                       QueryCondition[] queryConditionsArr,
                                                       boolean isQueryCount) {

        StringBuffer sql = new StringBuffer();
        if (isQueryCount) {
            sql.append("select count(*) from " + tableName);
        } else {
            sql.append("select * from " + tableName);
        }

        sql.append(convertWhereToSQL(queryConditionsArr));
        return sql;
    }

    /**
     * convert query conditions to sql
     */
    protected StringBuffer convertReportQueryDataToSQL(String tableName,
                                                       StringBuffer selectField,
                                                       StringBuffer groupByField,
                                                       ComputeField[] computeFields,
                                                       QueryCondition[] queryConditionsArr) {

        StringBuffer sql = new StringBuffer();
        sql.append("select " + selectField + CommonDao.COMMA);
        for (ComputeField computeField : computeFields) {
            sql.append(computeField.toSql() + CommonDao.COMMA);
        }
        sql.deleteCharAt(sql.length() - 1);//","的长度为1，所以删除最后一个","即删除下标为sql.length()-1字符
        sql.append(" from " + tableName);
        sql.append(convertWhereToSQL(queryConditionsArr));
        sql.append(" group by " + groupByField);
        return sql;
    }

    /**
     * convert "where" to sql
     */
    protected StringBuffer convertWhereToSQL(QueryCondition[] queryConditionsArr) {
        String sqlAnd = " and ";
        String sqlOr = " or ";
        String sqlWhere = " where ";
        boolean haveCondition = false;

        StringBuffer sql = new StringBuffer();
        sql.append(sqlWhere);
        for (QueryCondition queryCondition : queryConditionsArr) {
            String key = queryCondition.getKey();
            switch (queryCondition.getType()) {
                case QueryCondition.QUERY_CONDITION_TYPE_STRING:
                    if (queryCondition.isValuesNotNULL() == true) {//不为空
                        haveCondition = true;
                        String value = queryCondition.getValue();
                        sql.append("( ");
                        sql.append(key + " like '%" + value + "%'");
                        sql.append(" )");
                        sql.append(sqlAnd);
                    } else {

                    }
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_DATE:
                    haveCondition = true;
                    String dateArr[] = queryCondition.getQueryConditionToArr();
                    String dateStart = dateArr[0];
                    String dateEnd = dateArr[1];
                    sql.append("( ");
                    if (dateStart.equals("") == true && dateEnd.equals("") == false) {
                        dateStart = "(select min(" + key + "))";
                        dateEnd = convertDateToNewFormat(dateEnd);
                        sql.append(key + " between " + dateStart + " and '" + dateEnd + "'");
                    } else if (dateStart.equals("") == false && dateEnd.equals("") == true) {
                        dateStart = convertDateToNewFormat(dateStart);
                        dateEnd = "(select max(" + key + "))";
                        sql.append(key + " between '" + dateStart + "' and " + dateEnd);
                    } else if (dateStart.equals("") == false && dateEnd.equals("") == false) {
                        dateStart = convertDateToNewFormat(dateStart);
                        dateEnd = convertDateToNewFormat(dateEnd);
                        sql.append(key + " between '" + dateStart + "' and '" + dateEnd + "'");
                    } else if (dateStart.equals("") == true && dateEnd.equals("") == true) {
                        dateStart = "(select min(" + key + "))";
                        dateEnd = "(select max(" + key + "))";
                        sql.append(key + " between " + dateStart + " and " + dateEnd);
                    }
                    sql.append(" )");
                    sql.append(sqlAnd);
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
                case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                    haveCondition = true;
                    String arr[] = queryCondition.getQueryConditionToArr();
                    String conditionStart = arr[0];
                    String conditionEnd = arr[1];
                    sql.append("( ");
                    if (conditionStart.equals("") == true && conditionEnd.equals("") == false) {
                        conditionStart = "(select min(" + key + "))";
                        sql.append(key + " between " + conditionStart + " and '" + conditionEnd + "'");
                    } else if (conditionStart.equals("") == false && conditionEnd.equals("") == true) {
                        conditionEnd = "(select max(" + key + "))";
                        sql.append(key + " between '" + conditionStart + "' and " + conditionEnd);
                    } else if (conditionStart.equals("") == false && conditionEnd.equals("") == false) {
                        sql.append(key + " between '" + conditionStart + "' and '" + conditionEnd + "'");
                    } else if (conditionStart.equals("") == true && conditionEnd.equals("") == true) {
                        conditionStart = "(select min(" + key + "))";
                        conditionEnd = "(select max(" + key + "))";
                        sql.append(key + " between " + conditionStart + " and " + conditionEnd);
                    }
                    sql.append(" )");
                    sql.append(sqlAnd);
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                    if (queryCondition.isValuesNotNULL() == true) {//不为空
                        haveCondition = true;
                        String listArr[] = queryCondition.getQueryConditionToArr();
                        sql.append("( ");
                        StringBuffer sqlList = new StringBuffer();
                        for (String list : listArr) {
                            if (!StringUtils.isEmpty(list)) {
                                sqlList.append(key + " like '%" + list + "%'");
                                sqlList.append(sqlOr);
                            }
                        }
                        if (sqlList.indexOf(sqlOr) >= 0) {
                            sqlList.delete((sqlList.length() - sqlOr.length()), sqlList.length());
                        }
                        sql.append(sqlList);
                        sql.append(" )");
                        sql.append(sqlAnd);
                    } else {

                    }
                    break;
                default:
                    break;
            }
        }
        if (sql.indexOf(sqlAnd) >= 0) {
            sql.delete((sql.length() - sqlAnd.length()), sql.length());
        }
        if (haveCondition == false) {
            sql.delete((sql.length() - sqlWhere.length()), sql.length());
        }
        return sql;
    }

    /**
     * convert "where" to sql
     */
    protected StringBuffer convertWhereToSQL(String[] columnNames, String[] values) {
        String sqlAnd = " and ";

        StringBuffer sql = new StringBuffer();
        sql.append(" where ");
        for (int i=0; i<columnNames.length; i++) {
            sql.append("( ");
            sql.append(columnNames[i] + " like '%" + values[i] + "%'");//value不能为null
            sql.append(" )");
            sql.append(sqlAnd);
        }
        if (sql.indexOf(sqlAnd) >= 0) {
            sql.delete((sql.length() - sqlAnd.length()), sql.length());
        }
        return sql;
    }

    /**
     * convert "order" to sql
     */
    public StringBuffer convertOrderToSQL(Map<String, String> order) {

        StringBuffer sql = new StringBuffer();
        sql.append(" order by ");
        Set<Map.Entry<String, String>> set = order.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            sql.append(entry.getKey() + " " + entry.getValue() + CommonDao.COMMA);
        }
        sql.deleteCharAt(sql.length() - 1);//","的长度为1，所以删除最后一个","即删除下标为sql.length()-1字符
        return sql;
    }

    /**
     * convert creating table with "id" primary key to sql
     */
    protected StringBuffer convertCreatingTableWithPrimaryKeyToSQL(String tableName, Map<String, String> columnNameType) {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE `" + tableName + "` (");
        sql.append(" `id` INT(16) NOT NULL AUTO_INCREMENT,");
        for (Map.Entry<String, String> entry : columnNameType.entrySet()) {
            //sql.append("`" + entry.getKey() + "` " + entry.getValue() + " NOT NULL,");//key字段名 value字段类型
            sql.append("`" + entry.getKey() + "` " + entry.getValue() + " DEFAULT \"\",");//key字段名 value字段类型
        }
        sql.append(" PRIMARY KEY (`id`)");
        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=gbk;");
//MyISAM适合：(1)做很多count 的计算；(2)插入不频繁，查询非常频繁；(3)没有事务。
//InnoDB适合：(1)可靠性要求比较高，或者要求事务；(2)表更新和查询都相当的频繁，并且行锁定的机会比较大的情况
        return sql;
    }

    /**
     * convert selections to sql
     */
    protected StringBuffer convertSelectionsToSQL(String tableName, String[] selectionCols) {

        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        for (String selectionCol : selectionCols) {
            sql.append(selectionCol + CommonDao.COMMA);
        }
        sql.deleteCharAt(sql.length() - 1);//","的长度为1，所以删除最后一个","即删除下标为sql.length()-1字符
        sql.append(" from " + tableName);

        return sql;
    }

    /**
     * convert groupBys to sql
     */
    protected StringBuffer convertGroupByToSQL(String[] groupBys) {

        StringBuffer sql = new StringBuffer();
        sql.append(" group by ");
        for (String groupBy : groupBys) {
            sql.append(groupBy + CommonDao.COMMA);
        }
        sql.deleteCharAt(sql.length() - 1);//","的长度为1，所以删除最后一个","即删除下标为sql.length()-1字符

        return sql;
    }
}
