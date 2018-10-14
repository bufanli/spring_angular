package com.example.eurasia.controller;

public class SearchResult {
    public final static String NG = "ng";
    public final static String OK= "ok";
    private String code;
    public SearchResult(String code){
        this.code = code;
    }
    public String getCode() {
        return code;
    }
}
