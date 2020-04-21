package com.example.eurasia.task;

import com.example.eurasia.service.Data.DeleteSameDataServiceImpl;
import com.example.eurasia.service.Data.IDeleteSameDataService;
import com.example.eurasia.service.User.UserInfoServiceImpl;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class DeleteSameDataTask{
    @Qualifier("DeleteSameDataServiceImpl")
    @Autowired
    private DeleteSameDataServiceImpl deleteSameDataService;

    @Scheduled(cron = "0 59 23 * * ?")
    public void deleteSameData() {
        Slf4jLogUtil.get().info("删除重复数据启动");
        try {
            deleteSameDataService.deleteSameData();
        }catch(Exception ex){

            Slf4jLogUtil.get().info("删除重复数据失败");
        }
        Slf4jLogUtil.get().info("删除重复数据结束");
    }
}
