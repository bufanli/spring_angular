package com.example.eurasia.controller;

import com.example.eurasia.entity.QueryCondition;
import com.example.eurasia.service.Data.IDownloadFileService;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class DownloadFileController {

    //注入Service服务对象
    @Qualifier("DownloadFileServiceImpl")
    @Autowired
    private IDownloadFileService downloadFileService;

    /**
     * @author
     * @date
     * @description 导出数据到文件
     */
    @RequestMapping(value="/downloadFile", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult downloadFiles(HttpServletResponse response, @RequestBody QueryCondition[] queryConditionsArr) throws IOException {
        ResponseResult responseResult;
        //导出excel
        try {
            log.info("进行excel文件导出开始");
            responseResult = this.exportExcel(response,queryConditionsArr);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("进行excel文件导出结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-10-14
     * @description 导出excel
     */
    public ResponseResult exportExcel(HttpServletResponse response, QueryCondition[] queryConditionsArr) throws Exception {
        /**
         * 导出excel比较重要的api有以下几个。
         *  创建一个excel文件工作薄；（HSSFWorkbook workbook = new HSSFWorkbook()）
         *  创建一张表；HSSFSheet sheet = workbook.createSheet("统计表")
         *  创建一行；HSSFRow row = sheet.createRow(0)
         *  填充一列数据; row.createCell(0).setCellValue("数据")
         *  设置一个单元格样式；cell.setCellStyle(style)
         */

        return downloadFileService.exportExcel(response,queryConditionsArr);
    }

}
