package com.stk123.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EnumCate {
    STOCK(1), INDEX(2), FUND(3), INDEX_10jqka(4), INDEX_eastmoney_gn(5);

    @Getter
    private Integer cate;

    public static EnumCate getCate(Integer cate) {
        if(cate == null) return null;
        for(EnumCate em : EnumCate.values()){
            if(em.getCate().intValue() == cate.intValue()){
                return em;
            }
        }
        return null;
    }
}