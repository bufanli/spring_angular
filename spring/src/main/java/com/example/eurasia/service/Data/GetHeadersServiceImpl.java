package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("GetHeadersServiceImpl")
@Component
public class GetHeadersServiceImpl implements IGetHeadersService {
    @Override
    public ResponseResult getHeaders() throws Exception {
        String tableName = "*";//T.B.D. dummy
        this.getHeadersFromSQL(tableName);
        return new ResponseResultUtil().success();
    }

    private void getHeadersFromSQL(String tableName) {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
        //ApplicationContext context = new FileSystemXmlApplicationContext("main/java/com/example/eurasia/config/applicationContext.xml");
        DataService dataService = (DataService) context.getBean("dataService");
        dataService.getHeaders(tableName);
    }
}
