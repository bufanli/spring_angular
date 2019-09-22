package com.example.eurasia.service.User;

import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoForDeleteServiceImpl")
@Component
public class UserInfoForDeleteServiceImpl {

    //UserService
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    public ResponseResult deleteUser(String userID) throws Exception {
        if (null == userID) {
            Slf4jLogUtil.get().error("删除用户名为NULL");
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_DELETE_FAILED);
        }
        if (userID.equals(UserService.USER_ADMINISTRATOR) || userID.equals(UserService.USER_DEFAULT)) {
            Slf4jLogUtil.get().error("管理员和默认用户不可删除");
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_DELETE_NOT_ALLOWED);
        }

        boolean ret = userService.deleteUser(userID);

        if (ret == false) {
            Slf4jLogUtil.get().error("删除用户失败");
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_DELETE_FAILED);
        }
        Slf4jLogUtil.get().info("删除用户成功");

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_DELETE_SUCCESS);
    }

}
