package com.example.eurasia.dao;

import com.example.eurasia.entity.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/*要注意这个DataMapper.java应该要和具体的Sql语句对应。*/
public class DataMapper implements RowMapper<Data> {
    @Override
    public Data mapRow(ResultSet resultSet, int i) throws SQLException {
        //Data data = new Data();
        //data.setKeyValue(resultSet.getInt(1));
        return null;
    }
}
