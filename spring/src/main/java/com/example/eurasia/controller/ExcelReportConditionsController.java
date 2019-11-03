package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.ExcelReportSettingData;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Data.IExcelReportConditionsService;
import com.example.eurasia.service.Data.IGetQueryConditionsService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Slf4j
@Controller
@RequestMapping("api")
public class ExcelReportConditionsController {

    //注入Service服务对象
    //注入Service服务对象
    @Qualifier("GetQueryConditionsServiceImpl")
    @Autowired
    private IGetQueryConditionsService getQueryConditionsService;
    @Qualifier("ExcelReportConditionsServiceImpl")
    @Autowired
    private IExcelReportConditionsService excelReportConditions;
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private UserInfoServiceImpl userInfoServiceImpl;

    /**
     * @author
     * @param
     * @date 2019-10-19
     * @description 取得Excel报表的过滤条件(备注：目前在页面上固定为"商品编码","月份","进出口")
     */
    @RequestMapping(value="/getExcelReportConditions", method = RequestMethod.POST)
    public @ResponseBody ResponseResult getExcelReportCondition(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("取得excel报表查询条件开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                // 取得可显示的查询条件
                responseResult = getQueryConditionsService.getQueryConditionDisplay(userID);
                QueryCondition[] queryConditions = (QueryCondition[])responseResult.getData();

                // 在可显示的查询条件中，添加"月份"查询条件(T.B.D 作为List类型，需要页面再调用getListValueWithPagination ???)
                responseResult = excelReportConditions.getExcelReportCondition(userID, queryConditions);

            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        Slf4jLogUtil.get().info("取得excel报表查询条件结束");
        return responseResult;
    }

    /**
     * @author
     * @param
     * @date 2019-10-19
     * @description 取得Excel报表的汇总类型
     *              (备注：1.页面上不应该有"美元总价","法定重量","平均单价",因为是报告表格里用到的数据.
     *              2.明细表,固定.)
     */
    @RequestMapping(value="/getExcelReportTypes", method = RequestMethod.POST)
    public @ResponseBody ResponseResult getExcelReportTypes(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("取得excel报表类型开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = excelReportConditions.getExcelReportTypes();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        Slf4jLogUtil.get().info("取得excel报表类型结束");
        return responseResult;
    }

    /**
     * @author
     * @param
     * @date 2019-10-19
     * @description 点击导出汇总报告按钮，导出汇总报告
     */
    @RequestMapping(value="/exportExcelReport", method = RequestMethod.POST)
    public @ResponseBody ResponseResult exportExcelReport(@RequestBody ExcelReportSettingData data,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("导出汇总报告开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = excelReportConditions.exportExcelReport(userID, data, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        Slf4jLogUtil.get().info("导出汇总报告结束");
        return responseResult;
    }
}
