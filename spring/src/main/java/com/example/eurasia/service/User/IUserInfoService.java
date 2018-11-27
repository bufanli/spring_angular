package com.example.eurasia.service.User;

import com.example.eurasia.entity.ColumnDisplay;
import com.example.eurasia.entity.UserAccessAuthority;
import com.example.eurasia.entity.UserBasicInfo;
import com.example.eurasia.entity.UserInfo;
import com.example.eurasia.service.Response.ResponseResult;

public interface IUserInfoService {
    boolean setLoginUserID(String loginUserID) throws Exception;
    ResponseResult saveUser(UserInfo userInfo) throws Exception;
    boolean addUser(UserInfo userInfo) throws Exception;
    ResponseResult getUserBasicInfoList() throws Exception;
    ResponseResult getUserBasicInfo(String userID) throws Exception;
    boolean saveUserBasicInfo(UserBasicInfo[] userBasicInfos) throws Exception;
    ResponseResult getUserAccessAuthority(String userID) throws Exception;
    boolean saveUserAccessAuthority(UserAccessAuthority[] userAccessAuthorities) throws Exception;
    ResponseResult getUserHeaderDisplay(String userID) throws Exception;
    boolean saveUserHeaderDisplay(ColumnDisplay[] columnDisplays) throws Exception;
}
