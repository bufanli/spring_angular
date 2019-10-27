package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.ImportExcelUtils;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.STRING;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("DownloadFileServiceImpl")
@Component
public class DownloadFileServiceImpl implements IDownloadFileService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult exportExcel(HttpServletResponse response, QueryCondition[] queryConditionsArr) throws Exception {

        Set<String> colsNameSet = dataService.getTitles(DataService.TABLE_DATA);
        if (colsNameSet == null || colsNameSet.size() == 0) {
            Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_ZERO.getMessage());
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_ZERO);
        }
        //List<Data> dataList = this.getRows(queryConditionsArr);
        //if (dataList.size() == 0) {
        List<String[]> dataArrList = dataService.getRows(DataService.TABLE_DATA, queryConditionsArr);
        if (dataArrList == null || dataArrList.size() == 0) {
            Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_ZERO.getMessage());
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_ZERO);
        }

        StringBuffer responseMsg = new StringBuffer();
        SXSSFWorkbook wb = new SXSSFWorkbook(DataService.ROW_ACCESS_WINDOW_SIZE);
        try {
            SXSSFSheet sheet = wb.createSheet(DataService.EXPORT_EXCEL_SHEET_NAME);
            //int rowIndex = this.writeExcel(wb, sheet, colsNameSet, dataList);
            int rowIndex = this.writeExcel(wb, sheet, colsNameSet, dataArrList);
            Date date = new Date(System.currentTimeMillis());
            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
            String fileName = dateFormat.format(date);//导出文件名是当天日期
            ImportExcelUtils.buildExcelDocument(fileName+".xlsx", wb, response);

            responseMsg.append("导出到文件的条目数：" + rowIndex);//包括title行
            Slf4jLogUtil.get().info("导出到文件的条目数：{}",rowIndex);//包括title行
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_INFO_FAILED);
        } finally {
            wb.dispose();
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.EXPORT_DATA_INFO_SUCCESS,responseMsg);
    }

    private int writeExcel(SXSSFWorkbook wb, SXSSFSheet sheet, Set<String> colsNameSet, List<String[]> rowList) {

        int titleRowIndex = writeTitlesToExcel(wb, sheet, colsNameSet);
        int dataRowIndex = writeRowsToExcel(wb, sheet, rowList, titleRowIndex);
        setSizeColumn(sheet, (colsNameSet.size() + 1));
        return (titleRowIndex + dataRowIndex);
    }

    private int writeTitlesToExcel(SXSSFWorkbook wb, SXSSFSheet sheet, Set<String> colsNameSet) {
        int rowIndex = 0;
        int colIndex = 0;

        // 设置字体
        Font titleFont = wb.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        // titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(182, 184, 192)));
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        setBorder(titleStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        Row titleRow = sheet.createRow(rowIndex);
        // titleRow.setHeightInPoints(25);
        colIndex = 0;

        for(String colsName: colsNameSet) {
            Cell cell = titleRow.createCell(colIndex);
            cell.setCellValue(colsName);
            cell.setCellStyle(titleStyle);
            colIndex++;
        }

        rowIndex++;
        return rowIndex;
    }

    private int writeRowsToExcel(SXSSFWorkbook wb, SXSSFSheet sheet, List<String[]> rowList, int rowStartIndex) {
        int colIndex = 0;
        int rowIndex = rowStartIndex;

        // 设置字体
        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        // dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = (XSSFCellStyle) wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataStyle.setFont(dataFont);
        setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        for (String[] rowData : rowList) {
            Row dataRow = sheet.createRow(rowIndex);
            // dataRow.setHeightInPoints(25);
            colIndex = 0;

            for (String data : rowData) {
                Cell cell = dataRow.createCell(colIndex);
                cell.setCellValue(data);
                cell.setCellStyle(dataStyle);
                colIndex++;
            }
            rowIndex++;
        }
        return rowList.size();
    }

    private void autoSizeColumns(XSSFSheet sheet, int columnNumber) {

        for (int i = 0; i < columnNumber; i++) {
            int orgWidth = sheet.getColumnWidth(i);
            //autoSizeColumn方法可以把Excel设置为根据内容自动调整列宽，然而这个方法对中文并不起效，只对数字和字母有效
            sheet.autoSizeColumn(i, true);
            int newWidth = sheet.getColumnWidth(i);
            if (newWidth > 255 ) {
                sheet.setColumnWidth(i, 255);
            } else {
                //sheet.setColumnWidth(i, newWidth);
                sheet.setColumnWidth(i, (newWidth * 17 / 10));// 解决自动设置列宽中文失效的问题
            }
        }
    }

    // 自适应宽度(中文支持)
    private void setSizeColumn(SXSSFSheet sheet, int columnNumber) {
        // start row
        int startRowNum = sheet.getLastRowNum() - DataService.ROW_ACCESS_WINDOW_SIZE;
        if(startRowNum < 0 ) {
            startRowNum = 0;
        }else{
            startRowNum = startRowNum + 1;
        }
        for (int columnNum = 0; columnNum < columnNumber; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = startRowNum; rowNum < sheet.getLastRowNum(); rowNum++) {
                SXSSFRow currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    Cell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellTypeEnum() == STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            if (columnWidth > 30) {
                columnWidth = 30;
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }

    private void setBorder(XSSFCellStyle style, BorderStyle border, XSSFColor color) {
        style.setBorderTop(border);
        style.setBorderLeft(border);
        style.setBorderRight(border);
        style.setBorderBottom(border);
        style.setBorderColor(XSSFCellBorder.BorderSide.TOP, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.LEFT, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, color);
    }


/*
    private List<Data> getRows(QueryCondition[] queryConditionsArr) throws Exception {
        List<Data> dataList = new ArrayList<>();;
        try {
            Slf4jLogUtil.get().info("文件导出，查询数据开始");

            long offset = 0;
            long limit = DataService.DOWNLOAD_RECODE_STEPS;
            Map<String, String> order = new LinkedHashMap<>();
            order.put("id","asc");//T.B.D

            long count = dataService.queryTableRows(DataService.TABLE_DATA,queryConditionsArr);
            while (offset <= count) {
                dataList.addAll(dataService.searchData(DataService.TABLE_DATA,queryConditionsArr, offset, limit,order));
                offset += DataService.DOWNLOAD_RECODE_STEPS;
            }
            //dataList = dataService.searchDataForDownload(DataService.TABLE_DATA, queryConditionsArr);
            //if (dataList == null) {
            //    throw new Exception(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_NULL.getMessage());
            //}

            Slf4jLogUtil.get().info("文件导出，查询数据结束");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_FAILED.getMessage());
        }
        return dataList;
    }
*/

}
