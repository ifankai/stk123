package com.stk123.model.mass;

import com.stk123.model.core.Stock;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class MassStockDistance {
    private Stock stock;
    private List<Double> distances = new ArrayList<>();
    private double distanceByWeight;

    public MassStockDistance(Stock stock, Double distance, double weight, double totalWeight){
        this.stock = stock;
        this.addDistance(distance, weight, totalWeight);
    }

    public void addDistance(Double distance, double weight, double totalWeight){
        distances.add(distance);
        distanceByWeight += distance * weight / totalWeight;
    }

    public double getDistance(){
        return distanceByWeight;
    }

    @Override
    public String toString() {
        return "MassStockDistance{distances=[" + StringUtils.join(distances, ",") + "], distance="+distanceByWeight+"}";
    }
}
