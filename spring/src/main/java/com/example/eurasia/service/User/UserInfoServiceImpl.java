package com.example.eurasia.service.User;

import com.example.eurasia.entity.*;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoServiceImpl")
@Component
public class UserInfoServiceImpl implements IUserInfoService {

    @Override
    public boolean setLoginUserID(String loginUserID) throws Exception {
        return true;
    }

    @Override
    public ResponseResult saveUser(UserInfo userInfo) throws Exception {

        ResponseResult responseResult;

        log.info("保存用户的基本信息开始");
        boolean isSaveSuccessful = this.saveUserBasicInfo(userInfo.getUserBasicInfos());
        if (isSaveSuccessful == true) {
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_SAVE_BASIC_INFO_SUCCESS);
        } else {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_SAVE_BASIC_INFO_FAILED);
        }
        log.info("保存用户的基本信息结束");

        log.info("保存用户的访问权限开始");
        isSaveSuccessful = this.saveUserAccessAuthority(userInfo.getUserAccessAuthorities());
        if (isSaveSuccessful == true) {
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_SAVE_ACCESS_AUTHORITY_INFO_SUCCESS);
        } else {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_SAVE_ACCESS_AUTHORITY_INFO_FAILED);
        }
        log.info("保存用户的访问权限结束");

        log.info("保存用户的可见表头开始");
        isSaveSuccessful = this.saveUserHeaderDisplay(null);//T.B.D
        if (isSaveSuccessful == true) {
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_SAVE_HEADER_DISPLAY_SUCCESS);
        } else {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_SAVE_HEADER_DISPLAY_FAILED);
        }
        log.info("保存用户的可见表头结束");

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_SAVE_FAILED);
    }

    @Override
    public boolean addUser(UserInfo userInfo) throws Exception {
        return false;
    }

    @Override
    public ResponseResult getUserBasicInfoList() throws Exception {
        return this.getUserBasicInfo("");
    }

    @Override
    public ResponseResult getUserBasicInfo(String userID) throws Exception {
        List<Data> userList = new ArrayList<Data>();
        try {

            if (StringUtils.isEmpty(userID)) {//userID为""的时候，代表全部。

            } else {

            }
            //Dummy
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
            userList.add(data);

            if (userList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_INFO_FROM_SQL_NULL);
            }
            if (userList.size() == 0) {
                return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_INFO_FROM_SQL_SUCCESS, userList);
    }

    @Override
    public boolean saveUserBasicInfo(UserBasicInfo[] userBasicInfos) throws Exception {
        return false;
    }

    @Override
    public ResponseResult getUserAccessAuthority(String userID) throws Exception {
        return null;
    }

    @Override
    public boolean saveUserAccessAuthority(UserAccessAuthority[] userAccessAuthorities) throws Exception {
        return false;
    }

    @Override
    public ResponseResult getUserHeaderDisplay(String userID) throws Exception {
        return null;
    }

    @Override
    public boolean saveUserHeaderDisplay(ColumnDisplay[] columnDisplays) throws Exception {
        return true;
    }

}
