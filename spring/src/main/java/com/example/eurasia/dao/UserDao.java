package com.example.eurasia.dao;

import com.example.eurasia.entity.Data;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserDao extends CommonDao {

    /**
     * 添加用户以及其相关数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public int addUser(String tableName, String userID, Data data) throws Exception {
        StringBuffer sql = new StringBuffer();
        int size = data.getKeyValue().size();
        String columnsNames = data.getKeys();
        String[] columnsValuesArr = data.getValuesToArray();

        sql.append("insert into " + tableName + "(userID," + columnsNames + ") values (");
        sql.append("?,");
        for (int i=0; i<size; i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - ",".length());
        sql.append(")");
        int num = getJdbcTemplate().update(sql.toString(),(Object[])columnsValuesArr);
        return num;//大于0，插入成功。
    }

    /**
     * T.B.D
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-15 23:11:00
     */
    public List getFirstName(int userID)
    {
        String sql = "select firstname from users where user_id = " + userID;

        SingleColumnRowMapper rowMapper = new SingleColumnRowMapper(String.class);
        List firstNameList = (List) getJdbcTemplate().query(sql, rowMapper);

        for(Object firstName: firstNameList)
            System.out.println(firstName.toString());

        return firstNameList;
    }

    /**
     * T.B.D
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-15 23:11:00
     */
    public List<Map<String, Object>> getUserData(int userID)
    {
        String sql = "select firstname, lastname, dept from users where userID = ? ";

        ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
        List<Map<String, Object>> userDataList =  getJdbcTemplate().query(sql, rowMapper, userID);

        for(Map<String, Object> map: userDataList){
            System.out.println("FirstName = " + map.get("firstname"));
            System.out.println("LastName = " + map.get("lastname"));
            System.out.println("Department = " + map.get("dept"));
        }

        return userDataList;

    }

    /**
     * 获取用户自定义属性
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-11-17 00:00:00
     */
    public List<List<String>> queryListForUserCustom(String tableName, String userID) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + tableName + " where user_id = " + userID);
        return getJdbcTemplate().query(sql.toString(), new PermissionMapper());
    }

}
