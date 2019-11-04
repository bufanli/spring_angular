package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.*;
import com.example.eurasia.service.Common.CommonService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.DataProcessingUtil;
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
public class SearchDataServiceImpl extends CommonService implements ISearchDataService {

    @Override
    public ResponseResult searchData(String userID, DataSearchParam dataSearchParam) throws Exception {

        SearchedData searchedData = null;
        QueryCondition[] queryConditionsArr = dataSearchParam.getQueryConditions();
        long offset = dataSearchParam.getOffset();
        long limit = dataSearchParam.getLimit();
        long userMax = -1;
        try {
            //检查查询条件的格式和内容
            String retCheck = checkQueryConditions(userID,queryConditionsArr);
            if (!StringUtils.isEmpty(retCheck)) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ERROR.getCode(),retCheck);
            }

            //为未输入的查询条件进行默认值设定
            setUserQueryConditionDefaultValue(userID,queryConditionsArr);

            //该用户可查询的条数
            userMax = getUserMax(userID);

            Map<String, String> order = new LinkedHashMap<>();
            order.put("id","asc");//T.B.D

            long count = dataService.queryTableRows(DataService.TABLE_DATA,queryConditionsArr);
            List<Data> dataList = dataService.searchData(DataService.TABLE_DATA,queryConditionsArr, offset, limit,order);
            searchedData = new SearchedData(count, dataList);

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
            String retCheck = checkQueryConditions(userID,queryConditionsArr);
            if (!StringUtils.isEmpty(retCheck)) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_QUERY_CONDITION_ERROR.getCode(),retCheck);
            }

            //为未输入的查询条件进行默认值设定
            setUserQueryConditionDefaultValue(userID,queryConditionsArr);

            //该用户可查询的条数
            userMax = getUserMax(userID);

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
                groupByField = DataService.STATISTICS_REPORT_PRODUCT_DATE;
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

            statisticsTypesList = dataService.getColumnsValues(DataService.TABLE_STATISTICS_SETTING_TYPE,
                    new String[]{DataService.STATISTICS_SETTING_TYPE_COLUMN_NAME});
            if (statisticsTypesList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_SETTING_TYPE_FROM_SQL_NULL);
            }

            groupByFieldsList = dataService.getColumnsValues(DataService.TABLE_STATISTICS_SETTING_GROUP_BY,
                    new String[]{DataService.STATISTICS_SETTING_GROUP_BY_COLUMN_NAME});
            if (groupByFieldsList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_SETTING_GROUP_BY_FROM_SQL_NULL);
            }

            computeByFieldsList = dataService.getColumnsValues(DataService.TABLE_STATISTICS_SETTING_COMPUTE_BY,
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

}
