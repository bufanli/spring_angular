package com.example.eurasia.service.User;

import com.example.eurasia.entity.*;
import com.example.eurasia.service.Response.ResponseResult;

public interface IUserInfoService {
    boolean setLoginUserID(String loginUserID) throws Exception;
    ResponseResult updateUser(UserInfo userInfo) throws Exception;
    boolean addUser(UserInfo userInfo) throws Exception;
    ResponseResult getAllUserBasicInfo() throws Exception;
    ResponseResult getUserBasicInfo(String userID) throws Exception;
    ResponseResult getUserDetailedInfos(String userID) throws Exception;
}
