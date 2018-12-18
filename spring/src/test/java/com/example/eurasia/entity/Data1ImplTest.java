package com.example.eurasia.entity;

import com.example.eurasia.entity.Data.Data1Impl;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class Data1ImplTest {

    @Test
    public void contextLoads() {

    }

    @Test
    public void testConstructor() {
        Resource resource = new ClassPathResource("com/example/eurasia/config/applicationContext.xml");
        BeanFactory factory=new DefaultListableBeanFactory();
        BeanDefinitionReader bdr=new XmlBeanDefinitionReader((BeanDefinitionRegistry) factory);
        bdr.loadBeanDefinitions(resource);
        Data1Impl data = (Data1Impl)factory.getBean("Data1Impl");
        data.toString();
    }
}
