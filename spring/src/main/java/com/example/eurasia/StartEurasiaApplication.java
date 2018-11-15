package com.example.eurasia;

import com.example.eurasia.service.Data.DataService;
import com.example.eurasia.service.User.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
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
        //dataService.createDatabase("eurasia");//T.B.D.
        try {
            dataService.createTable(DataService.TABLE_DATA);
            dataService.createTable(DataService.TABLE_QUERY_CONDITION_DEFAULT_DISPLAY);
            dataService.createTable(DataService.TABLE_QUERY_CONDITION_TYPE);

            dataService.createTable(UserService.TABLE_USER_ACCESS_AUTHORITY);
            dataService.createTable(UserService.TABLE_USER_BASIC_INFO);
            dataService.createTable(UserService.TABLE_USER_QUERY_CONDITION);
            dataService.createTable(UserService.TABLE_USER_COLUMN_WIDTH);
            dataService.createTable(UserService.TABLE_USER_COLUMN_DISPLAY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(StartEurasiaApplication.class, args);
	}
}
