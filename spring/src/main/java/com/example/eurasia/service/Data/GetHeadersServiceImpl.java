package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Header;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
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
@Service("GetHeadersServiceImpl")
@Component
public class GetHeadersServiceImpl implements IGetHeadersService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult getHeaders() throws Exception {

        Header[] headers;
        List<Map<String,String>> colsNameList;
        try {
            colsNameList = this.getHeadersFromSQL(DataService.TABLE_DATA);
            if (colsNameList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_NULL);
            }
            if (colsNameList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_ZERO);
            }

            headers = new Header[colsNameList.size()];
            int i = 0;
            for(Map<String,String> colsName: colsNameList) {
                Set<Map.Entry<String, String>> set = colsName.entrySet();
                Iterator<Map.Entry<String, String>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<String,String> entry = it.next();
                    //System.out.println("Key:" + entry.getKey() + " Value:" + entry.getValue());
                    headers[i] = new Header(entry.getValue().toString(), entry.getValue().toString());
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_SUCCESS, headers);
    }

    private List<Map<String,String>> getHeadersFromSQL(String tableName) throws Exception {
        return dataService.getHeaders(tableName);
    }

}
