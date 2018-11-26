package com.example.eurasia.service.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoServiceImpl")
@Component
public class UserInfoServiceImpl implements IUserInfoService {

    @Override
    public boolean setLoginUserID(String loginUserID) throws Exception {
        return true;
    }

    @Override
    public void addUser() throws Exception {

    }

    @Override
    public void getUserBasicInfo() throws Exception {

    }

    @Override
    public void setUserBasicInfo() throws Exception {

    }

    @Override
    public void getUserAccessAuthority() throws Exception {

    }

    @Override
    public void setUserAccessAuthority() throws Exception {

    }
}
