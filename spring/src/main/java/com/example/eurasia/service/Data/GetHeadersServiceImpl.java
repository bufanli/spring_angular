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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("GetHeadersServiceImpl")
@Component
public class GetHeadersServiceImpl implements IGetHeadersService {
    @Override
    public ResponseResult getHeaders() throws Exception {

        Header[] headers;
        List<Map<String,Object>> colsNameList;
        try {
            colsNameList = this.getHeadersFromSQL("eurasiaTable");
            if (colsNameList == null || colsNameList.size() <= 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_NULL);
            }

            headers = new Header[colsNameList.size()];
            int i = 0;
            for(Map<String,Object> colsName: colsNameList) {
                Set<Map.Entry<String, Object>> set = colsName.entrySet();
                Iterator<Map.Entry<String, Object>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<String,Object> entry = it.next();
                    //System.out.println("Key:" + entry.getKey() + " Value:" + entry.getValue());
                    headers[i] = new Header();
                    headers[i].key = entry.getValue().toString();
                    headers[i].value = entry.getValue();
                }
                i++;
            }

            log.info("取得表头结束");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_SUCCESS, headers);
    }

    private List<Map<String,Object>> getHeadersFromSQL(String tableName) throws Exception {
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

    class Header {
        String key;
        Object value;

        Header () {
            key = new String();
            value = new Object();
        }
    }

}
