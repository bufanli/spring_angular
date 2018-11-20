package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;

public interface IGetHeadersService {
    ResponseResult getAllHeaders() throws Exception;
    ResponseResult getHeaderDisplay() throws Exception;
}
