package com.example.eurasia.dao;

import com.example.eurasia.entity.Data.ComputeField;
import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.QueryCondition;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class DataDao extends CommonDao {

    public static int INSERT_RECODE_STEPS = 10000;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_1 = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_1);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_2 = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_2);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_3 = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_3);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_4 = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_4);

    /**
     * 添加数据
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int addData(String tableName, Data data) throws Exception {

        StringBuffer sql = new StringBuffer();
        int size = data.getKeyValue().size();
        String columnsNames = data.getKeys();
        //String columnsValues = data.getValues();
        //String[] columnsValuesArr = columnsValues.split(CommonDao.COMMA,-1);
        String[] columnsValuesArr = data.getValuesToArray();

        sql.append("insert into " + tableName + "(" + columnsNames + ") values (");
        for (int i = 0; i < size; i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - CommonDao.COMMA.length());
        sql.append(")");
        int num = getJdbcTemplate().update(sql.toString(), (Object[]) columnsValuesArr);
        return num;//大于0，插入成功。返回影响的行数。
/*
StringBuffer sbf = new  StringBuffer("Hello World!");
sbf .setLength(0);//设置长度 (清楚内容效率最高)
sbf.delete(0, sbf.length());//删除(清楚内容效率最差)
sbf = new StringBuffer("");//重新new
*/
    }

    /**
     * 批量添加数据
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-06-15 00:00:00
     */
    public int[] batchAddData(String tableName, List<Data> dataList, Data dataType) throws Exception {
        StringBuffer sql = new StringBuffer();
        int size = dataList.get(0).getKeyValue().size();

        Set<Integer> indexSet = new LinkedHashSet<>();

        int index = 0;
        for (String columnName : dataList.get(0).getKeyValue().keySet()) {

            for (Map.Entry<String, String> dataTypeEntry : dataType.getKeyValue().entrySet()) {
                if (columnName.equals(dataTypeEntry.getKey())) {
                    if (dataTypeEntry.getValue().equals(QueryCondition.QUERY_CONDITION_TYPE_DATE)) {
                        indexSet.add(index);
                        break;
                    }
                }
            }

            index++;
        }

        String columnsNames = dataList.get(0).getKeys();
        sql.append("insert into " + tableName + "(" + columnsNames + ") values ");

        sql.append("(");
        for (int i = 0; i < size; i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - CommonDao.COMMA.length());
        sql.append(")");
/*
        List<Object[]> columnsValuesArrList = new ArrayList<>();
        for (Data data : dataList) {
            Object[] columnsValuesArr = data.getKeyValue().values().toArray();
            columnsValuesArrList.add(columnsValuesArr);

        }

        int[] numArr = getJdbcTemplate().batchUpdate(sql.toString(),columnsValuesArrList);
*/
        List<InsertStep> batchInsertSteps = splitBatchInsertSteps(dataList.size());
        int[] totalNumArr = null;
        for (InsertStep insertStep : batchInsertSteps) {
            MultiInsertBatchPreparedStatementSetter setter = new
                    MultiInsertBatchPreparedStatementSetter();
            setter.setColumnsNumber(size);
            setter.setDataList(dataList);
            setter.setDataColumns(indexSet);
            setter.setInsertStep(insertStep);
            int[] numArr = getJdbcTemplate().batchUpdate(sql.toString(),setter);
            if(totalNumArr == null){
                totalNumArr = numArr;
            }else{
                totalNumArr = concat(totalNumArr,numArr);
            }
        }
        return totalNumArr;//大于0，插入成功。返回影响的行数
    }
    private int[] concat(int[] left,int[] right){
        int[] dst = new int[left.length + right.length];
        System.arraycopy(left,0,dst,0,left.length);
        System.arraycopy(right,0,dst, left.length,right.length);
        return dst;
    }
    private List<InsertStep> splitBatchInsertSteps(int dataListSize) {
        List<InsertStep> batchInsertSteps = new ArrayList<InsertStep>();
        int offset = 0;
        while (dataListSize > 0) {
            if (dataListSize > INSERT_RECODE_STEPS) {
                InsertStep insertStep = new InsertStep(offset, INSERT_RECODE_STEPS);
                batchInsertSteps.add(insertStep);
                offset += INSERT_RECODE_STEPS;
                dataListSize -= INSERT_RECODE_STEPS;
            } else {
                InsertStep insertStep = new InsertStep(offset, dataListSize);
                batchInsertSteps.add(insertStep);
                offset += dataListSize;
                dataListSize = 0;
            }
        }
        return batchInsertSteps;
    }

    private class MultiInsertBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
        // insert date
        private List<Data> dataList = null;
        // insert date type
        private Data dataType = null;
        // insert Date column index Set
        private Set<Integer> dateColumns = null;
        // insert step
        private InsertStep insertStep = null;
        // columns number
        private int columnsNumber = 0;

        // set data list
        public void setDataList(List<Data> dataList) {
            this.dataList = dataList;
        }

        // set data type
        public void setDataType(Data dataType) {
            this.dataType = dataType;
        }

        // set Date column index Set
        public void setDataColumns(Set<Integer> dateColumns) {
            this.dateColumns = dateColumns;
        }

        // set step
        public void setInsertStep(InsertStep insertStep) {
            this.insertStep = insertStep;
        }

        // set columns number
        public void setColumnsNumber(int columnsNumber) {
            this.columnsNumber = columnsNumber;
        }
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Data rowData = this.dataList.get(this.insertStep.getOffset() + i);
            String[] rowDataValues = rowData.getValuesToArray();
            for (int index = 0; index < this.columnsNumber; index++) {
                if (this.dateColumns.contains(index)) {
                    ps.setString(index + 1, formatDate(rowDataValues[index]));
                } else {
                    ps.setString(index + 1, rowDataValues[index]);
                }
            }
        }
        @Override
        public int getBatchSize() {
            return this.insertStep.limit;
        }
    }

    // insert step class
    private class InsertStep {
        private int offset = 0;
        private int limit = 0;

        public InsertStep(int offset, int limit) {
            this.limit = limit;
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }
    }

    /**
     * 添加数据
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int addData(String tableName, String columnName, String columnValue) throws Exception {

        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + tableName + "(" + columnName + ") values (?)");

        int num = getJdbcTemplate().update(sql.toString(), columnValue);
        return num;//大于0，插入成功。返回影响的行数。
    }

    /**
     * 批处理添加数据
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void batchAddData(String tableName, Data[] data) throws Exception {
        //Nothing to do
    }

    /**
     * 删除数据
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int deleteData(String tableName, Data data) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("delete from " + tableName + " where ");

        Set<Map.Entry<String, String>> set = data.getKeyValue().entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            sql.append(entry.getKey() + "='" + entry.getValue() + "'");
        }

        int num = getJdbcTemplate().update(sql.toString());
        return num;//大于0，插入成功。返回影响的行数。
    }

    /**
     * 删除重复的数据，只保留一行
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int deleteSameData(String tableName) throws Exception {

        StringBuffer strColsName = new StringBuffer();
        List<Map<String, Object>> colsNameList = this.queryListForColumnName(tableName);
        for (Map<String, Object> colsName : colsNameList) {
            strColsName.append(colsName.get("COLUMN_NAME").toString());
            strColsName.append(CommonDao.COMMA);
        }

        strColsName.deleteCharAt(strColsName.length() - CommonDao.COMMA.length());
        strColsName.replace(strColsName.indexOf(CommonDao.ID_COMMA), CommonDao.ID_COMMA.length(), "");//indexOf从0开始计算,没有查到指定的字符则该方法返回-1
        String[] name = strColsName.toString().split(CommonDao.COMMA, -1);
/*
mysql根据两个字段判断重复的数据并且删除，只保留一条。
DELETE from table
where id Not IN
(select id from
(select MIN(id) as id,count(列1) as count from table
GROUP BY 列1,date
HAVING count(列1)>1) as temp);

MySQL统计重复数据，根据多条字段查询。
SELECT count(*),列1,列2,列3 from table
GROUP BY 列1,列2,列3 having count(*) > 1;
 */
        StringBuffer sql = new StringBuffer();
        sql.append("delete from " + tableName);
        sql.append(" where id not in");
        sql.append(" (select minid from");
        sql.append(" (select min(id) as minid");
        sql.append(" from " + tableName);
        sql.append(" group by " + strColsName);
        sql.append(" having count(*) >= 1) as tempSameDataTable)");

        int num = getJdbcTemplate().update(sql.toString());//执行成功返回数据库当中受影响的记录条数，失败则返回-1
        return num;
    }

    /**
     * 删除全部的数据
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-06-03 00:00:00
     */
    public int deleteAllData(String tableName) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("delete from " + tableName);

        int num = getJdbcTemplate().update(sql.toString());//执行成功返回数据库当中受影响的记录条数，失败则返回-1
        return num;
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void updateData(String tableName, Data data) throws Exception {
        //Nothing to do
    }

    /**
     * 批处理更新数据
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void batchUpdateData(String tableName, Data[] data) throws Exception {
        //Nothing to do
    }

    /**
     * 查询返回对象
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public Object queryForObject(String tableName, Data queryConditions) throws Exception {
        //Nothing to do
        return null;
    }

    /**
     * 添加字段
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-06-07 00:00:00
     */
    public int addColumn(String tableName, String columnName, String columnType) throws Exception {

        StringBuffer sql = new StringBuffer();
        //sql.append("alter table " + tableName + " add column " + columnName + " " + columnType + " NOT NULL");
        sql.append("alter table " + tableName + " add column " + columnName + " " + columnType + " DEFAULT \"\"");

        int num = getJdbcTemplate().update(sql.toString());
        return num;//返回影响的行数。(成功的话，0 rows affected)
    }

    /**
     * 字段
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-06-08 00:00:00
     */
    public int deleteColumn(String tableName, String columnName) throws Exception {

        StringBuffer sql = new StringBuffer();
        sql.append("alter table " + tableName + " drop column " + columnName);

        int num = getJdbcTemplate().update(sql.toString());
        return num;//返回影响的行数。(成功的话，0 rows affected)
    }

    /**
     * 字段各值的数
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-06-08 00:00:00
     */
    public List<Long> getColumnValueCounts(String tableName, String columnName) throws Exception {

        StringBuffer sql = new StringBuffer();
        //sql.append("select count(*) from " + tableName + " where " + columnName + "<>\"\" group by " + columnName);
        sql.append("select count(distinct(" + columnName + ")) from " + tableName);

        List<Long> numList = getJdbcTemplate().queryForList(sql.toString(), Long.class);
        return numList;//如果>0说明存在有值行数据
    }

    /**
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> queryListForObject(String tableName, Data queryConditions) {
        String sqlAnd = " and ";
        String sqlOr = " or ";
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

StringUtils.isBlank(""); // true
StringUtils.isBlank(" "); // true
StringUtils.isBlank("     "); // true
StringUtils.isBlank("\t"); // true
StringUtils.isBlank("\r"); // true
StringUtils.isBlank("\n"); // true
StringUtils.isBlank(null); // true

StringUtils.isEmpty(""); // true
StringUtils.isEmpty(" "); // false
StringUtils.isEmpty("     "); // false
StringUtils.isEmpty("\t"); // false
StringUtils.isEmpty("\r"); // false
StringUtils.isEmpty("\n"); // false
StringUtils.isEmpty(null); // true

StringUtils.isWhitespace(""); // true
StringUtils.isWhitespace(" "); // true
StringUtils.isWhitespace("    "); // true
StringUtils.isWhitespace("\t"); // true
StringUtils.isWhitespace("\r"); // true
StringUtils.isWhitespace("\n"); // true
StringUtils.isWhitespace(null); // false
从上面的结果可以看出，
    blank：代表的是空串("")、空白符(空格""，" "，制表符"\t"，回车符"\r"，"\n"等)以及null值；
    empty：代表的是空串("")和null值，不包含空白符；
    whitespace：包含空串("")和空白符，不包含null值.
*/
            if (!StringUtils.isEmpty(entry.getValue().toString())) {
                if (!entry.getValue().toString().contains("||")) {
                    sql.append(" " + entry.getKey().toString() + " like '%" + entry.getValue().toString() + "%'");
                    sql.append(sqlAnd);
                } else {
                    String values[] = entry.getValue().toString().split("\\|\\|", -1);
                    sql.append("( ");
                    for (String value : values) {
                        sql.append(entry.getKey().toString() + " like '%" + value + "%'");
                        sql.append(sqlOr);
                    }
                    if (sql.indexOf(sqlOr) >= 0) {
                        sql.delete((sql.length() - sqlOr.length()), sql.length());
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
        } else if (dateStart.equals("") == true && dateEnd.equals("") == true) {
            if (sql.indexOf(sqlAnd) >= 0) {
                sql.delete((sql.length() - sqlAnd.length()), sql.length());
            }
        }

        //sql.append(" concat_ws(" + queryConditions.getKeys() + ")");

        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }

    /**
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> queryListForObject(String tableName,
                                         QueryCondition[] queryConditionsArr,
                                         long offset,
                                         long limit,
                                         Map<String, String> order) {
        StringBuffer sql = convertQueryConditionsToSQL(tableName, queryConditionsArr, false);
        StringBuffer sqlOrder = convertOrderToSQL(order);
        sql.append(sqlOrder);
        sql.append(" LIMIT " + String.valueOf(offset) + "," + String.valueOf(limit));
/*
Mysql limit offset示例
例1，假设数据库表student存在13条数据。

代码示例:
语句1：select * from student limit 9,4
语句2：slect * from student limit 4 offset 9
// 语句1和2均返回表student的第10、11、12、13行
// 语句2中的4表示返回4行，9表示从表的第十行开始
例2，通过limit和offset 或只通过limit可以实现分页功能。
假设 numberperpage 表示每页要显示的条数，pagenumber表示页码，
那么返回第pagenumber页，每页条数为numberperpage的sql语句：

代码示例:
语句3：select * from studnet limit (pagenumber-1)*numberperpage,numberperpage
语句4：select * from student limit numberperpage offset (pagenumber-1)*numberperpage
 */
        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }

    /**
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Data> queryListForAllObject(String tableName, QueryCondition[] queryConditionsArr) throws Exception {
        StringBuffer sql = convertQueryConditionsToSQL(tableName, queryConditionsArr, false);

        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }

    /**
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<String[]> queryListForAllObject(String tableName,
                                            QueryCondition[] queryConditionsArr,
                                            long offset,
                                            long limit,
                                            Map<String, String> order) throws Exception {
        StringBuffer sql = convertQueryConditionsToSQL(tableName, queryConditionsArr, false);

        List<String[]> dataArrList = getJdbcTemplate().query(sql.toString(), new DownloadDataMapper());
        return dataArrList;
    }


    /**
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Data> queryListForAllObject(String tableName,
                                            StringBuffer selectFieldSql,
                                            StringBuffer groupByFieldSql,
                                            StringBuffer orderSql,
                                            ComputeField[] computeFields,
                                            QueryCondition[] queryConditionsArr) throws Exception {

        StringBuffer sql = convertReportQueryDataToSQL(tableName, selectFieldSql, groupByFieldSql, computeFields, queryConditionsArr);
        sql.append(orderSql);

        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }

    /**
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Data> queryListForAllObject(String tableName) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName);
        return getJdbcTemplate().query(sql.toString(), new DataMapper());
    }

    /**
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Map<String, Object>> queryListForColumnAllValues(String tableName, String[] selectionCols) throws Exception {
        StringBuffer sql = convertSelectionsToSQL(tableName, selectionCols);

        List<Map<String, Object>> selectionColsList = getJdbcTemplate().queryForList(sql.toString());

        return selectionColsList;
    }

    /**
     * 查询并返回分组的List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Map<String, Object>> queryListForColumnAllValuesByGroup(String tableName, String[] selectionCols) throws Exception {
        StringBuffer sql = new StringBuffer();
        StringBuffer sqlSelections = convertSelectionsToSQL(tableName, selectionCols);
        StringBuffer sqlGroupBys = convertGroupByToSQL(selectionCols);
        sql.append(sqlSelections);
        sql.append(sqlGroupBys);

        List<Map<String, Object>> selectionColsList = getJdbcTemplate().queryForList(sql.toString());

        return selectionColsList;
    }

    /**
     * 查询并返回分组并分页的List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Map<String, Object>> queryListForColumnAllValuesByGroupWithPagination(String tableName,
                                                                                      String category,
                                                                                      String term,
                                                                                      long offset,
                                                                                      long limit,
                                                                                      Map<String, String> order) throws Exception {
/*
group by 与order by 一起使用是要遵守一定原则的：
1.order by 的列，必须是出现在group by 子句里的列
2.order by 要 放在 group by的 后面
*/
        StringBuffer sql = new StringBuffer();
        StringBuffer sqlSelections = convertSelectionsToSQL(tableName, new String[]{category});
        StringBuffer sqlWhere = convertWhereToSQL(new String[]{category}, new String[]{term});
        StringBuffer sqlGroupBys = convertGroupByToSQL(new String[]{category});
        StringBuffer sqlOrder = convertOrderToSQL(order);
        sql.append(sqlSelections);
        sql.append(sqlWhere);
        sql.append(sqlGroupBys);
        sql.append(sqlOrder);
        sql.append(" LIMIT " + String.valueOf(offset) + "," + String.valueOf(limit));

        List<Map<String, Object>> selectionColsList = getJdbcTemplate().queryForList(sql.toString());

        return selectionColsList;
    }

    /**
     * 拼接语句，往表里面插入数据
     *
     * @param tableName
     * @param data
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int saveData(String tableName, Data data) throws Exception {
        int re = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into " + tableName + " (");
            Map<String, String> map = data.getKeyValue();
            Set<String> set = map.keySet();
            for (String key : set) {
                sql.append(key + CommonDao.COMMA);
            }
            sql.deleteCharAt(sql.length() - CommonDao.COMMA.length());
            sql.append(" ) values ( ");
            for (String key : set) {
                sql.append("'" + map.get(key) + "',");
            }
            sql.deleteCharAt(sql.length() - CommonDao.COMMA.length());
            sql.append(" ) ");
            re = getJdbcTemplate().update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 查询某表的列名
     *
     * @param tableName
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-15 23:11:00
     */
    public List<Map<String, Object>> queryListForColumnName(String tableName) throws Exception {
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
     * 查询并返回List集合
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Map<String, Object>> queryListForValuesIfs(String tableName,
                                                           String[] selectionCols,
                                                           String criterionCol,
                                                           String criterionValue) throws Exception {
        StringBuffer sql = convertSelectionsToSQL(tableName, selectionCols);
        sql.append(" where ");
        sql.append(criterionCol + " = '" + criterionValue + "'");

        List<Map<String, Object>> selectionColsList = getJdbcTemplate().queryForList(sql.toString());

        return selectionColsList;
    }
    private String formatDate(String dateStr) {
        Date date = null;
        try {
            date = DataDao.SIMPLE_DATE_FORMAT_1.parse(dateStr);
            return DataDao.SIMPLE_DATE_FORMAT_1.format(date);
        } catch (ParseException e) {
            try {
                date = DataDao.SIMPLE_DATE_FORMAT_2.parse(dateStr);
                return DataDao.SIMPLE_DATE_FORMAT_1.format(date);
            } catch (ParseException e1) {
                try {
                    date = DataDao.SIMPLE_DATE_FORMAT_3.parse(dateStr);
                    return DataDao.SIMPLE_DATE_FORMAT_1.format(date);
                } catch (ParseException e2) {
                    int daysFrom1900 = Integer.parseInt(dateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.set(1900, 0, 1);
                    cal.add(Calendar.DAY_OF_MONTH, daysFrom1900);
                    return DataDao.SIMPLE_DATE_FORMAT_1.format(cal.getTime());
                } catch (NumberFormatException e4) {
                    return dateStr;
                }
            }
        }
    }
}

