package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.CategorySelectionsWithTotalCount;
import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.GetListValueParam;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("GetQueryConditionsServiceImpl")
@Component
public class GetQueryConditionsServiceImpl implements IGetQueryConditionsService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    //注入UserService服务对象
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    /**
     * 取得所有的查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-22 00:00:00
     */
    @Override
    public ResponseResult getAllQueryConditions() throws Exception {
        QueryCondition[] queryConditions;

        try {
            // 取得所有的查询条件(Data的Map-key是查询条件的key，Data的Map-value是查询条件的type)
            List<Data> allQueryConditionsList = dataService.getAllQueryConditions();
            if (allQueryConditionsList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_NULL);
            }
            if (allQueryConditionsList.size() != 1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_SIZE_WRONG);
            }

            queryConditions = new QueryCondition[allQueryConditionsList.get(0).getKeyValue().size()];
            int i = 0;
            Set<Map.Entry<String, String>> set = allQueryConditionsList.get(0).getKeyValue().entrySet();
            Iterator<Map.Entry<String, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> entry = it.next();
                queryConditions[i].setKey(entry.getKey());
                queryConditions[i].setValue(getQueryConditionDisplayValue(UserService.USER_DEFAULT,entry.getKey(),entry.getValue()));
                queryConditions[i].setType(entry.getValue());
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_SUCCESS, queryConditions);
    }

    /**
     * 取得可显示的查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-22 00:00:00
     */
    @Override
    public ResponseResult getQueryConditionDisplay(String userID) throws Exception {
        QueryCondition[] queryConditions;

        try {
            // 取得所有的查询条件(Data的Map-key是查询条件的key，Data的Map-value是查询条件的type)
            List<Data> allQueryConditionsList = dataService.getAllQueryConditions();
            if (allQueryConditionsList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_NULL);
            }
            if (allQueryConditionsList.size() != 1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_SIZE_WRONG);
            }

            // 取得该用户可显示的查询条件
            List<String> userQueryConditionDisplayList = userService.getUserQueryConditionDisplayByTrue(userID);
            if (userQueryConditionDisplayList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_FROM_SQL_NULL);
            }
            if (userQueryConditionDisplayList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO);
            }

            // 取得该用户可显示的查询条件的最大数
            String strConditionDisplayCount = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                    UserService.MUST_CONDITION_DISPLAY_COUNT,
                    userID);
            if (userQueryConditionDisplayList.size() > Integer.parseInt(strConditionDisplayCount)) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_MAXSIZE_FROM_SQL_FAILED);
            }

            // 重新组合成数组返回
            int i = 0;
            queryConditions = new QueryCondition[userQueryConditionDisplayList.size()];
            Set<Map.Entry<String, String>> set = allQueryConditionsList.get(0).getKeyValue().entrySet();
            Iterator<Map.Entry<String, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> entry = it.next();

                for (String userQueryConditionDisplay:userQueryConditionDisplayList) {
                    if (entry.getKey().equals(userQueryConditionDisplay)) {
                        queryConditions[i] = new QueryCondition();
                        queryConditions[i].setKey(entry.getKey());
                        queryConditions[i].setValue(getQueryConditionDisplayValue(userID,entry.getKey(),entry.getValue()));
                        queryConditions[i].setType(entry.getValue());
                        i++;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_FROM_SQL_SUCCESS, queryConditions);
    }

    private String getQueryConditionDisplayValue(String userID, String key, String type) throws Exception {

        String queryConditionValue = null;
        switch (type) {
            case QueryCondition.QUERY_CONDITION_TYPE_DATE:
                if (key.equals(UserService.MUST_PRODUCT_DATE)) {
                    queryConditionValue = this.getUserTheLastMonth(userID);
                } else {
                    queryConditionValue = QueryCondition.QUERY_CONDITION_SPLIT;
                }
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
            case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                queryConditionValue = QueryCondition.QUERY_CONDITION_SPLIT;
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                if (key.equals(UserService.MUST_PRODUCT_NUMBER)) {
                    queryConditionValue = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                            UserService.MUST_PRODUCT_NUMBER,
                            userID);
                } else {
                    queryConditionValue = "";
                }
                /* List改为翻页形式。2019-06-26。
                    queryConditionValue = QueryCondition.QUERY_CONDITION_SPLIT;
                }

                // 如果是 QueryCondition.QUERY_CONDITION_SPLIT 的话，返回该列所有的元素
                if (queryConditionValue.equals(QueryCondition.QUERY_CONDITION_SPLIT)) {
                    List<Map<String, Object>> colValuesListMap = dataService.getColumnAllValuesByGroup(DataService.TABLE_DATA,new String[]{key});
                    queryConditionValue = DataProcessingUtil.getListMapValuesOfOneColumnWithQueryConditionSplit(colValuesListMap);
                }
                */
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_STRING:
            default:
                queryConditionValue = "";
                break;
        }
        return queryConditionValue;
    }

    /**
     * 取得List类型的查询条件的值
     * @exception
     * @author FuJia
     * @Time 2019-06-26 00:00:00
     */
    public ResponseResult getListValueWithPagination(GetListValueParam getListValueParam) throws Exception {

        CategorySelectionsWithTotalCount categorySelectionsWithTotalCount = new CategorySelectionsWithTotalCount();
        String category = getListValueParam.getCategory();
        String term = getListValueParam.getTerm();
        int offset = getListValueParam.getOffset();
        int limit = getListValueParam.getLimit();
        try {

            Map<String, String> order = new LinkedHashMap<>();
            order.put(category,"asc");//T.B.D

            List<Long> countsList = dataService.getColumnValueCounts(DataService.TABLE_DATA, category);
            long count = countsList.size();
            categorySelectionsWithTotalCount.setTotalCount(count);

            List<Map<String, Object>> colValuesListMap = dataService.getColumnValuesWithPagination(DataService.TABLE_DATA,
                                                                                                    category,
                                                                                                    term,
                                                                                                    offset,
                                                                                                    limit,
                                                                                                    order);

            int i= offset+1;
            for (Map<String, Object> map : colValuesListMap) {
                for (Map.Entry<String, Object> m : map.entrySet()) {
                    categorySelectionsWithTotalCount.pushSelection(new CategorySelectionsWithTotalCount.Selection(i+1,(String) m.getValue()));
                    i++;
                }
            }

            if (categorySelectionsWithTotalCount.getResults() == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_LIST_VALUE_FROM_SQL_NULL);
            }
            if (categorySelectionsWithTotalCount.getResults().size() < 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_LIST_VALUE_FROM_SQL_ZERO);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_LIST_VALUE_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_LIST_VALUE_FROM_SQL_SUCCESS,
                                                categorySelectionsWithTotalCount);
    }
    /**
     * 取得数据中最近的月份
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-22 00:00:00
     */
    private String getUserTheLastMonth(String userID) throws Exception {

        String[] dateArr = null;
        try {
            dateArr = userService.getUserTheLastMonth(DataService.TABLE_DATA,userID);
            if (dateArr == null) {
                throw new Exception(ResponseCodeEnum.QUERY_CONDITION_DATE_DEFAULT_VALUE_NULL.getMessage());
            }
            if (dateArr.length != 2) {
                throw new Exception(ResponseCodeEnum.QUERY_CONDITION_DATE_DEFAULT_VALUE_WRONG.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(ResponseCodeEnum.QUERY_CONDITION_DATE_DEFAULT_VALUE_FAILED.getMessage());
        }

        String defaultValue = dateArr[0] + QueryCondition.QUERY_CONDITION_SPLIT + dateArr[1] ;
        return defaultValue;
    }

}
