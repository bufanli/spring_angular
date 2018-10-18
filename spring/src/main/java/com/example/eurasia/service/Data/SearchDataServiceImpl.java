package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("SearchDataServiceImpl")
@Component
public class SearchDataServiceImpl implements ISearchDataService {
    @Override
    public ResponseResult searchData(Data data) throws Exception {
        String tableName = "*";//T.B.D. dummy
        this.searchDataFromSQL(tableName, data);
        return new ResponseResultUtil().success();
    }

    @Override
    public ResponseResult getHeaders() throws Exception {
        String tableName = "*";//T.B.D. dummy
        this.getHeadersFromSQL(tableName);
        return new ResponseResultUtil().success();
    }

    private void searchDataFromSQL(String tableName, Data data) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        DataService dataService = (DataService) context.getBean("dataService");
        dataService.searchData(tableName, data);
    }

    private void getHeadersFromSQL(String tableName) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        DataService dataService = (DataService) context.getBean("dataService");
        dataService.getHeaders(tableName);
    }
}
