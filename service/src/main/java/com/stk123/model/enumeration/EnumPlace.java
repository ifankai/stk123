package com.stk123.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
public enum EnumPlace {
    SH(1), SZ(2);

    @Getter
    private Integer place;

    public static EnumPlace getPlace(Integer place) {
        if(place == null) return null;
        for(EnumPlace em : EnumPlace.values()){
            if(em.getPlace().intValue() == place.intValue()){
                return em;
            }
        }
        return null;
    }

    public static boolean isSH(Integer place){
        return Objects.equals(SH.place, place);
    }
}
