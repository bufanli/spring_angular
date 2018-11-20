package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Header;
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
@Service("GetHeadersServiceImpl")
@Component
public class GetHeadersServiceImpl implements IGetHeadersService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    //注入UserService服务对象
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Override
    public ResponseResult getAllHeaders() throws Exception {

        Header[] headers;
        List<Map<String,String>> allHeadersList;
        try {
            allHeadersList = this.getAllHeadersFromSQL();
            if (allHeadersList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_NULL);
            }
            if (allHeadersList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_ZERO);
            }

            headers = new Header[allHeadersList.size()];
            for (int i = 0;i < allHeadersList.size();i++) {
                Set<Map.Entry<String, String>> set = allHeadersList.get(i).entrySet();
                Iterator<Map.Entry<String, String>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<String,String> entry = it.next();
                    //System.out.println("Key:" + entry.getKey() + " Value:" + entry.getValue());
                    headers[i] = new Header(entry.getValue().toString(), entry.getValue().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_SUCCESS, headers);
    }

    @Override
    public ResponseResult getHeaderDisplay() throws Exception {

        Header[] headers;
        List<String> headerDisplayList;
        try {
            headerDisplayList = this.getHeaderDisplayFromSQL();
            if (headerDisplayList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_NULL);
            }
            if (headerDisplayList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_ZERO);
            }

            headers = new Header[headerDisplayList.size()];
            for (int i = 0;i < headerDisplayList.size();i++) {
                headers[i] = new Header(headerDisplayList.get(i), headerDisplayList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_SUCCESS, headers);
    }

    private List<Map<String,String>> getAllHeadersFromSQL() throws Exception {
        return dataService.getAllHeaders();
    }

    private List<String> getHeaderDisplayFromSQL() throws Exception {
        return userService.getHeaderDisplay(userService.getUserID());
    }

}
