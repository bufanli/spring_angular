package com.example.eurasia.service.Data;

import com.example.eurasia.dao.CommonDao;
import com.example.eurasia.dao.DataDao;
import com.example.eurasia.entity.Data.ComputeField;
import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.DataXMLReader;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public static final String TABLE_DATA = "eurasiaTable";
    public static final String TABLE_DATA_TEMP = "eurasiaTempTable";
    public static final String TABLE_QUERY_CONDITION_TYPE = "queryConditionTypeTable";
    public static final String TABLE_STATISTICS_SETTING_GROUP_BY = "statisticsSettingGroupByTable";
    public static final String TABLE_STATISTICS_SETTING_TYPE = "statisticsSettingTypeTable";
    public static final String TABLE_STATISTICS_SETTING_COMPUTE_BY = "statisticsSettingComputeByTable";
    public static final String TABLE_COLUMNS_DICTIONARY = "columnsDictionaryTable";
    public static final String TABLE_COLUMNS_FOR_SAME_DATA = "columnsForSameDataTable";
    public static final String COLUMN_NAME_FOR_SAME_DATA= "columnName";
    public static final String TABLE_DATA_DICTIONARY_SUMMARY = "dataDictionarySummaryTable";

    public static final String BEAN_NAME_COLUMNS_DEFAULT_NAME = "columnDefaultName";
    public static final String BEAN_NAME_QUERY_CONDITION_TYPE_NAME = "queryConditionTypeName";
    public static final String BEAN_NAME_QUERY_CONDITION_TYPE_VALUE = "queryConditionTypeValue";
    public static final String BEAN_NAME_COLUMNS_DICTIONARY_NAME = "columnsDictionaryName";

    public static final String STATISTICS_SETTING_GROUP_BY_COLUMN_NAME = "GroupByName";
    public static final String STATISTICS_SETTING_TYPE_COLUMN_NAME = "Type";
    public static final String STATISTICS_SETTING_COMPUTE_BY_COLUMN_NAME = "ComputeByName";

    public static final String COLUMNS_DICTIONARY_SYNONYM = "synonym";
    public static final String COLUMNS_DICTIONARY_COLUMN_NAME = "columnName";
    public static final String COLUMNS_FOR_SAME_DATA_COLUMN_NAME = "columnName";
    public static final String DATA_DICTIONARY_NAME = "dictionaryName";

    public static final String STATISTICS_REPORT_PRODUCT_DATE = "日期";
    public static final String STATISTICS_REPORT_PRODUCT_DATE_YEAR = "年";
    public static final String STATISTICS_REPORT_PRODUCT_DATE_MONTH = "月";
    public static final String STATISTICS_REPORT_PRODUCT_DATE_QUARTER = "季度";

    public static final String EXCEL_EXPORT_SHEET_COVER = "封面";
    public static final String EXCEL_EXPORT_SHEET_COVER_TITLE_EXTEND = "报告";
    public static final int EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_NUM = 3;
    public static final String[] EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_NAME = {"报告日期","Copyright","电话"};
    public static String[] EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_VALUE = {"","大连伟亚信息","0411-39337158"};
    public static final String EXCEL_EXPORT_SHEET_CONTENTS = "目录";
    public static final String EXCEL_EXPORT_SHEET_CONTENTS_EXTEND = "汇总";
    public static final String EXCEL_EXPORT_SHEET_STATISTICS_TEMPLATE = "汇总模版";
    public static final String EXCEL_EXPORT_TYPE_DETAIL = "明细表";

    public static final String DOWNLOAD_EXCEL_SHEET_NAME = "统计表";
    public static final String STATISTICS_REPORT_NAME_EX = "汇总报表";
    public static final String BR = "<br/>";

    public static final int DOWNLOAD_RECODE_STEPS = 10000;
    public static final int ROW_ACCESS_WINDOW_SIZE = 10000;

    public static final String IMPORT_EXCEL_SUCCESS_MESSAGE = "导入成功";
    public static final String IMPORT_EXCEL_FAILED_MESSAGE = "导入失败";
    public static final String IMPORT_EXCEL_DATA_WRONG_MESSAGE = "导入数据问题";

    public static final int UPLOAD_FILE_MAX_SIZE = 20;

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
            if (this.createTable(DataService.TABLE_DATA,DataService.BEAN_NAME_COLUMNS_DEFAULT_NAME) == true) {

            }
            if (this.createTable(DataService.TABLE_QUERY_CONDITION_TYPE,DataService.BEAN_NAME_QUERY_CONDITION_TYPE_NAME) == true) {
                dataXMLReader = (DataXMLReader) context.getBean(DataService.BEAN_NAME_QUERY_CONDITION_TYPE_VALUE);
                Data queryConditionTypeDataValue = new Data(dataXMLReader.getKeyValue());
                getDataDao().addData(DataService.TABLE_QUERY_CONDITION_TYPE,queryConditionTypeDataValue);
            }
            Map<String, String> groupByNameType = new LinkedHashMap<String, String>();
            groupByNameType.put(DataService.STATISTICS_SETTING_GROUP_BY_COLUMN_NAME,"VARCHAR(255)");
            if (this.createTable(DataService.TABLE_STATISTICS_SETTING_GROUP_BY,groupByNameType) == true) {
                String[] groupByArr = {"日期","进出口","申报单位名称","货主单位名称","经营单位名称","申报单位代码","货主单位代码","经营单位代码",
                                        "企业性质","运输工具名称","提运单号","海关编码","附加码","商品名称","制作或保存方法","加工方法",
                                        "部位","包装规格","英文品名","品牌","加工厂号","加工企业名称","牛种","牛龄","级别",
                                        "饲养方式","签约日期","申报要素","成交方式","监管方式","运输方式","目的地","包装种类",
                                        "主管关区","报关口岸","装货港","中转国","贸易国","企业性质","地址",
                                        "手机","电话","传真","Email","法人","联系人","城市","省份","区域","大洲"};
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
            computeByNameType.put(DataService.STATISTICS_SETTING_COMPUTE_BY_COLUMN_NAME,"VARCHAR(255)");
            if (this.createTable(DataService.TABLE_STATISTICS_SETTING_COMPUTE_BY,computeByNameType) == true) {
                String[] computeByArr = {"美元总价","法定重量","申报总价","申报数量","件数"};
                for (int i=0; i<computeByArr.length; i++) {
                    getDataDao().addData(DataService.TABLE_STATISTICS_SETTING_COMPUTE_BY,DataService.STATISTICS_SETTING_COMPUTE_BY_COLUMN_NAME,computeByArr[i]);
                }
            }
            if (this.createTable(DataService.TABLE_COLUMNS_DICTIONARY,DataService.BEAN_NAME_COLUMNS_DICTIONARY_NAME) == true) {
                String[] synonymArr = {"进口","商品编码","商品编号","商品编码2","产品名称","牛肉部位","贸易方式",
                        "申报单位","货主单位","经营单位","企业代码","进口关区","原产国","装/卸货港",
                        "规格型号","电子邮件","目的地（原产地）"};
                String[] columnNameArr = {"进出口","海关编码","海关编码","附加码","商品名称","部位","监管方式",
                        "申报单位名称","货主单位名称","经营单位名称","经营单位代码","报关口岸","贸易国","装货港",
                        "申报要素","Email","目的地"};
                for (int i=0; i<synonymArr.length; i++) {
                    Map<String, String> columnsDicMap = new LinkedHashMap<String, String>();
                    columnsDicMap.put(DataService.COLUMNS_DICTIONARY_SYNONYM,synonymArr[i]);
                    columnsDicMap.put(DataService.COLUMNS_DICTIONARY_COLUMN_NAME,columnNameArr[i]);
                    Data columnsDicData = new Data(columnsDicMap);
                    getDataDao().addData(DataService.TABLE_COLUMNS_DICTIONARY,columnsDicData);
                }
            }
            Map<String, String> columnsForSameData = new LinkedHashMap<String, String>();
            columnsForSameData.put(DataService.COLUMNS_FOR_SAME_DATA_COLUMN_NAME,"VARCHAR(255)");
            if (this.createTable(DataService.TABLE_COLUMNS_FOR_SAME_DATA,columnsForSameData) == true) {
                String[] columnsForSameDataArr = {"日期","进出口","申报单位名称","货主单位名称","经营单位名称","经营单位代码","海关编码",
                        "附加码","商品名称","申报要素","成交方式","申报单价","申报总价","申报币制","美元总价","申报数量","申报数量单位",
                        "法定重量","毛重","净重","监管方式","运输方式","目的地","包装种类","主管关区","报关口岸","装货港","贸易国"};

                for (int i=0; i<columnsForSameDataArr.length; i++) {
                    getDataDao().addData(DataService.TABLE_COLUMNS_FOR_SAME_DATA,DataService.COLUMNS_FOR_SAME_DATA_COLUMN_NAME,columnsForSameDataArr[i]);
                }
            }
            Map<String, String> dataDictionarySummary = new LinkedHashMap<String, String>();
            dataDictionarySummary.put(DataService.DATA_DICTIONARY_NAME,"VARCHAR(255)");
            if (this.createTable(DataService.TABLE_DATA_DICTIONARY_SUMMARY,dataDictionarySummary) == true) {

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
     * 批量添加数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-15 00:00:00
     */
    public int[] batchAddData(String tableName, List<Data> dataList, Data dataType) throws Exception {
        if (StringUtils.isEmpty(tableName) || dataList == null) {
            return null;
        }

        int[] numArr = getDataDao().batchAddData(tableName, dataList, dataType);

        return numArr;
    }

    /**
     * 删除表里的相同数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-09-09 00:00:00
     */
    public int deleteSameDataByDistinct(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return -1;
        }

        getDataDao().deleteSameDataByDistinct(
                tableName,
                this.getColumnsForSameData(false),
                this.getColumnsForSameData(true),
                true);
        return 0;
    }

    /**
     * 取得判断数据表(DataService.TABLE_DATA)相同数据的列名
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-12 00:00:00
     */
    private String getColumnsForSameData(boolean isCustomize) throws Exception {

        List<Map<String, Object>> colsNameList = null;
        if (isCustomize) {
            //自定义判断数据表相同数据的列名
            colsNameList = getDataDao().queryListForColumnAllValues(
                    DataService.TABLE_COLUMNS_FOR_SAME_DATA,
                    new String[]{DataService.COLUMN_NAME_FOR_SAME_DATA});
        } else {
            //全部列名
            colsNameList = getDataDao().queryListForColumnName(DataService.TABLE_DATA);
        }
        String key = null;
        if(isCustomize){
           key = "columnName";
        }else{
            key = "COLUMN_NAME";
        }
        StringBuffer strColsName = new StringBuffer();
        for (Map<String, Object> colsName : colsNameList) {
            strColsName.append(colsName.get(key).toString());
            strColsName.append(CommonDao.COMMA);
        }

        strColsName.deleteCharAt(strColsName.length() - 1);//","的长度为1，所以删除最后一个","即删除下标为strColsName.length()-1字符
        if(isCustomize == false) {
            strColsName.replace(strColsName.indexOf(CommonDao.ID_COMMA), CommonDao.ID_COMMA.length(), "");//indexOf从0开始计算,没有查到指定的字符则该方法返回-1
        }
        return strColsName.toString();
    }

    /**
     * 删除表里的全部数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-03 00:00:00
     */
    public int deleteAllData(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return -1;
        }

        int deleteNum = getDataDao().deleteAllData(tableName);//失败时，返回-1

        return deleteNum;
    }

    /**
     * 取得数据对应关系的词典名，并取得其列名
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-30 00:00:00
     */
    public Map<String, Set<String>> getDataDictionariesColumnNamesMap() throws Exception {

        // 取得数据对应关系的词典名
        Map<String, Set<String>> dataDicColNamesMap = new HashMap<>();
        List<Map<String, Object>> dataDictionariesList = this.getColumnsValues(DataService.TABLE_DATA_DICTIONARY_SUMMARY,
                new String[]{DataService.DATA_DICTIONARY_NAME});
        if (dataDictionariesList != null && dataDictionariesList.size() != 0) {//有数据对应关系的词典
            for (Map<String, Object> map : dataDictionariesList) {
                for (Map.Entry<String, Object> m : map.entrySet()) {
                    //System.out.print(m.getKey());
                    //System.out.println(m.getValue());
                    String dictionaryName = (String) m.getValue();

                    // 逐个取得数据对应关系的词典中的列名(在使用的地方要注意其是否为空和为0)
                    Set<String> dataDicHeadersSet = this.getAllColumnNamesWithoutID(dictionaryName);// 取得所有列名(不包括id)
                    dataDicColNamesMap.put(dictionaryName, dataDicHeadersSet);
                }
            }
        } else {//没有数据对应关系的词典
            Slf4jLogUtil.get().info("取得数据对应关系的词典列表为空");
        }
        return dataDicColNamesMap;
    }

    /**
     * 根据数据对应关系的词典表进行数据的扩充
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-30 00:00:00
     */
    public Map<String, Integer> reMakeUpDataByDataDictionaries(String tempTableName, Set<String> colsNameSet, Map<String, Set<String>> dataDicColNamesMap) throws Exception {
        Map<String, Integer> ret = new HashMap<>();//key:词典名，value:影响的条目数
        for (Map.Entry<String, Set<String>> m : dataDicColNamesMap.entrySet()) {
            //System.out.print(m.getKey());
            //System.out.println(m.getValue());
            String dataDictionaryName = m.getKey();
            Set<String> dataDicHeadersSet = m.getValue();
            if (dataDicHeadersSet == null || dataDicHeadersSet.isEmpty()) {
                Slf4jLogUtil.get().error("取得数据对应关系的词典" + dataDictionaryName + "时,从数据库取得表头信息为空");
            } else {
                int num = getDataDao().reMakeUpDataByDataDictionary(tempTableName, colsNameSet, dataDictionaryName, dataDicHeadersSet);
                ret.put(dataDictionaryName, num);
            }
        }
        return ret;
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
        return getDataDao().copyTableData(src, des);
    }

    /**
     * 保存数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public int saveDataToSQL(String tableName, List<Data> dataList) throws Exception {
        int deleteNum = 0;
        if (dataList.size() > 0) {
            // 取得所有的查询条件(Data的Map-key是查询条件的key，Data的Map-value是查询条件的type)
            List<Data> allQueryConditionsList = this.getAllQueryConditions();
            if (allQueryConditionsList == null) {
                return -1;
            }
            if (allQueryConditionsList.size() != 1) {
                return -1;
            }

            int[] numArr = this.batchAddData(tableName, dataList, allQueryConditionsList.get(0));
            if (numArr == null) {
                return -1;
            }
 //do deleteSameData after uploadFiles
 //           if (numArr.length > 0) {
 //               deleteNum = this.deleteSameData(tableName);
 //           }
            int num = numArr.length - deleteNum;//T.B.D
            return (num < 0) ? -1 : num;
        } else {
            return 0;
        }
    }

    /**
     * 添加新字段(For同义词)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-07 00:00:00
     */
    public int addColumnToSQL(List<String> addColList) throws Exception {
        if (addColList == null) {
            return -1;
        }

        int addColNum = addColList.size();
        for (String columnName : addColList) {
            int intoDataNum = getDataDao().addColumn(
                    DataService.TABLE_DATA, columnName, "VARCHAR(255)");
            int intoQueryConTypeNum = getDataDao().addColumn(
                    DataService.TABLE_QUERY_CONDITION_TYPE, columnName, "VARCHAR(20)");
            int intoStaticSettingGroupByNum = getDataDao().addData(
                    DataService.TABLE_STATISTICS_SETTING_GROUP_BY,
                    DataService.STATISTICS_SETTING_GROUP_BY_COLUMN_NAME,
                    columnName);
            Slf4jLogUtil.get().info("添加新字段 {} ！",columnName);
        }

        return addColNum;
    }

    /**
     * 删除字段(For同义词)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-07 00:00:00
     */
    public int deleteColumnFromSQL(Set<String> colsNameSet) throws Exception {
        if (colsNameSet == null) {
            return -1;
        }

        int delColNum = 0;
        for (String columnName:colsNameSet) {
            if (this.deleteColumnFromSQL(columnName)) {
                delColNum++;
            } else {
                continue;
            }
        }
        Slf4jLogUtil.get().info("删除共{}条字段！",delColNum);
        return delColNum;
    }

    /**
     * 删除字段(For同义词)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-07 00:00:00
     */
    public boolean deleteColumnFromSQL(String columnName) throws Exception {

        Long counts = this.getColumnValueCounts(DataService.TABLE_DATA, columnName);
        if (counts.longValue() > 0) {
            return false;
        } else {
            int delDataNum = getDataDao().deleteColumn(DataService.TABLE_DATA, columnName);
            int delQueryConTypeNum = getDataDao().deleteColumn(DataService.TABLE_QUERY_CONDITION_TYPE, columnName);

            Map<String, String> keyValue = new LinkedHashMap<>();
            keyValue.put(DataService.STATISTICS_SETTING_GROUP_BY_COLUMN_NAME, columnName);
            Data deleteData = new Data(keyValue);
            List<Data> deleteDataList = new ArrayList<>();
            deleteDataList.add(deleteData);
            int delStaticSettingGroupByNum = this.deleteDataFromSQL(DataService.TABLE_STATISTICS_SETTING_GROUP_BY, deleteDataList);

            Slf4jLogUtil.get().info("删除字段 {} ！",columnName);
            return true;
        }
    }

    /**
     * 删除数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-07 00:00:00
     */
    public int deleteDataFromSQL(String tableName, List<Data> dataList) throws Exception {

        int delDataNum = 0;
        for (Data data : dataList) {
            delDataNum += getDataDao().deleteData(tableName, data);
        }
        return delDataNum;
    }

    /**
     * 判断数据字段名或同义词，是否存在(For同义词)
     * 1.如果查询的字段有空格,则将空格去掉。
     * 2.如果在同义词表里，则替换成数据表中的字段名。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<String> isTitleExist(List<String> titleList) throws Exception {

        boolean isInTableData;
        boolean isInTableColsDicData;

        List<String> titleIsNotExistList = new ArrayList<>();

        // 取得数据表的所有列名[名字],不包括id
        Set<String> colsNameSet = this.getAllColumnNamesWithoutID(DataService.TABLE_DATA);

        // 取得数据字典表指定列的所有值
        List<Map<String, Object>> colValuesListMap = this.getColumnAllValuesByGroup(DataService.TABLE_COLUMNS_DICTIONARY,
                new String[]{DataService.COLUMNS_DICTIONARY_SYNONYM, DataService.COLUMNS_DICTIONARY_COLUMN_NAME});

        for (int i=0; i<titleList.size(); i++) {
            isInTableData = false;
            isInTableColsDicData = false;

            String title = titleList.get(i);
            if (!title.contains(" ")) {

            } else {
                String newTitle = org.apache.commons.lang3.StringUtils.deleteWhitespace(title);
                titleList.set(i, newTitle);
            }

            title = titleList.get(i);
            if (colsNameSet.contains(title)) {//在数据表中找到该字段
                isInTableData = true;
            }

            if (isInTableData == false) {
                //在数据表中没找到该字段
                for (Map<String, Object> map : colValuesListMap) {//在数据字典表中找该字段
                    String synonymValue = (String) map.get(DataService.COLUMNS_DICTIONARY_SYNONYM);
                    if (synonymValue.equals(title)) {//在数据字典表的同义词里找到该字段
                        isInTableColsDicData = true;
                        String colNameValue = (String) map.get(DataService.COLUMNS_DICTIONARY_COLUMN_NAME);
                        //替换同义词
                        titleList.set(i, colNameValue);
                        break;
                    }
                }

                if (isInTableColsDicData == false) {
                    //不存在的字段
                    titleIsNotExistList.add(title);
                } else {
                    //存在的字段
                }

            } else {
                //在数据表中找到该字段
            }

        }

        return titleIsNotExistList;
    }

    /**
     * 判断数据字段名或同义词，是否存在(For数据对应关系的词典)
     * 1.如果查询的字段有空格,则将空格去掉。
     * 2.如果在同义词表里，则替换成数据表中的字段名。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<String> isTitleExist(String[] titleArr) throws Exception {

        boolean isInTableData;
        boolean isInTableColsDicData;

        List<String> titleIsNotExistList = new ArrayList<>();

        // 取得数据表的所有列名[名字],不包括id
        Set<String> colsNameSet = this.getAllColumnNamesWithoutID(DataService.TABLE_DATA);

        // 取得数据字典表指定列的所有值
        List<Map<String, Object>> colValuesListMap = this.getColumnAllValuesByGroup(DataService.TABLE_COLUMNS_DICTIONARY,
                new String[]{DataService.COLUMNS_DICTIONARY_SYNONYM, DataService.COLUMNS_DICTIONARY_COLUMN_NAME});

        for (int i=0; i<titleArr.length; i++) {
            isInTableData = false;
            isInTableColsDicData = false;

            String title = titleArr[i];
            if (!title.contains(" ")) {

            } else {
                String newTitle = org.apache.commons.lang3.StringUtils.deleteWhitespace(title);
                titleArr[i] = newTitle;
            }

            title = titleArr[i];
            if (colsNameSet.contains(title)) {//在数据表中找到该字段
                isInTableData = true;
            }

            if (isInTableData == false) {
                //在数据表中没找到该字段
                for (Map<String, Object> map : colValuesListMap) {//在数据字典表中找该字段
                    String synonymValue = (String) map.get(DataService.COLUMNS_DICTIONARY_SYNONYM);
                    if (synonymValue.equals(title)) {//在数据字典表的同义词里找到该字段
                        isInTableColsDicData = true;
                        String colNameValue = (String) map.get(DataService.COLUMNS_DICTIONARY_COLUMN_NAME);
                        //替换同义词
                        titleArr[i] = colNameValue;
                        break;
                    }
                }

                if (isInTableColsDicData == false) {
                    //不存在的字段
                    titleIsNotExistList.add(title);
                } else {
                    //存在的字段
                }

            } else {
                //在数据表中找到该字段
            }

        }

        return titleIsNotExistList;
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
    public List<Data> searchData(String tableName,
                                 QueryCondition[] queryConditionsArr,
                                 long offset,
                                 long limit,
                                 Map<String, String> order) throws Exception {
        if (StringUtils.isEmpty(tableName) || queryConditionsArr == null) {
            return null;
        }

        return getDataDao().queryListForObject(tableName,queryConditionsArr,offset,limit,order);
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
    public List<String[]> searchDataForDownload(String tableName,
                                            QueryCondition[] queryConditionsArr,
                                            long offset,
                                            long limit,
                                            Map<String, String> order) throws Exception {
        if (StringUtils.isEmpty(tableName) || queryConditionsArr == null) {
            return null;
        }

        return getDataDao().queryListForAllObject(tableName,queryConditionsArr,offset,limit,order);
    }

    /**
     * 数据查询
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-19 00:00:00
     */
    public List<String[]> searchDataForDownload(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return null;
        }

        return getDataDao().queryListStringForAllObject(tableName);
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
        StringBuffer orderSql = new StringBuffer();//T.B.D
