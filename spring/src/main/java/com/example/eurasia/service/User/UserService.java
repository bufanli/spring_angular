package com.example.eurasia.service.User;

import com.example.eurasia.dao.UserDao;
import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.Data.DataXMLReader;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.entity.User.UserCustom;
import com.example.eurasia.entity.User.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserService {

    // 属性注入
    // 加入UserDao作为成员变变量
    @Autowired
    private UserDao userDao;
    // 注意这里要增加get和set方法
    public UserDao getUserDao() {
        return this.userDao;
    }
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    // 生成的表名
    public static final String TABLE_USER_BASIC_INFO = "userBasicInfoTable";//用户基本信息
    public static final String TABLE_USER_ACCESS_AUTHORITY = "userAccessAuthorityTable";//用户权限
    public static final String TABLE_USER_QUERY_CONDITION_DISPLAY = "userQueryConditionDisplayTable";//用户查询条件显示
    public static final String TABLE_USER_HEADER_WIDTH = "userHeaderWidthTable";//用户列宽
    public static final String TABLE_USER_HEADER_DISPLAY = "userHeaderDisplayTable";//用户列显示

    // 生成表用的bean。key字段名 value字段类型
    public static final String BEAN_NAME_USER_BASIC_INFO = "userBasicInfo";//用户基本信息。
    public static final String BEAN_NAME_USER_ACCESS_AUTHORITY = "userAccessAuthority";//用户权限。
    public static final String BEAN_NAME_USER_QUERY_CONDITION_DISPLAY = "userQueryConditionDisplay";//用户查询条件显示。
    public static final String BEAN_NAME_USER_HEADER_WIDTH = "userHeaderWidth";//用户列宽。
    public static final String BEAN_NAME_USER_HEADER_DISPLAY = "userHeaderDisplay";//用户列显示。

    //  生成表用的bean。key字段名 value默认值
    public static final String BEAN_NAME_BASIC_INFO_DEFAULT = "basicInfoDefault";//用户默认基本信息。
    public static final String BEAN_NAME_BASIC_INFO_ADMIN = "basicInfoAdmin";//管理员用户基本信息。
    public static final String BEAN_NAME_ACCESS_AUTHORITY_DEFAULT = "accessAuthorityDefault";//用户默认权限。
    public static final String BEAN_NAME_ACCESS_AUTHORITY_ADMIN = "accessAuthorityAdmin";//管理员用户权限。
    public static final String BEAN_NAME_QUERY_CONDITION_DISPLAY_DEFAULT = "queryConditionDisplayDefault";//用户默认查询条件显示。
    public static final String BEAN_NAME_QUERY_CONDITION_DISPLAY_ADMIN = "queryConditionDisplayAdmin";//管理员用户查询条件显示。
    public static final String BEAN_NAME_HEADER_WIDTH_DEFAULT = "headerWidthDefault";//用户列宽。
    public static final String BEAN_NAME_HEADER_WIDTH_ADMIN = "headerWidthAdmin";//用户列宽。
    public static final String BEAN_NAME_HEADER_DISPLAY_DEFAULT = "headerDisplayDefault";//用户默认列显示。
    public static final String BEAN_NAME_HEADER_DISPLAY_ADMIN = "headerDisplayAdmin";//管理员用户列显示。

    // 默认用户名字
    public static final String USER_DEFAULT = "sinoshuju_default";//默认用户
    public static final String USER_ADMINISTRATOR = "sinoshuju_admin";//管理员用户
    public static final String USER_GUEST = "sinoshuju_guest";//临时访问客户
    public static final String USER_ALL = "sinoshuju_all";//所有的客户,包括默认用户，管理员用户，临时访问客户等。
    public static final String USER_ALL_NORMAL = "sinoshuju_all_normal";//所有的客户,不包括默认用户，管理员用户，临时访问客户等,仅仅是注册的客户。

    // 必要的字段名称
    public static final String MUST_USER_ID = "userID";
    public static final String MUST_USER_PW = "密码";
    public static final String MUST_USER_NAME = "名字";
    public static final String MUST_USER_PHONE = "手机号码";
    public static final String MUST_USER_VALID = "有效期";
    public static final String MUST_PRODUCT_DATE = "日期";
    public static final String MUST_PRODUCT_NUMBER = "商品编码";

    //登陆用字段
    public static final String LOGIN_USER_ID = "用户名";
    public static final String LOGIN_USER_PW = "密码";

    public static final String PERMITION_TRUE = "TRUE";//有权限
    public static final String PERMITION_TRUE_ANY = "TRUE_ANY";//有权限,所有数据无限制
    public static final String PERMITION_FALSE = "FALSE";//没有权限

    public static final String COLUMN_SHOW_MORE = "更多";//在显示数据的表格最后增加的列

    /**
     * 添加数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public void userServiceInit() throws Exception {
        try {
            //创建用户用表
            if (this.createTable(UserService.TABLE_USER_BASIC_INFO,BEAN_NAME_USER_BASIC_INFO) == true) {
                //给以下字段添加唯一属性
                this.addUnique(UserService.TABLE_USER_BASIC_INFO, UserService.MUST_USER_ID);
                this.addUnique(UserService.TABLE_USER_BASIC_INFO, UserService.MUST_USER_PHONE);

                //添加默认用户，管理员，临时客户以及其相关数据
                this.addUserForBasicInfo(UserService.USER_ADMINISTRATOR,null);
                this.addUserForBasicInfo(UserService.USER_DEFAULT,null);

            }
            if (this.createTable(UserService.TABLE_USER_ACCESS_AUTHORITY,BEAN_NAME_USER_ACCESS_AUTHORITY) == true) {
                //给以下字段添加唯一属性
                this.addUnique(UserService.TABLE_USER_ACCESS_AUTHORITY, UserService.MUST_USER_ID);

                //添加默认用户，管理员，临时客户以及其相关数据
                this.addUserForAccessAuthority(UserService.USER_ADMINISTRATOR,null);
                this.addUserForAccessAuthority(UserService.USER_DEFAULT,null);
            }
            if (this.createTable(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,BEAN_NAME_USER_QUERY_CONDITION_DISPLAY) == true) {
                //给以下字段添加唯一属性
                this.addUnique(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY, UserService.MUST_USER_ID);

                //添加默认用户，管理员，临时客户以及其相关数据
                this.addUserForQueryConditionDisplay(UserService.USER_ADMINISTRATOR,null);
                this.addUserForQueryConditionDisplay(UserService.USER_DEFAULT,null);
            }
            //if (this.createTable(UserService.TABLE_USER_HEADER_WIDTH,BEAN_NAME_USER_HEADER_WIDTH) == true) {
                //添加默认用户，管理员，临时客户以及其相关数据
            //}
            if (this.createTable(UserService.TABLE_USER_HEADER_DISPLAY,BEAN_NAME_USER_HEADER_DISPLAY) == true) {
                //给以下字段添加唯一属性
                this.addUnique(UserService.TABLE_USER_HEADER_DISPLAY, UserService.MUST_USER_ID);

                //添加默认用户，管理员，临时客户以及其相关数据
                this.addUserForHeaderDisplay(UserService.USER_ADMINISTRATOR,null);
                this.addUserForHeaderDisplay(UserService.USER_DEFAULT,null);
            }

            //创建session用表
            getUserDao().createSpringSessionTable();
            getUserDao().createSpringSessionAttributesTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据表名称创建一张表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public boolean createTable(String tableName, String beanName) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(beanName)) {
            return false;
        }

        try {
            return getUserDao().createTable(tableName,beanName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据表名称创建一张表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public boolean addUnique(String tableName, String columnName) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
            return false;
        }

        try {
            return getUserDao().addUnique(tableName,columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加用户
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public boolean addDefaultUser(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return false;
        }

        try {
            this.addUserForBasicInfo(userID,null);
            this.addUserForAccessAuthority(userID,null);
            this.addUserForQueryConditionDisplay(userID,null);
            this.addUserForHeaderDisplay(userID,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 添加用户
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public boolean addUser(UserInfo userInfo) throws Exception {

        if (userInfo == null) {
            return false;
        } else {
            this.addUserForBasicInfo(userInfo.getUserIDFromBasicInfos(),
                    this.userCustomsArrToData(userInfo.getUserBasicInfos()));
            this.addUserForAccessAuthority(userInfo.getUserIDFromBasicInfos(),
                    this.userCustomsArrToData(userInfo.getUserDetailedInfos().getUserAccessAuthorities()));
            this.addUserForQueryConditionDisplay(userInfo.getUserIDFromBasicInfos(),
                    this.userCustomsArrToData(userInfo.getUserDetailedInfos().getUserQueryConditionDisplays()));
            this.addUserForHeaderDisplay(userInfo.getUserIDFromBasicInfos(),
                    this.userCustomsArrToData(userInfo.getUserDetailedInfos().getUserHeaderDisplays()));
        }
        return true;

    }

    /**
     * 添加用户基本信息(默认用户，管理员，临时客户的case，只在初始化时执行)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    private int addUserForBasicInfo(String userID, Data value) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return -1;
        }

        ApplicationContext context;
        DataXMLReader dataXMLReader;
        Data data;
        switch (userID) {
            case UserService.USER_DEFAULT:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_BASIC_INFO_DEFAULT);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_BASIC_INFO,data);
            case UserService.USER_ADMINISTRATOR:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_BASIC_INFO_ADMIN);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_BASIC_INFO,data);
            case UserService.USER_GUEST:
                return getUserDao().addUser(UserService.TABLE_USER_BASIC_INFO,null);
            default:
                return getUserDao().addUser(UserService.TABLE_USER_BASIC_INFO,value);
        }
    }

    /**
     * 添加用户权限信息(默认用户，管理员，临时客户的case，只在初始化时执行)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    private int addUserForAccessAuthority(String userID, Data value) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return -1;
        }

        ApplicationContext context;
        DataXMLReader dataXMLReader;
        Data data;
        switch (userID) {
            case UserService.USER_DEFAULT:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_ACCESS_AUTHORITY_DEFAULT);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_ACCESS_AUTHORITY,data);
            case UserService.USER_ADMINISTRATOR:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_ACCESS_AUTHORITY_ADMIN);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_ACCESS_AUTHORITY,data);
            case UserService.USER_GUEST:
                return getUserDao().addUser(UserService.TABLE_USER_ACCESS_AUTHORITY,null);
            default:
                return getUserDao().addUser(UserService.TABLE_USER_ACCESS_AUTHORITY,value);
        }
    }

    /**
     * 添加用户查询条件信息(默认用户，管理员，临时客户的case，只在初始化时执行)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    private int addUserForQueryConditionDisplay(String userID, Data value) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return -1;
        }

        ApplicationContext context;
        DataXMLReader dataXMLReader;
        Data data;
        switch (userID) {
            case UserService.USER_DEFAULT:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_QUERY_CONDITION_DISPLAY_DEFAULT);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,data);
            case UserService.USER_ADMINISTRATOR:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_QUERY_CONDITION_DISPLAY_ADMIN);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,data);
            case UserService.USER_GUEST:
                return getUserDao().addUser(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,null);
            default:
                return getUserDao().addUser(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,value);
        }
    }

    /**
     * 添加用户列宽信息(默认用户，管理员，临时客户的case，只在初始化时执行)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    private int addUserForHeaderWidth(String userID, Data value) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return -1;
        }

        switch (userID) {
            case UserService.USER_DEFAULT:
                return -1;
            case UserService.USER_ADMINISTRATOR:
                return -1;
            case UserService.USER_GUEST:
                return -1;
            default:
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_WIDTH,value);
        }
    }

    /**
     * 添加用户列显示信息(默认用户，管理员，临时客户的case，只在初始化时执行)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    private int addUserForHeaderDisplay(String userID, Data value) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return -1;
        }

        ApplicationContext context;
        DataXMLReader dataXMLReader;
        Data data;
        switch (userID) {
            case UserService.USER_DEFAULT:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_HEADER_DISPLAY_DEFAULT);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_DISPLAY,data);
            case UserService.USER_ADMINISTRATOR:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_HEADER_DISPLAY_ADMIN);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_DISPLAY,data);
            case UserService.USER_GUEST:
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_DISPLAY,null);
            default:
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_DISPLAY,value);
        }
    }

    /**
     * 该用户是否在数据库中。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 00:00:00
     */
    public int getUserIDNumber(String userID) throws Exception {
        if (StringUtils.isEmpty(userID) == true) {
            return -1;
        }
        return getUserDao().queryUserID(UserService.TABLE_USER_BASIC_INFO,UserService.MUST_USER_ID,userID);
    }

    /**
     * 该电话号码是否在数据库中。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 00:00:00
     */
    public int getUserPhoneNumber(String phone) throws Exception {
        if (StringUtils.isEmpty(phone) == true) {
            return -1;
        }
        return getUserDao().queryUserID(UserService.TABLE_USER_BASIC_INFO,UserService.MUST_USER_PHONE,phone);
    }

    /**
     * 判断用户名和密码对不对。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-10 00:00:00
     */
    public String getUserPassWord(String userID) throws Exception {
        if (StringUtils.isEmpty(userID) == true) {
            return "";
        }

        return this.getOneUserCustom(UserService.TABLE_USER_BASIC_INFO,UserService.MUST_USER_PW,userID);
    }

    /**
     * 取得用户有效期。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-10 00:00:00
     */
    public String getUserValid(String userID) throws Exception {
        if (StringUtils.isEmpty(userID) == true) {
            return "";
        }

        return this.getOneUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,UserService.MUST_USER_VALID,userID);
    }

    /**
     * 用户信息对象数组转成Data。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-07 00:00:00
     */
    public Data userCustomsArrToData(UserCustom[] userCustoms) throws Exception {
        LinkedHashMap<String, String> keyValue = new LinkedHashMap<>();
        for (UserCustom userCustom:userCustoms) {
            keyValue.put(userCustom.getKey(),userCustom.getValue());
        }

        return new Data(keyValue);
    }

    /**
     * 获取数据库中最近的一个月。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-15 00:00:00
     */
    public String[] getUserTheLastMonth(String tableName, String userID) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(userID)) {
            return null;
        }

        switch (userID) {
            case UserService.USER_DEFAULT:
            case UserService.USER_ADMINISTRATOR:
            case UserService.USER_GUEST:
                //查询数据库中的最近一个月
                return getUserDao().queryListForTheLastMouth(tableName,UserService.MUST_PRODUCT_DATE);
            default:
                //查询用户可见的最近一个月
                List<Data> userBasicInfosList = this.getUserBasicInfo(userID);
                if (userBasicInfosList != null) {
                    String strDate = "";

                    Data userBasicInfo = userBasicInfosList.get(0);
                    Set<Map.Entry<String, String>> set = userBasicInfo.getKeyValue().entrySet();
                    Iterator<Map.Entry<String, String>> it = set.iterator();
                    while (it.hasNext()) {
                        Map.Entry<String,String> entry = it.next();
                        if (entry.getKey().equals(UserService.MUST_PRODUCT_DATE)) {
                            strDate = entry.getValue();
                            break;
                        }
                    }

                    return strDate.split(QueryCondition.QUERY_CONDITION_SPLIT);
                } else {
                    return null;
                }
        }

    }

    /**
     * 取得所有的用户基本属性。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<Data> getAllUserBasicInfo() throws Exception {

        List<Data> userBasicInfosList = this.getUserCustom(UserService.TABLE_USER_BASIC_INFO, UserService.USER_ALL);

        return userBasicInfosList;
    }

    /**
     * 取得用户基本属性。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<Data> getUserBasicInfo(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<Data> userBasicInfosList = this.getUserCustom(UserService.TABLE_USER_BASIC_INFO, userID);

        return userBasicInfosList;
    }

    /**
     * 保存用户基本属性。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public boolean updateUserBasicInfo(String userID, UserCustom[] userCustoms) throws Exception {
        if (StringUtils.isEmpty(userID) || userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_BASIC_INFO,userID,userCustoms);
    }

    /**
     * 获取用户访问权限。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-21 00:00:00
     */
    public List<Data> getUserAccessAuthority(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<Data> userAccessAuthoritiesList = this.getUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY, userID);

        return userAccessAuthoritiesList;
    }

    /**
     * 保存用户访问权限。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public boolean updateUserAccessAuthority(String userID, UserCustom[] userCustoms) throws Exception {
        if (StringUtils.isEmpty(userID) || userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY,userID,userCustoms);
    }

    /**
     * 取得用户可显示的查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<String> getUserQueryConditionDisplayByTrue(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<String> userQueryConditionDisplayList = this.getUserTrueCustom(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID);

        return userQueryConditionDisplayList;
    }

    /**
     * 取得用户可显示的查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-07 00:00:00
     */
    public List<Data> getUserQueryConditionDisplay(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<Data> userQueryConditionDisplayList = this.getUserCustom(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID);

        return userQueryConditionDisplayList;
    }

    /**
     * 保存用户可显示的查询条
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public boolean updateUserQueryConditionDisplay(String userID, UserCustom[] userCustoms) throws Exception {
        if (StringUtils.isEmpty(userID) || userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID,userCustoms);
    }

    /**
     * 取得可显示的表头的宽度
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<Data> getUserHeaderWidth(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        //T.B.D依赖于getUserHeaderDisplay的返回值
        List<Data> userHeaderWidthsList = this.getUserCustom(UserService.TABLE_USER_HEADER_WIDTH, userID);

        return userHeaderWidthsList;
    }

    /**
     * 保存可显示的表头的宽度
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public boolean updateUserHeaderWidth(String userID, UserCustom[] userCustoms) throws Exception {
        if (StringUtils.isEmpty(userID) || userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_HEADER_WIDTH,userID,userCustoms);
    }

    /**
     * 取得可显示的表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<String> getUserHeaderDisplayByTrue(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<String> userHeaderDisplayList = this.getUserTrueCustom(UserService.TABLE_USER_HEADER_DISPLAY,userID);

        return userHeaderDisplayList;
    }

    /**
     * 取得可显示的表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<Data> getUserHeaderDisplay(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<Data> userHeaderDisplayList = this.getUserCustom(UserService.TABLE_USER_HEADER_DISPLAY,userID);

        return userHeaderDisplayList;
    }

    /**
     * 保存可显示的表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public boolean updateUserHeaderDisplay(String userID, UserCustom[] userCustoms) throws Exception {
        if (StringUtils.isEmpty(userID) || userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_HEADER_DISPLAY,userID,userCustoms);
    }

    /**
     * 获取用户自定义属性。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-21 00:00:00
     */
    private List<Data> getUserCustom(String tableName, String userID) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(userID)) {
            return null;
        }

        List<Data> customsList;
        if (userID.equals(UserService.USER_ALL)) {
            customsList = getUserDao().queryListForAllUserCustom(tableName,null);
        } else if (userID.equals(UserService.USER_ALL_NORMAL)) {
            ArrayList<String> strArray = new ArrayList<String> ();
            strArray.add(UserService.USER_DEFAULT);
            strArray.add(UserService.USER_ADMINISTRATOR);
            customsList = getUserDao().queryListForAllUserCustom(tableName,strArray);
        } else {
            customsList = getUserDao().queryListForUserCustom(tableName,userID);
            if (customsList.size() != 1) {
                return null;
            }
        }

        return customsList;
    }

    /**
     * 保存用户自定义属性。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    private boolean updateUserCustom(String tableName, String userID, UserCustom[] userCustoms) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(userID) ||userCustoms == null) {
            return false;
        }

        int num = getUserDao().updateUserCustom(tableName,userID,userCustoms);
        if (num > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取用户自定义属性是TRUE的属性。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-21 00:00:00
     */
    private List<String> getUserTrueCustom(String tableName, String userID) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(userID)) {
            return null;
        }

        List<List<String>> displaysList = getUserDao().queryListForUserTrueCustom(tableName,userID);
        if (displaysList.size() != 1) {
            return null;
        }
        List<String> displayList = displaysList.get(0);
        return displayList;
    }

    /**
     * 获取指定的用户属性的值。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-07 00:00:00
     */
    public String getOneUserCustom(String tableName, String columnName, String userID) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName) || StringUtils.isEmpty(userID)) {
            return null;
        }

        List<Data> dataList = getUserDao().queryOneForUserCustom(tableName,columnName,userID);
        if (dataList.size() != 1) {
            return null;
        }
        if (dataList.get(0).getKeyValue().size() != 1) {
            return null;
        }

        return dataList.get(0).getKeyValue().get(columnName);
    }
}

