package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.User.UserService;
import com.example.eurasia.service.Util.DataProcessingUtil;
import com.example.eurasia.service.Util.ImportExcelUtils;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ImportExcelRowReader {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    private List<String> titleList;
    private List<Data> dataList;
    private List<String> titleIsNotExistList;
    private Set<String> sameTitleSet;
    private Map<String, List<Integer>> invalidDataMap;
    private List<Integer> exAndImRowList;

    ImportExcelRowReader () {
        this.dataList = new ArrayList<Data>();
        this.invalidDataMap = new HashMap<String, List<Integer>>();
        this.exAndImRowList = new ArrayList<Integer>();
    }

    /** poi事件模式下的业务逻辑实现方法
     * @param sheetIndex
     * @param row
     * @param rowList
     */
    public void getRows(int sheetIndex, int row, List<String> rowList) {

        //如果需要的话，判断行内容是否都为空，
        //也可以在startElement/endElement中通过if (qName.equals("row")) 进行判断。
        /*
        for (int i = 0; i < rowList.size(); i++) {
            //System.out.print(rowList.get(i) + " ");
            //T.B.D
        }
        */

        if (row == 0) {
            try {
                this.titleIsNotExistList = dataService.isTitleExist(rowList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.sameTitleSet = ImportExcelUtils.getSameTitle(rowList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (this.getTitleIsNotExistList().size() == 0 && this.getSameTitleSet().size() == 0) {
                this.titleList = new ArrayList<>(Arrays.asList(new String[rowList.size()]));
                Collections.copy(this.titleList, rowList);
                //this.titleList.addAll(rowList);//addAll实现的是浅拷贝
/*
                //为了做成MD5数据库列，先进行升序。注意：是根据的汉字的拼音的字母排序的，而不是根据汉字一般的排序方法
                Collections.sort(this.titleList, Collator.getInstance(java.util.Locale.CHINA));
                StringBuffer src = new StringBuffer();
                for (String title : this.titleList) {
                    src.append(title.trim());
                }
                String strMD5 = DigestUtils.md5Hex(src.toString());

 */
/*例子
                ArrayList<String>  newArray=  new ArrayList<String>();
                newArray.add("汽车");//排序后：10
                newArray.add("ab12");//排序后：0
                newArray.add("AB12");//排序后：1
                newArray.add("ab21");//排序后：2
                newArray.add("Ab21");//排序后：4
                newArray.add("aB21");//排序后：3
                newArray.add("公安");//排序后：8
                newArray.add("怡");//排序后：12
                newArray.add("张新");//排序后：11
                newArray.add("广州");//排序后：9
                newArray.add("test");//排序后：6
                newArray.add("pp");//排序后：5
                newArray.add("？23");//排序后：7
                Collections.sort(newArray, Collator.getInstance(java.util.Locale.CHINA));
 */
            }

            /*
            Collections有一个copy方法。可是不好用啊总是报错。查看api才知道，它的capacity（容纳能力大小）可以指定（最好指定）。
            而初始化时size的大小永远默认为0，只有在进行add和remove等相关操作 时，size的大小才变化。
            然而进行copy()时候，首先做的是将desc的size和src的size大小进行比较，
            只有当desc的 size 大于或者等于src的size时才进行拷贝，否则抛出IndexOutOfBoundsException异常；
             */
        } else if (row > 0 && this.getTitleIsNotExistList().size() == 0 && this.getSameTitleSet().size() == 0) {
            Map<String, String> keyValue = new LinkedHashMap<>();
            for (int i=0; i<this.getTitleList().size(); i++) {

                String title = this.getTitleList().get(i);
                String value = rowList.get(i);

                // 过滤不符合要求的Data(e.g. "进出口"不可为空)
                if (!this.checkData(row, title, value)) {
                    keyValue.clear();
                    return;
                }

                keyValue.put(title, value);//首行(表头)值，单元格值
            }
            Data data = new Data(keyValue);
            this.addDataToDataList(data);
        }
    }

    public List<String> getTitleList() {
        return this.titleList;
    }

    public List<Data> getDataList() {
        return this.dataList;
    }

    public void addDataToDataList(Data data) {
        this.dataList.add(data);
    }

    public void setDataList(List<Data> dataList) {
        this.dataList.addAll(dataList);//addAll实现的是浅拷贝
    }

    public void clearDataList() {
        this.dataList.clear();
    }

    public List<String> getTitleIsNotExistList() {
        return this.titleIsNotExistList;
    }

    public void clearTitleIsNotExistList() {
        this.titleIsNotExistList.clear();
    }

    public Set<String> getSameTitleSet() {
        return this.sameTitleSet;
    }

    public void clearSameTitleSet() {
        this.sameTitleSet.clear();
    }

    public Map<String, List<Integer>> getInvalidDataMap() {
        return this.invalidDataMap;
    }

    public void addInvalidDataToMap(String invalidDataKey) {
        switch (invalidDataKey) {
            case UserService.MUST_EXPORT_AND_IMPORT:
                this.invalidDataMap.put(invalidDataKey, this.exAndImRowList);
                break;
            default:
                break;
        }
    }

    public void clearInvalidDataMap() {
        for(Map.Entry<String , List<Integer>> maps : this.invalidDataMap.entrySet()) {
            maps.getValue().clear();
        }
        this.invalidDataMap.clear();
    }

    public String invalidDataMapToString() {
        StringBuffer str = new StringBuffer();
        for(Map.Entry<String , List<Integer>> maps : this.invalidDataMap.entrySet()) {

            List<Integer> rows = maps.getValue();
            if (rows.size() > 0) {
                String key = maps.getKey();
                str.append("\"" + key + ":");

                for (Integer row : rows) {
                    str.append(row);
                    str.append(",");
                }
                str.deleteCharAt(str.length() - 1);//","的长度为1，所以删除最后一个","即删除下标为sb.length()-1字符
                str.append("\"");
            }

        }
        return str.toString();
    }

    /*
     * Event Mode用
     */
    public int saveDataToSQLForEventMode(String tableName) throws Exception {
        return this.saveDataToSQLCommon(tableName, this.getDataList());
    }

    /*
     * User Mode用
     */
    public int saveDataToSQLForUserMode(String tableName, List<Data> dataList) throws Exception {
        return this.saveDataToSQLCommon(tableName, dataList);
    }

    private int saveDataToSQLCommon(String tableName, List<Data> dataList) throws Exception {
        // 取得数据表的所有列名[名字],不包括id
        Set<String> colsNameSet = dataService.getAllColumnNamesWithoutID(DataService.TABLE_DATA);

        // 取得数据对应关系的词典名，并取得其列名
        Map<String, Set<String>> dataDicColNamesMap = dataService.getDataDictionariesColumnNamesMap();
        if (dataDicColNamesMap.size() > 0) {//有数据对应关系的词典
            // 创建临时表，保存数据到临时表
            if (dataService.copyTableStructure(DataService.TABLE_DATA, DataService.TABLE_DATA_TEMP)) {
                int saveNum = dataService.saveDataToSQL(DataService.TABLE_DATA_TEMP, dataList);

                // 利用数据关系对应的词典，扩张要保存的数据
                Map<String, Integer> reMakeUpRet = dataService.reMakeUpDataByDataDictionaries(DataService.TABLE_DATA_TEMP, colsNameSet, dataDicColNamesMap);

                // 将临时表的数据保存到数据表
                int copyNum = dataService.copyTableData(DataService.TABLE_DATA_TEMP, DataService.TABLE_DATA);

                // 删除临时表
                boolean deleteRet = dataService.deleteTable(DataService.TABLE_DATA_TEMP);

                return copyNum;

            } else {
                //T.B.D
                Slf4jLogUtil.get().info("创建临时表失败");
                throw new Exception(ResponseCodeEnum.CREATE_DATA_TEMP_TABLE_FAILED.getMessage());
            }
        } else {//没数据对应关系的词典
            return dataService.saveDataToSQL(tableName, dataList);
        }
    }

    /**
     * 不存在的列名List转String
     * @param
     * @return
     */
    public String titleIsNotExistListToString() {
        return DataProcessingUtil.listToStringWithComma(this.getTitleIsNotExistList());
    }

    /**
     * 重复列名Set转String
     * @param
     * @return
     */
    public String sameTitleSetToString() {
        return DataProcessingUtil.setToStringWithComma(this.getSameTitleSet());
    }

    /**
     * 过滤不符合要求的Data(e.g. "进出口"不可为空)
     * @param
     * @return
     */
    public List<Data> checkData(List<Data> dataList) {
        List<Data> invalidDataList = new ArrayList<>();

        for (int i=0; i<dataList.size(); i++) {
            Set<Map.Entry<String, String>> set = dataList.get(i).getKeyValue().entrySet();
            Iterator<Map.Entry<String, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> entry = it.next();
                switch (entry.getKey()) {
                    case UserService.MUST_EXPORT_AND_IMPORT://进出口
                        if (entry.getValue().trim().equals("")) {
                            invalidDataList.add(dataList.get(i));
                            dataList.remove(dataList.get(i));
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return invalidDataList;
    }

    /**
     * 过滤不符合要求的Data(e.g. "进出口"不可为空)
     * @param
     * @return
     */
    public boolean checkData(int row, String title, String value) {

        boolean ret = true;
        switch (title) {
            case UserService.MUST_EXPORT_AND_IMPORT://进出口
                if (value.trim().equals("")) {
                    this.exAndImRowList.add(row+1);
                    ret = false;
                }
                break;
            default:
                break;
        }

        return ret;
    }
}
