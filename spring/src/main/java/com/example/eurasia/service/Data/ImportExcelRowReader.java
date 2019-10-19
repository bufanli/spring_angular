package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.service.Util.DataProcessingUtil;
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

    private List<String> titleList;
    private List<Data> dataList;
    private List<String> titleIsNotExistList;
    private Set<String> sameTitleSet;

    ImportExcelRowReader () {
        dataList = new ArrayList<Data>();
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

            if (getTitleIsNotExistList().size() == 0 && getSameTitleSet().size() == 0) {
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
        } else if (row > 0 && getTitleIsNotExistList().size() == 0 && getSameTitleSet().size() == 0) {
            Map<String, String> keyValue = new LinkedHashMap<>();
            for (int i=0; i<this.titleList.size(); i++) {
                keyValue.put(this.titleList.get(i), rowList.get(i));//首行(表头)值，单元格值
            }
            Data data = new Data(keyValue);
            this.dataList.add(data);
        }
    }

    public List<Data> getDataList() {
        return this.dataList;
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

    public int saveDataToSQL(String tableName) throws Exception {
        return dataService.saveDataToSQL(tableName, this.getDataList());
    }

    public int saveDataToSQL(String tableName, List<Data> dataList) throws Exception {
        return dataService.saveDataToSQL(tableName, dataList);
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
}
