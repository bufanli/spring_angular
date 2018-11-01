package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UploadFileServiceImpl")
@Component
public class UploadFileServiceImpl implements IUploadFileService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult batchUpload(File uploadDir, MultipartFile[] files) throws Exception {
        ResponseResult responseResult;
        String fileName = null;
        int fileNumber = files.length;
        StringBuffer responseMsg = new StringBuffer();
        StringBuffer responseOK = new StringBuffer();
        StringBuffer responseNG = new StringBuffer();
        int fileOKNum = 0;
        int fileNGNum = 0;
        log.info("文件上传目录:{}",uploadDir.getPath());

        //遍历文件数组
        for (int i=0; i<files.length; i++) {
            //上传文件名
            fileName = files[i].getOriginalFilename();

            log.info("第{}/{}个文件开始上传,文件名:{}",(i+1),fileNumber,fileName);

            try {
                //需要自定义文件名的情况
                //String suffix = files.getOriginalFilename().substring(files.getOriginalFilename().lastIndexOf("."));
                //String fileName = UUID.randomUUID() + suffix;
                //服务器端保存端文件对象
                File serverFile = new File(uploadDir.getPath() + "/" + fileName);
                if (!serverFile.exists()) {
                    log.info("文件名:{}存在的话，则覆盖。",fileName);
                }
                //将上传的文件写入到服务器端的文件内
                files[i].transferTo(serverFile);
            } catch (IOException e) {
                e.printStackTrace();
                fileNGNum++;
                responseNG.append(fileName +":上传IO异常" + DataService.BR);
                log.error("第{}/{}个文件上传IO异常,文件名:{}",(i+1),fileNumber,fileName);
                continue;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                fileNGNum++;
                responseNG.append(fileName + ":上传IllegalState异常" + DataService.BR);
                log.error("第{}/{}个文件上传IllegalState异常,文件名:{}",(i+1),fileNumber,fileName);
                continue;
            }

            fileOKNum++;
            log.info("第{}/{}个文件上传OK结束,文件名:{}",(i+1),fileNumber,fileName);

            responseOK.append(fileName + DataService.BR);
        }

