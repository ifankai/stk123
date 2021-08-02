package com.stk123.model.strategy;

public interface Sortable {

    double getValue();

    void setOrder(int order);

    void setPercentile(double percentile);

    double getPercentile();

    static int compare(boolean asc, Sortable o1, Sortable o2){
        //return (int) (asc ? o1.getValue() - o2.getValue() : o2.getValue() - o1.getValue());
        return asc ? (o1.getValue() > o2.getValue() ? 1:-1) : (o2.getValue() > o1.getValue() ? 1:-1);
    }

}
