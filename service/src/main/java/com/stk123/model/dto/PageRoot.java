package com.stk123.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageRoot<T> implements Serializable {

    private static final long serialVersionUID = -5967179816929602484L;

    private String about;
    private Integer count;
    private String key;
    private List<T> list;
    private Integer perPage; //每页数量
    private Integer maxPage;
    private Integer page;

    public PageRoot(){}

    public PageRoot(List<T> list){
        this.list = list;
    }

    public static <T> PageRoot<T> unPageable(List<T> list, Integer count) {
        PageRoot<T> root = new PageRoot<T>(list);
        root.setPage(-1);
        root.setCount(count);
        return root;
    }

    public static <T> PageRoot<T> pageable(List<T> list, Integer page, Integer perPage, Integer count) {
        PageRoot root = new PageRoot(list);
        root.setPage(page);
        root.setPerPage(perPage);
        root.setCount(count);
        return root;
    }

}
