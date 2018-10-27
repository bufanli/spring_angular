package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("GetQueryConditionsServiceImpl")
@Component
public class GetQueryConditionsServiceImpl implements IGetQueryConditionsService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult getQueryConditions() throws Exception {
        String tableName = "*";//T.B.D. dummy
        this.getQueryConditionsFromSQL(tableName);
        return new ResponseResultUtil().success();
    }

    private void getQueryConditionsFromSQL(String tableName) {
        dataService.getHeaders(tableName);
    }
}
