package com.example.eurasia.service.Util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: PhoneValidatorUtil
 * @Description: TODO
 * @Author xiaohuai
 * @Date 2019-01-05 11:38
 * @Version 1.0
 */
public class PhoneValidatorUtil {

    public static boolean matchPhone(String number, int type){
        if (StringUtils.isEmpty(number)) {
            return false;
        }

        boolean isPhone = false;
        if (type == 0) {
            //验证固定电话号码的合法性
            Pattern p1 = null, p2 = null;
            Matcher m = null;

            p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
            p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
            if (number.length() > 8) {
                m = p1.matcher(number);
                isPhone = m.matches();
            } else {
                m = p2.matcher(number);
                isPhone = m.matches();
            }
        } else if (type == 1){
            //验证手机电话号码的合法性,目前支持13、14、15、17、18开头的号码
            isPhone = Pattern.matches("^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9]|17[0|6|7|8])\\d{8}$", number);
        }
        return isPhone;
    }

}
