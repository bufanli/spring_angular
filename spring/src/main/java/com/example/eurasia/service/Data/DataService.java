package com.example.eurasia.service.Data;

import com.example.eurasia.dao.DataDao;
import com.example.eurasia.entity.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataService {

    // 属性注入
    // 加入DataDao作为成员变变量
    @Autowired
    private DataDao dataDao;
    // 注意这里要增加get和set方法
    public DataDao getDataDao() {
        return this.dataDao;
    }
    public void setDataDao(DataDao dataDao) {
        this.dataDao = dataDao;
    }

    public static final String TABLE_NAME = "eurasiaTable";

    /**
     * 添加数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void addData(String tableName, Data data) {
        getDataDao().addData(tableName, data);
    }

    /**
     * 根据查询条件进行数据查询
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Data> searchData(String tableName, Data queryConditions) {
        return getDataDao().queryListForObject(tableName, queryConditions);
    }

    /**
     * 查询所有数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-27 00:00:00
     */
    public List<Data> searchAllData(String tableName) {
        return getDataDao().queryListForAllObject(tableName);
    }

    /**
     * 取得表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public List<Map<String,Object>> getHeaders(String tableName) {
        return getDataDao().queryListForColumnName(tableName);
    }

    /**
     * 取得查询条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public String getQueryConditions(String tableName) {
        return null;//T.B.D
    }

    /**
     * 根据表名称创建一张表
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createTable(String tableName) {
        try {
            return getDataDao().createTable(tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建数据库
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public boolean createDatabase(String databaseName) {
        try {
            return getDataDao().createDatabase(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
