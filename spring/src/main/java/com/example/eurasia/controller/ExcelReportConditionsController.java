package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.DataSearchParam;
import com.example.eurasia.entity.Data.ExcelReportSettingData;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

//@Slf4j
@Controller
@RequestMapping("api")
public class ExcelReportConditionsController {

    @RequestMapping(value="/getExcelReportConditions", method = RequestMethod.POST)
    public @ResponseBody ResponseResult getExcelReportCondition(HttpServletRequest request) {
        QueryCondition qcs[] = new QueryCondition[3];
        QueryCondition qc1 = new QueryCondition();
        qc1.setKey("海关编码");
        qc1.setType(QueryCondition.QUERY_CONDITION_TYPE_LIST);
        QueryCondition qc2 = new QueryCondition();
        qc2.setKey("月份");
        qc2.setType(QueryCondition.QUERY_CONDITION_TYPE_LIST);
         QueryCondition qc3 = new QueryCondition();
        qc3.setKey("进出口");
        qc3.setType(QueryCondition.QUERY_CONDITION_TYPE_LIST);
        qcs[0] = qc1;
        qcs[1] = qc2;
        qcs[2] = qc3;
       return new ResponseResult(200,"取得excel报表查询条件成功",qcs);
    }

    @RequestMapping(value="/getExcelReportTypes", method = RequestMethod.POST)
    public @ResponseBody ResponseResult getExcelReportTypes(HttpServletRequest request) {
      String [] excelReportTypes = new String[6];
      excelReportTypes[0] = "申报单位名称";
      excelReportTypes[1] = "货主单位名称";
      excelReportTypes[2] = "经营单位名称";
      excelReportTypes[3] = "经营单位代码";
      excelReportTypes[4] = "运输工具名称";
      excelReportTypes[5] = "提运单号";
      return new ResponseResult(200,"取得excel报表类型成功",excelReportTypes);
    }
    @RequestMapping(value="/exportExcelReport", method = RequestMethod.POST)
    public @ResponseBody ResponseResult exportExcelReport(@RequestBody ExcelReportSettingData data,
                                                          HttpServletResponse response) {
        try {
            String fileName = "C:\\Users\\Dell\\workspace\\spring_angular\\reference\\33061010_201907_进口_报告_10HS.xlsx";
            FileInputStream stream = new FileInputStream(fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(new File(fileName).getName(), "gbk"));
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len = stream.read(buffer)) != -1){
                outputStream.write(buffer,0,len);
            }
            outputStream.flush();
            outputStream.close();
        }catch (IOException exception){
            // nothing to do
        }
        String responseMsg = "excel report exporting successfully";
        return new ResponseResultUtil().success(ResponseCodeEnum.EXPORT_DATA_INFO_SUCCESS,responseMsg);
    }
}
