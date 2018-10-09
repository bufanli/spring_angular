package com.example.eurasia.service;

import com.example.eurasia.entity.Data;
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

/*@Transactional(readOnly = true)事物注解*/
@Service("UploadFileServiceImpl")
@Component
public class UploadFileServiceImpl implements IUploadFileService {

    @Override
    public String batchUpload(String filePath, MultipartFile[] files) throws Exception {

        String fileName  = "";
        try {
            //遍历文件数组
            for (int i=0; i<files.length; i++) {
                if (ExcelImportUtils.isExcelFileValidata(files[i]) == true) {
                    //上传文件名
                    fileName = files[i].getOriginalFilename();
                    //需要自定义文件名的情况
                    //String suffix = files.getOriginalFilename().substring(files.getOriginalFilename().lastIndexOf("."));
                    //String fileName = UUID.randomUUID() + suffix;
                    //服务器端保存端文件对象
                    File serverFile = new File(filePath  + fileName);
                    //将上传的文件写入到服务器端的文件内
                    files[i].transferTo(serverFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return fileName + "上传失败";
        }
        return fileName + "上传成功";

    }

    @Override
    public String readExcelFile(File fileDir) throws Exception {

        List<String> filesArr = new ArrayList<>();
        InputStream inputStream = null;//初始化输入流
        Workbook workbook = null;//根据版本选择创建Workbook的方式

        File[] files = fileDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                filesArr.add(files[i].toString());

                try {
                    inputStream = new FileInputStream(files[i]);
                    if (ExcelImportUtils.isExcel2003(files[i].toString())) {
                        workbook = new XSSFWorkbook(inputStream);
                    } else if (ExcelImportUtils.isExcel2007(files[i].toString())) {
                        workbook = new HSSFWorkbook(inputStream);
                    }

                    this.readExcelFileSheets(workbook);//读Excel中所有的sheet
                }catch(Exception e){
                    e.printStackTrace();
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

            if (files[i].isDirectory()) {
                //Nothing to do
            }
        }

        return "";
    }

    private String readExcelFileSheets(Workbook workbook) {
        String errorMsg = "";//错误信息接收器
        String br = "<br/>";

        int numSheets = workbook.getNumberOfSheets();
        for(int m = 0; m < numSheets; m++) {//循环sheet
            Sheet sheet = workbook.getSheetAt(m);//得到第一个sheet
            if(sheet == null){
                errorMsg += "第" + (m + 1) + "个sheet有问题，请仔细检查；";
                continue;
            }

            errorMsg += br + this.readExcelFileSheet(sheet);//读一个sheet内容
        }
        return errorMsg;
    }

    private String readExcelFileSheet(Sheet sheet) {
        List<String> titleList = new ArrayList<>();
        List<Data> dataList = new ArrayList<>();
        Map<String, String> keyValue = new HashMap<>();

        String errorMsg = "";//错误信息接收器
        String br = "<br/>";

        String dataStyle = this.checkSheetDataStyle(sheet);
        if (dataStyle.equals("Data1")) {

        } else if (dataStyle.equals("Data2")) {

        }

        //读取标题行
        String titleErrMsg = this.readExcelFileSheetTitleRow(sheet, titleList);
        //读取内容(从第二行开始)
        String dataErrMsg = this.readExcelFileSheetDataRow(sheet, titleList, dataList);

        //sheet里全部验证通过才导入到数据库
        if(StringUtils.isEmpty(titleErrMsg) && StringUtils.isEmpty(dataErrMsg)){
            for(Data data : dataList){
                this.saveDataToSQL(data);
            }
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
            }

            String cellValue = cell.getStringCellValue();
            if (StringUtils.isEmpty(cellValue)) {
                rowMessage += "第" + (n + 1) + "列标题为空；";
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
                continue;
            }

            for(int q = 0; q <numTitleCells; q++) {//循环列
                Cell cell = row.getCell(q);
                if (null == cell) {
                    errorMsg += br+ "第" + (p + 1) + "行";
                    errorMsg += br+ "第" + (q + 1) + "列数据有问题，请仔细检查；";
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

        if (numTitleCells == 47) {
            return "Data1";
        } else if (numTitleCells == 35) {
            return "Data2";
        }
        return "";
    }

    private void saveDataToSQL(Data data) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        DataService dataService = (DataService) context.getBean("dataService");
        dataService.addData(data);
    }
}
