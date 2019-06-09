package com.example.eurasia.service.Data;


import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class ImportExcelByEventMode {
    @Autowired
    private Excel2003Reader excel2003Reader;
    @Autowired
    private Excel2007Reader excel2007Reader;
    public String readExcelFile(File file) throws Exception {
        Slf4jLogUtil.get().info("EventModel读取文件:" + file.getName());

        StringBuffer msg = new StringBuffer();//信息接收器
        InputStream inputStream = null;//初始化输入流

        try {
            inputStream = new FileInputStream(file);
            if (!inputStream.markSupported()) {
                inputStream = new PushbackInputStream(inputStream, 8);
            }

            if (ImportExcelUtils.isExcel2003(file.toString())) {
                excel2003Reader.clearMessage();
                excel2003Reader.processAllSheets(inputStream);
                msg = excel2003Reader.getMessage();

            } else if (ImportExcelUtils.isExcel2007(file.toString())) {
                excel2007Reader.clearMessage();
                excel2007Reader.processAllSheets(inputStream);
                msg = excel2007Reader.getMessage();
            }

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
        return msg.toString();
    }
}
