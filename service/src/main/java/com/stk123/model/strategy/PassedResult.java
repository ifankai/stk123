package com.stk123.model.strategy;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PassedResult {

    private Stock stock;
    private List<StrategyResult> strategyResults;

    public void addStrategyResult(StrategyResult strategyResult){
        if(strategyResults == null){
            strategyResults = new ArrayList<>();
        }
        strategyResults.add(strategyResult);
    }

    public Strategy getStrategy(){
        return strategyResults.get(0).getStrategy();
    }

    @SneakyThrows
    public String toString(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        String json = objectMapper.writerWithView(View.Default.class).writeValueAsString(strategyResults);

        return "{\"strategyResults\": "+ json + "}";
    }
}
