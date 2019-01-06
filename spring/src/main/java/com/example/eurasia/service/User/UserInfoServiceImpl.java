package com.example.eurasia.service.User;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.entity.User.UserCustom;
import com.example.eurasia.entity.User.UserDetailedInfos;
import com.example.eurasia.entity.User.UserInfo;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.HttpSessionEnum;
import com.example.eurasia.service.Util.PhoneValidatorUtil;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoServiceImpl")
@Component
public class UserInfoServiceImpl implements IUserInfoService {

    //UserService
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Override
    public String getLoginUserID(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String loginUserID = (String)session.getAttribute(HttpSessionEnum.LOGIN_ID.getAttribute());
        Slf4jLogUtil.get().info("openid=(" + loginUserID + ")");
        return loginUserID;
    }

    public boolean isUserIDExist(String userID) throws Exception {
        if (!StringUtils.isEmpty(userID) && userService.getUserIDNumber(userID) == 1) {
            Slf4jLogUtil.get().info("用户ID(" + userID + ")已存在");
            return true;
        }

        return false;
    }

    @Override
    public ResponseResult updateUser(UserInfo userInfo) throws Exception {
        if (null == userInfo) {
            new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_FAILED);
        }

        /* T.B.D 一时回避
        String retCheck = this.checkUserInfo(userInfo);
        if (!StringUtils.isEmpty(retCheck)) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_CHECK_INFO_FAILED.getCode(),retCheck);
        }
        */

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

    @Override
    public ResponseResult addUser(UserInfo userInfo) throws Exception {
        if (null == userInfo) {
            new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_FAILED);
        }

