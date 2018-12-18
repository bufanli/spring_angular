package com.example.eurasia.service.User;

import com.example.eurasia.entity.User.UserInfo;
import com.example.eurasia.service.Response.ResponseResult;

import javax.servlet.http.HttpServletRequest;

public interface IUserInfoService {
    String getUserID(HttpServletRequest request) throws Exception;
    boolean isUserIDExist(String userID) throws Exception;
    ResponseResult updateUser(UserInfo userInfo) throws Exception;
    boolean addUser(UserInfo userInfo) throws Exception;
    ResponseResult getAllUserBasicInfo() throws Exception;
    ResponseResult getUserDefaultBasicInfo() throws Exception;
    ResponseResult getUserBasicInfo(String editUserID) throws Exception;
    ResponseResult getUserDefaultDetailedInfos() throws Exception;
    ResponseResult getUserDetailedInfos(String editUserID) throws Exception;
    ResponseResult getUserAccessAuthority(String userID) throws Exception;
    boolean checkUserPhone(UserInfo userInfo) throws Exception;
    boolean checkUserPassWord(String userID, String password) throws Exception;
    boolean checkUserValid(String userID) throws Exception;
}
