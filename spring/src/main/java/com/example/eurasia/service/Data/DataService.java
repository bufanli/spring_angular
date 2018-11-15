package com.example.eurasia.service.Data;

import com.example.eurasia.dao.DataDao;
import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    // 属性注入
    // 加入DataDao作为成员变变量
    @Autowired
    private DataDao dataDao;
    // 注意这里要增加get和set方法
    public DataDao getDataDao() {
        return this.dataDao;
    }
    public void setDataDao(DataDao dataDao) {
        this.dataDao = dataDao;
    }

    public static final String TABLE_DATA = "eurasiaTable";
    public static final String TABLE_QUERY_CONDITION_DEFAULT_DISPLAY = "queryConditionDefaultDisplayTable";
    public static final String TABLE_QUERY_CONDITION_TYPE = "queryConditionTypeTable";
    public static final String EXPORT_EXCEL_SHEET_NAME = "统计表";
    public static final String BR = "<br/>";

    /**
     * 添加数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int addData(String tableName, Data data) throws Exception {
        return getDataDao().addData(tableName, data);
    }

    /**
     * 根据查询条件进行数据查询
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> searchData(String tableName, Data queryConditions) throws Exception {
        if (!queryConditions.isValuesAllNULL()) {
            return getDataDao().queryListForObject(tableName, queryConditions);
        } else {
            return this.searchAllData(tableName);
        }
    }

    /**
     * 根据查询条件进行数据查询
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> searchData(String tableName, QueryCondition[] queryConditionsArr) throws Exception {
        for (QueryCondition queryCondition : queryConditionsArr) {
            if (queryCondition.isValuesNotNULL()) {
                return getDataDao().queryListForObject(tableName, queryConditionsArr);
            }
        }

        return this.searchAllData(tableName);
    }

    /**
     * 查询所有数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Data> searchAllData(String tableName) throws Exception {
        return getDataDao().queryListForAllObject(tableName);
    }

    /**
     * 取得表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Map<String,String>> getHeaders(String tableName) throws Exception {
        List<Map<String,String>> headerList = new ArrayList<>();

        List<Map<String, Object>> colsNameList = getDataDao().queryListForColumnName(tableName);
        for(Map<String,Object> colsName: colsNameList) {
            Map<String,String> nameMap = new LinkedHashMap<>();
            nameMap.put(colsName.get("ORDINAL_POSITION").toString(),colsName.get("COLUMN_NAME").toString());
            headerList.add(nameMap);
        }

        return headerList;
    }

    /**
     * 取得表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-06 00:00:00
     */
    public List<String> getHeaderNames(String tableName) throws Exception {
        List<String> headerList = new ArrayList<>();

        List<Map<String, Object>> colsNameList = getDataDao().queryListForColumnName(tableName);
        for(Map<String,Object> colsName: colsNameList) {
            headerList.add(colsName.get("COLUMN_NAME").toString());
        }

        return headerList;
    }

    /**
     * 取得查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public QueryCondition[] getQueryConditions(String tableName) throws Exception {
        return getDataDao().queryListForQueryConditions(tableName);
    }

    /**
     * 根据表名称创建一张表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createTable(String tableName) throws Exception {
        try {
            return getDataDao().createTable(tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建数据库
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createDatabase(String databaseName) throws Exception {
        try {
            return getDataDao().createDatabase(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取默认搜索日期区间,数据库中最新的一个月。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-15 00:00:00
     */
    public String[] getTheLastMonth(String databaseName) throws Exception {

        return null;
    }

}
