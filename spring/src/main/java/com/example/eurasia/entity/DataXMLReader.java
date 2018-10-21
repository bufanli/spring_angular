package com.example.eurasia.entity;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataXMLReader {

    private String filePath;
    private Map<String, String> keyValue;

    public void init() {
        parseXml();
    }

    private void parseXml() {
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(DataXMLReader.class.getClassLoader()
                    .getResourceAsStream(this.getFilePath()));
        } catch (DocumentException e) {
            System.out.println("加载xml初始化文件出错" + e);
            e.printStackTrace();
        }
        this.keyValue = new HashMap<>();
        Element root = doc.getRootElement();
        List<Element> eleList = root.selectNodes("/mapping/column");
        for (Element e : eleList) {// 循环读取每个节点
            String key = e.attributeValue("key");
            String value = e.attributeValue("value");
            System.out.println(key + value);
            this.keyValue.put(key, value);
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public Map<String, String> getKeyValue() {
        return this.keyValue;
    }

    /**
     * 复制map对象
     * @explain 将paramsMap中的键值对全部拷⻉贝到resultMap中;
     * paramsMap中的内容不不会影响到resultMap(深拷⻉贝)
     * @param paramsMap 被拷⻉贝对象
     * @param resultMap 拷⻉贝后的对象
     */
    public static void mapCopy(Map paramsMap, Map resultMap) {
        if (resultMap == null) resultMap = new HashMap();
        if (paramsMap == null) return;
        Iterator it = paramsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            resultMap.put(key, paramsMap.get(key) != null ? paramsMap.get(key) : "");
        }
    }
}