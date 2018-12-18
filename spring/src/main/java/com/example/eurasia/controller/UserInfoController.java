package com.example.eurasia.controller;

import com.example.eurasia.entity.User.UserCustom;
import com.example.eurasia.entity.User.UserInfo;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.IUserInfoService;
import com.example.eurasia.service.User.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class UserInfoController {

    //注入Service服务对象
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private IUserInfoService userInfoServiceImpl;

    /**
     * @author
     * @date 2018-12-08
     * @description 设定登陆用户ID
     */
    @RequestMapping(value="/dummyLogin", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult dummyLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userID", UserService.USER_DEFAULT);

        return new ResponseResultUtil().success();
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 设定登陆用户ID
     */
    @RequestMapping(value="/loginAdmin", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult loginAdmin(@RequestBody UserCustom[] userCustoms, HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("设定登陆用户ID开始");
            String userName = null;
            String userPassword = null;
            for (UserCustom userCustom:userCustoms) {
                switch (userCustom.getKey()) {
                    case UserService.LOGIN_USER_ID:
                        userName = userCustom.getValue();
                        break;
                    case UserService.LOGIN_USER_PW:
                        userPassword = userCustom.getValue();
                        break;
                    default:
                        return new ResponseResultUtil().error(ResponseCodeEnum.USER_LOGIN_FAILED);
                }
            }

            if (userInfoServiceImpl.checkUserPassWord(userName,userPassword) == false) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.SYSTEM_LOGIN_SUCCESS);
            }
            if (userInfoServiceImpl.checkUserValid(userName) == false) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_USER_INVALID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_LOGIN_FAILED);
        }
        log.info("设定登陆用户ID结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-08
     * @description 判读用户session是否过期或者userID不存在
     */
    @RequestMapping(value="/getUserID", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserID(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("判断用户session过期开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_NOT_ING);
            } else {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.SYSTEM_LOGIN_ING);
            }
            if (userInfoServiceImpl.checkUserValid(userID) == false) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_USER_INVALID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
        }
        log.info("判断用户session过期结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-08
     * @description 判读添加的用户ID是否已存在
     */
    @RequestMapping(value="/isUserIDExist", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult isUserIDExist(HttpServletRequest request) {
        ResponseResult responseResult;
        boolean isExist = false;
        try {
            log.info("判断添加用户是否已存在开始");
            HttpSession session = request.getSession();
            String userID = (String)session.getAttribute("userID");
            isExist = userInfoServiceImpl.isUserIDExist(userID);
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_ADD_IS_EXIST,isExist);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_FAILED);
        }
        log.info("判断添加用户是否已存在结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 保存(更新)用户
     */
    @RequestMapping(value="/updateUser", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult updateUser(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("保存用户开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.updateUser(userInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_FAILED);
        }
        log.info("保存用户结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 添加用户
     */
    @RequestMapping(value="/addUser", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult addUser(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("添加用户开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {

                if (userInfoServiceImpl.checkUserPhone(userInfo) == false) {
                    return new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_PHONE_FAILED);
                }


                boolean isAddSuccessful = userInfoServiceImpl.addUser(userInfo);
                if (isAddSuccessful == true) {
                    responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_ADD_SUCCESS);
                } else {
                    responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_FAILED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("添加用户结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得所有用户的基本信息
     */
    @RequestMapping(value="/getAllUserBasicInfo", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getAllUserBasicInfo(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("取得所有用户的基本信息开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getAllUserBasicInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得所有用户的基本信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户默认的基本信息
     */
    @RequestMapping(value="/getUserDefaultBasicInfo", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDefaultBasicInfo(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("取得用户默认的基本信息开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserDefaultBasicInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户默认的基本信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户的基本信息
     */
    @RequestMapping(value="/getUserBasicInfo{editUserID}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserBasicInfo(HttpServletRequest request, @PathVariable(value="editUserID") String editUserID) {
        ResponseResult responseResult;
        try {
            log.info("取得用户的基本信息开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserBasicInfo(editUserID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户的基本信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-07
     * @description 取得用户默认的详细信息(访问权限，可显示列等)
     */
    @RequestMapping(value="/getUserDefaultDetailedInfos", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDefaultDetailedInfos(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("取得用户默认的详细信息开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserDefaultDetailedInfos();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户默认的详细信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-07
     * @description 取得用户的详细信息(访问权限，可显示列等)
     */
    @RequestMapping(value="/getUserDetailedInfos/{editUserID}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDetailedInfos(HttpServletRequest request, @PathVariable(value="editUserID") String editUserID) {
        ResponseResult responseResult;
        try {
            log.info("取得用户的详细信息开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserDetailedInfos(editUserID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户的详细信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-08
     * @description 取得用户的可以访问的访问权限
     */
    @RequestMapping(value="/getUserAccessAuthority", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserAccessAuthority(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("取得用户的详细信息开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserAccessAuthority(userID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户的详细信息结束");
        return responseResult;
    }

}
