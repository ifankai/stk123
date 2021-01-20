package com.stk123.model.elasticsearch;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResult {

    private boolean success;
    private int status;
    private String errorMsg;
    private List<EsDocument> results;


    public static SearchResult success(List<EsDocument> results) {
        return new SearchResult(true, results);
    }

    public static SearchResult failure(int status) {
        return new SearchResult(false, status, null);
    }

    public SearchResult(){}

    public SearchResult(boolean success){
        this.success = success;
    }

    public SearchResult(boolean success, List<EsDocument> results){
        this.success = success;
        this.results = results;
    }

    public SearchResult(boolean success, int status, String errorMsg){
        this.success = success;
        this.status = status;
        this.errorMsg = errorMsg;
    }
}
