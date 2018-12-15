package com.example.eurasia.dao;

import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.QueryCondition;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class DataDao extends CommonDao {

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
        //String[] columnsValuesArr = columnsValues.split(CommonDao.COMMA,-1);
        String[] columnsValuesArr = data.getValuesToArray();

        sql.append("insert into " + tableName + "(" + columnsNames + ") values (");
        for (int i=0; i<size; i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - CommonDao.COMMA.length());
        sql.append(")");
        int num = getJdbcTemplate().update(sql.toString(),(Object[])columnsValuesArr);
        return num;//大于0，插入成功。返回影响的行数。
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
                strCcolsName.append(CommonDao.COMMA);
            }
        }
        strCcolsName.deleteCharAt(strCcolsName.length() - CommonDao.COMMA.length());
        String[] name = strCcolsName.toString().split(CommonDao.COMMA,-1);

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
        sql.append("select * from " + tableName + " where ");

        for (QueryCondition queryCondition : queryConditionsArr) {
            String key = queryCondition.getKey();
            switch (queryCondition.getType()) {
                case QueryCondition.QUERY_CONDITION_TYPE_STRING:
                    if (queryCondition.isValuesNotNULL() == true) {
                        String value = queryCondition.getValue();
                        sql.append(key + " like '%" + value + "%'");
                        sql.append(sqlAnd);
                    } else {

                    }
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_DATE:
                    String dateArr[] = queryCondition.getQueryConditionToArr();
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
                case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
                case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                    String arr[] = queryCondition.getQueryConditionToArr();
                    String conditionStart = arr[0];
                    String conditionEnd = arr[1];
                    if (conditionStart.equals("") == true && conditionEnd.equals("") == false) {
                        conditionStart = "(select min(" + key + ")";
                        sql.append(" (" + key + " between " + conditionStart + " and '" + conditionEnd + "')");
                    } else if (conditionStart.equals("") == false && conditionEnd.equals("") == true) {
                        conditionEnd = "(select max(" + key + "))";
                        sql.append(" (" + key + " between '" + conditionStart + "' and " + conditionEnd + ")");
                    } else if (conditionStart.equals("") == false && conditionEnd.equals("") == false) {
                        sql.append(" (" + key + " between '" + conditionStart + "' and '" + conditionEnd + "')");
                    } else if (conditionStart.equals("") == true && conditionEnd.equals("") == true) {
                        if (sql.indexOf(sqlAnd) >= 0) {
                            sql.delete((sql.length() - sqlAnd.length()),sql.length());
                        }
                    }
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                    if (queryCondition.isValuesNotNULL() == false) {

                    } else {
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
                            sqlList.delete((sqlList.length() - sqlOr.length()),sqlList.length());
                        }
                        sql.append(sqlList);
                        sql.append(" )");
                        sql.append(sqlAnd);
                    }
                    break;
                default:
                    break;
            }
        }

        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
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

}
