package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UploadFileServiceImpl")
@Component
public class UploadFileServiceImpl implements IUploadFileService {

    @Override
    public ResponseResult batchUpload(String filePath, MultipartFile[] files) throws Exception {
        log.info("文件上传目录:{}",filePath);
        try {
            //遍历文件数组
            for (int i=0; i<files.length; i++) {
                //上传文件名
                String fileName = files[i].getOriginalFilename();

                log.info("第{}/{}个文件开始上传,文件名:{}",(i+1),files.length,fileName);

                //需要自定义文件名的情况
                //String suffix = files.getOriginalFilename().substring(files.getOriginalFilename().lastIndexOf("."));
                //String fileName = UUID.randomUUID() + suffix;
                //服务器端保存端文件对象
                File serverFile = new File(filePath  + fileName);
                //将上传的文件写入到服务器端的文件内
                files[i].transferTo(serverFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return new ResponseResultUtil().success();
    }

    @Override
    public ResponseResult readFile(File fileDir) throws Exception {
        log.info("文件读取目录:{}",fileDir);

        try {
            File[] files = fileDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {

                    log.info("第{}/{}个文件开始读取,文件名:{}",(i+1),files.length,files[i].getName());

                    if (ExcelImportUtils.isExcelFileValidata(files[i]) == true) {
                        this.readExcelFile(files[i]);
                    } else {
                        log.error("文件格式有问题，请仔细检查");
                    }

                }
                if (files[i].isDirectory()) {
                    //Nothing to do
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return new ResponseResultUtil().success();
    }

    private void readExcelFile(File file) throws Exception {

        InputStream inputStream = null;//初始化输入流
        Workbook workbook = null;//根据版本选择创建Workbook的方式

        try {
            inputStream = new FileInputStream(file);
            if (ExcelImportUtils.isExcel2003(file.toString())) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (ExcelImportUtils.isExcel2007(file.toString())) {
                workbook = new HSSFWorkbook(inputStream);
            }

            this.readExcelFileSheets(workbook);//读Excel中所有的sheet
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception();
        } finally{
            if(inputStream != null)
            {
                try{
                    inputStream.close();
                }catch(IOException e){
                    inputStream = null;
                    e.printStackTrace();
                }
            }
        }

    }

    private void readExcelFileSheets(Workbook workbook) {

        int numSheets = workbook.getNumberOfSheets();
        for(int m = 0; m < numSheets; m++) {//循环sheet
            Sheet sheet = workbook.getSheetAt(m);//得到第一个sheet
            if(sheet == null){
                log.error("第{}/{}个sheet有问题，请仔细检查。sheet名：{}",(m+1),numSheets,sheet.getSheetName());
                continue;
            }
            log.info("第{}/{}个sheet开始读取,sheet名:{}",(m+1),numSheets,sheet.getSheetName());
            this.readExcelFileSheet(sheet);//读一个sheet内容
        }
    }

    private String readExcelFileSheet(Sheet sheet) {
        List<String> titleList = new ArrayList<>();
        List<Data> dataList = new ArrayList<>();
        Map<String, String> keyValue = new HashMap<>();

        String errorMsg;//错误信息接收器
        String br = "<br/>";

        String tableName = "Data";
        String dataStyle = this.checkSheetDataStyle(sheet);
        if (dataStyle.equals("Data1")) {//T.B.D. dummy

        } else if (dataStyle.equals("Data2")) {//T.B.D. dummy

        }

        //读取标题行
        String titleErrMsg = this.readExcelFileSheetTitleRow(sheet, titleList);
        //读取内容(从第二行开始)
        String dataErrMsg = this.readExcelFileSheetDataRow(sheet, titleList, dataList);

        //sheet里全部验证通过才导入到数据库
        if(StringUtils.isEmpty(titleErrMsg) && StringUtils.isEmpty(dataErrMsg)){
            for(Data data : dataList){
                this.saveDataToSQL(tableName, data);
            }
            log.info("导入成功，共{}条数据！",dataList.size());
            errorMsg = "导入成功，共" + dataList.size() + "条数据！";
        } else {
            errorMsg = titleErrMsg + br + dataErrMsg;
        }
        return errorMsg;
    }

    private String readExcelFileSheetTitleRow(Sheet sheet, List<String> titleList) {
        String errorMsg = "";//错误信息接收器
        String rowMessage = "";//行错误信息接收器
        String br = "<br/>";

        List<String> valueList = new ArrayList<>();

        Row titleRow = sheet.getRow(0);//标题行
        int numTitleCells = titleRow.getLastCellNum();//标题行的列数
        for (int n = 0; n <numTitleCells; n++) {//循环列
            Cell cell = titleRow.getCell(n);
            if (null == cell) {
                errorMsg += br + "第1行";
                errorMsg += br + "第" + (n + 1) + "列标题有问题，请仔细检查；";
                log.error("第{}/{}列标题有问题，请仔细检查。",(n+1),numTitleCells);
            }

            String cellValue = cell.getStringCellValue();
            if (StringUtils.isEmpty(cellValue)) {
                rowMessage += "第" + (n + 1) + "列标题为空；";
                log.error("第{}/{}列标题为空。",(n+1),numTitleCells);
            }
            valueList.add(cellValue);
        }

        //拼接每行的错误提示
        if (!StringUtils.isEmpty(rowMessage)) {
            errorMsg += br + "标题行，" + rowMessage;
        } else {
            titleList.addAll(valueList);
        }

        return errorMsg;
    }

    private String readExcelFileSheetDataRow(Sheet sheet, List<String> titleList, List<Data> dataList) {
        String errorMsg = "";//错误信息接收器
        String rowMessage = "";//行错误信息接收器
        String br = "<br/>";

        Map<String, String> keyValue = new HashMap<>();

        //int numRows = sheet.getPhysicalNumberOfRows();//得到Excel的行数,不包括那些空行（隔行）的情况。
        int numRows = sheet.getLastRowNum();//获取的是最后一行的编号（编号从0开始）。
        int numTitleCells = sheet.getRow(0).getLastCellNum();//标题行的列数

        for (int p = 1; p <= numRows; p++) {//循环Excel行数,从第二行开始。标题不入库
            Row row = sheet.getRow(p);
            if (row == null) {
                errorMsg += br+ "第" + (p + 1) + "行数据有问题，请仔细检查！";
                log.error("第{}/{}行数据有问题，请仔细检查。",(p+1),numRows);
                continue;
            }

            for(int q = 0; q <numTitleCells; q++) {//循环列
                Cell cell = row.getCell(q);
                if (null == cell) {
                    errorMsg += br+ "第" + (p + 1) + "行";
                    errorMsg += br+ "第" + (q + 1) + "列数据有问题，请仔细检查；";
                    log.error("第{}行,第{}/{}列数据有问题，请仔细检查。",(p+1),(q+1),numTitleCells);
                }

                String cellValue = cell.getStringCellValue();
                keyValue.put(titleList.get(q), cellValue);
            }
            Data data = new Data(keyValue);

            //拼接每行的错误提示
            if(!StringUtils.isEmpty(rowMessage)){
                errorMsg += br + "第" + (p+1) + "行，" + rowMessage;
            }else{
                dataList.add(data);
            }
        }

        return errorMsg;
    }

    private String checkSheetDataStyle(Sheet sheet) {
        int numTitleCells = sheet.getRow(0).getLastCellNum();//标题行的列数

        // 多表格的情况下，需要区分将数据导入哪个数据表里。
        // T.B.D. dummy
        return "";
    }

    private void saveDataToSQL(String tableName, Data data) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        DataService dataService = (DataService) context.getBean("dataService");
        dataService.addData(tableName, data);
    }
}
