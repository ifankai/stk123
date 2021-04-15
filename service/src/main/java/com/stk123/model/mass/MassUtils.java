package com.stk123.model.mass;

import com.stk123.common.CommonUtils;
import com.stk123.common.ml.KhivaUtils;
import com.stk123.common.util.ImageUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.model.core.Bar;
import com.stk123.model.core.Stock;
import com.stk123.util.ServiceUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CommonsLog
public class MassUtils {

    public static MassResult massByMA(List<Stock> allList, String stockCode, String startDate, int days, String png, double targetDistance){
        return mass(allList, stockCode, startDate, days, png, targetDistance, bar -> bar.getMA(5, Bar.EnumValue.C));
    }

    public static MassResult massByClose(List<Stock> allList, String stockCode, String startDate, int days, String png, double targetDistance){
        return mass(allList, stockCode, startDate, days, png, targetDistance, Bar::getClose);
    }

    public static MassResult mass(List<Stock> allList, String stockCode, String startDate, int days, String png, double targetDistance, Function<Bar, Double> function){
        List<Stock> stocks = new ArrayList<>();
        List<double[]> array = new ArrayList<>();
        for(Stock stock : allList){
            Bar bar = stock.getBar();
            if(bar==null)continue;
            List<Double> close = bar.map(days, function);
            double[] doubles = close.stream().mapToDouble(Double::doubleValue).toArray();
            if(doubles.length == days){
                stocks.add(stock);
                array.add(doubles);
            }
        }

        Stock stock = Stock.build(stockCode);
        Bar a = stock.getBarSeries().getBar(startDate);
        List<Double> close = a.map(days, function);
        double[] query = close.stream().mapToDouble(Double::doubleValue).toArray();

        log.info("array.length:"+array.size());
        double[] distances = KhivaUtils.mass(array, query);
        int[] indexes = KhivaUtils.getIndexesOfMin(distances, 5);
        log.info(Arrays.toString(indexes));

        String imageStr = ImageUtils.getImageStr(ServiceUtils.getResourceFileAsBytes("similar_stock_image/"+png));
        String imageHtml = CommonUtils.getImgBase64(imageStr, 450, 300);
        List<String> list = returnDistancesLessThenTargetDistance(stocks, indexes, distances, 3, targetDistance);
        List<String> data = ListUtils.createList(stock.getNameAndCodeWithLink() +"-"+startDate+"<br/>"+ imageHtml,
                "",StringUtils.join(list, "<br/><br/>"));
        return new MassResult(data, list.size());

    }

    public static void massByMaAndVolume(MassContext context, List<Stock> stocks, String stockCode, String startDate, int days, String png, double targetDistance){
        MassFunction function1 = new MassFunction(2, bar -> bar.getMA(5, Bar.EnumValue.C));
        MassFunction function2 = new MassFunction(1, Bar::getVolume);
        mass(context, stocks, stockCode, startDate, days, png, targetDistance, function1, function2);
    }

    public static void mass(MassContext context, List<Stock> stocks, String stockCode, String startDate, int days, String png, double targetDistance, MassFunction... functions){
        Stock stock = Stock.build(stockCode);
        List<MassStockDistance> minDistances = MassUtils.getMinDistances(stocks, stock, startDate, days, functions);
        List<MassStockDistance> listStocks = minDistances.stream().filter(massStockDistance -> massStockDistance.getDistance() <= targetDistance).collect(Collectors.toList());

        List<String> list = new ArrayList<>();
        for(MassStockDistance massStockDistance : listStocks){
           list.add(massStockDistance.getStock().getNameAndCodeWithLink() + ", distance:" + massStockDistance.getDistance() + "<br/>" + massStockDistance.getStock().getDayBarImage());
        }
        String imageStr = ImageUtils.getImageStr(ServiceUtils.getResourceFileAsBytes("similar_stock_image/"+png));
        String imageHtml = CommonUtils.getImgBase64(imageStr, 450, 300);
        List<String> data = ListUtils.createList(stock.getNameAndCodeWithLink() +"-"+startDate+"<br/>"+ imageHtml,
                "",StringUtils.join(list, "<br/><br/>"));

        context.getDatas().add(data);
        context.count(list.size());
    }

    public static List<MassStockDistance> getMinDistances(List<Stock> stocks, Stock templateStock, String startDate, int days, MassFunction... functions){
        Map<String, MassStockDistance> massStocks = new HashMap<>();
        for(MassFunction massFunction : functions){
            List<Stock> astocks = new ArrayList<>();
            List<double[]> array = new ArrayList<>();
            for(Stock stock : stocks){
                Bar bar = stock.getBar();
                if(bar==null)continue;
                List<Double> close = bar.map(days, massFunction.getFunction());
                double[] doubles = close.stream().mapToDouble(Double::doubleValue).toArray();
                if(doubles.length == days){
                    astocks.add(stock);
                    array.add(doubles);
                }
            }

            Bar a = templateStock.getBarSeries().getBar(startDate);
            List<Double> close = a.map(days, massFunction.getFunction());
            double[] query = close.stream().mapToDouble(Double::doubleValue).toArray();

            log.info("array.length:"+array.size());
            double[] distances = KhivaUtils.mass(array, query);

            for (int i = 0; i < astocks.size(); i++) {
                Stock stk = astocks.get(i);
                MassStockDistance msd = massStocks.get(stk.getCode());
                if(msd == null) {
                    massStocks.put(stk.getCode(), new MassStockDistance(stk, distances[i], massFunction.getWeight()));
                }else{
                    msd.addDistance(distances[i], massFunction.getWeight());
                }
            }
        }
        List<MassStockDistance> list = massStocks.values().stream().filter(massStockDistance -> massStockDistance.getDistances().size() == functions.length).collect(Collectors.toList());
        log.info("list="+list);
        double[] distances = list.stream().mapToDouble(mass -> mass.getDistances().stream().mapToDouble(Double::doubleValue).sum()).toArray();
        int[] indexes = KhivaUtils.getIndexesOfMin(distances, 3);
        log.info(Arrays.toString(indexes));

        List<MassStockDistance> minDistances = new ArrayList<>();
        for(int index : indexes){
            minDistances.add(list.get(index));
        }
        log.info("minDistances="+minDistances);
        return minDistances;
    }

    private static List<String> returnDistancesLessThenTargetDistance(List<Stock> stocks, int[] indexes, double[] distances, int n, double targetDistance){
        List<String> list = new ArrayList<>();
        for(int i=0;i<n;i++){
            if(distances[indexes[i]] <= targetDistance) {
                list.add(stocks.get(indexes[i]).getNameAndCodeWithLink() + ", distance:" + distances[indexes[i]] + "<br/>" + stocks.get(indexes[i]).getDayBarImage());
            }
        }
        return list;
    }


}
