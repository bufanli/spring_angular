package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
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
@Service("SearchDataServiceImpl")
@Component
public class SearchDataServiceImpl implements ISearchDataService {
    @Override
    public ResponseResult searchData(Data queryConditions) throws Exception {

        try {
            this.searchDataFromSQL("eurasiaTable", queryConditions);
            log.info("查询数据结束");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_GET_INFO_FROM_SQL_SUCCESS);
    }

    private void searchDataFromSQL(String tableName, Data queryConditions) {
        DataService dataService = null;
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
            //ApplicationContext context = new FileSystemXmlApplicationContext("main/java/com/example/eurasia/config/applicationContext.xml");
            dataService = (DataService) context.getBean("dataService");
        } catch (BeansException e) {
            e.printStackTrace();
        }
        dataService.searchData(tableName, queryConditions);
    }

}
