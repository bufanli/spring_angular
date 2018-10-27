package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public ResponseResult searchData(Data queryConditions) throws Exception {

        //List<Data> dataListTest = this.createTestData();
        //dataService.addData(DataService.TABLE_NAME, dataListTest.get(0));

        List<Data> dataList;
        try {
            dataList = this.searchDataFromSQL(DataService.TABLE_NAME, queryConditions);
            if (dataList == null || dataList.size() <= 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_GET_INFO_FROM_SQL_NULL);
            }

            log.info("查询数据结束");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_GET_INFO_FROM_SQL_SUCCESS, dataList);
    }

    public ResponseResult searchData() throws Exception {
        List<Data> dataList = this.createTestData();
        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_GET_INFO_FROM_SQL_SUCCESS, dataList);
    }

    private List<Data> searchDataFromSQL(String tableName, Data queryConditions) {
        if (!queryConditions.isValuesAllNULL()) {
            return dataService.searchData(tableName, queryConditions);
        } else {
            return dataService.searchAllData(tableName);
        }
    }

    private List<Data> createTestData() {
        List<Data> dataList = new ArrayList<>();
        Map<String, String> keyValue = new HashMap<>();
        keyValue.put("结束日期","2018/10/28");
        keyValue.put("起始日期","2018/10/28");
        keyValue.put("商品名称","1");
        keyValue.put("海关编码","1");
        keyValue.put("企业名称","1");
        keyValue.put("收发货地","1");
        keyValue.put("贸易方式","1");
        keyValue.put("原产国（目的国）","1");
        keyValue.put("商品编码_8","1");
        keyValue.put("商品编码_2","1");
        keyValue.put("产品名称","1");
        keyValue.put("规格型号","1");
        dataList.add(new Data(keyValue));
        return dataList;
    }

}
