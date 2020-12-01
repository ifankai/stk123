package com.stk123.model.core;

import com.stk123.util.ServiceUtils;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

import static com.stk123.model.core.BarSeries.EnumPeriod.DAY;

@Data
public class BarSeries {

    public enum EnumPeriod {
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
        Bar last = list.peekLast();
        if(last != null){
            if(bar.after(last)){
                throw new RuntimeException("Cannot add bar which after existing bar.");
            }
            bar.setAfter(last);
            last.setBefore(bar);
        }
        list.add(bar);
        if(restoration){

            Bar after = bar.getAfter();
            if(after != null && bar.getClose() != after.getLastClose()) {
                double factor = after.getLastClose() / bar.getClose(); //复权因子
                restorationChange = factor * restorationChange;
//                System.out.println("date="+bar.getDate()+",factor="+factor+",restorationChange="+restorationChange);
            }
            if(restorationChange != 1) {
//                System.out.println("bar.getHigh() * restorationChange=="+bar.getHigh() * restorationChange);
//                System.out.println("bar.getOpen() * restorationChange=="+bar.getOpen() * restorationChange);
//                System.out.println("bar.getLow() * restorationChange=="+bar.getLow() * restorationChange);
                bar.setOpen(ServiceUtils.numberFormat(bar.getOpen() * restorationChange, 2));
                bar.setClose(ServiceUtils.numberFormat(bar.getClose() * restorationChange, 2));
                bar.setHigh(ServiceUtils.numberFormat(bar.getHigh() * restorationChange, 2));
                bar.setLow(ServiceUtils.numberFormat(bar.getLow() * restorationChange, 2));
            }
        }
        return this;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("restoration=" + restoration).append(", restorationChange=" + restorationChange).append(", typePeriod=" + typePeriod).append("\n");
        for(Bar bar : list){
            sb.append(bar.toString()).append("\n");
        }
        return sb.toString();
    }
}
