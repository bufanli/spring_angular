package com.example.eurasia.service.Data;

import com.example.eurasia.entity.QueryCondition;
import com.example.eurasia.service.Response.ResponseResult;

public interface ISearchDataService {
    ResponseResult searchData(QueryCondition[] queryConditionsArr) throws Exception;
}
