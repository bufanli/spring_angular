package com.example.eurasia.service;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class ExcelImportUtils {

    /**
     * 是否是2003的excel
     * @param filePath
     * @return 返回true是2003
     */
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 是否是2007的excel
     * @param filePath
     * @return 返回true是2007
     */
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 验证EXCEL文件名
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath) {
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))){
            return false;
        }
        return true;
    }

    /**
     * 验证EXCEL文件
     * @param file
     * @return
     */
    public static boolean isExcelFileValidata(MultipartFile file) {
        //判断文件是否为空
        if (file == null) {
            //"文件为空";
            return false;
        }

        //获取文件名
        String fileName = file.getOriginalFilename();

        //验证文件名是否合格
        if(!ExcelImportUtils.validateExcel(fileName)){
            //"文件excel格式错误";
            return false;
        }

        //进一步判断文件内容是否为空（即判断其大小是否为0或其名称是否为null）
        long size = file.getSize();
        if(StringUtils.isEmpty(fileName) || size==0){
            //"文件为空";
            return false;
        }

        //"文件检查ok";
        return true;
    }
}
