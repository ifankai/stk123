package com.stk123.model.enumeration;

public enum EnumPeriod {
    DAY,D, WEEK,W, MONTH,M;

    public static EnumPeriod getPeriod(String name){
        for(EnumPeriod em : EnumPeriod.values()){
            if(em.name().equalsIgnoreCase(name)){
                return em;
            }
        }
        return null;
    }

    public <T> T select(T d, T w, T m){
        switch (this) {
            case W:
            case WEEK:
                return w;
            case M:
            case MONTH:
                return m;
            default:
                return d;
        }
    }

}
