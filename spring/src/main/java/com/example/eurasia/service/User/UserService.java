package com.example.eurasia.service.User;

import com.example.eurasia.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // 属性注入
    // 加入UserDao作为成员变变量
    @Autowired
    private UserDao userDao;
    // 注意这里要增加get和set方法
    public UserDao getDataDao() {
        return this.userDao;
    }
    public void setUserDao(UserDao dataDao) {
        this.userDao = userDao;
    }

    public static final String TABLE_USER_BASIC_INFO = "userBasicInfoTable";
    public static final String TABLE_USER_ACCESS_AUTHORITY = "userAccessAuthorityTable";
    public static final String TABLE_USER_QUERY_CONDITION = "userQueryConditionTable";
    public static final String TABLE_USER_COLUMN_WIDTH = "userColumnWidthTable";
    public static final String TABLE_USER_COLUMN_DISPLAY = "userColumnDISPLAYTable";

    /**
     * 获取用户可以搜索的日期区间。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-15 00:00:00
     */
    public String[] getUserCanSearchDate(String databaseName) throws Exception {

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
    public String[] getUserTheLastMonth(String databaseName) throws Exception {

        return null;
    }
}

