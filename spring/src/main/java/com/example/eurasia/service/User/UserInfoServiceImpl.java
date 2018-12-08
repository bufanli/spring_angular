package com.example.eurasia.service.User;

import com.example.eurasia.entity.*;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoServiceImpl")
@Component
public class UserInfoServiceImpl implements IUserInfoService {

    //UserService
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Override
    public String getUserID(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String userID = (String)session.getAttribute("userID");

        if (!StringUtils.isEmpty(userID) && userService.isUserIDExist(userID) == true) {
            return userID;
        } else {
            return null;
        }
    }

    public boolean isUserIDExist(String userID) throws Exception {
        if (!StringUtils.isEmpty(userID) && userService.isUserIDExist(userID) == true) {
            return true;
        }

        return false;
    }

    @Override
    public ResponseResult updateUser(UserInfo userInfo) throws Exception {
        if (null == userInfo) {
            new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_FAILED);
        }

        if (this.checkUserInfo(userInfo) == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_CHECK_INFO_FAILED);
        }

        log.info("保存用户的基本信息开始");
        boolean isupdateSuccessful = userService.updateUserBasicInfo(userInfo.getUserBasicInfos());
        if (isupdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_BASIC_INFO_FAILED);
        }
        log.info("保存用户的基本信息结束");

        log.info("保存用户的访问权限开始");
        isupdateSuccessful = userService.updateUserAccessAuthority(userInfo.getUserDetailedInfos().getUserAccessAuthorities());
        if (isupdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_ACCESS_AUTHORITY_INFO_FAILED);
        }
        log.info("保存用户的访问权限结束");

        log.info("保存用户的可见查询条件开始");
        isupdateSuccessful = userService.updateUserQueryConditionDisplay(userInfo.getUserDetailedInfos().getUserQueryConditionDisplays());
        isupdateSuccessful = true;//T.B.D
        if (isupdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_QUERY_CONDITION_DISPLAY_FAILED);
        }
        log.info("保存用户的可见查询条件结束");

        log.info("保存用户的可见表头开始");
        isupdateSuccessful = userService.updateUserHeaderDisplay(userInfo.getUserDetailedInfos().getUserHeaderDisplays());
        isupdateSuccessful = true;//T.B.D
        if (isupdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_HEADER_DISPLAY_FAILED);
        }
        log.info("保存用户的可见表头结束");

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_UPDATE_SUCCESS);
    }

    @Override
    public boolean addUser(UserInfo userInfo) throws Exception {
        if (null == userInfo) {
            return false;
        }

        if (this.checkUserInfo(userInfo) == false) {
            return false;
        }

        return userService.addUser(userInfo);
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
            if (userAccessAuthoritiesList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_ZERO);
            }

            userQueryConditionDisplaysList = userService.getUserQueryConditionDisplay(editUserID);
            if (userQueryConditionDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_NULL);
            }
            if (userQueryConditionDisplaysList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO);
            }

            userHeaderDisplaysList = userService.getUserHeaderDisplay(editUserID);
            if (userHeaderDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_HEADER_DISPLAY_FROM_SQL_NULL);
            }
            if (userHeaderDisplaysList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_HEADER_DISPLAY_FROM_SQL_ZERO);
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
     * 保存用户访问权限时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    private boolean checkUserInfo(UserInfo userInfo) {

        if (this.checkUserBasicInfo(userInfo.getUserBasicInfos()) == false) {

        }

        if (this.checkUserAccessAuthority(userInfo.getUserDetailedInfos().getUserAccessAuthorities()) == false) {

        }

        if (this.checkUserQueryConditionDisplay(userInfo.getUserDetailedInfos().getUserAccessAuthorities(),
                userInfo.getUserDetailedInfos().getUserAccessAuthorities()) == false) {

        }

        if (this.checkUserHeaderDisplay(userInfo.getUserDetailedInfos().getUserAccessAuthorities()) == false) {

        }

        return true;//T.B.D
    }

    /**
     * 保存用户基本信息时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private boolean checkUserBasicInfo(UserCustom[] userCustoms) {
        int isOK = 0xFFFF;
        for (UserCustom userCustom:userCustoms) {
            if (userCustom.getKey().equals(userService.MUST_USER_NAME) ||
                    userCustom.getKey().equals(userService.MUST_USER_PHONE)) {
                if (!StringUtils.isEmpty(userCustom.getValue())) {
                    isOK = 0xFFFF << 1;
                    break;
                }
            }
        }

        if (isOK != 0xFFFF) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存用户访问权限时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private boolean checkUserAccessAuthority(UserCustom[] userCustoms) {
        return this.checkProductDateAndProductNumber(userCustoms);
    }

    /**
     * 保存用户可显示的查询条件时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private boolean checkUserQueryConditionDisplay(UserCustom[] userQueryConditionDisplays, UserCustom[] userHeaderDisplays) {
        //可显示的查询条件，应是可显示列的子集
        return true;//T.B.D
    }

    /**
     * 保存用户可显示列时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private boolean checkUserHeaderDisplay(UserCustom[] userCustoms) {
        return this.checkProductDateAndProductNumber(userCustoms);
    }

    /**
     * 保存用户访问权限时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private boolean checkProductDateAndProductNumber(UserCustom[] userCustoms) {
        int isOK = 0xFFFF;
        for (UserCustom userCustom:userCustoms) {
            if (userCustom.getKey().equals(userService.MUST_PRODUCT_DATE) ||
                    userCustom.getKey().equals(userService.MUST_PRODUCT_NUMBER)) {
                String queryConditionArr[] = userCustom.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
                for (String queryCondition : queryConditionArr) {
                    if (!StringUtils.isEmpty(queryCondition)) {
                        isOK = 0xFFFF << 1;
                        break;
                    }
                }
            }
        }

        if (isOK != 0xFFFF) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存用户基本信息时，检查电话号码是否重复
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    public boolean checkUserPhone(UserInfo userInfo) throws Exception {

        try {
            for (UserCustom userCustom:userInfo.getUserBasicInfos()) {
                if (userCustom.getKey().equals(userService.MUST_USER_PHONE)) {
                    return userService.isUserPhoneExist(userCustom.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
