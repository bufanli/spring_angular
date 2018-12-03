package com.example.eurasia.service.User;

import com.example.eurasia.entity.*;
import com.example.eurasia.service.Response.ResponseResult;

public interface IUserInfoService {
    boolean setLoginUserID(String loginUserID) throws Exception;
    ResponseResult updateUser(UserInfo userInfo) throws Exception;
    boolean addUser(UserInfo userInfo) throws Exception;
    ResponseResult getUserBasicInfoList() throws Exception;
    ResponseResult getUserBasicInfo(String userID) throws Exception;
    boolean updateUserBasicInfo(UserCustom[] userCustoms) throws Exception;
    ResponseResult getUserAccessAuthority(String userID) throws Exception;
    boolean updateUserAccessAuthority(UserCustom[] userCustoms) throws Exception;
    ResponseResult getUserQueryConditionDisplay(String userID) throws Exception;
    boolean updateUserQueryConditionDisplay(UserCustom[] userCustoms) throws Exception;
    ResponseResult getUserHeaderDisplay(String userID) throws Exception;
    boolean updateUserHeaderDisplay(UserCustom[] userCustoms) throws Exception;
    ResponseResult getUserHeaderWidth(String userID) throws Exception;
    boolean updateUserHeaderWidth(UserCustom[] userCustoms) throws Exception;
}