        if (fileNGNum == 0) {
            responseMsg.append(fileOKNum + "个文件导入成功。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.UPLOAD_FILE_SUCCESS.getCode(), responseMsg.toString(), responseOK.toString());
        } else if (fileOKNum == 0 && fileNGNum != 0) {
            responseMsg.append(fileNGNum + "个文件导入失败。");
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.UPLOAD_FILE_FAILED.getCode(), responseMsg.toString(), responseNG.toString());
        } else {
            responseMsg.append(fileOKNum + "个文件导入成功," + fileNGNum + "个文件导入失败。");
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
    public ResponseResult readFile(File fileDir) throws Exception {
        ResponseResult responseResult;
        String fileName = null;
        int fileNumber = 0;
        StringBuffer responseMsg = new StringBuffer();
        StringBuffer responseOK = new StringBuffer();
        StringBuffer responseNG = new StringBuffer();
        int fileOKNum = 0;
        int fileNGNum = 0;
        log.info("文件读取目录:{}",fileDir);

        //遍历文件数组
        File[] files = fileDir.listFiles();
        fileNumber = files.length;
        for (int i=0; i<fileNumber; i++) {
            if (files[i].isDirectory()) {
                //Nothing to do
                log.info("第{}/{}，是个目录,跳过。",(i+1),fileNumber);
                continue;
            }

            String responseRead = null;
            if (files[i].isFile()) {
                //读取文件名
                fileName = files[i].getName();

                log.info("第{}/{}个文件开始读取,文件名:{}",(i+1),fileNumber,fileName);

                try {
                    if (ExcelImportUtils.isExcelFileValidata(files[i]) == true) {
                        responseRead = this.readExcelFile(files[i]);//返回值的利用
                    } else {
                        fileNGNum++;
                        responseNG.append(fileName +": 文件格式有问题" + DataService.BR);
                        log.error("第{}/{}个文件格式有问题,文件名:{}",(i+1),fileNumber,fileName);
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    fileNGNum++;
                    responseNG.append(fileName +": 读取IO异常" + DataService.BR);
                    log.error("第{}/{}个文件读取IO异常,文件名:{}",(i+1),fileNumber,fileName);
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    fileNGNum++;
                    responseNG.append(fileName +": 读取或者保存到数据库异常" + DataService.BR);
                    log.error("第{}/{}个文件读取异常,文件名:{}",(i+1),fileNumber,fileName);
                    continue;
                }

            }

            fileOKNum++;
            log.info("第{}/{}个文件读取OK结束,文件名:{}",(i+1),fileNumber,fileName);

            responseOK.append(fileName + ":" + responseRead + DataService.BR);
        }

        if (fileNGNum == 0) {
            responseMsg.append(fileOKNum + "个文件读取成功。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.READ_UPLOADED_FILE_SUCCESS.getCode(), responseMsg.toString(), responseOK.toString());
        } else if (fileOKNum == 0 && fileNGNum != 0) {
            responseMsg.append(fileNGNum + "个文件读取失败。");
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.READ_UPLOADED_FILE_FAILED.getCode(), responseMsg.toString(), responseNG.toString());
        } else {
            responseMsg.append(fileOKNum + "个文件读取成功," + fileNGNum + "个文件读取失败。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());

            String[] responseNGArr = new String[2];
            responseNGArr[0] = responseOK.toString();
            responseNGArr[1] = responseNG.toString();

            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.READ_UPLOADED_FILE_FAILED.getCode(), responseMsg.toString(), responseNGArr);
        }
        return responseResult;
    }

    private String readExcelFile(File file) throws Exception {

        InputStream inputStream = null;//初始化输入流
        Workbook workbook = null;//根据版本选择创建Workbook的方式

        try {
            inputStream = new FileInputStream(file);
            if (ExcelImportUtils.isExcel2003(file.toString())) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (ExcelImportUtils.isExcel2007(file.toString())) {
                workbook = new XSSFWorkbook(inputStream);
            }

            return this.readExcelFileSheets(workbook);//读Excel中所有的sheet
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            if (inputStream != null) {
                try{
                    inputStream.close();
                } catch(IOException e) {
                    inputStream = null;
                    e.printStackTrace();
                }
            }
        }

    }

    private String readExcelFileSheets(Workbook workbook) throws Exception {

        StringBuffer msg = new StringBuffer();//信息接收器

        int numSheets = workbook.getNumberOfSheets();
        for (int m = 0; m < numSheets; m++) {//循环sheet
            Sheet sheet = workbook.getSheetAt(m);//得到第一个sheet
            if (sheet == null) {
                log.error("第{}/{}个sheet有问题，请仔细检查。sheet名：{}",(m+1),numSheets,sheet.getSheetName());
                continue;
            }
            log.info("第{}/{}个sheet开始读取,sheet名:{}",(m+1),numSheets,sheet.getSheetName());
            msg = this.readExcelFileSheet(sheet);//读一个sheet内容
            log.info("第{}/{}个sheet读取结束,sheet名:{}",(m+1),numSheets,sheet.getSheetName());
        }
        return msg.toString();
    }

    private StringBuffer readExcelFileSheet(Sheet sheet) throws Exception {
        List<Map<Integer,String>> titleList = new ArrayList<>();
        List<Data> dataList = new ArrayList<>();
        Map<String, String> keyValue = new HashMap<>();

        StringBuffer sheetMsg = new StringBuffer();//信息接收器

        String dataStyle = this.checkSheetDataStyle(sheet);
        if (dataStyle.equals("Data1")) {//T.B.D. dummy

        } else if (dataStyle.equals("Data2")) {//T.B.D. dummy

        }

        //读取标题行
        StringBuffer titleErrMsg = this.readExcelFileSheetTitleRow(sheet, titleList);
        //标题行验证通过才导入到数据库
        if (titleList.size() != 0) {
            //读取内容(从第二行开始)
            StringBuffer dataErrMsg = this.readExcelFileSheetDataRow(sheet, titleList, dataList);

            int addDataNum = 0;
            for (Data data : dataList) {
                addDataNum = this.saveDataToSQL(DataService.TABLE_NAME, data);//导入一行数据。
            }
            log.info("导入成功，共{}条数据！",dataList.size());
            sheetMsg.append("导入成功，共" + dataList.size() + "条数据！");
        } else {
            sheetMsg.append(titleErrMsg);
        }
        return sheetMsg;
    }

    private StringBuffer readExcelFileSheetTitleRow(Sheet sheet, List<Map<Integer,String>> titleList) throws Exception {
        StringBuffer msg = new StringBuffer();//信息接收器
        StringBuffer rowErrorMsg = new StringBuffer();//行错误信息接收器

        List<Map<Integer,String>> valueList = new ArrayList<>();

        msg.append("第1行标题行:");

        Row titleRow = sheet.getRow(0);//标题行
        int numTitleCells = titleRow.getLastCellNum();//标题行的列数
        for (int n=0; n<numTitleCells; n++) {//循环列
            Cell cell = titleRow.getCell(n);
            if (null == cell) {
                rowErrorMsg.append(DataService.BR + "第" + (n+1) + "列标题有问题，请仔细检查。");
                log.error("第{}/{}列标题有问题，请仔细检查。",(n+1),numTitleCells);
                continue;
            }

            cell.setCellType(CellType.STRING);
            String cellValue = cell.getStringCellValue();
            if (StringUtils.isEmpty(cellValue)) {
                rowErrorMsg.append(DataService.BR + "第" + (n+1) + "列标题为空，请仔细检查。");
                log.error("第{}/{}列标题为空。",(n+1),numTitleCells);
                continue;
            }
            if (!this.checkTitles(cellValue)) {//对比SQL表头
                rowErrorMsg.append(DataService.BR + "第" + (n+1) + "列标题在数据中不存在，请仔细检查。");
                log.error("第{}/{}列标题在数据中不存在。",(n+1),numTitleCells);
                continue;
            }
            Map<Integer,String> titleMap = new HashMap<>();
            titleMap.put(n,cellValue);//列号，列值
            valueList.add(titleMap);
        }

        //拼接每行的错误提示
        if (rowErrorMsg.length() != 0) {
            msg.append(rowErrorMsg);
        } else {
            msg.setLength(0);
        }

        titleList.addAll(valueList);
        return msg;
    }

    private StringBuffer readExcelFileSheetDataRow(Sheet sheet, List<Map<Integer,String>> titleList, List<Data> dataList) {
        StringBuffer msg = new StringBuffer();//信息接收器
        StringBuffer colErrorMsg = new StringBuffer();//列错误信息接收器

        List<Data> valueList = new ArrayList<>();
        Map<String, String> keyValue = new HashMap<>();

        //int numRows = sheet.getPhysicalNumberOfRows();//得到Excel的行数,不包括那些空行（隔行）的情况。
        int numRows = sheet.getLastRowNum();//获取的是最后一行的编号（编号从0开始）。

        for (int p = 1; p <= numRows; p++) {//循环Excel行数,从第二行开始。标题不入库
            Row row = sheet.getRow(p);
            if (row == null) {
                msg.append("第" + (p+1) + "行数据有问题，请仔细检查。" + DataService.BR);
                log.error("第{}/{}行数据有问题，请仔细检查。",(p+1),numRows);
                continue;
            }

            for (Map<Integer, String> title : titleList) {//循环列
                Set<Map.Entry<Integer, String>> set = title.entrySet();
                Iterator<Map.Entry<Integer, String>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, String> entry = it.next();

                    int q = entry.getKey().intValue();//列号
                    Cell cell = row.getCell(q);
                    String cellValue = "";
                    if (null == cell) {
//                        colErrorMsg.append("第" + (p+1) + "行第" + (q+1) + "列数据有问题，请仔细检查。" + DataService.BR);
//                        log.error("第{}行,第{}列数据有问题，请仔细检查。",(p+1),(q+1));
//                        continue;
                    } else {
                        cell.setCellType(CellType.STRING);
                        cellValue = cell.getStringCellValue();
                    }
                    keyValue.put(entry.getValue(), cellValue);//首行(表头)值，单元格值.T.B.D有重复列的的话，会覆盖之前的。
                }
            }

            //拼接每行的错误提示
            if(colErrorMsg.length() != 0){
                msg.append(colErrorMsg);
                colErrorMsg.setLength(0);
            }else{
                Data data = new Data(keyValue);
                valueList.add(data);
            }

            keyValue.clear();
        }

        dataList.addAll(valueList);
        return msg;
    }

    private String checkSheetDataStyle(Sheet sheet) {
        int numTitleCells = sheet.getRow(0).getLastCellNum();//标题行的列数

        // 多表格的情况下，需要区分将数据导入哪个数据表里。
        // T.B.D. dummy
        return "";
    }

    private boolean checkTitles(String cellValue) throws Exception {
        List<Map<String,Object>> colsNameList = this.getTitles(DataService.TABLE_NAME);
        if (colsNameList.size() == 0) {
            log.info(ResponseCodeEnum.UPLOAD_GET_HEADER_INFO_FROM_SQL_ZERO.getMessage());
            return false;
        }

        for(Map<String,Object> colsName: colsNameList) {
            Set<Map.Entry<String, Object>> set = colsName.entrySet();
            Iterator<Map.Entry<String, Object>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,Object> entry = it.next();
                //System.out.println("Key:" + entry.getKey() + " Value:" + entry.getValue());
                if (entry.getValue().toString().equals(cellValue) == true) {
                    return true;//excel这个表头，在数据库表头中找到了
                }
            }
        }

        return false;//没找到
    }

    private int saveDataToSQL(String tableName, Data data) {
        return dataService.addData(tableName, data);
    }

    private List<Map<String,Object>> getTitles(String tableName) throws Exception {
        List<Map<String,Object>> colsNameList;
        try {
            log.info("文件上传，取得表头开始");

            colsNameList = dataService.getHeaders(tableName);
            if (colsNameList == null) {
                throw new Exception(ResponseCodeEnum.UPLOAD_GET_HEADER_INFO_FROM_SQL_NULL.getMessage());
            }

            log.info("文件上传，取得表头结束");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(ResponseCodeEnum.UPLOAD_GET_HEADER_INFO_FROM_SQL_FAILED.getMessage());
        }

        return colsNameList;
    }
}
