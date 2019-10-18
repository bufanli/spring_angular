package com.example.eurasia.service.Data;

import org.springframework.util.StringUtils;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ImportExcelUtils {

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
    public static boolean isExcelFileValidate(File file) {
        //判断文件是否为空
        if (file == null) {
            //"文件为空";
            return false;
        }

        String fileName = file.getName();
        //进一步判断文件内容是否为空（即判断其大小是否为0,或其名称是否为null/""）
        long size = file.length();
        if(StringUtils.isEmpty(fileName) || size==0){
            //"文件为空";
            return false;
        }

        //验证文件名是否合格
        if(!ImportExcelUtils.validateExcel(fileName)){
            //"文件excel格式错误";
            return false;
        }

        //"文件检查ok";
        return true;
    }

    /**
     * 判断列名是否有重复
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public static Set<String> getSameTitle(List<String> elementsList) throws Exception {
        System.out.println("Elements : " + elementsList);
        Set<String> set = new LinkedHashSet<>();
        Set<String> duplicateElements = new LinkedHashSet<>();
        for (String element : elementsList) {
            if(!set.add(element)){//如果添加的元素重复的话将返回false
                duplicateElements.add(element);
            }
        }
/*
        //将list放入set中对其去重
        Set<String> set = new HashSet<>(elementsList);
        //获得list与set的差集
        Collection rs = CollectionUtils.disjunction(elementsList,set);
        //将collection转换为list
        List<String> list1 = new ArrayList<>(rs);
*/

        System.out.println("Duplicate Elements : " + duplicateElements);
        return duplicateElements;
    }
}
