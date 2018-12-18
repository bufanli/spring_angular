package com.example.eurasia.entity.User;

/**
 * 用户类
 * @author FuJia
 * @Time 2018-12-07 00:00:00
 */
public class UserDetailedInfos implements Cloneable {

    private UserCustom[] userAccessAuthorities;
    private UserCustom[] userQueryConditionDisplays;
    private UserCustom[] userHeaderDisplays;

    public UserDetailedInfos(UserCustom[] userAccessAuthorities,
                             UserCustom[] userQueryConditionDisplays,
                             UserCustom[] userHeaderDisplays) {

        this.userAccessAuthorities = new UserCustom[userAccessAuthorities.length];
        System.arraycopy(userAccessAuthorities, 0, this.userAccessAuthorities, 0, userAccessAuthorities.length);

        this.userQueryConditionDisplays = new UserCustom[userQueryConditionDisplays.length];
        System.arraycopy(userQueryConditionDisplays, 0, this.userQueryConditionDisplays, 0, userQueryConditionDisplays.length);

        this.userHeaderDisplays = new UserCustom[userHeaderDisplays.length];
        System.arraycopy(userHeaderDisplays, 0, this.userHeaderDisplays, 0, userHeaderDisplays.length);
    }

    public void setUserAccessAuthorities(UserCustom[] userAccessAuthorities) {
        this.userAccessAuthorities = new UserCustom[userAccessAuthorities.length];
        System.arraycopy(userAccessAuthorities, 0, this.userAccessAuthorities, 0, userAccessAuthorities.length);
    }
    public UserCustom[] getUserAccessAuthorities() {
        return this.userAccessAuthorities;
    }
    public void setUserQueryConditionDisplays(UserCustom[] userQueryConditionDisplays) {
        this.userQueryConditionDisplays = new UserCustom[userQueryConditionDisplays.length];
        System.arraycopy(userQueryConditionDisplays, 0, this.userQueryConditionDisplays, 0, userQueryConditionDisplays.length);
    }
    public UserCustom[] getUserQueryConditionDisplays() {
        return this.userQueryConditionDisplays;
    }
    public void setUserHeaderDisplays(UserCustom[] userHeaderDisplays) {
        this.userHeaderDisplays = new UserCustom[userHeaderDisplays.length];
        System.arraycopy(userHeaderDisplays, 0, this.userHeaderDisplays, 0, userHeaderDisplays.length);
    }
    public UserCustom[] getUserHeaderDisplays() {
        return this.userHeaderDisplays;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

