package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ResponseResult searchData(String userID, QueryCondition[] queryConditionsArr) throws Exception {

        List<Data> dataList;
        try {
            if (this.checkQueryConditions(queryConditionsArr) == false) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ERROR);
            }

            this.setUserQueryConditionDefaultValue(userID,queryConditionsArr);

            dataList = dataService.searchData(DataService.TABLE_DATA, queryConditionsArr);

            if (dataList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_NULL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_SUCCESS, dataList);
    }

    private boolean checkQueryConditions(QueryCondition[] queryConditionsArr) {
        for (QueryCondition queryCondition : queryConditionsArr) {
            if (queryCondition.checkValue() == false) {
                return false;
            }
        }
        return true;
    }

    private void setUserQueryConditionDefaultValue(String userID, QueryCondition[] queryConditionsArr) throws Exception {

        for (QueryCondition queryCondition : queryConditionsArr) {
            String queryConditionValue = null;
            switch (queryCondition.getType()) {
                case QueryCondition.QUERY_CONDITION_TYPE_STRING:
                    queryConditionValue = "";
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_DATE:
                    if (queryCondition.getKey().equals(UserService.MUST_PRODUCT_DATE)) {
                        queryConditionValue = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                                UserService.MUST_PRODUCT_DATE,
                                userID);
                    } else {
                        queryConditionValue = QueryCondition.QUERY_CONDITION_SPLIT;
                    }
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
                case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                    queryConditionValue = QueryCondition.QUERY_CONDITION_SPLIT;
                    break;
                case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                    if (queryCondition.getKey().equals(UserService.MUST_PRODUCT_NUMBER)) {
                        queryConditionValue = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                                UserService.MUST_PRODUCT_NUMBER,
                                userID);
                    } else {
                        queryConditionValue = QueryCondition.QUERY_CONDITION_SPLIT;
                    }
                    break;
                default:
                    queryConditionValue = "";
                    break;
            }
            queryCondition.setValue(queryConditionValue);
        }
    }
}
