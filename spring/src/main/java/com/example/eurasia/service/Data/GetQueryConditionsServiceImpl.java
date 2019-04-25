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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            // 取得所以的查询条件(Data的Map-key是查询条件的key，Data的Map-value是查询条件的type)
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
                    queryConditionValue = QueryCondition.QUERY_CONDITION_SPLIT;
                }

                // 如果是 QueryCondition.QUERY_CONDITION_SPLIT 的话，返回该列所有的元素
                if (queryConditionValue.equals(QueryCondition.QUERY_CONDITION_SPLIT)) {
                    List<Map<String, Object>> listMaps = dataService.getColumnAllValues(DataService.TABLE_DATA,new String[]{key});
                    queryConditionValue = getListMapValue(listMaps);
                }
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_STRING:
            default:
                queryConditionValue = "";
                break;
        }
        return queryConditionValue;
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

    private String getListMapValue(List<Map<String, Object>> listMaps) {
        StringBuffer values = new StringBuffer();
        for (Map<String, Object> map : listMaps) {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                //System.out.print(m.getKey());
                //System.out.println(m.getValue());
                values.append(m.getValue() + QueryCondition.QUERY_CONDITION_SPLIT);
            }
        }
        if (values.indexOf(QueryCondition.QUERY_CONDITION_SPLIT) >= 0) {
            values.delete((values.length() - QueryCondition.QUERY_CONDITION_SPLIT.length()),values.length());
        }
        return values.toString();
    }
}
