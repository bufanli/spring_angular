package com.example.eurasia.service.Util;

import com.example.eurasia.entity.Data.QueryCondition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: DataProcessingUtil
 * @Description: TODO
 * @Author xiaohuai
 * @Date 2019-05-25 10:52
 * @Version 1.0
 */
public class DataProcessingUtil {

    public static String[] getListMapValuesOfOneColumn(List<Map<String, Object>> listMaps) {
        String[] values = new String[listMaps.size()];
        int i = 0;
        for (Map<String, Object> map : listMaps) {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                //System.out.print(m.getKey());
                //System.out.println(m.getValue());
                values[i] = new String();
                values[i] = (String) m.getValue();
                i++;
            }
        }
        return values;
    }

    public static String getListMapValuesOfOneColumnWithQueryConditionSplit(List<Map<String, Object>> listMaps) {
        StringBuffer values = new StringBuffer();
        for (Map<String, Object> map : listMaps) {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                //System.out.print(m.getKey());
                //System.out.println(m.getValue());
                values.append(m.getValue() + QueryCondition.QUERY_CONDITION_SPLIT);
            }
        }
        if (values.indexOf(QueryCondition.QUERY_CONDITION_SPLIT) >= 0) {
            values.delete((values.length() - QueryCondition.QUERY_CONDITION_SPLIT.length()),values.length());
        }
        return values.toString();
    }

    /**
     * 检查是否为空行
     * @param rowList
     * @return
     */
    public static boolean checkNullRow(List<String> rowList){
        boolean isNull = false;
        String temp;
        for(int i=0,size = rowList.size();i<size;i++){
            temp = rowList.get(i);
            if(temp == null || temp.trim().length() == 0)continue;
            isNull = true;
            break;
        }
        return isNull;
    }

    /**
     * List转String
     * @param
     * @return
     */
    public static String listToStringWithComma(List<String> src) {
        StringBuilder sb = new StringBuilder();

        for (String str : src) {
            sb.append(str);
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - ",".length());
        return sb.toString();
    }

    /**
     * Set转String
     * @param
     * @return
     */
    public static String setToStringWithComma(Set<String> src) {
        StringBuilder sb = new StringBuilder();

        for (String str : src) {
            sb.append(str);
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - ",".length());
        return sb.toString();
    }
}
