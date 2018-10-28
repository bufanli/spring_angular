package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("DownloadFileServiceImpl")
@Component
public class DownloadFileServiceImpl implements IDownloadFileService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult exportExcel(OutputStream out, QueryCondition[] queryConditionsArr) throws Exception {

        List<Map<String,Object>> colsNameList = this.getTitles(DataService.TABLE_NAME);
        if (colsNameList.size() == 0) {
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_ZERO);
        }
        List<Data> dataList = this.getRows(DataService.TABLE_NAME,queryConditionsArr);
        if (dataList.size() == 0) {
            log.info(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_ZERO.getMessage());
        }

        log.info("文件导出,创建文件开始");
        XSSFWorkbook wb = new XSSFWorkbook();
        try {

            XSSFSheet sheet = wb.createSheet(DataService.EXPORT_EXCEL_SHEET_NAME);
            this.writeExcel(wb, sheet, colsNameList, dataList);

            wb.write(out);

            log.info("文件导出结束");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_INFO_FAILED);
        } finally {
            wb.close();
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.EXPORT_DATA_INFO_SUCCESS);
    }

    //创建表头
    public void createTitle(HSSFWorkbook workbook, HSSFSheet sheet, Data title) {
        HSSFRow row = sheet.createRow(0);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        sheet.setColumnWidth(1,12*256);
        sheet.setColumnWidth(3,17*256);

        //设置为居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);

        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("ID");//T.B.D.
        cell.setCellStyle(style);


        cell = row.createCell(1);
        cell.setCellValue("显示名");//T.B.D.
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("用户名");//T.B.D.
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("创建时间");//T.B.D.
        cell.setCellStyle(style);
    }

    //创建行数据
    public void createRows(HSSFWorkbook workbook, HSSFSheet sheet, Data rows) {
//        List<Data> rows = userService.getAll();
//
//        //设置日期格式
//        HSSFCellStyle style = workbook.createCellStyle();
//        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
//
//        //新增数据行，并且设置单元格数据
//        int rowNum=1;
//        for(Data data:rows){
//            HSSFRow row = sheet.createRow(rowNum);
//            row.createCell(0).setCellValue(data.getId());
//            row.createCell(1).setCellValue(data.getName());
//            row.createCell(2).setCellValue(data.getUsername());
//
//            HSSFCell cell = row.createCell(3);
//            cell.setCellValue(data.getCreate_time());
//            cell.setCellStyle(style);
//            rowNum++;
//        }
    }

    private void writeExcel(XSSFWorkbook wb, Sheet sheet, List<Map<String,Object>> titleList, List<Data> rowList) {

        int rowIndex = 0;

        int i = 0;
        for(Map<String,Object> colsName: titleList) {
            Set<Map.Entry<String, Object>> set = colsName.entrySet();
            Iterator<Map.Entry<String, Object>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,Object> entry = it.next();
                //System.out.println("Key:" + entry.getKey() + " Value:" + entry.getValue());
            }
            i++;
        }

//        rowIndex = writeTitlesToExcel(wb, sheet, data.getTitles());//T.B.D.
//        writeRowsToExcel(wb, sheet, data.getRows(), rowIndex);//T.B.D.
//        autoSizeColumns(sheet, data.getTitles().size() + 1);//T.B.D.

    }

    private int writeTitlesToExcel(XSSFWorkbook wb, Sheet sheet, List<String> titles) {
        int rowIndex = 0;
        int colIndex = 0;

        // 设置字体
        Font titleFont = wb.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        // titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(182, 184, 192)));
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        setBorder(titleStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        Row titleRow = sheet.createRow(rowIndex);
        // titleRow.setHeightInPoints(25);
        colIndex = 0;

        for (String field : titles) {
            Cell cell = titleRow.createCell(colIndex);
            cell.setCellValue(field);
            cell.setCellStyle(titleStyle);
            colIndex++;
        }

        rowIndex++;
        return rowIndex;
    }

    private int writeRowsToExcel(XSSFWorkbook wb, Sheet sheet, List<List<Object>> rows, int rowIndex) {
        int colIndex = 0;

        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        // dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setFont(dataFont);
        setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        for (List<Object> rowData : rows) {
            Row dataRow = sheet.createRow(rowIndex);
            // dataRow.setHeightInPoints(25);
            colIndex = 0;

            for (Object cellData : rowData) {
                Cell cell = dataRow.createCell(colIndex);
                if (cellData != null) {
                    cell.setCellValue(cellData.toString());
                } else {
                    cell.setCellValue("");
                }

                cell.setCellStyle(dataStyle);
                colIndex++;
            }
            rowIndex++;
        }
        return rowIndex;
    }

    private void autoSizeColumns(Sheet sheet, int columnNumber) {

        for (int i = 0; i < columnNumber; i++) {
            int orgWidth = sheet.getColumnWidth(i);
            sheet.autoSizeColumn(i, true);
            int newWidth = (int) (sheet.getColumnWidth(i) + 100);
            if (newWidth > orgWidth) {
                sheet.setColumnWidth(i, newWidth);
            } else {
                sheet.setColumnWidth(i, orgWidth);
            }
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
    protected void buildExcelFile(String filename, XSSFWorkbook workbook) throws Exception{
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.flush();
        fos.close();

        // flush()其实是继承于其父类OutputStream的。而OutputStream类的flush()却什么也没做
        // 当OutputStream是BufferedOutputStream时,flush()才有效.
    }

    //浏览器下载excel
    protected void buildExcelDocument(String filename, XSSFWorkbook workbook, HttpServletResponse response) throws Exception{
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    private List<Map<String,Object>> getTitles(String tableName) throws Exception {
        List<Map<String,Object>> colsNameList;
        try {
            colsNameList = dataService.getHeaders(DataService.TABLE_NAME);
            if (colsNameList == null) {
                throw new Exception(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_NULL.getMessage());
            }

            log.info("文件导出，取得表头结束");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(ResponseCodeEnum.EXPORT_GET_HEADER_INFO_FROM_SQL_FAILED.getMessage());
        }

        return colsNameList;
    }

    private List<Data> getRows(String tableName, QueryCondition[] queryConditionsArr) throws Exception {
        Data queryConditions = new Data(dataService.queryConditionsArrToMap(queryConditionsArr));
        List<Data> dataList;
        try {
            dataList = dataService.searchData(DataService.TABLE_NAME, queryConditions);
            if (dataList == null) {
                throw new Exception(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_NULL.getMessage());
            }

            log.info("文件导出，查询数据结束");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(ResponseCodeEnum.EXPORT_GET_DATA_INFO_FROM_SQL_FAILED.getMessage());
        }
        return dataList;
    }

}
