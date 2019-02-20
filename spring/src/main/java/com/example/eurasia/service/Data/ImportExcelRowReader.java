package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

public class ImportExcelRowReader {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    private List<String> titleList;
    private List<Data> dataList;

    ImportExcelRowReader () {
        dataList = new ArrayList<Data>();
    }

    /** poi事件模式下的业务逻辑实现方法
     * @param sheetIndex
     * @param row
     * @param rowList
     */
    public void getRows(int sheetIndex, int row, List<String> rowList) {

        if (row == 0) {
            this.titleList = new ArrayList<String>(Arrays.asList(new String[rowList.size()]));
            Collections.copy(this.titleList, rowList);
            //this.titleList.addAll(rowList);//addAll实现的是浅拷贝
            /*
            Collections有一个copy方法。可是不好用啊总是报错。查看api才知道，它的capacity（容纳能力大小）可以指定（最好指定）。
            而初始化时size的大小永远默认为0，只有在进行add和remove等相关操作 时，size的大小才变化。
            然而进行copy()时候，首先做的是将desc的size和src的size大小进行比较，
            只有当desc的 size 大于或者等于src的size时才进行拷贝，否则抛出IndexOutOfBoundsException异常；
             */
        } else if (row > 0) {
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

    public int saveDataToSQL(String tableName, List<Data> dataList) throws Exception {
        int addDataNum = 0;
        int deleteNum = 0;
        if (dataList.size() > 0) {
            for (Data data : dataList) {
                addDataNum += dataService.addData(DataService.TABLE_DATA, data);//导入一行数据。
            }
            if (addDataNum > 0) {
                deleteNum = dataService.deleteSameData(DataService.TABLE_DATA);
            }
            int num = addDataNum - deleteNum;//T.B.D
            return (num < 0) ? 0 : num;
        } else {
            return 0;
        }
    }
}
