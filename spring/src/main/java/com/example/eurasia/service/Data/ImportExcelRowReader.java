package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
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
                titleIsNotExistList = dataService.isTitleExist(rowList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.titleList = new ArrayList<String>(Arrays.asList(new String[rowList.size()]));
            Collections.copy(this.titleList, rowList);
            //this.titleList.addAll(rowList);//addAll实现的是浅拷贝
            /*
            Collections有一个copy方法。可是不好用啊总是报错。查看api才知道，它的capacity（容纳能力大小）可以指定（最好指定）。
            而初始化时size的大小永远默认为0，只有在进行add和remove等相关操作 时，size的大小才变化。
            然而进行copy()时候，首先做的是将desc的size和src的size大小进行比较，
            只有当desc的 size 大于或者等于src的size时才进行拷贝，否则抛出IndexOutOfBoundsException异常；
             */
        } else if (row > 0 && titleIsNotExistList.size() == 0) {
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

    public int saveDataToSQL(String tableName) throws Exception {
        return dataService.saveDataToSQL(tableName, this.getDataList());
    }

    public int saveDataToSQL(String tableName, List<Data> dataList) throws Exception {
        return dataService.saveDataToSQL(tableName, dataList);
    }

    /**
     * 检查是否为空行
     * @param rowList
     * @return
     */
    public boolean checkNullRow(List<String> rowList){
        boolean isNull = false;
        String temp;
        for(int i=0,size = rowList.size();i<size;i++){
            temp = rowList.get(i);
            if(temp == null || temp.trim().length() == 0)continue;
            isNull = true;
            break;
        }
        return isNull;
    }

    /**
     * 不存在的列名List转String
     * @param
     * @return
     */
    public String titleIsNotExistListToString() {
        StringBuilder sb = new StringBuilder();

        for (String title : this.getTitleIsNotExistList()) {
            sb.append(title);
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - ",".length());
        return sb.toString();
    }
}
