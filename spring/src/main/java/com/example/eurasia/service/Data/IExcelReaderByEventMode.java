package com.example.eurasia.service.Data;

import java.io.InputStream;

/**
 * @ClassName: IExcelReaderByEventMode
 * @Description: TODO
 * @Author xiaohuai
 * @Date 2019-05-23 21:36
 * @Version 1.0
 */
public interface IExcelReaderByEventMode {
    StringBuffer getMessage();
    void processAllSheets(InputStream inputStream) throws Exception;
}