        String retCheck = this.checkUserInfo(userInfo);
        if (!StringUtils.isEmpty(retCheck)) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_CHECK_INFO_FAILED.getCode(),retCheck);
        }

        Slf4jLogUtil.get().info("添加用户ID=(" + userInfo.getUserIDFromBasicInfos() + ")");
        Slf4jLogUtil.get().info("添加用户的基本信息开始");
        int addUserNum = userService.addUserForBasicInfo(userInfo.getUserIDFromBasicInfos(),
                userService.userCustomsArrToData(userInfo.getUserBasicInfos()));
        if (addUserNum <= 0) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_BASIC_INFO_FAILED);
        }
        Slf4jLogUtil.get().info("添加用户的基本信息结束");

        Slf4jLogUtil.get().info("添加用户的访问权限开始");
        addUserNum = userService.addUserForAccessAuthority(userInfo.getUserIDFromBasicInfos(),
                userService.userCustomsArrToData(userInfo.getUserDetailedInfos().getUserAccessAuthorities()));
        if (addUserNum <= 0) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_ACCESS_AUTHORITY_INFO_FAILED);
        }
        Slf4jLogUtil.get().info("添加用户的访问权限结束");

        Slf4jLogUtil.get().info("添加用户的可见查询条件开始");
        addUserNum = userService.addUserForQueryConditionDisplay(userInfo.getUserIDFromBasicInfos(),
                userService.userCustomsArrToData(userInfo.getUserDetailedInfos().getUserQueryConditionDisplays()));
        if (addUserNum <= 0) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_QUERY_CONDITION_DISPLAY_FAILED);
        }
        Slf4jLogUtil.get().info("添加用户的可见查询条件结束");

        Slf4jLogUtil.get().info("添加用户的可见表头开始");
        addUserNum = userService.addUserForHeaderDisplay(userInfo.getUserIDFromBasicInfos(),
                userService.userCustomsArrToData(userInfo.getUserDetailedInfos().getUserHeaderDisplays()));
        if (addUserNum <= 0) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_HEADER_DISPLAY_FAILED);
        }
        Slf4jLogUtil.get().info("添加用户的可见表头结束");

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_ADD_SUCCESS);
    }

    @Override
    public ResponseResult getAllUserBasicInfo() throws Exception {
        List<Data> userBasicInfosList;
        try {
            userBasicInfosList = userService.getAllUserBasicInfo();
            if (userBasicInfosList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ALL_BASIC_INFO_FROM_SQL_NULL);
            }
            if (userBasicInfosList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ALL_BASIC_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ALL_BASIC_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_ALL_BASIC_INFO_FROM_SQL_SUCCESS, userBasicInfosList);
    }

    @Override
    public ResponseResult getUserDefaultBasicInfo() throws Exception {
        List<Data> userBasicInfosList;
        try {
            userBasicInfosList = userService.getUserBasicInfo(UserService.USER_DEFAULT);
            if (userBasicInfosList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_BASIC_INFO_FROM_SQL_NULL);
            }
            if (userBasicInfosList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_BASIC_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_BASIC_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_DEFAULT_BASIC_INFO_FROM_SQL_SUCCESS, userBasicInfosList);
    }

    @Override
    public ResponseResult getUserBasicInfo(String editUserID) throws Exception {
        List<Data> userBasicInfosList;
        try {
            userBasicInfosList = userService.getUserBasicInfo(editUserID);
            if (userBasicInfosList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_NULL);
            }
            if (userBasicInfosList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_SUCCESS, userBasicInfosList);
    }

    @Override
    public ResponseResult getUserDefaultDetailedInfos() throws Exception {
        List<Data> userAccessAuthoritiesList;
        List<Data> userQueryConditionDisplaysList;
        List<Data> userHeaderDisplaysList;
        try {
            userAccessAuthoritiesList = userService.getUserAccessAuthority(UserService.USER_DEFAULT);
            if (userAccessAuthoritiesList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_ACCESS_AUTHORITY_FROM_SQL_NULL);
            }
            if (userAccessAuthoritiesList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_ACCESS_AUTHORITY_FROM_SQL_ZERO);
            }

            userQueryConditionDisplaysList = userService.getUserQueryConditionDisplay(UserService.USER_DEFAULT);
            if (userQueryConditionDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_QUERY_CONDITION_DISPLAY_FROM_SQL_NULL);
            }
            if (userQueryConditionDisplaysList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO);
            }

            userHeaderDisplaysList = userService.getUserHeaderDisplay(UserService.USER_DEFAULT);
            if (userHeaderDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_HEADER_DISPLAY_FROM_SQL_NULL);
            }
            if (userHeaderDisplaysList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_HEADER_DISPLAY_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DEFAULT_DETAILED_INFOS_FAILED);
        }
        UserDetailedInfos userDetailedInfos = new UserDetailedInfos(userAccessAuthoritiesList.get(0).toUserCustomArr(),
                userQueryConditionDisplaysList.get(0).toUserCustomArr(),
                userHeaderDisplaysList.get(0).toUserCustomArr());
        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_DEFAULT_DETAILED_INFOS_SUCCESS, userDetailedInfos);
    }

    @Override
    public ResponseResult getUserDetailedInfos(String editUserID) throws Exception {
        List<Data> userAccessAuthoritiesList;
        List<Data> userQueryConditionDisplaysList;
        List<Data> userHeaderDisplaysList;
        try {
            userAccessAuthoritiesList = userService.getUserAccessAuthority(editUserID);
            if (userAccessAuthoritiesList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_NULL);
            }
            if (userAccessAuthoritiesList.size() != 1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_FAILED);
            }

            userQueryConditionDisplaysList = userService.getUserQueryConditionDisplay(editUserID);
            if (userQueryConditionDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_NULL);
            }
            if (userQueryConditionDisplaysList.size() != 1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_FAILED);
            }

            userHeaderDisplaysList = userService.getUserHeaderDisplay(editUserID);
            if (userHeaderDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_HEADER_DISPLAY_FROM_SQL_NULL);
            }
            if (userHeaderDisplaysList.size() != 1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_HEADER_DISPLAY_FROM_SQL_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_DETAILED_INFOS_FAILED);
        }
        UserDetailedInfos userDetailedInfos = new UserDetailedInfos(userAccessAuthoritiesList.get(0).toUserCustomArr(),
                userQueryConditionDisplaysList.get(0).toUserCustomArr(),
                userHeaderDisplaysList.get(0).toUserCustomArr());
        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_DETAILED_INFOS_SUCCESS, userDetailedInfos);
    }

    @Override
    public ResponseResult getUserAccessAuthority(String userID) throws Exception {
        List<Data> userAccessAuthoritiesList;
        try {
            userAccessAuthoritiesList = userService.getUserAccessAuthority(userID);
            if (userAccessAuthoritiesList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_NULL);
            }
            if (userAccessAuthoritiesList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_ZERO);
            }

