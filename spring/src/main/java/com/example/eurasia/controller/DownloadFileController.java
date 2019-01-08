package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Data.IDownloadFileService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserInfoServiceImpl;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Slf4j
@Controller
public class DownloadFileController {

    //注入Service服务对象
    @Qualifier("DownloadFileServiceImpl")
    @Autowired
    private IDownloadFileService downloadFileService;
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private UserInfoServiceImpl userInfoServiceImpl;

    /**
     * @author
     * @date
     * @description 导出数据到文件
     */
    @RequestMapping(value="/downloadFile", method = RequestMethod.POST)
    public void downloadFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody QueryCondition[] queryConditionsArr) throws IOException {
        ResponseResult responseResult;
        //导出excel
        try {
            Slf4jLogUtil.get().info("进行excel文件导出开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                /**
                 * 导出excel比较重要的api有以下几个。
                 *  创建一个excel文件工作薄；（HSSFWorkbook workbook = new HSSFWorkbook()）
                 *  创建一张表；HSSFSheet sheet = workbook.createSheet("统计表")
                 *  创建一行；HSSFRow row = sheet.createRow(0)
                 *  填充一列数据; row.createCell(0).setCellValue("数据")
                 *  设置一个单元格样式；cell.setCellStyle(style)
                 */
                responseResult = downloadFileService.exportExcel(response,queryConditionsArr);
            }
            Slf4jLogUtil.get().info("进行excel文件导出结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }

    }
}
