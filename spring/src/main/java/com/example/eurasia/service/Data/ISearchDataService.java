package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseResult;

public interface ISearchDataService {
    ResponseResult searchData(String userID, QueryCondition[] queryConditionsArr, long offset, long limit) throws Exception;
}
