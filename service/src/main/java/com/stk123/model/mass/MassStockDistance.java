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
    private int totalWeight = 0;

    public MassStockDistance(Stock stock, Double distance, int weight){
        this.stock = stock;
        this.addDistance(distance, weight);
    }

    public void addDistance(Double distance, int weight){
        distances.add(distance * weight);
        this.totalWeight += weight;
    }

    public double getDistance(){
        if(this.totalWeight != 0) {
            return distances.stream().mapToDouble(Double::doubleValue).sum() / totalWeight;
        }
        return distances.stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    public String toString() {
        return "MassStockDistance{distances=[" + StringUtils.join(distances, ",") + "], totalWeight=" + totalWeight + '}';
    }
}
