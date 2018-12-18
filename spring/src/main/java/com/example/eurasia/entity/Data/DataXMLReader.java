package com.example.eurasia.entity.Data;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.*;

public class DataXMLReader {

    private String filePath;
    private LinkedHashMap<String, String> keyValue;

    public void init() {
        parseXml();
    }

    private void parseXml() {
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            /* Just for check path
            URL url1 = DataXMLReader.class.getResource("");
            URL url2 = DataXMLReader.class.getResource("/");
            URL url3 = DataXMLReader.class.getResource("/com");
            URL url4 = DataXMLReader.class.getResource("/com/example/eurasia");
            */

            ClassLoader loader = DataXMLReader.class.getClassLoader();
            InputStream inputStream = loader.getResourceAsStream(this.getFilePath());
            doc = reader.read(inputStream);
/* e.g.
1)InputStream inStream=DaoFactory.class.getResourceAsStream("dao.properties");
​2)inStream=DaoFactory.class.getResourceAsStream("/com/jdbc/dao/dao.properties")
3)inStream=DaoFactory.class.getClassLoader().getResourceAsStream("com/jdbc/dao/dao.properties")
*/
        } catch (DocumentException e) {
            //System.out.println("加载xml初始化文件出错" + e);
            e.printStackTrace();
        }
        this.keyValue = new LinkedHashMap<>();//有序的。但，跟listOrderMap的区别？T.B.D
        Element root = doc.getRootElement();
        List eleList = root.selectNodes("/mapping/column");
        Iterator it = eleList.iterator();
        while (it.hasNext()) {// 循环读取每个节点
            Element entry = (Element)it.next();
            String key = entry.attributeValue("key");//字段名
            String value = entry.attributeValue("value");//字段类型
            //System.out.println(key + value);
            this.keyValue.put(key, value);
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public LinkedHashMap<String, String> getKeyValue() {
        return this.keyValue;
    }

    /**
     * 复制map对象
     * @explain 将paramsMap中的键值对全部拷⻉贝到resultMap中;
     * paramsMap中的内容不不会影响到resultMap(深拷⻉贝)
     * @param paramsMap 被拷⻉贝对象
     * @param resultMap 拷⻉贝后的对象
     */
    public static void mapCopy(LinkedHashMap<String, String> paramsMap, LinkedHashMap<String, String> resultMap) {
        if (resultMap == null) resultMap = new LinkedHashMap<String, String>();
        if (paramsMap == null) return;
        Iterator it = paramsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            resultMap.put(String.valueOf(key), paramsMap.get(key) != null ? paramsMap.get(key) : "");
        }
    }

}
