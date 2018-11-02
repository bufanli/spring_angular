package com.example.eurasia.service.Data;

import com.example.eurasia.dao.DataDao;
import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public static final String TABLE_NAME = "eurasiaTable";
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
    public List<Map<String,Object>> getHeaders(String tableName) throws Exception {
        return getDataDao().queryListForColumnName(tableName);
    }

    /**
     * 取得查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public String getQueryConditions(String tableName) throws Exception {
        return null;//T.B.D
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
     * 创建数据库
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public Map<String, String> queryConditionsArrToMap(QueryCondition[] queryConditionsArr) throws Exception {
        Map<String, String> keyValue = new LinkedHashMap<>();
        for (int i=0; i<queryConditionsArr.length; i++) {
            keyValue.put(queryConditionsArr[i].getKey(), queryConditionsArr[i].getValue());
        }
        return keyValue;
    }

    /**
     * 测试dummy数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Data> geteTestData() {
        List<Data> dataList = new ArrayList<>();
        Map<String, String> keyValue = new LinkedHashMap<>();
        keyValue.put("结束日期","2018/10/27");
        keyValue.put("起始日期","2018/10/27");
        keyValue.put("商品名称","1");
        keyValue.put("海关编码","1");
        keyValue.put("企业名称","1");
        keyValue.put("收发货地","1");
        keyValue.put("贸易方式","1");
        keyValue.put("原产国（目的国）","1");
        keyValue.put("商品编码_8","1");
        keyValue.put("商品编码_2","1");
        keyValue.put("产品名称","1");
        keyValue.put("规格型号","1");
        dataList.add(new Data(keyValue));

        return dataList;
    }
}
