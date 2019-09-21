package com.example.eurasia.service.Data;

import com.example.eurasia.entity.User.UserCustom;
import com.example.eurasia.service.Response.ResponseResult;

public interface IGetHeadersService {
    ResponseResult getAllHeaders() throws Exception;
    ResponseResult getHeaderDisplayByTrue(String userID) throws Exception;
    ResponseResult getHeaderDisplay(String userID) throws Exception;
    ResponseResult setHeaderDisplay(String userID, UserCustom[] userHeaderDisplays) throws Exception;
}
