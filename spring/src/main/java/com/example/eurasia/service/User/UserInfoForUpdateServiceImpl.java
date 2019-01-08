package com.example.eurasia.service.User;

import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.entity.User.UserCustom;
import com.example.eurasia.entity.User.UserInfo;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.PhoneValidatorUtil;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoForUpdateServiceImpl")
@Component
public class UserInfoForUpdateServiceImpl {

    //UserService
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    public ResponseResult updateUser(UserInfo userInfo) throws Exception {
        if (null == userInfo) {
            new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_FAILED);
        }

        String retCheck = this.checkUserInfoForUpdate(userInfo);
        if (!StringUtils.isEmpty(retCheck)) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_CHECK_INFO_FAILED.getCode(),retCheck);
        }

        Slf4jLogUtil.get().info("更新用户ID=(" + userInfo.getUserIDFromBasicInfos() + ")");
        Slf4jLogUtil.get().info("更新用户的基本信息开始");
        boolean isUpdateSuccessful = userService.updateUserBasicInfo(userInfo.getUserIDFromBasicInfos(),userInfo.getUserBasicInfos());
        if (isUpdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_BASIC_INFO_FAILED);
        }
        Slf4jLogUtil.get().info("更新用户的基本信息结束");

        Slf4jLogUtil.get().info("更新用户的访问权限开始");
        isUpdateSuccessful = userService.updateUserAccessAuthority(userInfo.getUserIDFromBasicInfos(),userInfo.getUserDetailedInfos().getUserAccessAuthorities());
        if (isUpdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_ACCESS_AUTHORITY_INFO_FAILED);
        }
        Slf4jLogUtil.get().info("更新用户的访问权限结束");

        Slf4jLogUtil.get().info("更新用户的可见查询条件开始");
        isUpdateSuccessful = userService.updateUserQueryConditionDisplay(userInfo.getUserIDFromBasicInfos(),userInfo.getUserDetailedInfos().getUserQueryConditionDisplays());
        isUpdateSuccessful = true;//T.B.D
        if (isUpdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_QUERY_CONDITION_DISPLAY_FAILED);
        }
        Slf4jLogUtil.get().info("更新用户的可见查询条件结束");

        Slf4jLogUtil.get().info("更新用户的可见表头开始");
        isUpdateSuccessful = userService.updateUserHeaderDisplay(userInfo.getUserIDFromBasicInfos(),userInfo.getUserDetailedInfos().getUserHeaderDisplays());
        isUpdateSuccessful = true;//T.B.D
        if (isUpdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_HEADER_DISPLAY_FAILED);
        }
        Slf4jLogUtil.get().info("更新用户的可见表头结束");

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_UPDATE_SUCCESS);
    }

    /**
     * 更新用户信息时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    private String checkUserInfoForUpdate(UserInfo userInfo) throws Exception {
        StringBuffer ret = new StringBuffer("");
        String id = userInfo.getIDFromBasicInfos();
        if (Integer.parseInt(id) >= 0) {

            ret.append(this.checkUserBasicInfo(id,userInfo.getUserBasicInfos()));
            ret.append(this.checkUserAccessAuthority(id,userInfo.getUserDetailedInfos().getUserAccessAuthorities()));
            ret.append(this.checkUserQueryConditionDisplay(id,userInfo.getUserDetailedInfos().getUserAccessAuthorities(),
                    userInfo.getUserDetailedInfos().getUserAccessAuthorities()));
            ret.append(this.checkUserHeaderDisplay(id,userInfo.getUserDetailedInfos().getUserAccessAuthorities()));

        } else {

        }

        return ret.toString();
    }

    /**
     * 更新用户基本信息时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private String checkUserBasicInfo(String id, UserCustom[] userCustoms) throws Exception {
        StringBuffer ret = new StringBuffer("");
        for (UserCustom userCustom:userCustoms) {
            switch (userCustom.getKey()) {
                case UserService.MUST_USER_ID:
                    if (StringUtils.isEmpty(userCustom.getValue())) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_ID_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (this.isUserIDNotExist(id,userCustom.getValue()) == false) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_ID_IS_EXIST.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                case UserService.MUST_USER_NAME:
                    if (StringUtils.isEmpty(userCustom.getValue())) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_NAME_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                case UserService.MUST_USER_PHONE:
                    if (StringUtils.isEmpty(userCustom.getValue())) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_PHONE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (this.isUserPhoneExist(id,userCustom.getValue()) == false) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_PHONE_IS_EXIST.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (PhoneValidatorUtil.matchPhone(userCustom.getValue(),1) == false) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_PHONE_FORMAT_ERROR.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                default:
                    break;
            }
        }

        return ret.toString();
    }

    /**
     * 更新用户访问权限时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private String checkUserAccessAuthority(String id, UserCustom[] userCustoms) throws Exception {
        StringBuffer ret = new StringBuffer("");
        for (UserCustom userCustom:userCustoms) {
            switch (userCustom.getKey()) {
                case UserService.MUST_USER_VALID:
                    String userValidArr[] = userCustom.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
                    if (userValidArr.length != 2) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_VALID_FORMAT_ERROR.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (StringUtils.isEmpty(userValidArr[0])) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_VALID_FROM_DATE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (StringUtils.isEmpty(userValidArr[1])) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_VALID_TO_DATE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                case UserService.MUST_PRODUCT_DATE:
                    String productDateArr[] = userCustom.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
                    if (productDateArr.length != 2) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_PRODUCT_DATE_FORMAT_ERROR.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (StringUtils.isEmpty(productDateArr[0])) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_PRODUCT_DATE_FROM_DATE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (StringUtils.isEmpty(productDateArr[1])) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_PRODUCT_DATE_TO_DATE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                case UserService.MUST_PRODUCT_NUMBER:
                    String productNumberArr[] = userCustom.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT);
                    boolean isNull = false;
                    for (String productNumber : productNumberArr) {
                        if (!StringUtils.isEmpty(productNumber)) {//只要有一个不为空
                            isNull = true;
                            break;
                        }
                    }
                    if (isNull == false) {
                        ret.append(ResponseCodeEnum.USER_UPDATE_PRODUCT_NUMBER_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                default:
                    break;
            }
        }

        return ret.toString();
    }

    /**
     * 更新用户可显示的查询条件时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private String checkUserQueryConditionDisplay(String id, UserCustom[] userQueryConditionDisplays,
                                                  UserCustom[] userHeaderDisplays) throws Exception {
        //可显示的查询条件，应是可显示列的子集
        StringBuffer ret = new StringBuffer("");
        /* T.B.D Web为对应，一时回避
        for (UserCustom userQueryConditionDisplay:userQueryConditionDisplays) {
            if (userQueryConditionDisplay.getValue().equals(UserService.PERMITION_TRUE) ) {
                for (UserCustom userHeaderDisplay:userHeaderDisplays) {
                    if (userHeaderDisplay.getKey().equals(userQueryConditionDisplay.getKey())) {
                        if (userHeaderDisplay.getValue().equals(UserService.PERMITION_FALSE) ) {
                            //T.B.D
                        }
                    }
                }
            }
        }
        */
        return ret.toString();
    }

    /**
     * 更新用户可显示列时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private String checkUserHeaderDisplay(String id, UserCustom[] userCustoms) throws Exception {
        StringBuffer ret = new StringBuffer("");
        return ret.toString();//T.B.D
    }

    public boolean isUserIDNotExist(String id, String userID) throws Exception {
        if (!StringUtils.isEmpty(id) && !StringUtils.isEmpty(userID)
                && userService.getUserIDNumberExcept(id,userID) == 0) {
            return true;
        }

        Slf4jLogUtil.get().info("用户ID(" + userID + ")已存在");
        return false;
    }

    /**
     * 更新用户基本信息时，检查电话号码是否重复
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    public boolean isUserPhoneExist(String id, String phone) throws Exception {

        if (!StringUtils.isEmpty(id) && !StringUtils.isEmpty(phone)
                && userService.getUserPhoneNumberExcept(id,phone) == 0) {
            return true;
        }
        Slf4jLogUtil.get().info("用户的电话号码(" + phone + ")已存在");
        return false;
    }

}
