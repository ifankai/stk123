package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.enumeration.EnumPeriod;
import com.stk123.model.json.View;
import com.stk123.util.ServiceUtils;
import lombok.Getter;

import java.util.LinkedList;

import static com.stk123.model.enumeration.EnumPeriod.DAY;

public class BarSeries {

    private boolean restoration = false; //是否前复权，默认为false, 通过BarService.updateKline() 统一更新为前复权数据
    private double restorationChange = 1; //
//    @JsonView(View.Default.class)
    private EnumPeriod period = DAY; //默认周期是day

    @JsonView(View.Default.class) @Getter
    private LinkedList<Bar> list = new LinkedList<>();

    private Bar first;


    //////////////////////////////////////////////////////////////////////////////////////

    public BarSeries(){
        this(false);
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

            //Bar after = bar.getAfter();
            if(ServiceUtils.numberFormat(bar.getClose()/(1+bar.getChange()/100d), 2) != bar.getLastClose()) {
                double factor = bar.getClose() / (1+bar.getChange()/100d) / bar.getLastClose(); //复权因子
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

    public void addToFirst(Bar bar) {
        Bar f = this.getFirst();
        if(f != null){
            int c = f.getDate().compareTo(bar.getDate());
            if (c > 0) return;
            else if(c == 0){
                f.setOpen(bar.getOpen());
                f.setClose(bar.getClose());
                f.setHigh(bar.getHigh());
                f.setLow(bar.getLow());
                f.setAmount(bar.getAmount());
                f.setVolume(bar.getVolume());
                f.setChange(bar.getChange());
                return;
            }
            bar.setBefore(f);
            f.setAfter(bar);
        }
        this.list.addFirst(bar);
    }

    public Bar getFirst(){
        if(this.first != null) return first;
        return list.peek();
    }

    public Bar getBar(){
        return this.getFirst();
    }

    public Bar getLast(){
        return list.peekLast();
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

    public int indexOf(String date) {
        return this.indexOfBefore(date);
    }
    public int indexOfAfter(String date) {
        int pos = 0;
        for(Bar k : this.list){
            if(k.getDate().compareTo(date) == 0 ||
                    (k.before(1) != null && k.before(1).getDate().compareTo(date) < 0)){
                break;
            }
            pos ++;
        }
        return pos==this.list.size()?(pos-1):pos;
    }
    public int indexOfBefore(String date) {
        int pos = 0;
        for(Bar k : this.list){
            if(k.getDate().compareTo(date) == 0 || k.getDate().compareTo(date) < 0){
                break;
            }
            pos ++;
        }
        return pos==this.list.size()?(pos-1):pos;
    }
    public Bar getBar(String date, int days) {
        if(this.list.size() == 0)return null;
        if(days < 0){
            return this.list.get(this.list.size()-1);
        }
        return this.list.get(this.indexOf(date)).before(days);
    }
    public Bar getBar(String date) {
        return getBar(date, 0);
    }


    public int size(){
        return this.getList().size();
    }

    public void clear(){
        this.list.clear();
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
