package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("SearchDataServiceImpl")
@Component
public class SearchDataServiceImpl implements ISearchDataService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult searchData(Data queryConditions) throws Exception {

        Header[] headers;
        List<Data> colsNameList;
        try {
            colsNameList = this.searchDataFromSQL("eurasiaTable", queryConditions);


            colsNameList = this.searchDataFromSQL("eurasiaTable", queryConditions);
            if (colsNameList == null || colsNameList.size() <= 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_NULL);
            }

            headers = new Header[colsNameList.size()];
            int i = 0;
            for(Data colsName: colsNameList) {
                Set<Map.Entry<String, String>> set = colsName.getKeyValue().entrySet();
                Iterator<Map.Entry<String, String>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<String,String> entry = it.next();
                    //System.out.println("Key:" + entry.getKey() + " Value:" + entry.getValue());
                    headers[i] = new SearchDataServiceImpl.Header(entry.getValue(), entry.getValue());
                }
                i++;
            }

            log.info("查询数据结束");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.SEARCH_GET_INFO_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.HEADER_GET_INFO_FROM_SQL_SUCCESS, headers);
    }

    private List<Data> searchDataFromSQL(String tableName, Data queryConditions) {
        if (queryConditions.toString().length() <= 0) {

        } else {

        }
        return dataService.searchData(tableName, queryConditions);
    }

    class Header implements Cloneable {
        private String field;
        private String title;

        Header (String field, String title) {
            this.field = field;
            this.title = title;
        }

        public void setField(String field) {
            this.field = field;
        }
        public String getField() {
            return this.field;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return this.title;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
