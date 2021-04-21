package com.stk123.model.mass;

import cn.hutool.core.collection.CollUtil;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.ImageUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.model.core.Bar;
import com.stk123.model.core.Stock;
import com.stk123.util.ServiceUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Mass {

    static MassStrategy a000408 = MassStrategy.build("000408", "20210326", 50, 2.8, "ST藏格[SZ000408]-20210326.png")
            .addMassFunction(1, bar -> bar.getMA(5, Bar.EnumValue.C));

    static MassStrategy a002538 = MassStrategy.build("002538", "20200703", 80, 7,"司尔特[SZ002538]-20200703.png")
            .addMassFunction(1, bar -> bar.getMA(5, Bar.EnumValue.C))
            .addMassFunction(3, Bar::getVolume);

    static MassStrategy a000516 = MassStrategy.build("000516", "20200703", 100, 6, "国际医学[SZ000516]-20200703.png")
            .addMassFunction(1, Bar::getClose);

    static MassStrategy a600859 = MassStrategy.build("600859", "20200430", 100, 5, "王府井[SH600859]-20200430.png")
            .addMassFunction(1, Bar::getClose);

    static MassStrategy a600958 = MassStrategy.build("600958", "20200630", 100,5,  "东方证券[SH600958]-20200630.png")
            .addMassFunction(1, bar -> bar.getMA(5, Bar.EnumValue.C));

    static MassStrategy a002524 = MassStrategy.build("002524", "20210412", 30, 5, "光正眼科[SZ002524]-20210412.png")
            .addMassFunction(1, Bar::getClose)
            .addMassFunction(1, Bar::getVolume, 15)
            .setCountOfMinDistance(5);

    static MassStrategy a002177 = MassStrategy.build("002177", "20210319", 80,7,  "御银股份[SZ002177]-20210319.png")
            .addMassFunction(1, Bar::getClose)
            .addMassFunction(1, Bar::getVolume)
            .addMassFunction(1, bar -> bar.getMA(120, Bar.EnumValue.C));

    static MassStrategy a002762 = MassStrategy.build("002762", "20210317", 40, 5, "金发拉比[SZ002762]-20210317.png")
            .addMassFunction(1, Bar::getClose)
            .addMassFunction(1, Bar::getVolume)
            .addMassFunction(1, bar -> bar.getMA(60, Bar.EnumValue.C));

    static MassStrategy a002735 = MassStrategy.build("002735", "20210322", 40, 4.5, "王子新材[SZ002735]-20210322.png")
            .addMassFunction(1, Bar::getClose)
            .addMassFunction(1, Bar::getVolume);

    static MassStrategy a002172 = MassStrategy.build("002172", "20210115", 60,6,  "澳洋健康[SZ002172]-20210115.png")
            .addMassFunction(1, Bar::getClose)
            .addMassFunction(1, Bar::getVolume);

    static MassStrategy a000807_1 = MassStrategy.build("000807", "20210203", 30, 4.5, "云铝股份[SZ000807]-20210203.png")
            .addMassFunction(1, Bar::getClose)
            .addMassFunction(1, Bar::getVolume);
    static MassStrategy a000807_2 = MassStrategy.build("000807", "20201102", 40, 6, "云铝股份[SZ000807]-20201102.png")
            .addMassFunction(1, Bar::getClose)
            .addMassFunction(1, Bar::getVolume);
    static MassStrategy a000807_3 = MassStrategy.build("000807", "20200630", 80, 10, "云铝股份[SZ000807]-20200630.png")
            .addMassFunction(2, Bar::getClose)
            .addMassFunction(1, Bar::getVolume);

    static MassStrategy a002813 = MassStrategy.build("002813", "20210331", 35, 10, "路畅科技[SZ002813]-20210331.png")
            .addMassFunction(1, Bar::getClose)
            .addMassFunction(1, Bar::getVolume);



    @SneakyThrows
    public static MassResult execute(List<Stock> stocks){
        Set<Field> fieldSet = ReflectionUtils.getFields(Mass.class, field -> StringUtils.startsWith(field.getName(), "a"));
        List<MassStrategy> strategies = new ArrayList<>();
        for(Field field : fieldSet){
            strategies.add((MassStrategy) field.get(null));
        }
        return Mass.execute(stocks, strategies.toArray(new MassStrategy[0]));
    }

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



    public static void main(String[] args) throws Exception {
        Set<Field> fieldSet = ReflectionUtils.getFields(Mass.class, field -> StringUtils.startsWith(field.getName(), "a"));
        System.out.println(fieldSet.size());
        fieldSet.forEach(field -> {
            try {
                System.out.println(field.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
