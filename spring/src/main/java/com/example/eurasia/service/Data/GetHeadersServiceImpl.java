package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
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
        String headers;
        try {
            headers = this.getHeadersFromSQL("eurasiaTable");
            log.info("取得表头结束");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_SUCCESS, headers);
    }

    private String getHeadersFromSQL(String tableName) throws Exception {
        DataService dataService = null;
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
            //ApplicationContext context = new FileSystemXmlApplicationContext("main/java/com/example/eurasia/config/applicationContext.xml");
            dataService = (DataService) context.getBean("dataService");
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return dataService.getHeaders(tableName);
    }
}
