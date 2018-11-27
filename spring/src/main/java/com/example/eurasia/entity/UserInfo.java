package com.example.eurasia.entity;

/**
 * 用户类
 * @author FuJia
 * @Time 2018-11-12 00:00:00
 */
public class UserInfo implements Cloneable {

    private UserBasicInfo[] userBasicInfos;
    private UserAccessAuthority[] userAccessAuthorities;

    public UserInfo (UserBasicInfo[] userBasicInfos, UserAccessAuthority[] userAccessAuthorities) {
        this.userBasicInfos = new UserBasicInfo[userBasicInfos.length];
        System.arraycopy(userBasicInfos, 0, this.userBasicInfos, 0, userBasicInfos.length);

        this.userAccessAuthorities = new UserAccessAuthority[userAccessAuthorities.length];
        System.arraycopy(userAccessAuthorities, 0, this.userAccessAuthorities, 0, userAccessAuthorities.length);
    }

    public void setUserBasicInfos(UserBasicInfo[] userBasicInfos) {
        this.userBasicInfos = new UserBasicInfo[userBasicInfos.length];
        System.arraycopy(userBasicInfos, 0, this.userBasicInfos, 0, userBasicInfos.length);
    }
    public UserBasicInfo[] getUserBasicInfos() {
        return this.userBasicInfos;
    }
    public void setUserAccessAuthorities(UserAccessAuthority[] userAccessAuthorities) {
        this.userAccessAuthorities = new UserAccessAuthority[userAccessAuthorities.length];
        System.arraycopy(userAccessAuthorities, 0, this.userAccessAuthorities, 0, userAccessAuthorities.length);
    }
    public UserAccessAuthority[] getUserAccessAuthorities() {
        return this.userAccessAuthorities;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