//            userAccessAuthorities = new UserCustom[userAccessAuthoritiesList.size()];
//            int i = 0;
//            Set<Map.Entry<String, String>> set = userAccessAuthoritiesList.get(0).getKeyValue().entrySet();
//            Iterator<Map.Entry<String, String>> it = set.iterator();
//            while (it.hasNext()) {
//                Map.Entry<String,String> entry = it.next();
//                userAccessAuthorities[i].setKey(entry.getKey());
//                userAccessAuthorities[i].setValue(entry.getValue());
//                i++;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_SUCCESS, userAccessAuthoritiesList);
    }

    /**
     * 添加/更新用户信息时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    private String checkUserInfo(UserInfo userInfo) throws Exception {
        StringBuffer ret = new StringBuffer("");

        ret.append(this.checkUserBasicInfo(userInfo.getUserBasicInfos()));
        ret.append(this.checkUserAccessAuthority(userInfo.getUserDetailedInfos().getUserAccessAuthorities()));
        ret.append(this.checkUserQueryConditionDisplay(userInfo.getUserDetailedInfos().getUserAccessAuthorities(),
                userInfo.getUserDetailedInfos().getUserAccessAuthorities()));
        ret.append(this.checkUserHeaderDisplay(userInfo.getUserDetailedInfos().getUserAccessAuthorities()));

        return ret.toString();
    }

    /**
     * 添加/更新用户基本信息时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private String checkUserBasicInfo(UserCustom[] userCustoms) throws Exception {
        StringBuffer ret = new StringBuffer("");
        for (UserCustom userCustom:userCustoms) {
            switch (userCustom.getKey()) {
                case UserService.MUST_USER_ID:
                    if (StringUtils.isEmpty(userCustom.getValue())) {
                        ret.append(ResponseCodeEnum.USER_ADD_ID_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (this.isUserIDExist(userCustom.getValue())) {
                        ret.append(ResponseCodeEnum.USER_ADD_ID_IS_EXIST.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                case UserService.MUST_USER_NAME:
                    if (StringUtils.isEmpty(userCustom.getValue())) {
                        ret.append(ResponseCodeEnum.USER_ADD_NAME_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                case UserService.MUST_USER_PHONE:
                    if (StringUtils.isEmpty(userCustom.getValue())) {
                        ret.append(ResponseCodeEnum.USER_ADD_PHONE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (this.isUserPhoneExist(userCustom.getValue()) == false) {
                        ret.append(ResponseCodeEnum.USER_ADD_PHONE_IS_EXIST.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (PhoneValidatorUtil.matchPhone(userCustom.getValue(),1) == false) {
                        ret.append(ResponseCodeEnum.USER_ADD_PHONE_FORMAT_ERROR.getMessage());
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
     * 添加/更新用户访问权限时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private String checkUserAccessAuthority(UserCustom[] userCustoms) throws Exception {
        StringBuffer ret = new StringBuffer("");
        for (UserCustom userCustom:userCustoms) {
            switch (userCustom.getKey()) {
                case UserService.MUST_USER_VALID:
                    String userValidArr[] = userCustom.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
                    if (userValidArr.length != 2) {
                        ret.append(ResponseCodeEnum.USER_ADD_VALID_FORMAT_ERROR.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (StringUtils.isEmpty(userValidArr[0])) {
                        ret.append(ResponseCodeEnum.USER_ADD_VALID_FROM_DATE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (StringUtils.isEmpty(userValidArr[1])) {
                        ret.append(ResponseCodeEnum.USER_ADD_VALID_TO_DATE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    break;
                case UserService.MUST_PRODUCT_DATE:
                    String productDateArr[] = userCustom.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
                    if (productDateArr.length != 2) {
                        ret.append(ResponseCodeEnum.USER_ADD_PRODUCT_DATE_FORMAT_ERROR.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (StringUtils.isEmpty(productDateArr[0])) {
                        ret.append(ResponseCodeEnum.USER_ADD_PRODUCT_DATE_FROM_DATE_IS_NULL.getMessage());
                        ret.append(UserService.BR);
                        break;
                    }
                    if (StringUtils.isEmpty(productDateArr[1])) {
                        ret.append(ResponseCodeEnum.USER_ADD_PRODUCT_DATE_TO_DATE_IS_NULL.getMessage());
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
                        ret.append(ResponseCodeEnum.USER_ADD_PRODUCT_NUMBER_IS_NULL.getMessage());
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
     * 添加/更新用户可显示的查询条件时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private String checkUserQueryConditionDisplay(UserCustom[] userQueryConditionDisplays,
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
     * 添加/更新用户可显示列时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private String checkUserHeaderDisplay(UserCustom[] userCustoms) throws Exception {
        StringBuffer ret = new StringBuffer("");
        return ret.toString();//T.B.D
    }

    /**
     * 添加/更新用户基本信息时，检查电话号码是否重复
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    @Override
    public boolean isUserPhoneExist(UserInfo userInfo) throws Exception {

        try {
            for (UserCustom userCustom:userInfo.getUserBasicInfos()) {
                if (userCustom.getKey().equals(UserService.MUST_USER_PHONE)) {
                    if (userService.getUserPhoneNumber(userCustom.getValue()) == 0) {
                        return true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Slf4jLogUtil.get().info("用户的电话号码已存在");
        return false;
    }

    /**
     * 添加/更新用户基本信息时，检查电话号码是否重复
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    public boolean isUserPhoneExist(String phone) throws Exception {

        if (userService.getUserPhoneNumber(phone) == 0) {
            return true;
        }
        Slf4jLogUtil.get().info("用户的电话号码(" + phone + ")已存在");
        return false;
    }

    /**
     * 用户登陆时，检查账号的有效期
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-15 00:00:00
     */
    public boolean checkUserValid(String userID) throws Exception {

        try {
            String userValid = userService.getUserValid(userID);
            if (StringUtils.isEmpty(userValid)) {
                Slf4jLogUtil.get().info("没有检索到用户(" + userID + ")的有效期");
                return false;
            }

            // 某些用户的有效期，像管理员的有效期一样，只有"～～"的情况
            if (userValid.equals(QueryCondition.QUERY_CONDITION_SPLIT)) {
                return true;
            }

            //因为数据库中，起始和结束时间都有，所有没有对起始和结束时间进行空检查。参照方法checkUserAccessAuthority
            String[] valid = userValid.split(QueryCondition.QUERY_CONDITION_SPLIT,-1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String nowDate = sdf.format(new Date());

            if (valid[1].compareTo(nowDate) >= 0) {//相等返回0，小于返回-1，大于返回1
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Slf4jLogUtil.get().info("用户(" + userID + ")的有效期已过");
        return false;
    }

    /**
     * 判断用户名和密码
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-10 00:00:00
     */
    public boolean checkUserPassWord(String userID, String password) throws Exception {

        Slf4jLogUtil.get().info("用户(" + userID + "),密码(" + password + ")");
        if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(password)) {
            return false;
        }

        String pw = userService.getUserPassWord(userID);
        if (!StringUtils.isEmpty(pw)) {
            if (pw.equals(password)) {
                return true;
            }
        }
        Slf4jLogUtil.get().info("用户密码不正确");
        return false;
    }
}
