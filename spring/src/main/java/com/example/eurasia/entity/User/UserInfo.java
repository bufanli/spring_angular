package com.example.eurasia.entity.User;

import com.example.eurasia.service.User.UserService;

/**
 * 用户类
 * @author FuJia
 * @Time 2018-11-12 00:00:00
 */
public class UserInfo implements Cloneable {

    private UserCustom[] userBasicInfos;
    private UserDetailedInfos userDetailedInfos;

    // add for controller
    public UserInfo(){
    }
    public UserInfo (UserCustom[] userBasicInfos, UserDetailedInfos userDetailedInfos) {
        this.userBasicInfos = new UserCustom[userBasicInfos.length];
        System.arraycopy(userBasicInfos, 0, this.userBasicInfos, 0, userBasicInfos.length);

        this.userDetailedInfos = new UserDetailedInfos(userDetailedInfos.getUserAccessAuthorities(),
                userDetailedInfos.getUserQueryConditionDisplays(),
                userDetailedInfos.getUserHeaderDisplays());
    }

    public void setUserBasicInfos(UserCustom[] userBasicInfos) {
        this.userBasicInfos = new UserCustom[userBasicInfos.length];
        System.arraycopy(userBasicInfos, 0, this.userBasicInfos, 0, userBasicInfos.length);
    }
    public UserCustom[] getUserBasicInfos() {
        return this.userBasicInfos;
    }
    public void setUserDetailedInfos(UserDetailedInfos userDetailedInfos) {
        this.userDetailedInfos = new UserDetailedInfos(userDetailedInfos.getUserAccessAuthorities(),
                userDetailedInfos.getUserQueryConditionDisplays(),
                userDetailedInfos.getUserHeaderDisplays());
    }
    public UserDetailedInfos getUserDetailedInfos() {
        return this.userDetailedInfos;
    }

    public String getIDFromBasicInfos() {
        for (UserCustom userCustom:this.userBasicInfos) {
            if (userCustom.getKey().equals(UserService.MUST_ID)) {
                return userCustom.getValue();
            }
        }
        return null;
    }

    public String getUserIDFromBasicInfos() {
        for (UserCustom userCustom:this.userBasicInfos) {
            if (userCustom.getKey().equals(UserService.MUST_USER_ID)) {
                return userCustom.getValue();
            }
        }
        return null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

