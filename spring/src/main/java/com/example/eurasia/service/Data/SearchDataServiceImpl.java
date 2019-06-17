package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.*;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserService;
import com.example.eurasia.service.Util.DataProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("SearchDataServiceImpl")
@Component
public class SearchDataServiceImpl implements ISearchDataService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    //注入UserService服务对象
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Override
    public ResponseResult searchData(String userID, DataSearchParam dataSearchParam) throws Exception {

        SearchedData searchedData = null;
        QueryCondition[] queryConditionsArr = dataSearchParam.getQueryConditions();
        long userOffset = dataSearchParam.getOffset();
        long userLimit = dataSearchParam.getLimit();
        long userMax = -1;
        try {
            //检查查询条件的格式和内容
            String retCheck = this.checkQueryConditions(userID,queryConditionsArr);
            if (!StringUtils.isEmpty(retCheck)) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ERROR.getCode(),retCheck);
            }

            //为未输入的查询条件进行默认值设定
            this.setUserQueryConditionDefaultValue(userID,queryConditionsArr);

            //该用户可查询的条数
            userMax = this.getUserMax(userID);

            Map<String, String> order = new LinkedHashMap<>();
            order.put("id","asc");//T.B.D

            searchedData = dataService.searchData(DataService.TABLE_DATA,queryConditionsArr,userOffset,userLimit,order);

            if (searchedData.getDataList() == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_NULL);
            }
            if (searchedData.getDataList().size() < 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_ZERO);
            }

            if (userMax < searchedData.getCount()) {
                searchedData.setCount(userMax);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_SUCCESS, searchedData);
    }

    public ResponseResult statisticsReport(String userID, StatisticsReportQueryData statisticsReportQueryData) throws Exception {

        StatisticReportValue[] statisticReportValues = null;
        List<Data> dataList = null;
        String groupByField = statisticsReportQueryData.getGroupByField();
        ComputeField[] computeFields = statisticsReportQueryData.getComputeFields();
        QueryCondition[] queryConditionsArr = statisticsReportQueryData.getQueryConditions();

        long userMax = -1;
        try {
            //检查查询条件的格式和内容
            String retCheck = this.checkQueryConditions(userID,queryConditionsArr);
            if (!StringUtils.isEmpty(retCheck)) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_QUERY_CONDITION_ERROR.getCode(),retCheck);
            }

            //为未输入的查询条件进行默认值设定
            this.setUserQueryConditionDefaultValue(userID,queryConditionsArr);

            //该用户可查询的条数
            userMax = this.getUserMax(userID);

            Map<String, String> order = new LinkedHashMap<>();
            order.put("id","asc");//T.B.D

            dataList = dataService.searchDataForStatisticReport(DataService.TABLE_DATA,groupByField,computeFields,queryConditionsArr);

            if (dataList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_FROM_SQL_NULL);
            }
            if (dataList.size() <= 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_FROM_SQL_ZERO);
            }

            if (groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_YEAR) ||
                    groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_MONTH) ||
                    groupByField.equals(DataService.STATISTICS_REPORT_PRODUCT_DATE_QUARTER)) {
                //报告类型是周期(年,月,季度)的情况
                groupByField =DataService.STATISTICS_REPORT_PRODUCT_DATE;
            } else {

            }

            //sql结果List，转成StatisticReportValue
            statisticReportValues = new StatisticReportValue[dataList.size()];
            for (int i=0; i<dataList.size(); i++) {
                Data data = dataList.get(i);
                Map<String, String> keyValue = data.getKeyValue();

                String groupByValue = keyValue.get(groupByField);//e.g."申报单位的名称"的Value
                ComputeValue[] computeValues = new ComputeValue[computeFields.length];
                for (int j=0; j<computeFields.length; j++) {
                    computeValues[j] = new ComputeValue();
                    computeValues[j].setFieldName(computeFields[j].getFieldName());//e.g."件数"
                    computeValues[j].setComputeValue(keyValue.get(computeFields[j].toSql().toString()));//e.g."SUM(件数)"的Value
                }
                statisticReportValues[i] = new StatisticReportValue();
                statisticReportValues[i].setGroupByField(groupByValue);
                statisticReportValues[i].setComputeValues(computeValues);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.STATISTICS_REPORT_FROM_SQL_SUCCESS, statisticReportValues);
    }

    public ResponseResult getStatisticsSetting() throws Exception {
        StatisticsFields  statisticsFields = new StatisticsFields();
        List<Map<String, Object>> statisticsTypesList = null;
        List<Map<String, Object>> groupByFieldsList = null;
        List<Map<String, Object>> computeByFieldsList = null;
        try {

            statisticsTypesList = dataService.getStatisticsSetting(DataService.TABLE_STATISTICS_SETTING_TYPE,
                    new String[]{DataService.STATISTICS_SETTING_TYPE_COLUMN_NAME});
            if (statisticsTypesList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_SETTING_TYPE_FROM_SQL_NULL);
            }

            groupByFieldsList = dataService.getStatisticsSetting(DataService.TABLE_STATISTICS_SETTING_GROUP_BY,
                    new String[]{DataService.STATISTICS_SETTING_GROUP_BY_COLUMN_NAME});
            if (groupByFieldsList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_SETTING_GROUP_BY_FROM_SQL_NULL);
            }

            computeByFieldsList = dataService.getStatisticsSetting(DataService.TABLE_STATISTICS_SETTING_COMPUTE_BY,
                    new String[]{DataService.STATISTICS_SETTING_COMPUTE_BY_COLUMN_NAME});
            if (computeByFieldsList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_SETTING_COMPUTE_BY_FROM_SQL_NULL);
            }

            String[] statisticsTypes = DataProcessingUtil.getListMapValuesOfOneColumn(statisticsTypesList);
            String[] groupByFields = DataProcessingUtil.getListMapValuesOfOneColumn(groupByFieldsList);
            String[] groupBySubFields = {"年","月","季度"};//T.B.D
            String[] computeFields = DataProcessingUtil.getListMapValuesOfOneColumn(computeByFieldsList);
            statisticsFields.setStatisticsTypes(statisticsTypes);
            statisticsFields.setGroupByFields(groupByFields);
            statisticsFields.setGroupBySubFields(groupBySubFields);
            statisticsFields.setComputeFields(computeFields);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_SETTING_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.STATISTICS_SETTING_FROM_SQL_SUCCESS, statisticsFields);
    }

    private String checkQueryConditions(String userID, QueryCondition[] queryConditionsArr) throws Exception {
        StringBuffer ret = new StringBuffer();

        // 取得所有的查询条件(Data的Map-key是查询条件的key，Data的Map-value是查询条件的type)
        List<Data> allQueryConditionsList = dataService.getAllQueryConditions();
        if (allQueryConditionsList == null) {
            ret.append(ResponseCodeEnum.QUERY_CONDITION_TYPE_FROM_SQL_NULL.getMessage());
            ret.append(UserService.BR);
            return ret.toString();
        }
        if (allQueryConditionsList.size() != 1) {
            ret.append(ResponseCodeEnum.QUERY_CONDITION_TYPE_FROM_SQL_SIZE_ERROR.getMessage());
            ret.append(UserService.BR);
            return ret.toString();
        }

        Map<String, String> queryConditionsBase = allQueryConditionsList.get(0).getKeyValue();
        for (QueryCondition queryCondition : queryConditionsArr) {

            // 检查格式
            if (queryCondition.checkQueryCondition(queryConditionsBase) == false) {
                ret.append(queryCondition.getKey() + ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_FORMAT_ERROR.getMessage());
                ret.append(UserService.BR);
            }

            // 检查内容
            String queryConditionValueFromSql = null;
            switch (queryCondition.getKey()) {
                case UserService.MUST_PRODUCT_NUMBER:
                    //检查海关/商品编码是否在该用户都权限内
                    queryConditionValueFromSql = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                            UserService.MUST_PRODUCT_NUMBER,
                            userID);
                    if (!queryConditionValueFromSql.equals(QueryCondition.QUERY_CONDITION_SPLIT)) {
                        String productNameArr[] = queryCondition.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT);
                        for (String productName:productNameArr) {
                            if (queryConditionValueFromSql.contains(productName) == false) {
                                ret.append(productName + ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ACCESS_ERROR.getMessage());
                                ret.append(UserService.BR);
                            }
                        }
                    } else {//"～～"的场合，1.没有限制 2.没有输入

                    }
                    break;
                default:
                    break;
            }

        }
        if (ret.indexOf(UserService.BR) >= 0) {
            ret.delete((ret.length() - UserService.BR.length()),ret.length());
        }
        return ret.toString();
    }

    private void setUserQueryConditionDefaultValue(String userID, QueryCondition[] queryConditionsArr) throws Exception {

        String queryConditionValueFromSql = null;
        for (QueryCondition queryCondition : queryConditionsArr) {
            if(queryCondition.getKey().equals(UserService.MUST_PRODUCT_DATE)) {
                String dateArr[] = queryCondition.getQueryConditionToArr();
                if (dateArr[0].equals("") || dateArr[1].equals("")) {//为了在起始日期和结束日期都存在都情况下，不查询该用户的日期范围
                    queryConditionValueFromSql = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                            UserService.MUST_PRODUCT_DATE,
                            userID);
                    String dateArrFromSql[] = queryConditionValueFromSql.split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
                    if (dateArr[0].equals("")) {
                        dateArr[0] = dateArrFromSql[0];
                    }
                    if (dateArr[1].equals("")) {
                        dateArr[1] = dateArrFromSql[1];
                    }
                    queryCondition.setArrToQueryCondition(dateArr);
                }
            } else if (queryCondition.getKey().equals(UserService.MUST_PRODUCT_NUMBER)) {
                if (queryCondition.getValue().equals(QueryCondition.QUERY_CONDITION_SPLIT)) {
                    //"～～"的场合，从该用户的权限设定中获取属于他的海关/商品编码范围
                    queryConditionValueFromSql = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                            UserService.MUST_PRODUCT_NUMBER,
                            userID);
                    if (queryConditionValueFromSql.equals(QueryCondition.QUERY_CONDITION_SPLIT)) {
                        //该用户的海关/商品编码访问没有限制
                    } else {
                        String dateArrFromSql[] = queryConditionValueFromSql.split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
                        queryCondition.setArrToQueryCondition(dateArrFromSql);
                    }
                }
            }
        }
    }

    private long getUserMax(String userID) throws Exception {
        long userMax = -1;
        String strCanSearchCount = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                UserService.MUST_SEARCH_COUNT,
                userID);
        if (!StringUtils.isEmpty(strCanSearchCount)) {//可查询条数有限制
            userMax = Long.parseLong(strCanSearchCount);
        } else {//可查询条数没有限制
            userMax = dataService.queryTableRows(DataService.TABLE_DATA);
        }
        return userMax;
    }

}
