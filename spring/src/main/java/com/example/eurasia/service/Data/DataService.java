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

    public void addData(String tableName, Data data) {
        getDataDao().addData(tableName, data);
    }

    public void deleteData(String tableName, Data data) {
        getDataDao().deleteData(tableName, data);
    }

    public void updateData(String tableName, Data data) {
        getDataDao().updateData(tableName, data);
    }

}
