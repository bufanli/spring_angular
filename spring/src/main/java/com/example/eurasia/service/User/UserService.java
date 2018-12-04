package com.example.eurasia.service.User;

import com.example.eurasia.dao.UserDao;
import com.example.eurasia.entity.Data;
import com.example.eurasia.entity.DataXMLReader;
import com.example.eurasia.entity.UserCustom;
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
    public static final String USER_ALL = "sinoshuju_all";//所有的客户,包括默认用户，管理员用户，临时访问客户等。
    public static final String USER_ALL_NORMAL = "sinoshuju_all_normal";//所有的客户,不包括默认用户，管理员用户，临时访问客户等,仅仅是注册的客户。

    // 必要的字段名称
    public static final String NAME_PRODUCT_DATE = "日期";
    public static final String NAME_PRODUCT_NUMBER = "商品编码";

    public static final String PERMITION_TRUE = "TRUE";//有权限
    public static final String PERMITION_TRUE_ANY = "TRUE_ANY";//有权限,所有数据无限制
    public static final String PERMITION_FALSE = "FALSE";//没有权限

    public static final String VALUE_CONDITION_SPLIT = "～～";//字段数据中的的分隔符号

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
     * 添加默认用户，管理员，临时客户以及其相关数据,初始化时用。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    private boolean addDefaultUser(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return false;
        }

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
        if (StringUtils.isEmpty(userID) || value == null) {
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
        if (StringUtils.isEmpty(userID) || value == null) {
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
        if (StringUtils.isEmpty(userID) || value == null) {
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
        if (StringUtils.isEmpty(userID) || value == null) {
            return -1;
        }

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
        if (StringUtils.isEmpty(userID) || value == null) {
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
     * 该用户是否在数据库中。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 00:00:00
     */
    public boolean isUserIDExist(String userID) throws Exception {
        if (getUserDao().queryUserID(userID) == 0) {
            return false;
        }
        return true;
    }

    /**
     * 添加用户时，检查数据的有效性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    public boolean checkUserData(String userID, Data data) throws Exception {
        if (StringUtils.isEmpty(userID) || data == null) {
            return false;
        }

        return getUserDao().checkUserData(userID,data);
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
                return getUserDao().queryListForTheLastMouth(tableName,UserService.NAME_PRODUCT_DATE);
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
                        if (entry.getKey().equals(UserService.NAME_PRODUCT_DATE)) {
                            strDate = entry.getValue();
                            break;
                        }
                    }

                    return strDate.split(UserService.VALUE_CONDITION_SPLIT);
                } else {
                    return null;
                }
        }

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
    public boolean updateUserBasicInfo(UserCustom[] userCustoms) throws Exception {
        if (userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_BASIC_INFO, userCustoms);
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
    public boolean updateUserAccessAuthority(UserCustom[] userCustoms) throws Exception {
        if (userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_ACCESS_AUTHORITY, userCustoms);
    }

    /**
     * 取得用户可显示的查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<String> getUserQueryConditionDisplay(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<String> userQueryConditionDisplayList = this.getUserCustomDisplay(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY,userID);

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
    public boolean updateUserQueryConditionDisplay(UserCustom[] userCustoms) throws Exception {
        if (userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_QUERY_CONDITION_DISPLAY, userCustoms);
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
    public boolean updateUserHeaderWidth(UserCustom[] userCustoms) throws Exception {
        if (userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_HEADER_WIDTH, userCustoms);
    }

    /**
     * 取得可显示的表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-18 00:00:00
     */
    public List<String> getUserHeaderDisplay(String userID) throws Exception {
        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        List<String> userHeaderDisplayList = this.getUserCustomDisplay(UserService.TABLE_USER_HEADER_DISPLAY,userID);

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
    public boolean updateUserHeaderDisplay(UserCustom[] userCustoms) throws Exception {
        if (userCustoms == null) {
            return false;
        }

        return this.updateUserCustom(UserService.TABLE_USER_HEADER_DISPLAY,userCustoms);
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
    private boolean updateUserCustom(String tableName, UserCustom[] userCustoms) throws Exception {
        if (StringUtils.isEmpty(tableName) || userCustoms == null) {
            return false;
        }

        int num = getUserDao().updateUserCustom(UserService.TABLE_USER_BASIC_INFO, userCustoms);
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
    private List<String> getUserCustomDisplay(String tableName, String userID) throws Exception {
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
}

