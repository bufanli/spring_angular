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

        return new ResponseResultUtil().success(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_SUCCESS, queryConditions);
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

        // 取得该用户可显示的查询条件
        List<String> queryConditionsDisplayList = userService.getQueryConditionDisplay(userService.getUserID());
        // 取得所以的查询条件(Data的Map-key是查询条件的key，Data的Map-value是查询条件的type)
        List<Data> allQueryConditionsList = dataService.getAllQueryConditions();

        if (queryConditionsDisplayList == null || allQueryConditionsList == null) {
            return null;
        }

        // 取得最近一个月的时间
        //T.B.D String[] = userService.getUserTheLastMonth(DataService.TABLE_DATA);

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
                    queryConditions[i].setKey(entry.getValue());
                    i++;
                }
            }
        }

        return queryConditions;
    }
}
