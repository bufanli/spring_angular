package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("DataDictionaryServiceImpl")
@Component
public class DataDictionaryServiceImpl implements IDataDictionaryService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    private String[] getDataDictionaries() throws Exception {
        return null;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下面是导入csv部分
    @Override
    public ResponseResult importCSVFile(File csvFile) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("*.csv"));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉
            String line = null;
            while((line=reader.readLine())!=null){
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分

                String last = item[item.length-1];//这就是你要的数据了
                //int value = Integer.parseInt(last);//如果是数值，可以转化为数值
                System.out.println(last);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下面是导入csv部分
    @Override
    public ResponseResult exportCSVFile(HttpServletResponse response) throws Exception {
        try {
            String filename = "D:/writers.csv";

            //浏览器下载excel
            response.setContentType("application/csv");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "gbk"));
            PrintWriter pw = response.getWriter();
            pw.println("订单号, 订单状态, 下单日期,商品名称,商品价格");
            pw.flush();
            pw.close();

        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
        }
        return null;
    }

}
