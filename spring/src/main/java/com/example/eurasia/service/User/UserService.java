package com.example.eurasia.service.User;

import com.example.eurasia.dao.UserDao;
import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.DataXMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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

    // 登陆用户ID
    private String userID;
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public static final String PERMITION_TRUE = "TRUE";//有权限
    public static final String PERMITION_FALSE = "FALSE";//没有权限

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
            this.createTable(UserService.TABLE_USER_BASIC_INFO,BEAN_NAME_USER_BASIC_INFO);
            this.createTable(UserService.TABLE_USER_ACCESS_AUTHORITY,BEAN_NAME_USER_ACCESS_AUTHORITY);
            this.createTable(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,BEAN_NAME_USER_QUERY_CONDITION_DISPLAY);
            //this.createTable(UserService.TABLE_USER_HEADER_WIDTH,BEAN_NAME_USER_HEADER_WIDTH);
            this.createTable(UserService.TABLE_USER_HEADER_DISPLAY,BEAN_NAME_USER_HEADER_DISPLAY);

            this.addDefaultUser(UserService.USER_DEFAULT);
            this.addDefaultUser(UserService.USER_ADMINISTRATOR);
            this.addDefaultUser(UserService.USER_GUEST);

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
        try {
            return getUserDao().createTable(tableName,beanName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加默认用户，管理员，临时客户以及其相关数据,初始化时用。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    private boolean addDefaultUser(String userID) throws Exception {
        try {
            this.addUserForBasicInfo(userID,null);
            this.addUserForAccessAuthority(userID,null);
            this.addUserForQueryConditionDisplay(userID,null);
            //this.addUserForHeaderWidth(userID,null);
            this.addUserForHeaderDisplay(userID,null);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加用户基本信息(默认用户，管理员，临时客户的case，只在初始化时执行)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public int addUserForBasicInfo(String userID, Data value) throws Exception {
        switch (userID) {
            case UserService.USER_DEFAULT:
                return -1;
            case UserService.USER_ADMINISTRATOR:
                return -1;
            case UserService.USER_GUEST:
                return -1;
            default:
                return getUserDao().addUser(UserService.TABLE_USER_BASIC_INFO,userID,value);
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
    public int addUserForAccessAuthority(String userID, Data value) throws Exception {
        ApplicationContext context;
        DataXMLReader dataXMLReader;
        Data data;
        switch (userID) {
            case UserService.USER_DEFAULT:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_ACCESS_AUTHORITY_DEFAULT);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_ACCESS_AUTHORITY,userID,data);
            case UserService.USER_ADMINISTRATOR:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_ACCESS_AUTHORITY_ADMIN);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_ACCESS_AUTHORITY,userID,data);
            case UserService.USER_GUEST:
                return getUserDao().addUser(UserService.TABLE_USER_ACCESS_AUTHORITY,userID,null);
            default:
                return getUserDao().addUser(UserService.TABLE_USER_ACCESS_AUTHORITY,userID,value);
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
    public int addUserForQueryConditionDisplay(String userID, Data value) throws Exception {
        ApplicationContext context;
        DataXMLReader dataXMLReader;
        Data data;
        switch (userID) {
            case UserService.USER_DEFAULT:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_QUERY_CONDITION_DISPLAY_DEFAULT);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID,data);
            case UserService.USER_ADMINISTRATOR:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_QUERY_CONDITION_DISPLAY_ADMIN);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID,data);
            case UserService.USER_GUEST:
                return getUserDao().addUser(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID,null);
            default:
                return getUserDao().addUser(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID,value);
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
    public int addUserForHeaderWidth(String userID, Data value) throws Exception {
        switch (userID) {
            case UserService.USER_DEFAULT:
                break;
            case UserService.USER_ADMINISTRATOR:
                break;
            case UserService.USER_GUEST:
                break;
            default:
                break;
        }
        return getUserDao().addUser(UserService.TABLE_USER_HEADER_WIDTH,userID,value);
    }

    /**
     * 添加用户列显示信息(默认用户，管理员，临时客户的case，只在初始化时执行)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public int addUserForHeaderDisplay(String userID, Data value) throws Exception {
        ApplicationContext context;
        DataXMLReader dataXMLReader;
        Data data;
        switch (userID) {
            case UserService.USER_DEFAULT:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_HEADER_DISPLAY_DEFAULT);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_DISPLAY,userID,data);
            case UserService.USER_ADMINISTRATOR:
                context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
                dataXMLReader = (DataXMLReader) context.getBean(UserService.BEAN_NAME_HEADER_DISPLAY_ADMIN);
                data = new Data(dataXMLReader.getKeyValue());
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_DISPLAY,userID,data);
            case UserService.USER_GUEST:
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_DISPLAY,userID,null);
            default:
                return getUserDao().addUser(UserService.TABLE_USER_HEADER_DISPLAY,userID,value);
        }
    }

    /**
     * 获取用户可以搜索的日期区间。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-15 00:00:00
     */
    public String[] getUserCanSearchDate(String tableName) throws Exception {

        return null;
    }

    /**
     * 获取用户默认搜索日期区间,数据库中最新的一个月。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-15 00:00:00
     */
    public String[] getUserTheLastMonth(String tableName) throws Exception {

        return null;
    }

    /**
     * 取得用户自定义属性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<String> getUserCustom(String tableName, String userID) throws Exception {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(userID)) {
            return null;
        }

        List<List<String>> userCustomsList = getUserDao().queryListForUserCustom(tableName,userID);
        if (userCustomsList.size() != 1) {
            return null;
        }
        List<String> userCustomList = userCustomsList.get(0);

        return userCustomList;
    }

    /**
     * 取得可显示的表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<String> getHeaderDisplay(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<List<String>> userCustomsList = getUserDao().queryListForUserCustom(UserService.TABLE_USER_HEADER_DISPLAY,userID);
        if (userCustomsList.size() != 1) {
            return null;
        }
        List<String> userCustomList = userCustomsList.get(0);

        return userCustomList;
    }

    /**
     * 取得可显示的查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<String> getQueryConditionDisplay(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<List<String>> userCustomsList = getUserDao().queryListForUserCustom(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID);
        if (userCustomsList.size() != 1) {
            return null;
        }
        List<String> queryConditionDisplayList = userCustomsList.get(0);

        return queryConditionDisplayList;
    }

}

