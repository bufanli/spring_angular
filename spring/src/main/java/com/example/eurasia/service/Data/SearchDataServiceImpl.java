package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
            if (isQueryConditionsNotNULL(queryConditionsArr) == false) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ERROR);
            }

            this.setUserQueryConditionDefaultValue(userID,queryConditionsArr);

            dataList = dataService.searchData(DataService.TABLE_DATA, queryConditionsArr);

            if (dataList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_NULL);
            }
            if (dataList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_SUCCESS, dataList);
    }

    private boolean isQueryConditionsNotNULL(QueryCondition[] queryConditionsArr) {
        for (QueryCondition queryCondition : queryConditionsArr) {
            if (queryCondition.getKey() == null || queryCondition.getValue() == null || queryCondition.getType() == null) {
                return false;
            }
        }
        return true;
    }

    private void setUserQueryConditionDefaultValue(String userID, QueryCondition[] queryConditionsArr) throws Exception {

        for (QueryCondition queryCondition : queryConditionsArr) {
            String queryConditionValue = null;
            switch (queryCondition.getKey()) {
                case UserService.MUST_PRODUCT_DATE://"日期"为空，从用户权限表里获得。
                    if (queryCondition.getValue().equals(QueryCondition.QUERY_CONDITION_SPLIT)) {
                        queryConditionValue = userService.getOneUserCustom(userService.TABLE_USER_ACCESS_AUTHORITY,
                                userID,
                                UserService.MUST_PRODUCT_DATE);
                    }
                    break;
                case UserService.MUST_PRODUCT_NUMBER://"商品编码"为空，从用户权限表里获得。
                    if (queryCondition.getValue().equals(QueryCondition.QUERY_CONDITION_SPLIT)) {
                        queryConditionValue = userService.getOneUserCustom(userService.TABLE_USER_ACCESS_AUTHORITY,
                                userID,
                                UserService.MUST_PRODUCT_NUMBER);
                    }
                    break;
                default:
                    break;
            }
            queryCondition.setValue(queryConditionValue);
        }
    }
}
