package com.example.eurasia.dao;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.QueryCondition;
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

        StringBuffer strColsName = new StringBuffer();
        List<Map<String,Object>> colsNameList = this.queryListForColumnName(tableName);
        for(Map<String,Object> colsName: colsNameList) {
            strColsName.append(colsName.get("COLUMN_NAME").toString());
            strColsName.append(CommonDao.COMMA);
        }

        strColsName.deleteCharAt(strColsName.length() - CommonDao.COMMA.length());
        strColsName.replace(strColsName.indexOf(CommonDao.ID_COMMA),CommonDao.ID_COMMA.length(),"");//indexOf从0开始计算,没有查到指定的字符则该方法返回-1
        String[] name = strColsName.toString().split(CommonDao.COMMA,-1);
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
        StringBuffer sql =  new StringBuffer();
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
    public List<Data> queryListForObject(String tableName, QueryCondition[] queryConditionsArr, long offset, long limit, Map<String, String> order) {
        StringBuffer sql = convertQueryConditionsToSQL(tableName,queryConditionsArr,false);
        sql.append(" order by ");
        Set<Map.Entry<String, String>> set = order.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            sql.append(entry.getKey() + " " + entry.getValue() + CommonDao.COMMA);
        }
        sql.deleteCharAt(sql.length() - CommonDao.COMMA.length());
        sql.append(" LIMIT " + String.valueOf(offset) + "," + String.valueOf(limit));

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
    public List<Data> queryListForAllObject(String tableName, QueryCondition[] queryConditionsArr) throws Exception {
        StringBuffer sql = convertQueryConditionsToSQL(tableName,queryConditionsArr,false);

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

