package com.example.eurasia.service.User;

import com.example.eurasia.entity.Data.CategorySelections;
import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.entity.User.UserDetailedInfos;
import com.example.eurasia.service.Data.DataService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.DataProcessingUtil;
import com.example.eurasia.service.Util.DateUtils;
import com.example.eurasia.service.Util.HttpSessionEnum;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UserInfoServiceImpl")
@Component
public class UserInfoServiceImpl {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    //注入UserService服务对象
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    public String getLoginUserID(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String loginUserID = (String)session.getAttribute(HttpSessionEnum.LOGIN_ID.getAttribute());
        Slf4jLogUtil.get().info("openid=(" + loginUserID + ")");
        return loginUserID;
    }

    public boolean isUserIDExist(String userID) throws Exception {
        if (!StringUtils.isEmpty(userID) && userService.getUserIDNumber(userID) > 0) {
            Slf4jLogUtil.get().info("用户ID(" + userID + ")已存在");
            return true;
        }

        return false;
    }

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

    public ResponseResult getUserBasicInfo(String editUserID) throws Exception {
        List<Data> userBasicInfosList;
        try {
            userBasicInfosList = userService.getUserBasicInfo(editUserID);
            if (userBasicInfosList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_NULL);
            }
            if (userBasicInfosList.size() != 1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_ZERO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_GET_BASIC_INFO_FROM_SQL_SUCCESS, userBasicInfosList);
    }

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

    public ResponseResult getCategoryList(String[] categories) throws Exception {
        ArrayList<CategorySelections> categorySelectionsList = new ArrayList<>();
        try {
            // 取得所有的查询条件(Data的Map-key是查询条件的key，Data的Map-value是查询条件的type)
            List<Data> allQueryConditionsList = dataService.getAllQueryConditions();
            if (allQueryConditionsList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_NULL);
            }
            if (allQueryConditionsList.size() != 1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.QUERY_CONDITION_FROM_SQL_SIZE_WRONG);
            }

            for (String category:categories) {
                if (!StringUtils.isEmpty(category)) {
                    Set<Map.Entry<String, String>> set = allQueryConditionsList.get(0).getKeyValue().entrySet();
                    Iterator<Map.Entry<String, String>> it = set.iterator();
                    while (it.hasNext()) {
                        Map.Entry<String,String> entry = it.next();
                        if (entry.getKey().equals(category) ) {
                            String[] queryConditionValues = null;
                            switch (entry.getValue()) {
                                case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                                    // 以后List改为翻页形式。2019-06-30。
                                    // 如果是 QueryCondition.QUERY_CONDITION_SPLIT 的话，返回该列所有的元素
                                    List<Map<String, Object>> colValuesListMap = dataService.getColumnAllValuesByGroup(DataService.TABLE_DATA,new String[]{category});
                                    queryConditionValues = DataProcessingUtil.getListMapValuesOfOneColumn(colValuesListMap);
                                break;
                                case QueryCondition.QUERY_CONDITION_TYPE_DATE:
                                case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
                                case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                                case QueryCondition.QUERY_CONDITION_TYPE_STRING:
                                default:
                                    queryConditionValues = new String[]{};
                                    break;
                            }
                            CategorySelections categorySelections = new CategorySelections(category,queryConditionValues);
                            categorySelectionsList.add(categorySelections);
                        } else {

                        }
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.USER_CATEGORY_SELECTIONS_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.USER_CATEGORY_SELECTIONS_SUCCESS, categorySelectionsList.toArray());
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

            SimpleDateFormat sdf = DateUtils.SIMPLE_DATE_FORMAT_1;
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
