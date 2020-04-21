package com.example.eurasia;

import com.example.eurasia.service.Data.DataService;
import com.example.eurasia.service.User.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/*
一个@SpringbootApplication相当于@Configuration,@EnableAutoConfiguration和 @ComponentScan 并具有他们的默认属性值
 */
@SpringBootApplication
@EnableScheduling
//@EnableJdbcHttpSession // 加上这个Enable后，启动出现"WARN: Establishing SSL connection without server's identity verification is not recommended."
public class StartEurasiaApplication {
/* 手动配置DataSource数据源
	@Autowired
	private Environment env;

    @Bean
	public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("spring.datasource.jdbc-url"));
        config.setUsername(env.getProperty("spring.datasource.username"));
        config.setPassword(env.getProperty("spring.datasource.password"));
        config.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		return new HikariDataSource(config);
	}
*/
	public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/example/eurasia/config/applicationContext.xml");
        //ApplicationContext context = new FileSystemXmlApplicationContext("main/java/com/example/eurasia/config/applicationContext.xml");
        DataService dataService = (DataService) context.getBean("dataService");
        UserService userService = (UserService) context.getBean("userService");
        //dataService.createDatabase("eurasia");//T.B.D.
        try {
            dataService.dataServiceInit();
            userService.userServiceInit();

        } catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(StartEurasiaApplication.class, args);
	}

    @Bean
    public TaskScheduler taskScheduler(){
        return new ThreadPoolTaskScheduler();
    }
}
