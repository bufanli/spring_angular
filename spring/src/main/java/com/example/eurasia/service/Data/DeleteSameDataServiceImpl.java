package com.example.eurasia.service.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DeleteSameDataServiceImpl implements IDeleteSameDataService{
    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    @Override
    public void deleteSameData() throws Exception{
        dataService.deleteSameDataByDistinct(DataService.TABLE_DATA);
    }
}
