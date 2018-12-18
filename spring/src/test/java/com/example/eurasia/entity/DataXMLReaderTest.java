package com.example.eurasia.entity;

import com.example.eurasia.entity.Data.DataXMLReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataXMLReaderTest {

    private DataXMLReader dataXMLReader;

    @Test
    public void contextLoads() {

    }

    public void testMapCopy() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(1);
        map.put("name", "Marydon");
        LinkedHashMap<String, Object> map2 = new LinkedHashMap<String, Object>(1);
        map2.put("age", 20);
//        // 测试⼀一:是否实现拷⻉贝
//        dataXMLReader.mapCopy(map2, map);
//        System.out.println(map);// {age=20, name=Marydon}
//        System.out.println(map2);// {age=20}
//        // 测试⼆二:拷⻉贝后的map对象是否受原map对象的影响 map2.clear();
//        System.out.println(map);// {age=20, name=Marydon}
//        System.out.println(map2);// {}
    }
}

/*
Junit常用注解：

@Before：初始化方法
@After：释放资源
@Test：测试方法，在这里可以测试期望异常和超时时间
@Ignore：忽略的测试方法
@BeforeClass：针对所有测试，只执行一次，且必须为static void
@AfterClass：针对所有测试，只执行一次，且必须为static void
@RunWith：指定使用的单元测试执行类
Junit测试用例执行顺序：

@BeforeClass ==> @Before ==> @Test ==> @After ==> @AfterClass
过程：就是先加载模拟的环境，再进行测试。
 */