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
import org.springframework.util.StringUtils;

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
    public ResponseResult searchData(String userID, QueryCondition[] queryConditionsArr, long offset, long limit) throws Exception {

        List<Data> dataList;
        try {
            String retCheck = this.checkQueryConditions(userID,queryConditionsArr);
            if (!StringUtils.isEmpty(retCheck)) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ERROR.getCode(),retCheck);
            }

            this.setUserQueryConditionDefaultValue(userID,queryConditionsArr);

            dataList = dataService.searchData(DataService.TABLE_DATA,queryConditionsArr,offset,limit);

            if (dataList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_NULL);
            }
            if (dataList.size() <= 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_SUCCESS, dataList);
    }

    private String checkQueryConditions(String userID, QueryCondition[] queryConditionsArr) throws Exception {
        StringBuffer ret = new StringBuffer("");
        for (QueryCondition queryCondition : queryConditionsArr) {

            // 检查格式
            if (queryCondition.checkValueFormat() == false) {
                ret.append(queryCondition.getKey() + ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_FORMAT_ERROR.getMessage());
                ret.append(UserService.BR);
            }

            // 检查内容
            String queryConditionValue = null;
            switch (queryCondition.getKey()) {
                case UserService.MUST_PRODUCT_DATE:
                    //检查商品编码是否在该用户都权限内
                    queryConditionValue = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                            UserService.MUST_PRODUCT_NUMBER,
                            userID);
                    if (!queryConditionValue.equals(QueryCondition.QUERY_CONDITION_SPLIT)//"～～"的场合，是没有限制
                            && queryConditionValue.contains(queryCondition.getValue()) == false) {
                        ret.append(queryCondition.getKey() + ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ACCESS_ERROR.getMessage());
                        ret.append(UserService.BR);
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

        String queryConditionValue = null;
        for (QueryCondition queryCondition : queryConditionsArr) {
            switch (queryCondition.getKey()) {
                case UserService.MUST_PRODUCT_DATE:
                    String dateArr[] = queryCondition.getQueryConditionToArr();
                    if (dateArr[0].equals("") || dateArr[1].equals("")) {//为了在起始日期和结束日期都存在都情况下，不查询该用户的日期范围
                        queryConditionValue = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                                UserService.MUST_PRODUCT_DATE,
                                userID);
                        String dateArrFromSql[] = queryConditionValue.split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
                        if (dateArr[0].equals("")) {
                            dateArr[0] = dateArrFromSql[0];
                        }
                        if (dateArr[1].equals("")) {
                            dateArr[1] = dateArrFromSql[1];
                        }
                        queryCondition.setValue(queryConditionValue);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
