package com.stk123.model.mass;

import com.stk123.common.ml.KhivaUtils;
import com.stk123.model.core.Bar;
import com.stk123.model.core.Stock;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CommonsLog
@Data
public class MassStrategy {

    private String templateStockStartDate;
    private int templateStockPeriod;
    private Stock templateStock;
    private double targetDistance;
    private String templateStockImage;
    private int countOfMinDistance = 3;

    private List<MassFunction> functions = new ArrayList<>();

    public static MassStrategy build(String code, String startDate, int period, double targetDistance, String image){
        return new MassStrategy(code, startDate, period, targetDistance, image);
    }

    private MassStrategy(String code, String startDate, int period, double targetDistance, String image){
        this.templateStockStartDate = startDate;
        this.templateStockPeriod = period;
        this.templateStock = Stock.build(code);
        this.targetDistance = targetDistance;
        this.templateStockImage = image;
    }

    public MassStrategy setCountOfMinDistance(int countOfMinDistance){
        this.countOfMinDistance = countOfMinDistance;
        return this;
    }

    public MassStrategy addMassFunction(double weight, Function<Bar, Double> function, Integer period){
        this.functions.add(new MassFunction(weight, period == null ? templateStockPeriod : period, function));
        return this;
    }

    public MassStrategy addMassFunction(double weight, Function<Bar, Double> function){
        this.functions.add(new MassFunction(weight, templateStockPeriod, function));
        return this;
    }

    public List<MassStockDistance> getMinDistances(List<Stock> stocks){
        Map<String, MassStockDistance> massStocks = new HashMap<>();
        double totalWeight = functions.stream().mapToDouble(MassFunction::getWeight).sum();
        for (MassFunction massFunction : functions) {
            List<Stock> astocks = new ArrayList<>();
            List<double[]> array = new ArrayList<>();
            for (Stock stock : stocks) {
                Bar bar = stock.getBar();
                if (bar == null) continue;
                List<Double> close = bar.map(massFunction.getPeriod(), massFunction.getFunction());
                double[] doubles = close.stream().mapToDouble(Double::doubleValue).toArray();
                if (doubles.length == massFunction.getPeriod()) {
                    astocks.add(stock);
                    array.add(doubles);
                }
            }

            Bar a = templateStock.getBarSeries().getBar(templateStockStartDate);
            List<Double> close = a.map(massFunction.getPeriod(), massFunction.getFunction());
            double[] query = close.stream().mapToDouble(Double::doubleValue).toArray();

            log.info("array.length:" + array.size());
            double[] distances = KhivaUtils.mass(array, query);

            for (int i = 0; i < astocks.size(); i++) {
                Stock stk = astocks.get(i);
                MassStockDistance msd = massStocks.get(stk.getCode());
                if (msd == null) {
                    massStocks.put(stk.getCode(), new MassStockDistance(stk, distances[i], massFunction.getWeight(), totalWeight));
                } else {
                    msd.addDistance(distances[i], massFunction.getWeight(), totalWeight);
                }
            }
        }
        List<MassStockDistance> list = massStocks.values().stream().filter(massStockDistance -> massStockDistance.getDistances().size() == functions.size()).collect(Collectors.toList());
        //log.info("list="+list);
        double[] distances = list.stream().mapToDouble(mass -> mass.getDistance()).toArray();
        int[] indexes = KhivaUtils.getIndexesOfMin(distances, countOfMinDistance);
        log.info(Arrays.toString(indexes));

        List<MassStockDistance> minDistances = new ArrayList<>();
        for(int index : indexes){
            minDistances.add(list.get(index));
        }
        log.info("minDistances="+minDistances);
        return minDistances;
    }
}
