package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.util.ServiceUtils;
import lombok.Data;

import java.util.LinkedList;

import static com.stk123.model.core.BarSeries.EnumPeriod.DAY;

@Data
public class BarSeries {

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

    }

    private boolean restoration = true; //是否前复权，默认为true
    private double restorationChange = 1; //
//    @JsonView(View.Default.class)
    private EnumPeriod period = DAY; //默认周期是day

    @JsonView(View.Default.class)
    private LinkedList<Bar> list = new LinkedList();
    private Bar first;


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

    public Bar getFirst(){
        if(this.first != null) return first;
        return list.peek();
    }

    public Bar setFirstBarFrom(String date){
        if(date == null) {
            this.first = null;
            return null;
        }
        Bar first = this.getFirst();
        if(first != null) {
            if(first.getDate().compareTo(date) > 0) {
                while (first.getDate().compareTo(date) > 0) {
                    //this.list.poll();
                    Bar before = first.before();
                    if(before == null) break;
                    first = this.first = before;
                    //first.setAfter(null);
                }
                if(first.getDate().compareTo(date) < 0) {
                    this.first = this.first.after();
                }
            }else {
                while (first.getDate().compareTo(date) < 0) {
                    Bar after = first.after();
                    if(after == null) break;
                    first = this.first = after;
                }
            }
        }
        return this.first;
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("restoration=" + restoration).append(", restorationChange=" + restorationChange).append(", period=" + period).append("\n");
        for(Bar bar : list){
            sb.append(bar.toString()).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {

    }
}
