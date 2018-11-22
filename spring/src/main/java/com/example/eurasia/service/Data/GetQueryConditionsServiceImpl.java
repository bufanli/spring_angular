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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
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
            queryConditions = this.getAllQueryConditionsFromSQL();
            if (queryConditions == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_NULL);
            }
            if (queryConditions.length == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_ZERO);
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
    public ResponseResult getQueryConditionDisplay() throws Exception {
        QueryCondition[] queryConditions;

        try {
            queryConditions = this.getQueryConditionDisplayFromSQL();

            if (queryConditions == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_FROM_SQL_NULL);
            }
            if (queryConditions.length == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_DISPLAY_FROM_SQL_SUCCESS, queryConditions);
    }

    /**
     * 取得数据中最近的月份
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-22 00:00:00
     */
    @Override
    public ResponseResult getDateDefaultValue() throws Exception {

        String[] dateArr = null;
        try {
            dateArr = userService.getUserTheLastMonth(DataService.TABLE_DATA);
            if (dateArr == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_DATE_DEFAULT_VALUE_NULL);
            }
            if (dateArr.length != 2) {
                return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_DATE_DEFAULT_VALUE_WRONG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_DATE_DEFAULT_VALUE_FAILED);
        }

        String defaultValue = dateArr[0] + QueryCondition.QUERY_CONDITION_SPLIT + dateArr[1] ;
        return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_DATE_DEFAULT_VALUE_SUCCESS, defaultValue);
    }

    private QueryCondition[] getAllQueryConditionsFromSQL() throws Exception {
        List<Data> allQueryConditionsList = dataService.getAllQueryConditions();
        if (allQueryConditionsList == null) {
            return null;
        }

        QueryCondition[] queryConditions = new QueryCondition[allQueryConditionsList.get(0).getKeyValue().size()];
        int i = 0;
        Set<Map.Entry<String, String>> set = allQueryConditionsList.get(0).getKeyValue().entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            queryConditions[i].setKey(entry.getKey());
            queryConditions[i].setKey(entry.getValue());
            i++;
        }

        return null;
    }

    private QueryCondition[] getQueryConditionDisplayFromSQL() throws Exception {

        // 取得该用户可显示的查询条件(注意：日期是必须的!)
        List<String> queryConditionsDisplayList = userService.getUserQueryConditionDisplay(userService.getUserID());
        // 取得所以的查询条件(Data的Map-key是查询条件的key，Data的Map-value是查询条件的type)
        List<Data> allQueryConditionsList = dataService.getAllQueryConditions();

        if (queryConditionsDisplayList == null || allQueryConditionsList == null) {
            return null;
        }

        // 组合成数组返回
        QueryCondition[] queryConditions = new QueryCondition[allQueryConditionsList.get(0).getKeyValue().size()];
        int i = 0;
        Set<Map.Entry<String, String>> set = allQueryConditionsList.get(0).getKeyValue().entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            for (String queryConditionsDisplay : queryConditionsDisplayList) {
                if (queryConditionsDisplay.equals(entry.getKey())) {
                    queryConditions[i].setKey(entry.getKey());
                    queryConditions[i].setValue(getQueryConditionDisplayValue(entry.getKey(),entry.getValue()));
                    queryConditions[i].setType(entry.getValue());
                    i++;
                }
            }
        }

        return queryConditions;
    }

    private String getQueryConditionDisplayValue(String key, String type) throws Exception {

        // 根据可显示的查询条件的类型以及权限，返回对应的值 T.B.D
        switch (type) {
            case QueryCondition.QUERY_CONDITION_TYPE_STRING:
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_DATE:
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
            case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                break;
            default:
                break;
        }
        return "";
    }
}
