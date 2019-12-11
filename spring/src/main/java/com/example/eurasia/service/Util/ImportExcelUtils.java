package com.example.eurasia.service.Util;

import com.example.eurasia.service.Data.DataService;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Thread.sleep;
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
     * 获取classpath下面的子目录
     * @param childFolder
     * @return
     */
    public static File getClassChildFolder(String childFolder) throws FileNotFoundException {
        //获取跟目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if(!path.exists()) {
            path = new File("");
        }
        //上传目录地址
        //在开发测试模式时，得到的地址为：{项目跟目录}/target/static/uploadFile/
        //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/uploadFile/
        File dir = new File(path.getAbsolutePath(),childFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
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

    public static int writeCellToExcel(Workbook wb, Sheet sheet, String cellValue, int rowIndex, int colIndex, short fontSize, boolean isBold) {

        // 设置字体
        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        dataFont.setBold(isBold);
        dataFont.setFontHeightInPoints(fontSize);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = (XSSFCellStyle)wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataStyle.setFont(dataFont);
        //ImportExcelUtils.setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));


        Row dataRow = sheet.createRow(rowIndex);
        // dataRow.setHeightInPoints(25);

        Cell cell = dataRow.createCell(colIndex);
        cell.setCellValue(cellValue);
        cell.setCellStyle(dataStyle);

        return 1;
    }

    public static int writeTitlesToExcel(Workbook wb, Sheet sheet, Set<String> colsNameSet, int rowStartIndex) {
        int rowIndex = rowStartIndex;
        int colIndex = 0;

        // 设置字体
        Font titleFont = wb.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.WHITE.index);

        XSSFCellStyle titleStyle = (XSSFCellStyle)wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(135, 206, 250)));// LightSkyBlue
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        ImportExcelUtils.setBorder(titleStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

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

    public static int writeRowsToExcel(Workbook wb, Sheet sheet, List<String[]> rowList, int rowStartIndex) {
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
        ImportExcelUtils.setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        for (String[] rowData : rowList) {
            Row dataRow = sheet.createRow(rowIndex);
            // dataRow.setHeightInPoints(25);

            for (int colIndex=0; colIndex<rowData.length; colIndex++) {
                Cell cell = dataRow.createCell(colIndex);
                cell.setCellValue(rowData[colIndex]);
                cell.setCellStyle(dataStyle);
            }
            rowIndex++;
        }
        return rowIndex;
    }

    // 自适应宽度(中文支持)
    public static void setSizeColumn(Sheet sheet, int columnNumber) {
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
                Row currentRow;
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
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF8"));
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    //浏览器下载excel
    public static void buildExcelDocument(String filename, XSSFWorkbook wb , HttpServletResponse response) throws Exception{
        //String filename = StringUtils.encodeFilename(StringUtils.trim(filename), request);//处理中文文件名
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF8"));
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    //写入临时文件
    public static void buildTempExcelDocument(String filename, XSSFWorkbook wb) throws Exception{
        FileOutputStream outputStream = new FileOutputStream(filename);
        FileChannel fileChannel = outputStream.getChannel();
        FileLock fileLock = null;
        while(true) {
            try {
                fileLock = fileChannel.tryLock();
                break;
            } catch (Exception e) {
                Slf4jLogUtil.get().info("有其他线程正在操作该文件，当前线程休眠1000毫秒");
                sleep(1000);
            }
        }
        wb.write(outputStream);
        if (fileChannel.isOpen()) {
            fileLock.release();
        }
        outputStream.flush();
        outputStream.close();
    }

    public static void copyCellStyle(XSSFCellStyle fromStyle, XSSFCellStyle toStyle) {
        toStyle.cloneStyleFrom(fromStyle);//此一行代码搞定
    }

    public static void mergeSheetAllRegion(XSSFSheet fromSheet, XSSFSheet toSheet) {//合并单元格
        int num = fromSheet.getNumMergedRegions();
        CellRangeAddress cellR = null;
        for (int i = 0; i < num; i++) {
            cellR = fromSheet.getMergedRegion(i);
            toSheet.addMergedRegion(cellR);
        }
    }

    public static void copyCell(XSSFWorkbook wb, XSSFCell fromCell, XSSFCell toCell) {
        XSSFCellStyle newstyle=wb.createCellStyle();
        copyCellStyle(fromCell.getCellStyle(), newstyle);
        //toCell.setEncoding(fromCell.getEncoding());
        //样式
        toCell.setCellStyle(newstyle);
        if (fromCell.getCellComment() != null) {
            toCell.setCellComment(fromCell.getCellComment());
        }
        // 不同数据类型处理
        switch (fromCell.getCellTypeEnum()) {
            case NUMERIC: // 数字
                if (HSSFDateUtil.isCellDateFormatted(fromCell)) {
                    toCell.setCellValue(fromCell.getDateCellValue());
                } else {
                    toCell.setCellValue(fromCell.getNumericCellValue());
                }
                break;
            case STRING: // 字符串
                toCell.setCellValue(fromCell.getRichStringCellValue());
                break;
            case BOOLEAN: // Boolean
                toCell.setCellValue(fromCell.getBooleanCellValue());
                break;
            case FORMULA: // 公式
                toCell.setCellFormula(fromCell.getCellFormula());
                break;
            case BLANK: // 空值
                // nothing21
                break;
            case ERROR: // 故障
                toCell.setCellErrorValue(fromCell.getErrorCellValue());
                break;
            default:
                // nothing29
                break;
        }
        /*
        int fromCellType = fromCell.getCellType();
        toCell.setCellType(fromCellType);
        if (fromCellType == XSSFCell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(fromCell)) {
                toCell.setCellValue(fromCell.getDateCellValue());
            } else {
                toCell.setCellValue(fromCell.getNumericCellValue());
            }
        } else if (fromCellType == XSSFCell.CELL_TYPE_STRING) {
            toCell.setCellValue(fromCell.getRichStringCellValue());
        } else if (fromCellType == XSSFCell.CELL_TYPE_BLANK) {
            // nothing21
        } else if (fromCellType == XSSFCell.CELL_TYPE_BOOLEAN) {
            toCell.setCellValue(fromCell.getBooleanCellValue());
        } else if (fromCellType == XSSFCell.CELL_TYPE_ERROR) {
            toCell.setCellErrorValue(fromCell.getErrorCellValue());
        } else if (fromCellType == XSSFCell.CELL_TYPE_FORMULA) {
            toCell.setCellFormula(fromCell.getCellFormula());
        } else { // nothing29
        }*/

    }

    public static void copyRow(XSSFWorkbook wb,XSSFRow oldRow,XSSFRow toRow){
        toRow.setHeight(oldRow.getHeight());
        for (Iterator cellIt = oldRow.cellIterator(); cellIt.hasNext();) {
            XSSFCell tmpCell = (XSSFCell) cellIt.next();
            XSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb,tmpCell, newCell);
        }
    }
    public static void copySheet(XSSFWorkbook wb,XSSFSheet fromSheet, XSSFSheet toSheet) {
        mergeSheetAllRegion(fromSheet, toSheet);
        //设置列宽
        for(int i=0;i<=fromSheet.getRow(fromSheet.getFirstRowNum()).getLastCellNum();i++){
            toSheet.setColumnWidth(i,fromSheet.getColumnWidth(i));
        }
        for (Iterator rowIt = fromSheet.rowIterator(); rowIt.hasNext();) {
            XSSFRow oldRow = (XSSFRow) rowIt.next();
            XSSFRow newRow = toSheet.createRow(oldRow.getRowNum());
            copyRow(wb,oldRow,newRow);
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

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Slf4jLogUtil.get().info("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Slf4jLogUtil.get().info("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                Slf4jLogUtil.get().info("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            Slf4jLogUtil.get().info("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Slf4jLogUtil.get().info("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = ImportExcelUtils.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = ImportExcelUtils.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            Slf4jLogUtil.get().info("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Slf4jLogUtil.get().info("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    /*
     * 绘制折线图
     * @param params params.get(0) 为图表位置 0123为起始横坐标，起始纵坐标，终点横坐标，终点纵坐标
     *
     * @param params params.get(1) 为x轴取值范围，其他为折线取值范围。起始行号，终止行号，起始列号，终止列号（折线名称坐标）。
     */
    public static void drawLineChart(XSSFSheet sheet, List<int[]> params) {

        Drawing drawing = sheet.createDrawingPatriarch();
        // 设置位置 起始横坐标，起始纵坐标，终点横坐标，终点纵坐标
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0,
                params.get(0)[0], params.get(0)[1], params.get(0)[2], params.get(0)[3]);
        Chart chart = drawing.createChart(anchor);
        // 创建图形注释的位置
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.BOTTOM);
        // 创建绘图的类型 LineChartData 折线图
        LineChartData chartData = chart.getChartDataFactory().createLineChartData();
        // 设置横坐标
        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        // 设置纵坐标
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        // 从Excel中获取数据.CellRangeAddress-->params: 起始行号，终止行号， 起始列号，终止列号
        // 数据类别（x轴）
        ChartDataSource xAxis = DataSources.fromStringCellRange(sheet,
                new CellRangeAddress(params.get(1)[0], params.get(1)[1], params.get(1)[2], params.get(1)[3]));
        for (int i = 2, len = params.size(); i < len; i++) {
            // 数据源
            ChartDataSource dataAxis = DataSources.fromNumericCellRange(sheet,
                    new CellRangeAddress(params.get(i)[0], params.get(i)[1], params.get(i)[2], params.get(i)[3]));
            LineChartSeries chartSeries = chartData.addSeries(xAxis, dataAxis);
            // 给每条折线创建名字
            chartSeries.setTitle(sheet.getRow(params.get(i)[4]).getCell(params.get(i)[5]).getStringCellValue());
        }
        // 图标标题
        setChartTitle((XSSFChart) chart, sheet.getSheetName(),123450);
        // 开始绘制折线图
        chart.plot(chartData, bottomAxis, leftAxis);
    }

    /*
     * 绘制折线图
     * ids -- category axis id, value axix id titleid
     */
    public static void drawCTLineChart(XSSFSheet sheet, int[] position,
                                       List<String> xString,
                                       Set<String> serTxName,
                                       List<String> dataRef,
                                        List<Integer>ids) {

    Drawing drawing = sheet.createDrawingPatriarch();
    ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, position[0], position[1], position[2], position[3]);

    Chart chart = drawing.createChart(anchor);

    CTChart ctChart = ((XSSFChart) chart).getCTChart();
    CTPlotArea ctPlotArea = ctChart.getPlotArea();

    CTLineChart ctLineChart = ctPlotArea.addNewLineChart();
    ctLineChart.addNewVaryColors().setVal(true);

    // telling the Chart that it has axis and giving them Ids
    setAxIds(ctLineChart,ids);
    // set cat axis
    setCatAx(ctPlotArea, STAxPos.B,ids);
    // set val axis
    setValAx(ctPlotArea, STAxPos.L,ids);
    // legend
    setLegend(ctChart);
    // set data label
//    setDataLabel(ctLineChart);
    // set chart title
    setChartTitle((XSSFChart) chart, sheet.getSheetName(),ids.get(2));

    paddingData(ctLineChart, xString, serTxName, dataRef);
}

    /*
     * 绘制柱状图
     * @param position 图表坐标 起始行，起始列，终点行，重点列
     *
     * @param xString 横坐标
     *
     * @param serTxName 图形示例
     *
     * @param dataRef 柱状图数据范围 ： sheetName!$A$1:$A$12
     *
     * @param ids -- category axis id , value axis id, titleid
     */
    public static void drawBarChart(XSSFSheet sheet, int[] position,
                                    List<String> xString,
                                    Set<String> serTxName,
                                    List<String> dataRef,
                                    List<Integer> ids) {

        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, position[0], position[1], position[2], position[3]);

        Chart chart = drawing.createChart(anchor);

        CTChart ctChart = ((XSSFChart) chart).getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();
        CTBarChart ctBarChart = ctPlotArea.addNewBarChart();

        ctBarChart.addNewVaryColors().setVal(true);
        ctBarChart.addNewBarDir().setVal(STBarDir.COL);

        // telling the Chart that it has axis and giving them Ids
        setAxIds(ctBarChart,ids);
        // set cat axis
        setCatAx(ctPlotArea, STAxPos.B,ids);
        // set val axis
        setValAx(ctPlotArea, STAxPos.L,ids);
        // add legend and set legend position
        setLegend(ctChart);
        // set data label
        setDataLabel(ctBarChart);
        // set chart title
        setChartTitle((XSSFChart) chart, sheet.getSheetName(),ids.get(2));
        // padding data to chart
        paddingData(ctBarChart, xString, serTxName, dataRef);
    }

    /*
     * 绘制柱状折线图
     */
    public static void drawBarAndCTLineChart(XSSFSheet sheet, int[] position,
                                             int titleId,
                                             // line parameters
                                             List<String> xLineString,
                                             Set<String> serLineTxName,
                                             List<String> dataLineRef,
                                             List<Integer> lineAxisIds,
                                             // bar parameters
                                             List<String> xBarString,
                                             Set<String> serBarTxName,
                                             List<String> dataBarRef,
                                             List<Integer> barAxisIds
                                             ) {
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, position[0], position[1], position[2], position[3]);

        Chart chart = drawing.createChart(anchor);

        CTChart ctChart = ((XSSFChart) chart).getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();

        // Bar----------
        CTBarChart ctBarChart = ctPlotArea.addNewBarChart();
        ctBarChart.addNewVaryColors().setVal(true);
        ctBarChart.addNewBarDir().setVal(STBarDir.COL);
        // padding data
        paddingData(ctBarChart, xBarString, serBarTxName, dataBarRef);
        // telling the Chart that it has axis and giving them Ids
        setAxIds(ctBarChart,barAxisIds);
        // CTLine----------
        CTLineChart ctLineChart = ctPlotArea.addNewLineChart();
        ctLineChart.addNewVaryColors().setVal(true);
        // padding data
        paddingData(ctLineChart, xLineString, serLineTxName, dataLineRef);
        // telling the Chart that it has axis and giving them Ids
        setAxIds(ctLineChart,lineAxisIds);
        // common of line and bar
         // set cat axis
        setCatAx(ctPlotArea, STAxPos.B,barAxisIds);
        // set val axis
        setValAx(ctPlotArea, STAxPos.L,barAxisIds);
        // set cat axis
        setCatAx(ctPlotArea, STAxPos.B,lineAxisIds);
        // set val axis
        setValAx(ctPlotArea, STAxPos.R,lineAxisIds);
        // legend
        setLegend(ctChart);
        // set title
       setChartTitle((XSSFChart) chart, sheet.getSheetName(),titleId);
    }

    private static void paddingData(CTBarChart ctBarChart, List<String> xString, Set<String> serTxName,
                                    List<String> dataRef) {
        Iterator<String> iterator = serTxName.iterator();
        for (int r = 0, len = dataRef.size(); r < len && iterator.hasNext(); r++) {
            CTBarSer ctBarSer = ctBarChart.addNewSer();

            ctBarSer.addNewIdx().setVal(r);
            // set legend value
            setLegend(iterator.next(), ctBarSer.addNewTx());
            // cat ax value
            setChartCatAxLabel(ctBarSer.addNewCat(), xString);
            // value range
            ctBarSer.addNewVal().addNewNumRef().setF(dataRef.get(r));
            // add border to chart
            ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 0, 0, 0 });
        }
    }

    private static void setLegend(String str, CTSerTx ctSerTx) {
        if (str.contains("$"))
            // set legend by str ref
            ctSerTx.addNewStrRef().setF(str);
        else
            // set legend by str
            ctSerTx.setV(str);
    }

    private static void paddingData(CTLineChart ctLineChart, List<String> xString, Set<String> serTxName,
                                    List<String> dataRef) {
        Iterator<String> iterator = serTxName.iterator();
        for (int r = 0, len = dataRef.size(); r < len && iterator.hasNext(); r++) {
            CTLineSer ctLineSer = ctLineChart.addNewSer();
            ctLineSer.addNewIdx().setVal(r);
            setLegend(iterator.next(), ctLineSer.addNewTx());
            setChartCatAxLabel(ctLineSer.addNewCat(), xString);
            ctLineSer.addNewVal().addNewNumRef().setF(dataRef.get(r));
            ctLineSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 0, 0, 0 });
        }
    }

    private static void setChartCatAxLabel(CTAxDataSource cttAxDataSource, List<String> xString) {
        if (xString.size() == 1) {
            cttAxDataSource.addNewStrRef().setF(xString.get(0));
        } else {
            CTStrData ctStrData = cttAxDataSource.addNewStrLit();
            for (int m = 0, xLen = xString.size(); m < xLen; m++) {
                CTStrVal ctStrVal = ctStrData.addNewPt();
                ctStrVal.setIdx((long) m);
                ctStrVal.setV(xString.get(m));
            }
        }
    }

    private static void setDataLabel(CTBarChart ctBarChart) {
        setDLShowOpts(ctBarChart.addNewDLbls());
    }

    private static void setDataLabel(CTLineChart ctLineChart) {
        CTDLbls dlbls = ctLineChart.addNewDLbls();
        setDLShowOpts(dlbls);
        setDLPosition(dlbls, null);
    }

    private static void setDLPosition(CTDLbls dlbls, STDLblPos.Enum e) {
        if (e == null)
            dlbls.addNewDLblPos().setVal(STDLblPos.T);
        else
            dlbls.addNewDLblPos().setVal(e);
    }

    private static void setDLShowOpts(CTDLbls dlbls) {
        // 添加图形示例的字符串
        dlbls.addNewShowSerName().setVal(false);
        // 添加x轴的坐标字符串
        dlbls.addNewShowCatName().setVal(false);
        // 添加图形示例的图片
        dlbls.addNewShowLegendKey().setVal(false);
        // 添加x对应y的值---全设置成false 就没什么用处了
        // dlbls.addNewShowVal().setVal(false);
    }

    private static void setAxIds(CTBarChart ctBarChart,List<Integer> ids) {
        ctBarChart.addNewAxId().setVal(ids.get(0));
        ctBarChart.addNewAxId().setVal(ids.get(1));
    }

    private static void setAxIds(CTLineChart ctLineChart,List<Integer> ids) {
        ctLineChart.addNewAxId().setVal(ids.get(0));
        ctLineChart.addNewAxId().setVal(ids.get(1));
    }

    private static void setLegend(CTChart ctChart) {
        CTLegend ctLegend = ctChart.addNewLegend();
        ctLegend.addNewLegendPos().setVal(STLegendPos.B);
        ctLegend.addNewOverlay().setVal(false);
    }

    private static void setCatAx(CTPlotArea ctPlotArea, STAxPos.Enum anEnum,List<Integer> ids) {
        CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
        ctCatAx.addNewAxId().setVal(ids.get(0)); // id of the cat axis
        CTScaling ctScaling = ctCatAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        ctCatAx.addNewDelete().setVal(false);
        ctCatAx.addNewAxPos().setVal(anEnum);
        ctCatAx.addNewCrossAx().setVal(ids.get(1)); // id of the val axis
        ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
    }

    // 不要y轴的标签，或者y轴尽可能的窄一些
    private static void setValAx(CTPlotArea ctPlotArea, STAxPos.Enum anEnum,List<Integer>ids) {
        CTValAx ctValAx = ctPlotArea.addNewValAx();
        ctValAx.addNewAxId().setVal(ids.get(1)); // id of the val axis
        CTScaling ctScaling = ctValAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        // 不现实y轴
        ctValAx.addNewDelete().setVal(false);
        ctValAx.addNewAxPos().setVal(anEnum);
        ctValAx.addNewCrossAx().setVal(ids.get(0)); // id of the cat axis
        ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
    }

    // 图标标题
    private static void setChartTitle(XSSFChart xChart, String titleStr,int titleId) {
        CTChart ctChart = xChart.getCTChart();

        CTTitle title = CTTitle.Factory.newInstance();
        CTTx cttx = title.addNewTx();
        CTStrData sd = CTStrData.Factory.newInstance();
        CTStrVal str = sd.addNewPt();
        str.setIdx(titleId);
        str.setV(titleStr);
        cttx.addNewStrRef().setStrCache(sd);

        ctChart.setTitle(title);
    }

}
