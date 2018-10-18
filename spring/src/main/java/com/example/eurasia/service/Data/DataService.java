package com.example.eurasia.service.Data;

import com.example.eurasia.dao.DataDao;
import com.example.eurasia.entity.Data;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    // 属性注入
    // 加入DataDao作为成员变变量
    private DataDao dataDao;
    // 注意这里要增加get和set方法
    public DataDao getDataDao() {
        return this.dataDao;
    }
    public void setDataDao(DataDao dataDao) {
        this.dataDao = dataDao;
    }

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
     * 查询数据
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public void searchData(String tableName, Data data) {
        getDataDao().queryForObject(tableName, data);
    }

    /**
     * 取得表头
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-09-20 00:00:00
     */
    public String getHeaders(String tableName) {
        return getDataDao().queryListForColumnName(tableName);
    }

}
