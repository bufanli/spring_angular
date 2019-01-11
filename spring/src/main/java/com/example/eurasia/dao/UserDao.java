package com.example.eurasia.dao;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.entity.User.UserCustom;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao extends CommonDao {

    /**
     * 添加用户以及其相关数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-08 00:00:00
     */
    public int addUser(String tableName, Data data) throws Exception {
        StringBuffer sql = new StringBuffer();
        int size = data.getKeyValue().size();
        String columnsNames = data.getKeys();
        String[] columnsValuesArr = data.getValuesToArray();

        sql.append("insert into " + tableName + " (" + columnsNames + ") values (");
        for (int i=0; i<size; i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - CommonDao.COMMA.length());
        sql.append(")");
        int num = getJdbcTemplate().update(sql.toString(),(Object[])columnsValuesArr);
        return num;//大于0，插入成功。
    }

    /**
     * 查询某表某字段指定值的个数
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 23:11:00
     */
    public Long queryCountOfColumnValue(String tableName, String columnName, String value)
    {
        //String sql = "select 1 from " + tableName +" where userID = '" + userID + "' limit 1";
        String sql = "select count(*) from " + tableName +" where " + columnName + " = '" + value + "'";
        Long count = getJdbcTemplate().queryForObject(sql,Long.class);
        return count;
    }

    /**
     * 查询某表某字段指定值的个数,本条数据以外。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 23:11:00
     */
    public Long queryCountOfColumnValueExcept(String id, String tableName, String columnName, String value)
    {
        String sql = "select count(*) from " + tableName +" where " + columnName + " = '" + value + "' and id no in ('" + id + "')";
        Long count = getJdbcTemplate().queryForObject(sql,Long.class);
        return count;
    }

    /**
     * 获取用户自定义属性是TRUE的属性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public List<List<String>> queryListForUserTrueCustom(String tableName, String userID) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName + " where userID = '" + userID + "'");
        return getJdbcTemplate().query(sql.toString(), new PermissionMapper());
    }

    /**
     * 获取某个数据库表里的用户属性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public List<Data> queryListForUserCustom(String tableName, String userID) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName + " where userID = '" + userID + "'");
        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }

    /**
     * 获取某个数据库表里的指定的用户属性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-07 00:00:00
     */
    public List<Data> queryOneForUserCustom(String tableName, String columnName, String userID) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select " + columnName + " from " + tableName + " where userID = '" + userID + "'");
        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }

    /**
     * 获取某个数据库表里的所有用户属性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-29 00:00:00
     */
    public List<Data> queryListForAllUserCustom(String tableName, ArrayList<String> outUserIDList) throws Exception {

        String sqlAnd = " and ";
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName);
        if (outUserIDList != null && outUserIDList.size() != 0) {
            sql.append(" where ");
            for (String outUserID : outUserIDList) {
                sql.append("userID<>'" + outUserID + "'");
                sql.append(sqlAnd);
            }
            sql.delete((sql.length() - sqlAnd.length()),sql.length());
        }
        List<Data> dataList = getJdbcTemplate().query(sql.toString(), new DataMapper());
        return dataList;
    }

    /**
     * 更新某个数据库表里的用户属性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    public int replaceUserCustom(String tableName, UserCustom[] userCustoms) throws Exception {

/*
replace into 跟 insert 功能类似，不同点在于：replace into 首先尝试插入数据到表中，
1. 如果发现表中已经有此行数据（根据主键或者唯一索引判断）则先删除此行数据，然后插入新的数据。
2. 否则，直接插入新数据。
要注意的是：插入数据的表必须有主键或者是唯一索引！否则的话，replace into 会直接插入数据，这将导致表中出现重复的数据。

在使用REPLACE时，表中必须有唯一索引，而且这个索引所在的字段不能允许空值，否则REPLACE就和INSERT完全一样的。
在执行REPLACE后，系统返回了所影响的行数，如果返回1，说明没有重复的记录，
如果返回2，说明有重复记录，系统先DELETE这条记录，然后再INSERT这条记录。

REPLACE INTO `table` (`unique_column`,`num`) VALUES ('$unique_value',$num);
跟INSERT INTO `table` (`unique_column`,`num`) VALUES('$unique_value',$num) ON DUPLICATE UPDATE num=$num;还是有些区别的.
区别就是replace into的时候会删除老记录。如果表中有一个自增的主键，那么就要出问题了。
首先，因为新纪录与老记录的主键值不同，所以其他表中所有与本表老数据主键id建立的关联全部会被破坏。
其次，就是，频繁的REPLACE INTO 会造成新纪录的主键的值迅速增大。
总有一天。达到最大值后就会因为数据太大溢出了。就没法再插入新纪录了。数据表满了，不是因为空间不够了，而是因为主键的值没法再增加了。
 */
        StringBuffer sql = new StringBuffer();
        StringBuffer sqlKey = new StringBuffer();
        StringBuffer sqlValue = new StringBuffer();
        String[] valueArray = new String[userCustoms.length];


        for (int i=0; i<userCustoms.length; i++) {
            sqlKey.append(userCustoms[i].getKey());
            sqlKey.append(CommonDao.COMMA);

            valueArray[i] = userCustoms[i].getValue();
            sqlValue.append("?,");
        }

        sqlKey.deleteCharAt(sqlKey.length() - CommonDao.COMMA.length());
        sql.append("replace into " + tableName + "(" + sqlKey + ") values (");
        sqlValue.deleteCharAt(sqlValue.length() - CommonDao.COMMA.length());
        sql.append(sqlValue + ")");

        int num = getJdbcTemplate().update(sql.toString(),(Object[])valueArray);
        return num;

    }

    /**
     * 更新某个数据库表里的用户属性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-02 00:00:00
     */
    public int updateUserCustom(String tableName, String userID, UserCustom[] userCustoms) throws Exception {

        StringBuffer sql = new StringBuffer();

        sql.append("update " + tableName + " set ");
        for (int i=0; i<userCustoms.length; i++) {
            sql.append(userCustoms[i].getKey() + " = " + "'" + userCustoms[i].getValue() + "'");
            sql.append(CommonDao.COMMA);
        }
        sql.deleteCharAt(sql.length() - CommonDao.COMMA.length());
        sql.append(" where userID = " + "'" + userID + "'");

        int num = getJdbcTemplate().update(sql.toString());
        return num;

    }

}
