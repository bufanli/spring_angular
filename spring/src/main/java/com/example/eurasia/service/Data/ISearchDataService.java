package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.service.Response.ResponseResult;

public interface ISearchDataService {
    ResponseResult searchData(Data queryConditions) throws Exception;
    ResponseResult searchData() throws Exception;
}
