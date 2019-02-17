package com.example.eurasia.service.Data;

import java.util.List;

public class ImportExcelRowReader {

    /** poi事件模式下的业务逻辑实现方法
     * @param sheetIndex
     * @param curRow
     * @param rowList
     */
    public static void getRows(int sheetIndex, int curRow, List<String> rowList) {
        for (int i = 0; i < rowList.size(); i++) {
            System.out.print(rowList.get(i) + " ");
            //T.B.D
        }
    }
}
