package com.example.eurasia.service.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoServiceImpl")
@Component
public class UserInfoServiceImpl implements IUserInfoService {
}
