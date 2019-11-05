package com.example.eurasia.service.Util;

import com.example.eurasia.service.Data.DataService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.apache.poi.ss.usermodel.CellType.STRING;

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
    public static boolean isExcelFileValidata(File file) {
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
        //System.out.println("Elements : " + elementsList);
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

        //System.out.println("Duplicate Elements : " + duplicateElements);
        return duplicateElements;
    }

    /**
     * 复制文件
     * @param s 源文件
     * @param t 复制到的新文件
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-23 00:00:00
     */
    public static void fileChannelCopy(File s, File t) {
        try {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(s),1024);
                out = new BufferedOutputStream(new FileOutputStream(t),1024);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer,0,len);
                }
            } finally {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载成功后删除
     * @param files
     * @author FuJia
     * @Time 2019-10-23 00:00:00
     */
    public static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
    
    // 自适应宽度(中文支持)
    public static void setSizeColumn(SXSSFSheet sheet, int columnNumber) {
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

    public static void setBorder(XSSFCellStyle style, BorderStyle border, XSSFColor color) {
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
    public static void buildExcelFile(String filename, XSSFWorkbook workbook) throws Exception{
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.flush();
        fos.close();

        // flush()其实是继承于其父类OutputStream的。而OutputStream类的flush()却什么也没做
        // 当OutputStream是BufferedOutputStream时,flush()才有效.
    }

    //浏览器下载excel
    public static void buildExcelDocument(String filename, SXSSFWorkbook wb , HttpServletResponse response) throws Exception{
        //String filename = StringUtils.encodeFilename(StringUtils.trim(filename), request);//处理中文文件名
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "gbk"));
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
