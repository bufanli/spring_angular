package com.example.eurasia.service.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    // 预览或者正式上传（true 为预览）
    private boolean isPreview = true;

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
     * @param file     @param file csv文件，csv文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportCSV(File file, List<String> dataList) {
        boolean isSuccess = false;

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {

            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out, "UTF-8");
            bw = new BufferedWriter(osw);
            if (dataList != null && !dataList.isEmpty()) {
                for (String data : dataList) {
                    bw.append(data).append("\r");
                }
            }
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

}
