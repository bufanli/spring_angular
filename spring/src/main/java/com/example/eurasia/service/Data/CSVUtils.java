package com.example.eurasia.service.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CSVUtils {

    // 预览或者正式上传（true 为预览）
    private boolean isPreview = true;
    private final static String CSV_SEPERATOR = ",";
    private final static String CSV_NEW_LINE= "\r";

    public CSVUtils() {
    }

    public CSVUtils(boolean isPreview) {
        this.isPreview = isPreview;
    }

    /**
     * 导入CSV文件
     * @param file csv文件
     * @return
     */
    public static List<String> importCSV(File file) {

        String fileName = file.getName();
        //  判断文件类型 是excel 文件还是csc
        if (".csv".equals(fileName.toLowerCase().substring(fileName.toLowerCase().lastIndexOf('.')))) {

        } else {
            return null;
        }

        List<String> dataList = new ArrayList<String>();

        FileInputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;
        try {
            // InputStreamReader 是用来读取原始字节流，可指定编码格式
            // 而FileReader是读取字符流，使用系统默认的编码格式，当读取中文文件是易出现乱码问题。
            in = new FileInputStream(file);
            inr = new InputStreamReader(in, "UTF-8");
            br = new BufferedReader(inr);
            String line = "";
            while ((line = br.readLine()) != null) {
                //String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                //String last = item[item.length-1];//这就是你要的数据了
                //int value = Integer.parseInt(last);//如果是数值，可以转化为数值
                // resolve utf8 bom problem
                if(line.startsWith("\uFEFF")){
                   line = line.replace("\uFEFF","");
                }
                dataList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inr != null) {
                try {
                    inr.close();
                    inr = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataList;
    }

    /**
     * 导出CSV文件
     * @param file csv文件
     * @param headDataSet 标题数据
     * @param dataList 数据
     * @return
     */
    public static boolean exportCSV(File file, Set<String> headDataSet, List<String[]> dataList) {
        boolean isSuccess = false;

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out, "gbk");
            bw = new BufferedWriter(osw);
            //if (headDataSet != null && !headDataSet.isEmpty()) {
            // set header
            StringBuffer headerBuffer = new StringBuffer();
            for (String data : headDataSet) {
                headerBuffer.append(data).append(CSV_SEPERATOR);
            }
            String header = headerBuffer.toString();
            if(header.endsWith(CSV_SEPERATOR)){
                header = header.substring(0,header.length() - CSV_SEPERATOR.length());
            }
            bw.append(header).append(CSV_NEW_LINE);
            //}
            // set data list
            StringBuffer dataBuffer = null;
            //if (dataList != null && !dataList.isEmpty()) {
                for (String[] dataArr : dataList) {
                    dataBuffer = new StringBuffer();
                    // for one data entry
                    int index = 0;// skip first column(id)
                    for (String data : dataArr) {
                        if(index == 0){
                            index++;
                            continue;
                        }else {
                            dataBuffer.append(data).append(CSV_SEPERATOR);
                            index++;
                        }
                    }
                    // get rid of last CSV_SEPERATOR
                    String dataStr = dataBuffer.toString();
                    if(dataStr.endsWith(CSV_SEPERATOR)){
                        dataStr= dataStr.substring(0,dataStr.length() - CSV_SEPERATOR.length());
                    }
                    bw.append(dataStr).append(CSV_NEW_LINE);
                }
            //}
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSuccess;
    }

    /**
     * 写入csv结束，写出流
     * @return
     */
    public static void outCSVStream(HttpServletResponse response, File tempFile, String dictionaryName) throws IOException {
        InputStream input = new BufferedInputStream(new FileInputStream(tempFile.getCanonicalPath()));
        byte[] buffer = new byte[input.available()];
        input.read(buffer);
        input.close();
        // 清空response
        response.reset();
        // 设置response的Header
        String fileName = dictionaryName + ".csv";
        response.setContentType("application/csv");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream output = new BufferedOutputStream(response.getOutputStream());
        //output.write(new   byte []{( byte ) 0xEF ,( byte ) 0xBB ,( byte ) 0xBF });//为了保证excel打开csv不出现中文乱码
        output.write(buffer);
        output.flush();
    }

    /**
     * 删除单个文件
     *
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile( File file) {
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
