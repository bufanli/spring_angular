package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;

public interface IGetQueryConditionsService {
    ResponseResult getAllQueryConditions() throws Exception;
    ResponseResult getQueryConditionDisplay(String userID) throws Exception;
}
