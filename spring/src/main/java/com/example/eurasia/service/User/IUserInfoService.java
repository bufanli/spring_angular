package com.example.eurasia.service.User;

import com.example.eurasia.entity.*;
import com.example.eurasia.service.Response.ResponseResult;

import javax.servlet.http.HttpServletRequest;

public interface IUserInfoService {
    String isUserIDExist(HttpServletRequest request) throws Exception;
    boolean isUserIDExist(String userID) throws Exception;
    ResponseResult updateUser(UserInfo userInfo) throws Exception;
    boolean addUser(UserInfo userInfo) throws Exception;
    ResponseResult getAllUserBasicInfo() throws Exception;
    ResponseResult getUserDefaultBasicInfo() throws Exception;
    ResponseResult getUserBasicInfo(String editUserID) throws Exception;
    ResponseResult getUserDefaultDetailedInfos() throws Exception;
    ResponseResult getUserDetailedInfos(String editUserID) throws Exception;
    boolean checkUserPhone(UserInfo userInfo) throws Exception;
}
