package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

//@Slf4j
@Controller
@RequestMapping("api")
public class ExcelReportConditionsController {

    @RequestMapping(value="/getExcelReportConditions", method = RequestMethod.POST)
    public @ResponseBody ResponseResult getExcelReportCondition(HttpServletRequest request) {
        QueryCondition qcs[] = new QueryCondition[3];
        QueryCondition qc1 = new QueryCondition();
        qc1.setKey("商品编码");
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
}
