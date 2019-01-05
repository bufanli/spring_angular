package com.example.eurasia.controller;

import com.example.eurasia.entity.User.UserCustom;
import com.example.eurasia.entity.User.UserInfo;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.IUserInfoService;
import com.example.eurasia.service.User.UserService;
import com.example.eurasia.service.Util.HttpSessionEnum;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//@Slf4j
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
     * @description 用户通过用户名密码登陆
     */
    @RequestMapping(value="/loginUserByUsernamePassword", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult loginUserByUsernamePassword(@RequestBody UserCustom[] userCustoms, HttpServletRequest request) {
        ResponseResult responseResult = null;
        try {
            Slf4jLogUtil.get().info("用户登陆开始");
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

            // 判断openid在数据库中是否存在
            if (userInfoServiceImpl.isUserIDExist(userName) == false) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_NO_USER);
            }
            if (userInfoServiceImpl.checkUserPassWord(userName,userPassword) == false) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_USERNAME_PASSWORD_ERROR);
            }
            if (userInfoServiceImpl.checkUserValid(userName) == false) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_USER_INVALID);
            }

            HttpSession session = request.getSession();
            session.setAttribute(HttpSessionEnum.LOGIN_ID.getAttribute(), userName);
            session.setAttribute(HttpSessionEnum.LOGIN_STATUS.getAttribute(), HttpSessionEnum.LOGIN_STATUS_SUCCESS);

            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.SYSTEM_LOGIN_SUCCESS);
            Slf4jLogUtil.get().info("用户登陆结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_LOGIN_FAILED);
        }
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
            Slf4jLogUtil.get().info("判断添加用户是否已存在开始");
            HttpSession session = request.getSession();
            String userID = (String)session.getAttribute(HttpSessionEnum.ADD_USER_ID.getAttribute());
            if (userInfoServiceImpl.isUserIDExist(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_IS_EXIST);
            } else {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_ADD_IS_NOT_EXIST);
            }
            Slf4jLogUtil.get().info("判断添加用户是否已存在结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_FAILED);
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-22
     * @description 用户通过微信登陆
     */
    @RequestMapping(value="/loginUserByWeChat", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult loginUserByWeChat(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("用户登陆开始");
            HttpSession session = request.getSession();
            String userID = (String)session.getAttribute(HttpSessionEnum.LOGIN_ID.getAttribute());
            if (!StringUtils.isEmpty(userID)) {
                // 扫描登陆的时候，才会有id
                String loginStatus = (String)session.getAttribute(HttpSessionEnum.LOGIN_STATUS.getAttribute());
                if (loginStatus.equals(HttpSessionEnum.LOGIN_STATUS_SUCCESS)) {
                    responseResult = new ResponseResultUtil().success(ResponseCodeEnum.SYSTEM_LOGIN_SUCCESS);
                } else if (loginStatus.equals(HttpSessionEnum.LOGIN_STATUS_NO_USER)) {
                    responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_NO_USER);
                } else {
                    responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
                }
            } else {
                // unlogin
                session.setAttribute(HttpSessionEnum.LOGIN_STATUS.getAttribute(),HttpSessionEnum.LOGIN_STATUS_UN_LOGIN);
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_NOT_ING);
            }
            Slf4jLogUtil.get().info("用户登陆结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 更新用户
     */
    @RequestMapping(value="/updateUser", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult updateUser(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("更新用户开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {

                if (null == userInfo) {
                    new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_FAILED);
                }

                responseResult = userInfoServiceImpl.updateUser(userInfo);
            }
            Slf4jLogUtil.get().info("更新用户结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_FAILED);
        }
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
            Slf4jLogUtil.get().info("添加用户开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                return new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {

                if (null == userInfo) {
                    new ResponseResultUtil().error(ResponseCodeEnum.USER_ADD_FAILED);
                }

                responseResult = userInfoServiceImpl.addUser(userInfo);
            }
            Slf4jLogUtil.get().info("添加用户结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
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
            Slf4jLogUtil.get().info("取得所有用户的基本信息开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getAllUserBasicInfo();
            }
            Slf4jLogUtil.get().info("取得所有用户的基本信息结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
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
            Slf4jLogUtil.get().info("取得用户默认的基本信息开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserDefaultBasicInfo();
            }
            Slf4jLogUtil.get().info("取得用户默认的基本信息结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户的基本信息
     */
    @RequestMapping(value="/getUserBasicInfo/{editUserID}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserBasicInfo(HttpServletRequest request, @PathVariable(value="editUserID") String editUserID) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("取得用户的基本信息开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserBasicInfo(editUserID);
            }
            Slf4jLogUtil.get().info("取得用户的基本信息结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 用户名密码登陆后取得用户的基本信息
     */
    @RequestMapping(value="/getUserBasicInfoByLogin/{loginUserID}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserBasicInfoByLogin(HttpServletRequest request, @PathVariable(value="loginUserID") String loginUserID) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("用户名密码登陆后取得用户的基本信息开始");
            // 此处没有检查用户名是否存在，是因为在登陆时loginUserByUsernamePassword检查过了。
            responseResult = userInfoServiceImpl.getUserBasicInfo(loginUserID);
            Slf4jLogUtil.get().info("用户名密码登陆后取得用户的基本信息结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
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
            Slf4jLogUtil.get().info("取得用户默认的详细信息开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserDefaultDetailedInfos();
            }
            Slf4jLogUtil.get().info("取得用户默认的详细信息结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
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
            Slf4jLogUtil.get().info("取得用户的详细信息开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = userInfoServiceImpl.getUserDetailedInfos(editUserID);
            }
            Slf4jLogUtil.get().info("取得用户的详细信息结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-07
     * @description 用户名密码登陆后取得用户的详细信息(访问权限，可显示列等)
     */
    @RequestMapping(value="/getUserDetailedInfosByLogin/{loginUserID}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDetailedInfosByLogin(HttpServletRequest request, @PathVariable(value="loginUserID") String loginUserID) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("用户名密码登陆后取得用户的详细信息开始");
            // 此处没有检查用户名是否存在，是因为在登陆时loginUserByUsernamePassword检查过了。
            responseResult = userInfoServiceImpl.getUserDetailedInfos(loginUserID);
            Slf4jLogUtil.get().info("用户名密码登陆后取得用户的详细信息结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

}
