package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.STRING;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("DownloadFileServiceImpl")
@Component
public class DownloadFileServiceImpl implements IDownloadFileService {

    private static int ROW_ACCESS_WINDOW_SIZE = 10000;
    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult exportExcel(HttpServletResponse response, QueryCondition[] queryConditionsArr) throws Exception {

        StringBuffer responseMsg = new StringBuffer();
        SXSSFWorkbook wb = new SXSSFWorkbook(ROW_ACCESS_WINDOW_SIZE);
        SXSSFSheet sheet = wb.createSheet(DataService.EXPORT_EXCEL_SHEET_NAME);
        try {
            
            Set<String> colsNameSet = this.getTitles(DataService.TABLE_DATA);
            if (colsNameSet.size() == 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_ZERO.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_ZERO);
            }
            int titleRowIndex = this.writeTitlesToExcel(wb, sheet, colsNameSet);
            this.setSizeColumn(sheet, (colsNameSet.size() + 1));


            long offset = 0;
            long limit = DataService.DOWNLOAD_RECODE_STEPS;
            Map<String, String> order = new LinkedHashMap<>();
            order.put("id","asc");//T.B.D

            long count = dataService.queryTableRows(DataService.TABLE_DATA,queryConditionsArr);

            int dataRowIndex = 0;
            for (int i = 0; i < (count/offset + 1); i++) {
                List<String[]> dataArrList = this.getRows(queryConditionsArr, offset, limit, order);
                if (dataArrList == null) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_NULL.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_NULL);
                }
                if (dataArrList.size() == 0) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_ZERO.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_ZERO);
                }
                int rowStartIndex = dataRowIndex + titleRowIndex;
                dataRowIndex += this.writeRowsToExcel(wb, sheet, dataArrList, rowStartIndex);

                offset += limit;
            }

            Date date = new Date(System.currentTimeMillis());
            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
            String fileName = dateFormat.format(date);//导出文件名是当天日期
            this.buildExcelDocument(fileName+".xlsx", wb, response);

            int rowIndex = titleRowIndex + dataRowIndex;
            responseMsg.append("导出到文件的条目数：" + rowIndex);//包括title行
            Slf4jLogUtil.get().info("导出到文件的条目数：{}",rowIndex);//包括title行
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_INFO_FAILED);
        } finally {
            wb.close();
            wb.dispose();
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.EXPORT_DATA_INFO_SUCCESS,responseMsg);
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
        int startRowNum = sheet.getLastRowNum() - ROW_ACCESS_WINDOW_SIZE;
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

    //生成excel文件
    private void buildExcelFile(String filename, XSSFWorkbook workbook) throws Exception{
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.flush();
        fos.close();

        // flush()其实是继承于其父类OutputStream的。而OutputStream类的flush()却什么也没做
        // 当OutputStream是BufferedOutputStream时,flush()才有效.
    }

    //浏览器下载excel
    private void buildExcelDocument(String filename,SXSSFWorkbook wb ,HttpServletResponse response) throws Exception{
        //String filename = StringUtils.encodeFilename(StringUtils.trim(filename), request);//处理中文文件名
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "gbk"));
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
        wb.dispose();
    }

    private Set<String> getTitles(String tableName) throws Exception {
        Set<String> colsNameSet;
        try {
            Slf4jLogUtil.get().info("文件导出，取得表头开始");

            colsNameSet = dataService.getAllColumnNames(tableName);
            if (colsNameSet == null) {
                throw new Exception(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_NULL.getMessage());
            }

            Slf4jLogUtil.get().info("文件导出，取得表头结束");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_FAILED.getMessage());
        }

        return colsNameSet;
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
    private List<String[]>  getRows(QueryCondition[] queryConditionsArr,
                                    long offset,
                                    long limit,
                                    Map<String, String> order) throws Exception {
        List<String[]> dataArrList = null;
        try {
            Slf4jLogUtil.get().info("文件导出，查询数据开始");

            dataArrList = dataService.searchDataForDownload(DataService.TABLE_DATA,queryConditionsArr, offset, limit,order);

            Slf4jLogUtil.get().info("文件导出，查询数据结束");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_FAILED.getMessage());
        }
        return dataArrList;
    }
}
