package com.example.eurasia.service.User;

public interface IUserInfoService {
    boolean setLoginUserID(String loginUserID) throws Exception;
    void addUser() throws Exception;
    void getUserBasicInfo() throws Exception;
    void setUserBasicInfo() throws Exception;
    void getUserAccessAuthority() throws Exception;
    void setUserAccessAuthority() throws Exception;
}
