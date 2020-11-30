package com.stk123.model.core;

import com.stk123.util.ServiceUtils;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

import static com.stk123.model.core.BarSeries.EnumPeriod.DAY;

@Data
public class BarSeries {

    enum EnumPeriod {
        DAY, WEEK, MONTH
    }

    private boolean restoration = true; //是否前复权，默认为true
    private double restorationChange = 1; //
    private EnumPeriod typePeriod = DAY; //默认周期是day

    private LinkedList<Bar> list = new LinkedList();


    public BarSeries(){
        this(true);
    }

    public BarSeries(boolean restoration){
        this.restoration = restoration;
    }

    /**
     * 从今天的bar向前add
     * @param bar
     * @return
     */
    public BarSeries add(Bar bar) {
        Bar last = list.getLast();
        if(bar.after(last)){
            throw new RuntimeException("Cannot add bar which after existing bar.");
        }
        list.add(bar);
        if(last != null){
            bar.setBefore(last);
            last.setAfter(bar);
        }
        if(restoration){
            if(restorationChange != 1) {
                bar.setOpen(ServiceUtils.numberFormat(bar.getOpen() * restorationChange, 2));
                bar.setClose(ServiceUtils.numberFormat(bar.getClose() * restorationChange, 2));
                bar.setHigh(ServiceUtils.numberFormat(bar.getHigh() * restorationChange, 2));
                bar.setLow(ServiceUtils.numberFormat(bar.getLow() * restorationChange, 2));
            }
            Bar before = bar.getBefore();
            if(before != null && before.getClose() == bar.getLastClose()) {
                double factor = bar.getClose() / (1 + bar.getChange()) / before.getClose(); //复权因子
                restorationChange = factor * restorationChange;
            }
        }
        return this;
    }
}
