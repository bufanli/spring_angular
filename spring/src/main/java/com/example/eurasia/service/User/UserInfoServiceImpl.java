package com.example.eurasia.service.User;

import com.example.eurasia.entity.*;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoServiceImpl")
@Component
public class UserInfoServiceImpl implements IUserInfoService {

    //UserService
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Override
    public boolean setLoginUserID(String loginUserID) throws Exception {
        if (userService.isUserIDExist(loginUserID) == false) {
            return false;
        }
        userService.setUserID(loginUserID);
        return true;
    }

    @Override
    public ResponseResult updateUser(UserInfo userInfo) throws Exception {

        ResponseResult responseResult;

        log.info("保存用户的基本信息开始");
        boolean isupdateSuccessful = this.updateUserBasicInfo(userInfo.getUserBasicInfos());
        if (isupdateSuccessful == true) {
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_UPDATE_BASIC_INFO_SUCCESS);
        } else {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_BASIC_INFO_FAILED);
        }
        log.info("保存用户的基本信息结束");

        log.info("保存用户的访问权限开始");
        isupdateSuccessful = this.updateUserAccessAuthority(userInfo.getUserAccessAuthorities());
        if (isupdateSuccessful == true) {
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_UPDATE_ACCESS_AUTHORITY_INFO_SUCCESS);
        } else {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_ACCESS_AUTHORITY_INFO_FAILED);
        }
        log.info("保存用户的访问权限结束");

        log.info("保存用户的可见查询条件开始");
        isupdateSuccessful = this.updateUserQueryConditionDisplay(null);//T.B.D
        if (isupdateSuccessful == true) {
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_UPDATE_QUERY_CONDITION_DISPLAY_SUCCESS);
        } else {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_QUERY_CONDITION_DISPLAY_FAILED);
        }
        log.info("保存用户的可见查询条件结束");

        log.info("保存用户的可见表头开始");
        isupdateSuccessful = this.updateUserHeaderDisplay(null);//T.B.D
        if (isupdateSuccessful == true) {
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_UPDATE_HEADER_DISPLAY_SUCCESS);
        } else {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_HEADER_DISPLAY_FAILED);
        }
        log.info("保存用户的可见表头结束");

        log.info("保存用户的可见表头宽度开始");
        isupdateSuccessful = this.updateUserHeaderWidth(null);//T.B.D
        if (isupdateSuccessful == true) {
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_UPDATE_HEADER_WIDTH_SUCCESS);
        } else {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_HEADER_WIDTH_FAILED);
        }
        log.info("保存用户的可见表头宽度结束");

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_UPDATE_SUCCESS);
    }

    @Override
    public boolean addUser(UserInfo userInfo) throws Exception {
        //T.B.D
        if (userService.createTable("","")) {
            return true;
        }
        return false;
    }

    @Override
    public ResponseResult getUserBasicInfoList() throws Exception {
        return this.getUserBasicInfo(userService.USER_ALL);
    }

    @Override
    public ResponseResult getUserBasicInfo(String userID) throws Exception {
        List<Data> userBasicInfosList;
        try {

            userBasicInfosList = userService.getUserBasicInfo(userID);

            //Dummy
            List<Data> userList = new ArrayList<>();
            Map<String, String> user = new HashMap<String, String>();
            user.put("userId", "webchat0001");
            user.put("昵称", "常海啸");
            user.put("性别", "男");
            user.put("名字", "张力");
            user.put("密码", "123456");
            user.put("年龄", "23");
            user.put("国家", "中国");
            user.put("城市", "南京");
            user.put("省份", "江苏");
            user.put("地址", "江苏省南京市**路");
            user.put("手机号码", "134534096847");
            user.put("电子邮件", "zhangli@163.com");
            Data data = new Data(user);
            user.put("userId", "webchat0002");
            Data data2 = new Data(user);
            userList.add(data);
            userList.add(data2);
            new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_SUCCESS, userList);

            if (userBasicInfosList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_NULL);
            }
            if (userBasicInfosList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_SUCCESS, userBasicInfosList);
    }

    @Override
    public boolean updateUserBasicInfo(UserCustom[] userCustoms) throws Exception {
        return userService.updateUserBasicInfo(userCustoms);
    }

    @Override
    public ResponseResult getUserAccessAuthority(String userID) throws Exception {
        List<Data> userAccessAuthoritiesList;
        try {

            userAccessAuthoritiesList = userService.getUserAccessAuthority(userID);

            if (userAccessAuthoritiesList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_NULL);
            }
            if (userAccessAuthoritiesList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_SUCCESS, userAccessAuthoritiesList);
    }

    @Override
    public boolean updateUserAccessAuthority(UserCustom[] userCustoms) throws Exception {
        return userService.updateUserAccessAuthority(userCustoms);
    }

    @Override
    public ResponseResult getUserQueryConditionDisplay(String userID) throws Exception {
        List<String> userQueryConditionDisplaysList;
        try {

            userQueryConditionDisplaysList = userService.getUserQueryConditionDisplay(userID);

            if (userQueryConditionDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_NULL);
            }
            if (userQueryConditionDisplaysList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_SUCCESS, userQueryConditionDisplaysList);
    }

    @Override
    public boolean updateUserQueryConditionDisplay(UserCustom[] userCustoms) throws Exception {
        return userService.updateUserQueryConditionDisplay(userCustoms);
    }

    @Override
    public ResponseResult getUserHeaderDisplay(String userID) throws Exception {
        List<String> userHeaderDisplaysList;
        try {

            userHeaderDisplaysList = userService.getUserHeaderDisplay(userID);

            if (userHeaderDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_HEADER_DISPLAY_FROM_SQL_NULL);
            }
            if (userHeaderDisplaysList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_HEADER_DISPLAY_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_HEADER_DISPLAY_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_HEADER_DISPLAY_FROM_SQL_SUCCESS, userHeaderDisplaysList);
    }

    @Override
    public boolean updateUserHeaderDisplay(UserCustom[] userCustoms) throws Exception {
        return userService.updateUserHeaderDisplay(userCustoms);
    }

    @Override
    public ResponseResult getUserHeaderWidth(String userID) throws Exception {
        List<Data> userHeaderWidthsList;
        try {

            userHeaderWidthsList = userService.getUserHeaderWidth(userID);

            if (userHeaderWidthsList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_HEADER_WIDTH_FROM_SQL_NULL);
            }
            if (userHeaderWidthsList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_HEADER_WIDTH_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_HEADER_WIDTH_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_HEADER_WIDTH_FROM_SQL_SUCCESS, userHeaderWidthsList);
    }

    @Override
    public boolean updateUserHeaderWidth(UserCustom[] userCustoms) throws Exception {
        return userService.updateUserHeaderWidth(userCustoms);
    }

}
