package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
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

    @Override
    public ResponseResult searchData(QueryCondition[] queryConditionsArr) throws Exception {

        //List<Data> dumyDataList = dataService.geteTestData();
        //dataService.addData(DataService.TABLE_NAME, dumyDataList.get(0));

        Data queryConditions = new Data(dataService.queryConditionsArrToMap(queryConditionsArr));
        List<Data> dataList;
        try {
            dataList = this.searchDataFromSQL(DataService.TABLE_NAME, queryConditions);
            if (dataList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_NULL);
            }
            if (dataList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_SUCCESS, dataList);
    }

    private List<Data> searchDataFromSQL(String tableName, Data queryConditions) throws Exception {
        return dataService.searchData(tableName, queryConditions);
    }

}
