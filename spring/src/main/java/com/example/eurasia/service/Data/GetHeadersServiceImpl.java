package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.Header;
import com.example.eurasia.entity.User.UserCustom;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserService;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Slf4j
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
            allHeadersList = dataService.getAllColumns();
            if (allHeadersList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_ALL_INFO_FROM_SQL_NULL);
            }
            if (allHeadersList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_ALL_INFO_FROM_SQL_ZERO);
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
            Slf4jLogUtil.get().info("取得的所有列名:" + headers);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_ALL_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_ALL_INFO_FROM_SQL_SUCCESS, headers);
    }

    @Override
    public ResponseResult getHeaderDisplayByTrue(String userID) throws Exception {

        Header[] headers;
        List<String> headerDisplayList;
        try {
            headerDisplayList = userService.getUserHeaderDisplayByTrue(userID);
            if (headerDisplayList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_BY_TRUE_FROM_SQL_NULL);
            }
            if (headerDisplayList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_BY_TRUE_FROM_SQL_ZERO);
            }

            headers = new Header[headerDisplayList.size()];
            for (int i = 0;i < headerDisplayList.size();i++) {
                headers[i] = new Header(headerDisplayList.get(i), headerDisplayList.get(i));
            }
            Slf4jLogUtil.get().info("取得的显示的列名:" + headers);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_BY_TRUE_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_BY_TRUE_FROM_SQL_SUCCESS, headers);
    }

    @Override
    public ResponseResult getHeaderDisplay(String userID) throws Exception {

        UserCustom[] userHeaderDisplays;
        List<Data> userHeaderDisplaysList;
        try {
            userHeaderDisplaysList = userService.getUserHeaderDisplay(userID);
            if (userHeaderDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_FROM_SQL_NULL);
            }
            if (userHeaderDisplaysList.size() != 1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_FROM_SQL_ZERO);
            }

            userHeaderDisplays = userHeaderDisplaysList.get(0).toUserCustomArr();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_FROM_SQL_SUCCESS, userHeaderDisplays);
    }

    @Override
    public ResponseResult setHeaderDisplay(String userID, UserCustom[] userHeaderDisplays) throws Exception {

        try {
            StringBuffer strUserHeaderDisplays = new StringBuffer();
            for (UserCustom userCustom:userHeaderDisplays) {
                strUserHeaderDisplays.append(userCustom.toString() + " ");
            }
            Slf4jLogUtil.get().info("保存的显示非显示的列名:" + strUserHeaderDisplays);
            boolean isUpdateSuccessful = userService.updateUserHeaderDisplay(userID, userHeaderDisplays);
            if (isUpdateSuccessful == false) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_SET_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_SET_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_SET_SUCCESS);
    }

}
