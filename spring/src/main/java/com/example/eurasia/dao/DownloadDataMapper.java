package com.example.eurasia.dao;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/*要注意这个*Mapper.java应该要和具体的Sql语句对应。*/
public class DownloadDataMapper implements RowMapper<String[]> {

    @Override
    public String[] mapRow(ResultSet resultSet, int rowNumber) throws SQLException {//T.B.D 返回值Object
        ResultSetMetaData md = resultSet.getMetaData();//获取键名
        int columnCount = md.getColumnCount(); //返回 ResultSet 中的列数。
        /*
        String columnName = md.getColumnName(1); //返回列序号为 int 的列名。
        String columnLabel = md.getColumnLabel(1); //返回此列暗含的标签。
        Boolean isCurrency = md.isCurrency(1); //如果此列包含带有货币单位的一个数字，则返回 true。
        Boolean isReadOnly = md.isReadOnly(1); //如果此列为只读，则返回 true。
        Boolean isAutoIncrement = md.isAutoIncrement(1); //如果此列自动递增，则返回 true。这类列通常为键，而且始终是只读的。
        int columnType = md.getColumnType(1); //返回此列的 SQL 数据类型。这些数据类型包括
        */
        int index = 0;
        String[] dataArr = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            dataArr[index] = resultSet.getString(i);
            index++;
        }

        return dataArr;
    }
}
