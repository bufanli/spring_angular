package com.example.eurasia.service.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("DeleteSameDataServiceImpl")
@Component
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
