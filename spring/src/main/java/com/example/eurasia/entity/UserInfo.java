package com.example.eurasia.entity;

/**
 * 用户类
 * @author FuJia
 * @Time 2018-11-12 00:00:00
 */
public class UserInfo implements Cloneable {

    private UserCustom[] userBasicInfos;
    private UserCustom[] userAccessAuthorities;

    public UserInfo (UserCustom[] userBasicInfos, UserCustom[] userAccessAuthorities) {
        this.userBasicInfos = new UserCustom[userBasicInfos.length];
        System.arraycopy(userBasicInfos, 0, this.userBasicInfos, 0, userBasicInfos.length);

        this.userAccessAuthorities = new UserCustom[userAccessAuthorities.length];
        System.arraycopy(userAccessAuthorities, 0, this.userAccessAuthorities, 0, userAccessAuthorities.length);
    }

    public void setUserBasicInfos(UserCustom[] userBasicInfos) {
        this.userBasicInfos = new UserCustom[userBasicInfos.length];
        System.arraycopy(userBasicInfos, 0, this.userBasicInfos, 0, userBasicInfos.length);
    }
    public UserCustom[] getUserBasicInfos() {
        return this.userBasicInfos;
    }
    public void setUserAccessAuthorities(UserCustom[] userAccessAuthorities) {
        this.userAccessAuthorities = new UserCustom[userAccessAuthorities.length];
        System.arraycopy(userAccessAuthorities, 0, this.userAccessAuthorities, 0, userAccessAuthorities.length);
    }
    public UserCustom[] getUserAccessAuthorities() {
        return this.userAccessAuthorities;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

