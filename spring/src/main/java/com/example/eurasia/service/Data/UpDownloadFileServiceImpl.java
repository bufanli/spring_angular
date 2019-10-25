package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.ColumnsDictionary;
import com.example.eurasia.entity.Data.Data;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.STRING;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UpDownloadFileServiceImpl")
@Component
public class UpDownloadFileServiceImpl implements IUpDownloadFileService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下面是导入excel部分

    @Autowired
    private ImportExcelByUserMode importExcelByUserMode;
    @Autowired
    private ImportExcelByEventMode importExcelByEventMode;

    @Override
    public ResponseResult batchUploadExcel(File uploadDir, MultipartFile[] files) throws Exception {
        ResponseResult responseResult;
        String fileName = null;
        int fileNumber = files.length;
        StringBuffer responseMsg = new StringBuffer();
        StringBuffer responseOK = new StringBuffer();
        StringBuffer responseNG = new StringBuffer();
        int fileOKNum = 0;
        int fileNGNum = 0;
        Slf4jLogUtil.get().info("文件保存目录:{}",uploadDir.getPath());

        //遍历文件数组
        for (int i=0; i<files.length; i++) {
            //上传文件名
            fileName = files[i].getOriginalFilename();

            Slf4jLogUtil.get().info("第{}/{}个文件开始保存,文件名:{}",(i+1),fileNumber,fileName);

            try {
                //需要自定义文件名的情况
                //String suffix = files.getOriginalFilename().substring(files.getOriginalFilename().lastIndexOf("."));
                //String fileName = UUID.randomUUID() + suffix;
                //服务器端保存端文件对象
                File serverFile = new File(uploadDir.getPath() + "/" + fileName);
                if (!serverFile.exists()) {
                    Slf4jLogUtil.get().info("文件名:{}存在的话，则覆盖。",fileName);
                }
                //将上传的文件写入到服务器端的文件内
                files[i].transferTo(serverFile);

                fileOKNum++;
                responseOK.append(fileName + DataService.BR);
                Slf4jLogUtil.get().info("第{}/{}个文件保存OK结束,文件名:{}",(i+1),fileNumber,fileName);
            } catch (IOException e) {
                e.printStackTrace();
                fileNGNum++;
                responseNG.append(fileName +":文件保存IO异常" + DataService.BR);
                Slf4jLogUtil.get().error("第{}/{}个文件保存IO异常,文件名:{}",(i+1),fileNumber,fileName);
                continue;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                fileNGNum++;
                responseNG.append(fileName + ":文件保存IllegalState异常" + DataService.BR);
                Slf4jLogUtil.get().error("第{}/{}个文件保存IllegalState异常,文件名:{}",(i+1),fileNumber,fileName);
                continue;
            }
        }

        if (fileNGNum == 0) {
            responseMsg.append(fileOKNum + "个文件保存成功。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.UPLOAD_FILE_SUCCESS.getCode(), responseMsg.toString(), responseOK.toString());
        } else if (fileOKNum == 0 && fileNGNum != 0) {
            responseMsg.append(fileNGNum + "个文件保存失败。");
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.UPLOAD_FILE_FAILED.getCode(), responseMsg.toString(), responseNG.toString());
        } else {
            responseMsg.append(fileOKNum + "个文件保存成功," + fileNGNum + "个文件保存失败。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());

            String[] responseOKNGArr = new String[2];
            responseOKNGArr[0] = responseOK.toString();
            responseOKNGArr[1] = responseNG.toString();

            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.UPLOAD_FILE_FAILED.getCode(), responseMsg.toString(), responseOKNGArr);
        }
        return responseResult;
    }

    @Override
    public ResponseResult readExcel(File fileDir) throws Exception {
        ResponseResult responseResult;
        String fileName = null;
        int fileNumber = 0;
        StringBuffer responseMsg = new StringBuffer();
        StringBuffer responseOK = new StringBuffer();
        StringBuffer responseNG = new StringBuffer();
        int fileOKNum = 0;
        int fileNGNum = 0;
        Slf4jLogUtil.get().info("文件导入目录:{}",fileDir);

        //遍历文件数组
        File[] files = fileDir.listFiles();
        fileNumber = files.length;
        for (int i=0; i<fileNumber; i++) {
            if (files[i].isDirectory()) {
                //Nothing to do
                Slf4jLogUtil.get().info("第{}/{}，是个目录,跳过。",(i+1),fileNumber);
                continue;
            }

            String responseRead = null;
            if (files[i].isFile()) {
                //读取文件名
                fileName = files[i].getName();

                Slf4jLogUtil.get().info("第{}/{}个文件开始导入,文件名:{}",(i+1),fileNumber,fileName);

                try {
                    if (ImportExcelUtils.isExcelFileValidate(files[i]) == true) {
                        //responseRead = importExcelByUserMode.readExcelFile(files[i]);//T.B.D UserMode，没有check列名的同义词
                        responseRead = importExcelByEventMode.readExcelFile(files[i]);
                        if (responseRead.indexOf(DataService.IMPORT_EXCEL_SUCCESS_MESSAGE) != -1) {
                            fileOKNum++;
                            responseOK.append(fileName + ":" + responseRead + DataService.BR);
                            Slf4jLogUtil.get().info("第{}/{}个文件导入OK结束,文件名:{}",(i+1),fileNumber,fileName);
                        } else if (responseRead.indexOf(DataService.IMPORT_EXCEL_FAILED_MESSAGE) != -1) {
                            fileNGNum++;
                            responseNG.append(fileName + ":" + responseRead + DataService.BR);
                            Slf4jLogUtil.get().error("第{}/{}个文件表头在数据库中不存在或者重复,文件名:{}",(i+1),fileNumber,fileName);
                        }
                    } else {
                        fileNGNum++;
                        responseNG.append(fileName + ": 文件格式有问题" + DataService.BR);
                        Slf4jLogUtil.get().error("第{}/{}个文件格式有问题,文件名:{}",(i+1),fileNumber,fileName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    fileNGNum++;
                    responseNG.append(fileName + ": 文件导入IO异常" + DataService.BR);
                    Slf4jLogUtil.get().error("第{}/{}个文件导入IO异常,文件名:{}",(i+1),fileNumber,fileName);
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    fileNGNum++;
                    responseNG.append(fileName + ": 文件导入或者保存到数据库异常" + DataService.BR);
                    Slf4jLogUtil.get().error("第{}/{}个文件导入异常,文件名:{}",(i+1),fileNumber,fileName);
                    continue;
                }

            }
        }

        if (fileNGNum == 0) {
            responseMsg.append(fileOKNum + "个文件导入完成。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.READ_UPLOADED_FILE_SUCCESS.getCode(), responseMsg.toString(), responseOK.toString());
        } else if (fileOKNum == 0 && fileNGNum != 0) {
            responseMsg.append(fileNGNum + "个文件导入异常。");
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.READ_UPLOADED_FILE_FAILED.getCode(), responseMsg.toString(), responseNG.toString());
        } else {
            responseMsg.append(fileOKNum + "个文件导入完成," + fileNGNum + "个文件导入异常。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());

            String[] responseNGArr = new String[2];
            responseNGArr[0] = responseOK.toString();
            responseNGArr[1] = responseNG.toString();

            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.READ_UPLOADED_FILE_FAILED.getCode(), responseMsg.toString(), responseNGArr);
        }
        return responseResult;
    }

    @Override
    public ResponseResult getColumnsDictionary() throws Exception {
        ResponseResult responseResult;
        // 取得数据表的所有列名
        Set<String> colsNameSet = dataService.getAllColumnNamesWithoutID(DataService.TABLE_DATA);
        if (colsNameSet.isEmpty()) {
            throw new Exception(ResponseCodeEnum.GET_COLUMNS_DICTIONARY_GET_HEADER_INFO_FROM_SQL_NULL.getMessage());
        }

        // 取得数据字典表指定列的所有值
        List<Map<String, Object>> colValuesListMap = dataService.getColumnAllValuesByGroup(DataService.TABLE_COLUMNS_DICTIONARY,
                new String[]{DataService.COLUMNS_DICTIONARY_SYNONYM, DataService.COLUMNS_DICTIONARY_COLUMN_NAME});

        // 组成数据字典实例数组
        ColumnsDictionary[] columnsDictionary = new ColumnsDictionary[colsNameSet.size()];
        int i = 0;
        for (String columnName:colsNameSet) {
            // init each column dictionary
            columnsDictionary[i] = new ColumnsDictionary();
            List<String> synonymList = new ArrayList<>();

            for (Map<String, Object> map : colValuesListMap) {
                String colNameValue = (String) map.get(DataService.COLUMNS_DICTIONARY_COLUMN_NAME);
                if (colNameValue.equals(columnName)) {//在该Map里找到指定原词(数据库字段名)
                    String synonymValue = (String) map.get(DataService.COLUMNS_DICTIONARY_SYNONYM);
                    synonymList.add(synonymValue);
                }
            }
            String[] synonyms = new String[synonymList.size()];//同义词
            synonymList.toArray(synonyms);

            columnsDictionary[i].setColumnName(columnName);
            columnsDictionary[i].setSynonyms(synonyms);
            i++;
        }
        responseResult = new ResponseResultUtil().success(ResponseCodeEnum.GET_COLUMNS_DICTIONARY_SUCCESS, columnsDictionary);
        return responseResult;
    }

    @Override
    public ResponseResult setColumnsDictionary(ColumnsDictionary[] columnsDictionaryArr) throws Exception {
        ResponseResult responseResult;

        List<Data> dataList = new ArrayList<>();
        List<String> addColList = new ArrayList<>();

        try {
            // 需要保存的数据词典的所有列名
            Set<String> newColsNameSet = new LinkedHashSet<>();

            // 取得数据表的所有列名
            Set<String> colsNameSet = dataService.getAllColumnNamesWithoutID(DataService.TABLE_DATA);

            for (ColumnsDictionary columnsDictionary : columnsDictionaryArr) {
                String columnName = columnsDictionary.getColumnName();
                newColsNameSet.add(columnName);

                if (colsNameSet.contains(columnName)) {

                } else {
                    Slf4jLogUtil.get().info("添加新的字段！");
                    addColList.add(columnName);
                }

                for (String synonym : columnsDictionary.getSynonyms()) {
                    Map<String, String> keyValue = new LinkedHashMap<>();
                    keyValue.put(DataService.COLUMNS_DICTIONARY_SYNONYM, synonym);
                    keyValue.put(DataService.COLUMNS_DICTIONARY_COLUMN_NAME, columnName);
                    Data data = new Data(keyValue);
                    dataList.add(data);
                }
            }

            // 删除字段
            for (String columnName:colsNameSet) {
                if (newColsNameSet.contains(columnName)) {

                } else {
                    if (dataService.deleteColumnFromSQL(columnName)) {
                        Slf4jLogUtil.get().info("删除字段 {} 成功！",columnName);
                    } else {
                        Slf4jLogUtil.get().info("删除字段 {} 已有数据，不可删除！",columnName);
                        responseResult = new ResponseResultUtil().error(ResponseCodeEnum.DELETE_COLUMN_DATA_IS_EXIST);
                        return responseResult;
                    }
                }
            }

            // 添加新字段
            int addColNum = dataService.addColumnToSQL(addColList);
            Slf4jLogUtil.get().info("添加共{}条新字段！",addColList.size());

            // 保存词典
            int deleteNum = dataService.deleteAllData(DataService.TABLE_COLUMNS_DICTIONARY);
            int addDataNum = dataService.saveDataToSQL(DataService.TABLE_COLUMNS_DICTIONARY, dataList);
            Slf4jLogUtil.get().info("导入数据词典成功，共{}条数据！",addDataNum);

            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.SET_COLUMNS_DICTIONARY_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SET_COLUMNS_DICTIONARY_FAILED);
        }

        return responseResult;
    }

    @Override
    public ResponseResult deleteColumn(String columnName) throws Exception {
        ResponseResult responseResult;
        try {
            if (dataService.deleteColumnFromSQL(columnName)) {
                Slf4jLogUtil.get().info("删除字段 {} 成功！",columnName);
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.DELETE_COLUMN_SUCCESS);
            } else {
                Slf4jLogUtil.get().info("删除字段 {} 失败！",columnName);
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.DELETE_COLUMN_FAILED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.DELETE_COLUMN_FAILED);
        }
        return responseResult;
    }

    @Override
    public ResponseResult deleteSameData() throws Exception {
        ResponseResult responseResult;
        try {
/*
删除规则是：1、如果有“提运单号”的优先保留  2、如果“提运单号”是空白的，则保留列数比较多的那一条数据。
*/
            //int deleteNum = dataService.deleteSameData(DataService.TABLE_DATA);
            int ret = dataService.deleteSameDataByDistinct(DataService.TABLE_DATA);
            if (ret == 0) {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.DELETE_SAME_DATA_SUCCESS);
            } else {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.DELETE_SAME_DATA_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.DELETE_SAME_DATA_FAILED);
        }
        return responseResult;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下面是导出excel部分
    private static int ROW_ACCESS_WINDOW_SIZE = 10000;

    @Override
    public ResponseResult downloadExcel(HttpServletResponse response, QueryCondition[] queryConditionsArr) throws Exception {

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

            long offset = 0;
            Map<String, String> order = new LinkedHashMap<>();
            order.put("id","asc");//T.B.D

            long count = dataService.queryTableRows(DataService.TABLE_DATA,queryConditionsArr);
            long steps = count / DataService.DOWNLOAD_RECODE_STEPS + 1;
            int dataRowIndex = 0;
            for (int i = 0; i < steps; i++) {
                List<String[]> dataArrList = this.getRows(
                        queryConditionsArr,
                        offset,
                        DataService.DOWNLOAD_RECODE_STEPS,
                        order);
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

                offset += DataService.DOWNLOAD_RECODE_STEPS;
            }
            // adjust column size
            this.setSizeColumn(sheet, (colsNameSet.size() + 1));
            // response http file download
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
    private void buildExcelDocument(String filename, SXSSFWorkbook wb, HttpServletResponse response) throws Exception{
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