/*
as不是给表里的字段取别名，而是给查询的结果字段取别名。
其目的是让查询的结果展现更符合人们观看习惯,在多张表查询的时候可以直接的区别多张表的同名的字段。
*/
        if (groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_YEAR)) {
            //报告类型是周期(年)的情况
            String yearFormat = "date_format(" + DataService.STATISTICS_REPORT_PRODUCT_DATE + ",'%Y')";
            selectFieldSql.append(yearFormat + " as " + DataService.STATISTICS_REPORT_PRODUCT_DATE);
            groupByFieldSql.append(yearFormat);

            order.put(yearFormat,"asc");
            orderSql.append(getDataDao().convertOrderToSQL(order));
        } else if (groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_MONTH)) {
            //报告类型是周期(月)的情况
            String monthFormat = "date_format(" + DataService.STATISTICS_REPORT_PRODUCT_DATE + ",'%Y-%m')";
            selectFieldSql.append(monthFormat + " as " + DataService.STATISTICS_REPORT_PRODUCT_DATE);
            groupByFieldSql.append(monthFormat);

            order.put(monthFormat,"asc");
            orderSql.append(getDataDao().convertOrderToSQL(order));
        } else if (groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_QUARTER)) {
            //报告类型是周期(季度)的情况
            String quarterFormat = "concat(date_format(" + DataService.STATISTICS_REPORT_PRODUCT_DATE + ", '%Y')," +
                    "FLOOR((date_format(" + DataService.STATISTICS_REPORT_PRODUCT_DATE + ", '%m')+2)/3))";
            selectFieldSql.append(quarterFormat + " as " + DataService.STATISTICS_REPORT_PRODUCT_DATE);
            groupByFieldSql.append(quarterFormat);

            order.put(quarterFormat,"asc");
            orderSql.append(getDataDao().convertOrderToSQL(order));
        } else {
            selectFieldSql.append(groupByField);
            groupByFieldSql.append(groupByField);

            order.put(groupByField,"asc");
            orderSql.append(getDataDao().convertOrderToSQL(order));
        }
        return getDataDao().queryListForAllObject(tableName,selectFieldSql,groupByFieldSql,orderSql,computeFields,queryConditionsArr);
    }

    /**
     * 取得表指定列的内容(如：统计数据用的GroupBY，Type，ComputeBy等)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-03-10 00:00:00
     */
    public List<Map<String, Object>> getColumnsValues(String tableName, String[] columnNames) throws Exception {
        if (StringUtils.isEmpty(tableName) || columnNames == null || columnNames.length == 0) {
            return null;
        }

        return getDataDao().queryListForColumnAllValues(tableName,columnNames);
    }

    /**
     * 根据查询条件进行数据查询
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-11-02 00:00:00
     */
    public List<Data> searchDataForExcelReport(String tableName,
                                               String groupByField,
                                               ComputeField[] computeFields,
                                               QueryCondition[] queryConditionsArr,
                                               String orderBy) throws Exception {
        if (StringUtils.isEmpty(tableName)
                || StringUtils.isEmpty(groupByField)
                || computeFields == null
                || queryConditionsArr == null
                || StringUtils.isEmpty(orderBy)) {
            return null;
        }
        StringBuffer selectFieldSql = new StringBuffer();
        StringBuffer groupByFieldSql = new StringBuffer();
        Map<String, String> order = new LinkedHashMap<>();
        StringBuffer orderSql = new StringBuffer();//T.B.D

        selectFieldSql.append(groupByField);
        groupByFieldSql.append(groupByField);
        order.put(orderBy,"desc");
        orderSql.append(getDataDao().convertOrderToSQL(order));

        return getDataDao().queryListForAllObject(tableName,selectFieldSql,groupByFieldSql,orderSql,computeFields,queryConditionsArr);
    }

    /**
     * 取得指定表的所有表头[COLUMN_NAME,名字]
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Map<String,String>> getAllColumns() throws Exception {
        List<Map<String,String>> colsList = new ArrayList<>();

        List<Map<String, Object>> colsNameList = getDataDao().queryListForColumnName(DataService.TABLE_DATA);
        for(Map<String,Object> colsName: colsNameList) {
            Map<String,String> nameMap = new LinkedHashMap<String,String>();
            nameMap.put(colsName.get("ORDINAL_POSITION").toString(),colsName.get("COLUMN_NAME").toString());//列位置，列名
            colsList.add(nameMap);
        }

        return colsList;
    }

    /**
     * 取得指定表的所有表头[名字]
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-06 00:00:00
     */
    public Set<String> getAllColumnNames(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName) || !this.isExistTableName(tableName)) {
            return null;
        }

        Set<String> colsSet = new LinkedHashSet<>();

        List<Map<String, Object>> colsNameList = getDataDao().queryListForColumnName(tableName);
        for (Map<String,Object> colsName: colsNameList) {
            colsSet.add(colsName.get("COLUMN_NAME").toString());
        }

        return colsSet;
    }

    /**
     * 取得指定表的所有表头[名字],不包括id
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-03 00:00:00
     */
    public Set<String> getAllColumnNamesWithoutID(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName) || !this.isExistTableName(tableName)) {
            return null;
        }

        Set<String> colsSet = new LinkedHashSet<>();

        List<Map<String, Object>> colsNameList = getDataDao().queryListForColumnName(tableName);
        for (Map<String,Object> colsName: colsNameList) {
            String colName = colsName.get("COLUMN_NAME").toString();
            if (colName.equals("id")) {
                continue;
            } else {
                colsSet.add(colName);
            }
        }

        return colsSet;
    }

    /**
     * 取得指定表的所有表头[名字],不包括id
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-03 00:00:00
     */
    public Set<String> getAllColumnNamesWithoutID(String tableName, String colNameExtend) throws Exception {
        if (StringUtils.isEmpty(tableName) || !this.isExistTableName(tableName)) {
            return null;
        }

        Set<String> colsSet = new LinkedHashSet<>();

        List<Map<String, Object>> colsNameList = getDataDao().queryListForColumnName(tableName);
        for (Map<String,Object> colsName: colsNameList) {
            String colName = colsName.get("COLUMN_NAME").toString();
            if (colName.equals("id")) {
                continue;
            } else {
                colsSet.add(colName + colNameExtend);
            }
        }

        return colsSet;
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
     * 根据表名称删除一张表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-18 00:00:00
     */
    public boolean deleteTable(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return false;
        }

        try {
            return getDataDao().deleteTable(tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 复制旧表结构到新表
     *
     * @param src 旧表名字
     * @param des 新表名字
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-10-31 00:00:00
     */
    public boolean copyTableStructure(String src, String des) throws Exception {
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty(des)) {
            return false;
        }

        return getDataDao().copyTableStructure(src, des);
    }

    /**
     * 根据表名称删除一张表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-18 00:00:00
     */
    public boolean isExistTableName(String tableName) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return false;
        }

        return getDataDao().isExistTableName(tableName);
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
     * 取得指定列分组的的值
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Map<String, Object>> getColumnAllValuesByGroup(String tableName, String[] columnNames) throws Exception {

        return getDataDao().queryListForColumnAllValuesByGroup(tableName, columnNames);
    }

    /**
     * 取得指定列分页的值
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-06-27 00:00:00
     */
    public List<Map<String, Object>> getColumnValuesWithPagination(String tableName,
                                                                   String category,
                                                                   String term,
                                                                   long offset,
                                                                   long limit,
                                                                   Map<String, String> order) throws Exception {

        return getDataDao().queryListForColumnAllValuesByGroupWithPagination(tableName, category, term, offset, limit, order);
    }

    /**
     * 取得条件列条件值所对应指定列的所有的值
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Map<String, Object>> queryListForValuesIfs(String tableName,
                                                           String[] selectionCols,
                                                           String criterionCol,
                                                           String criterionValue) throws Exception {

        return getDataDao().queryListForValuesIfs(tableName, selectionCols, criterionCol, criterionValue);
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
    public Long getColumnValueCounts(String tableName, String columnName) throws Exception {

        return getDataDao().getColumnValueCounts(tableName, columnName);
    }

    /**
     * 字段中指定值的数。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 00:00:00
     */
    public long getColumnValueNumber(String tableName, String columnName, String value) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            return -1;
        }
        return getDataDao().queryCountOfColumnValue(tableName,columnName,value).longValue();
    }

    /**
     * 导出表的数据到csv文件(带表头)
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-10-19 00:00:00
     */
    public List<String[]> getDataToCSV(String tableName, String filePath) throws Exception {

        return getDataDao().queryListStringForAllObjectToCSV(tableName, filePath);
    }

    /**
     * 导出表的数据到csv文件(不带表头)
     *
     * @param
     * @return
     * @throws
     * @author FuJia
     * @Time 2019-10-19 00:00:00
     */
    public List<String[]> getDataToCSVWithoutHeaders(String tableName, String filePath) throws Exception {

        return getDataDao().queryListStringForAllObjectToCSVWithoutHeaders(tableName, filePath);
    }

}
