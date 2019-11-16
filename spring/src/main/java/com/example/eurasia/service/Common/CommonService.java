package com.example.eurasia.service.Common;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Data.DataService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.User.UserService;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public abstract class CommonService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    protected DataService dataService;
    //注入UserService服务对象
    @Qualifier("userService")
    @Autowired
    protected UserService userService;

    protected String checkQueryConditions(String userID, QueryCondition[] queryConditionsArr) throws Exception {
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
            String userProductNumbers = null;
            switch (queryCondition.getKey()) {
                case UserService.MUST_PRODUCT_NUMBER:
                        if (queryCondition.getValue().equals(QueryCondition.QUERY_CONDITION_SPLIT)) {// "～～"的场合，没有输入

                        } else {
                            String productNameArr[] = queryCondition.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT);

                            // 该用户的海关/商品编码范围
                            userProductNumbers = userService.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,
                                    UserService.MUST_PRODUCT_NUMBER,
                                    userID);

                            if (!userProductNumbers.equals(QueryCondition.QUERY_CONDITION_SPLIT)) {

                                for (String productName:productNameArr) {

                                    // 检查海关/商品编码是否在该用户都权限内
                                    if (userProductNumbers.contains(productName) == false) {
                                        Slf4jLogUtil.get().info(ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ACCESS_ERROR.getMessage());
                                        ret.append(productName + ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_ACCESS_ERROR.getMessage());
                                        ret.append(UserService.BR);
                                    }

                                    // 在DataService.TABLE_DATA中是否存在
                                    long numInTableData = dataService.getColumnValueNumber(DataService.TABLE_DATA,
                                            UserService.MUST_PRODUCT_NUMBER,
                                            productName);
                                    if (numInTableData == 0 ) {
                                        Slf4jLogUtil.get().info(ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_IS_NOT_EXIST.getMessage());
                                        ret.append(productName + ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_IS_NOT_EXIST.getMessage());
                                        ret.append(UserService.BR);
                                    }
                                }

                            } else {// "～～"的场合，没有限制

                                for (String productName:productNameArr) {

                                    // 在DataService.TABLE_DATA中是否存在
                                    long numInTableData = dataService.getColumnValueNumber(DataService.TABLE_DATA,
                                            UserService.MUST_PRODUCT_NUMBER,
                                            productName);
                                    if (numInTableData == 0 ) {
                                        Slf4jLogUtil.get().info(ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_IS_NOT_EXIST.getMessage());
                                        ret.append(productName + ResponseCodeEnum.SEARCH_DATA_QUERY_CONDITION_IS_NOT_EXIST.getMessage());
                                        ret.append(UserService.BR);
                                    }
                                }

                            }
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

    protected void setUserQueryConditionDefaultValue(String userID, QueryCondition[] queryConditionsArr) throws Exception {

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

    protected long getUserMax(String userID) throws Exception {
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
