package com.stk123.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView(View.Default.class)
public class PageRoot<T> implements Serializable {

    private static final long serialVersionUID = -5967179816929602484L;

    private String about;
    private Long total;
    private String key;
    private List<T> list;
    private Integer pageSize; //每页数量
    private Integer maxPage;
    private Integer page;

    public PageRoot(){}

    public PageRoot(List<T> list){
        this.list = list;
    }

    public static <T> PageRoot<T> unPageable(List<T> list, Long total) {
        PageRoot<T> root = new PageRoot<T>(list);
        root.setPage(-1);
        root.setTotal(total);
        return root;
    }

    public static <T> PageRoot<T> pageable(List<T> list, Integer page, Integer pageSize, Long total) {
        PageRoot root = new PageRoot(list);
        root.setPage(page);
        root.setPageSize(pageSize);
        root.setTotal(total);
        return root;
    }

}
