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
    public boolean setLoginUserID(String loginUserID) throws Exception {
        if (userService.isUserIDExist(loginUserID) == false) {
            return false;
        }
        userService.setUserID(loginUserID);
        return true;
    }

    @Override
    public ResponseResult updateUser(UserInfo userInfo) throws Exception {

        log.info("保存用户的基本信息开始");
        boolean isupdateSuccessful = userService.updateUserBasicInfo(userInfo.getUserBasicInfos());
        if (this.checkUserBasicInfo(userInfo.getUserBasicInfos()) == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_CHECK_BASIC_INFO_FAILED);
        }
        if (isupdateSuccessful == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_BASIC_INFO_FAILED);
        }
        log.info("保存用户的基本信息结束");

        log.info("保存用户的访问权限开始");
        if (this.checkUserAccessAuthority(userInfo.getUserDetailedInfos().getUserAccessAuthorities()) == false) {
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_CHECK_ACCESS_AUTHORITY_INFO_FAILED);
        }
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
        return userService.addUser(userService.getUserID(),userInfo);
    }

    @Override
    public ResponseResult getAllUserBasicInfo() throws Exception {
        return this.getUserBasicInfo(userService.USER_ALL);
    }

    @Override
    public ResponseResult getUserBasicInfo(String userID) throws Exception {
        List<Data> userBasicInfosList;
        try {
            userBasicInfosList = userService.getUserBasicInfo(userID);
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
    public ResponseResult getUserDetailedInfos(String userID) throws Exception {
        List<Data> userAccessAuthoritiesList;
        List<Data> userQueryConditionDisplaysList;
        List<Data> userHeaderDisplaysList;
        try {
            userAccessAuthoritiesList = userService.getUserAccessAuthority(userID);
            if (userAccessAuthoritiesList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_NULL);
            }
            if (userAccessAuthoritiesList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_ACCESS_AUTHORITY_FROM_SQL_ZERO);
            }

            userQueryConditionDisplaysList = userService.getUserQueryConditionDisplay(userID);
            if (userQueryConditionDisplaysList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_NULL);
            }
            if (userQueryConditionDisplaysList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO);
            }

            userHeaderDisplaysList = userService.getUserHeaderDisplay(userID);
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

    /**
     * 保存用户访问权限时，检查数据的有效性
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
}
