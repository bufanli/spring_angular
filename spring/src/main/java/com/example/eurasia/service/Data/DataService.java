package com.example.eurasia.service.Data;

import com.example.eurasia.dao.DataDao;
import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.DataXMLReader;
import com.example.eurasia.entity.Data.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public static final String TABLE_QUERY_CONDITION_TYPE = "queryConditionTypeTable";

    public static final String BEAN_NAME_COLUMNS_DEFAULT_NAME = "columnDefaultName";
    public static final String BEAN_NAME_QUERY_CONDITION_TYPE_NAME = "queryConditionTypeName";
    public static final String BEAN_NAME_QUERY_CONDITION_TYPE_VALUE = "queryConditionTypeValue";

    public static final String EXPORT_EXCEL_SHEET_NAME = "统计表";
    public static final String BR = "<br/>";

    /**
     * 添加数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public void dataServiceInit() throws Exception {
        try {
            this.createTable(DataService.TABLE_DATA,BEAN_NAME_COLUMNS_DEFAULT_NAME);
            this.createTable(DataService.TABLE_QUERY_CONDITION_TYPE,BEAN_NAME_QUERY_CONDITION_TYPE_NAME);

            ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
            DataXMLReader dataXMLReader = (DataXMLReader) context.getBean(BEAN_NAME_QUERY_CONDITION_TYPE_VALUE);
            Data queryConditionTypeValue = new Data(dataXMLReader.getKeyValue());
            getDataDao().addData(DataService.TABLE_QUERY_CONDITION_TYPE,queryConditionTypeValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (StringUtils.isEmpty(tableName) || data == null) {
            return -1;
        }

        int addNum = 0;
        int deleteNum = 0;

        addNum = getDataDao().addData(tableName, data);
        if (addNum > 0) {
            /* T.B.D 一时回避
            deleteNum = getDataDao().deleteSameData(tableName);
            */
        }

        return (deleteNum != -1) ? addNum : 0;
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
        if (StringUtils.isEmpty(tableName) || queryConditions == null) {
            return null;
        }

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
        if (StringUtils.isEmpty(tableName) || queryConditionsArr == null) {
            return null;
        }

        return getDataDao().queryListForObject(tableName, queryConditionsArr);
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
        if (StringUtils.isEmpty(tableName)) {
            return null;
        }

        return getDataDao().queryListForAllObject(tableName);
    }

    /**
     * 取得指定表的所有表头[COLUMN_NAME,名字]
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Map<String,String>> getAllHeaders() throws Exception {
        List<Map<String,String>> headerList = new ArrayList<>();

        List<Map<String, Object>> colsNameList = getDataDao().queryListForColumnName(DataService.TABLE_DATA);
        for(Map<String,Object> colsName: colsNameList) {
            Map<String,String> nameMap = new LinkedHashMap<String,String>();
            nameMap.put(colsName.get("ORDINAL_POSITION").toString(),colsName.get("COLUMN_NAME").toString());
            headerList.add(nameMap);
        }

        return headerList;
    }

    /**
     * 取得指定表的所有表头[名字]
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-06 00:00:00
     */
    public List<String> getAllHeaderNames(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return null;
        }

        List<String> headerList = new ArrayList<>();

        List<Map<String, Object>> colsNameList = getDataDao().queryListForColumnName(tableName);
        for (Map<String,Object> colsName: colsNameList) {
            headerList.add(colsName.get("COLUMN_NAME").toString());
        }

        return headerList;
    }

    /**
     * 取得所有的查询条件(key和type)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> getAllQueryConditions() throws Exception {
        List<Data> allQueryConditionsList = getDataDao().queryListForAllObject(DataService.TABLE_QUERY_CONDITION_TYPE);
        if (allQueryConditionsList.size() != 1) {
            return null;
        }

        return allQueryConditionsList;
    }

    /**
     * 根据表名称创建一张表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createTable(String tableName, String beanName) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(beanName)) {
            return false;
        }

        try {
            return getDataDao().createTable(tableName,beanName);
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
        if (StringUtils.isEmpty(databaseName)) {
            return false;
        }

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
        if (StringUtils.isEmpty(databaseName)) {
            return null;
        }

        return null;
    }

}
