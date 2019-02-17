package com.example.eurasia.service.Data;


import org.apache.poi.openxml4j.opc.OPCPackage;

import java.io.*;

public class ImportExcelByEventMode {

    public String readExcelFile(File file) throws Exception {

        StringBuffer msg = new StringBuffer();//信息接收器
        InputStream inputStream = null;//初始化输入流

        try {
            inputStream = new FileInputStream(file);
            if (!inputStream.markSupported()) {
                inputStream = new PushbackInputStream(inputStream, 8);
            }

            ImportExcelRowReader reader = new ImportExcelRowReader();
            if (ImportExcelUtils.isExcel2003(file.toString())) {
                Excel2003Reader excel03 = new Excel2003Reader();
                excel03.setRowReader(reader);
                excel03.process(inputStream);
            } else if (ImportExcelUtils.isExcel2007(file.toString())) {
                OPCPackage pag = OPCPackage.open(inputStream);
                Excel2007Reader excel07 = new Excel2007Reader(pag, -1, reader);
                excel07.process();
                pag.close();
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
