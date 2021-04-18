package com.stk123.model.mass;

import com.stk123.common.CommonUtils;
import com.stk123.common.util.ImageUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.model.core.Stock;
import com.stk123.util.ServiceUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Mass {

    public static MassResult execute(List<Stock> stocks, MassStrategy... massStrategies){
        MassResult massResult = new MassResult();
        for(MassStrategy strategy : massStrategies){
            List<MassStockDistance> minDistances = strategy.getMinDistances(stocks);
            List<MassStockDistance> listStocks = minDistances.stream().filter(massStockDistance -> massStockDistance.getDistance() <= strategy.getTargetDistance()).collect(Collectors.toList());

            List<String> list = new ArrayList<>();
            for(MassStockDistance massStockDistance : listStocks){
                list.add(massStockDistance.getStock().getNameAndCodeWithLink() + ", distance:" + massStockDistance.getDistance() + "<br/>" + massStockDistance.getStock().getDayBarImage());
            }
            String imageStr = ImageUtils.getImageStr(ServiceUtils.getResourceFileAsBytes("similar_stock_image/" + strategy.getTemplateStockImage()));
            String imageHtml = CommonUtils.getImgBase64(imageStr, 450, 300);
            List<String> data = ListUtils.createList(strategy.getTemplateStock().getNameAndCodeWithLink() +"-"+strategy.getTemplateStockStartDate()+"<br/>"+ imageHtml,
                    "",StringUtils.join(list, "<br/><br/>"));

            massResult.getDatas().add(data);
            massResult.count(list.size());
        }
        return massResult;
    }
}
