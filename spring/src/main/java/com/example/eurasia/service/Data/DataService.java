package com.example.eurasia.service.Data;

import com.example.eurasia.dao.DataDao;
import com.example.eurasia.entity.Data.*;
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
    public static final String TABLE_STATISTICS_SETTING_GROUP_BY = "statisticsSettingGroupByTable";
    public static final String TABLE_STATISTICS_SETTING_TYPE = "statisticsSettingTypeTable";
    public static final String TABLE_STATISTICS_SETTING_COMPUTE_BY = "statisticsSettingComputeByTable";

    public static final String BEAN_NAME_COLUMNS_DEFAULT_NAME = "columnDefaultName";
    public static final String BEAN_NAME_QUERY_CONDITION_TYPE_NAME = "queryConditionTypeName";
    public static final String BEAN_NAME_QUERY_CONDITION_TYPE_VALUE = "queryConditionTypeValue";
    public static final String STATISTICS_SETTING_GROUP_BY_COLUMN_NAME = "GroupByName";
    public static final String STATISTICS_SETTING_TYPE_COLUMN_NAME = "Type";
    public static final String STATISTICS_SETTING_COMPUTE_BY_COLUMN_NAME = "ComputeByName";

    public static final String STATISTICS_REPORT_PRODUCT_DATE = "日期";
    public static final String STATISTICS_REPORT_PRODUCT_DATE_YEAR = "年";
    public static final String STATISTICS_REPORT_PRODUCT_DATE_MONTH = "月";
    public static final String STATISTICS_REPORT_PRODUCT_DATE_QUARTER = "季度";

    public static final String EXPORT_EXCEL_SHEET_NAME = "统计表";
    public static final String STATISTICS_REPORT_NAME_EX = "汇总报表";
    public static final String BR = "<br/>";

    /**
     * 初始化数据表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public void dataServiceInit() throws Exception {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
            DataXMLReader dataXMLReader = null;
            Data dataValue = null;
            if (this.createTable(DataService.TABLE_DATA,DataService.BEAN_NAME_COLUMNS_DEFAULT_NAME) == true) {

            }
            if (this.createTable(DataService.TABLE_QUERY_CONDITION_TYPE,DataService.BEAN_NAME_QUERY_CONDITION_TYPE_NAME) == true) {
                dataXMLReader = (DataXMLReader) context.getBean(DataService.BEAN_NAME_QUERY_CONDITION_TYPE_VALUE);
                dataValue = new Data(dataXMLReader.getKeyValue());
                getDataDao().addData(DataService.TABLE_QUERY_CONDITION_TYPE,dataValue);
            }
            Map<String, String> groupByNameType = new LinkedHashMap<String, String>();
            groupByNameType.put(DataService.STATISTICS_SETTING_GROUP_BY_COLUMN_NAME,"VARCHAR(255)");
            if (this.createTable(DataService.TABLE_STATISTICS_SETTING_GROUP_BY,groupByNameType) == true) {
                String[] groupByArr = {"日期","进出口","申报单位名称","货主单位名称","经营单位名称","经营单位代码",
                                        "运输工具名称","提运单号","海关编码","附加码","商品名称", "部位","包装规格",
                                        "英文品名","品牌","加工厂号","加工企业名称","牛种","牛龄","级别",
                                        "饲养方式","申报要素","成交方式","监管方式","运输方式","目的地","包装种类",
                                        "主管关区","报关口岸","装货港","中转国","贸易国","企业性质","地址",
                                        "手机","电话","传真","Email","法人","联系人"};
                for (int i=0; i<groupByArr.length; i++) {
                    getDataDao().addData(DataService.TABLE_STATISTICS_SETTING_GROUP_BY,DataService.STATISTICS_SETTING_GROUP_BY_COLUMN_NAME,groupByArr[i]);
                }
            }
            Map<String, String> typeNameType = new LinkedHashMap<String, String>();
            typeNameType.put(DataService.STATISTICS_SETTING_TYPE_COLUMN_NAME,"VARCHAR(50)");
            if (this.createTable(DataService.TABLE_STATISTICS_SETTING_TYPE,typeNameType) == true) {
                String[] typeArr = {"柱状图","饼状图","折线图"};
                for (int i=0; i<typeArr.length; i++) {
                    getDataDao().addData(DataService.TABLE_STATISTICS_SETTING_TYPE,DataService.STATISTICS_SETTING_TYPE_COLUMN_NAME,typeArr[i]);
                }
            }
            Map<String, String> computeByNameType = new LinkedHashMap<String, String>();
            computeByNameType.put(DataService.STATISTICS_SETTING_COMPUTE_BY_COLUMN_NAME,"VARCHAR(50)");
            if (this.createTable(DataService.TABLE_STATISTICS_SETTING_COMPUTE_BY,computeByNameType) == true) {
                String[] computeByArr = {"法定重量","申报数量","件数"};
                for (int i=0; i<computeByArr.length; i++) {
                    getDataDao().addData(DataService.TABLE_STATISTICS_SETTING_COMPUTE_BY,DataService.STATISTICS_SETTING_COMPUTE_BY_COLUMN_NAME,computeByArr[i]);
                }
            }

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

        int addNum = getDataDao().addData(tableName, data);

        return addNum;
    }

    /**
     * 删除相同数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int deleteSameData(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return -1;
        }

        int deleteNum = getDataDao().deleteSameData(tableName);//失败时，返回-1

        return deleteNum;
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
    public SearchedData searchData(String tableName, QueryCondition[] queryConditionsArr, long offset, long limit, Map<String, String> order) throws Exception {
        if (StringUtils.isEmpty(tableName) || queryConditionsArr == null) {
            return null;
        }

        long count = queryTableRows(tableName,queryConditionsArr);
        List<Data> dataList = getDataDao().queryListForObject(tableName,queryConditionsArr,offset,limit,order);

        return new SearchedData(count,dataList);
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
     * 根据查询条件进行数据查询
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> searchDataForDownload(String tableName, QueryCondition[] queryConditionsArr) throws Exception {
        if (StringUtils.isEmpty(tableName) || queryConditionsArr == null) {
            return null;
        }

        return getDataDao().queryListForAllObject(tableName,queryConditionsArr);
    }

    /**
     * 根据查询条件进行数据查询
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> searchDataForStatisticReport(String tableName, String groupByField, ComputeField[] computeFields, QueryCondition[] queryConditionsArr) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(groupByField) || computeFields == null || queryConditionsArr == null) {
            return null;
        }
        StringBuffer selectFieldSql = new StringBuffer();
        StringBuffer groupByFieldSql = new StringBuffer();
        Map<String, String> order = new LinkedHashMap<>();
        StringBuffer orderSql = new StringBuffer();
        /*
as不是给表里的字段取别名，而是给查询的结果字段取别名。
其目的是让查询的结果展现更符合人们观看习惯,在多张表查询的时候可以直接的区别多张表的同名的字段。
 */
        if (groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_YEAR)) {
            //报告类型是周期(年)的情况
            String yearFormat = "date_format(" + DataService.STATISTICS_REPORT_PRODUCT_DATE + ",'%Y')";
            selectFieldSql.append(yearFormat + " as " + DataService.STATISTICS_REPORT_PRODUCT_DATE);
            groupByFieldSql.append(yearFormat);

            order.put(yearFormat,"desc");
            orderSql.append(getDataDao().convertOrderToSQL(order));
        } else if (groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_MONTH)) {
            //报告类型是周期(月)的情况
            String monthFormat = "date_format(" + DataService.STATISTICS_REPORT_PRODUCT_DATE + ",'%Y-%m')";
            selectFieldSql.append(monthFormat + " as " + DataService.STATISTICS_REPORT_PRODUCT_DATE);
            groupByFieldSql.append(monthFormat);

            order.put(monthFormat,"desc");
            orderSql.append(getDataDao().convertOrderToSQL(order));
        } else if (groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_QUARTER)) {
            //报告类型是周期(季度)的情况
            String quarterFormat = "concat(date_format(" + DataService.STATISTICS_REPORT_PRODUCT_DATE + ", '%Y')," +
                    "FLOOR((date_format(" + DataService.STATISTICS_REPORT_PRODUCT_DATE + ", '%m')+2)/3))";
            selectFieldSql.append(quarterFormat + " as " + DataService.STATISTICS_REPORT_PRODUCT_DATE);
            groupByFieldSql.append(quarterFormat);

            order.put(quarterFormat,"desc");
            orderSql.append(getDataDao().convertOrderToSQL(order));
        } else {
            selectFieldSql.append(groupByField);
            groupByFieldSql.append(groupByField);
        }
        return getDataDao().queryListForAllObject(tableName,selectFieldSql,groupByFieldSql,orderSql,computeFields,queryConditionsArr);
    }

    /**
     * 取得统计数据用的GroupBY，Type，ComputeBy等
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-03-10 00:00:00
     */
    public List<Map<String, Object>> getStatisticsSetting(String tableName, String[] columnNames) throws Exception {
        if (StringUtils.isEmpty(tableName) || columnNames == null || columnNames.length == 0) {
            return null;
        }

        return getDataDao().queryListForColumns(tableName,columnNames);
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

        return getDataDao().queryListForAllObject(DataService.TABLE_QUERY_CONDITION_TYPE);
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
     * 根据表名称创建一张表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createTable(String tableName, Map<String, String> columnNameType) throws Exception {
        if (StringUtils.isEmpty(tableName) || columnNameType == null || columnNameType.isEmpty()) {
            return false;
        }

        try {
            return getDataDao().createTable(tableName,columnNameType);
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
     * 查询表的记录数
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public long queryTableRows(String tableName) throws Exception {
        return getDataDao().queryTableRows(tableName).longValue();
    }

    /**
     * query table row with query conditions
     * @param tableName
     * @return
     * @throws Exception
     */
    public long queryTableRows(String tableName,
                               QueryCondition[] queryConditions) throws Exception {
        return getDataDao().queryTableRows(tableName,queryConditions).longValue();
    }


    /**
     * 取得指定列的所有的值
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Map<String, Object>> getColumnAllValues(String tableName,String[] columnNames) throws Exception {

        return getDataDao().queryListForColumnValues(tableName,columnNames);
    }
}
