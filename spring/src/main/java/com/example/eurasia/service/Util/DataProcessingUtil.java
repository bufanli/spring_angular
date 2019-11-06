package com.example.eurasia.service.Util;

import com.example.eurasia.entity.Data.QueryCondition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        ArrayList valuesArray = new ArrayList();
        for (Map<String, Object> map : listMaps) {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                valuesArray.add (m.getValue());
            }
        }
        String[] values = new String[valuesArray.size()];
        int i = 0;
        for (Object value:valuesArray) {
            values[i] = (String)value;
            i++;
        }
        return values;
    }

    public static String[] getListMapValuesOfOneColumnForString(List<Map<String, String>> listMaps) {
        String[] values = new String[listMaps.size()];
        int i = 0;
        for (Map<String, String> map : listMaps) {
            for (Map.Entry<String, String> m : map.entrySet()) {
                //System.out.print(m.getKey());
                //System.out.println(m.getValue());
                values[i] = m.getValue();
                i++;
            }
        }
        return values;
    }

    public static String[] getListMapValuesOfOneColumnForString(List<Map<String, String>> listMaps, String strExtend) {
        String[] values = new String[listMaps.size()];
        int i = 0;
        for (Map<String, String> map : listMaps) {
            for (Map.Entry<String, String> m : map.entrySet()) {
                //System.out.print(m.getKey());
                //System.out.println(m.getValue());
                values[i] = m.getValue() + strExtend;
                i++;
            }
        }
        return values;
    }

    public static String[] getSetValuesOfOneColumnForString(Set<String> valueSet, String strExtend) {
        String[] values = new String[valueSet.size()];
        int i = 0;
        for (String value : valueSet) {
            values[i] = value + strExtend;
            i++;
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
    public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_1);//格式化为年月

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    public static String[] getDateBetween(String mouth) throws ParseException {
        String[] dateArr = new String[2];
        SimpleDateFormat sdf = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_4);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(mouth));
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        dateArr[0] = "1";
        dateArr[1] = String.valueOf(days);

        return dateArr;
    }
}
