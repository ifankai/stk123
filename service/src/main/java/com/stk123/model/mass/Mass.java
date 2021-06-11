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

    public static List<MassStrategy> getStrategies(){
        List<MassStrategy> strategies = new ArrayList<>();

        MassStrategy ms = null;

        ms = MassStrategy.build("000408", "20210326", 50, 2.8, "ST藏格[SZ000408]-20210326.png")
                .addMassFunction(1, bar -> bar.getMA(5, Bar.EnumValue.C));
        strategies.add(ms);

        ms = MassStrategy.build("002538", "20200703", 80, 7, "司尔特[SZ002538]-20200703.png")
                .addMassFunction(1, bar -> bar.getMA(5, Bar.EnumValue.C))
                .addMassFunction(3, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("002524", "20210412", 30, 5, "光正眼科[SZ002524]-20210412.png")
                .addMassFunction(1, Bar::getClose)
                .addMassFunction(1, Bar::getVolume, 15)
                .setCountOfMinDistance(5);
        strategies.add(ms);
        ms = MassStrategy.build("002524", "20210407", 20, 5, "光正眼科[SZ002524]-20210412.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("000516", "20200703", 100, 6, "国际医学[SZ000516]-20200703.png")
                .addMassFunction(1, Bar::getClose).setCountOfMinDistance(5);
        strategies.add(ms);
        ms = MassStrategy.build("000516", "20200702", 50, 4.5, "国际医学[SZ000516]-20200703.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(2, bar -> bar.getMA(60, Bar.EnumValue.C)-bar.getClose())
                .addMassFunction(1, Bar::getClose, 8);
        strategies.add(ms);

        ms = MassStrategy.build("600859", "20200430", 100, 5, "王府井[SH600859]-20200430.png")
                .addMassFunction(1, Bar::getClose);
        strategies.add(ms);

        ms = MassStrategy.build("600958", "20200630", 100, 5, "东方证券[SH600958]-20200630.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getVolume);
        strategies.add(ms);
        ms = MassStrategy.build("600958", "20200630", 13, 1, "东方证券[SH600958]-20200630.png")
                .addMassFunction(1, Bar::getChange).addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("002177", "20210323", 80, 7, "御银股份[SZ002177]-20210319.png")
                .addMassFunction(1, Bar::getClose)
                .addMassFunction(1, Bar::getVolume)
                .addMassFunction(1, bar -> bar.getMA(120, Bar.EnumValue.C));
        strategies.add(ms);

        ms = MassStrategy.build("002762", "20210317", 40, 5, "金发拉比[SZ002762]-20210317.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getClose, 20)
                .addMassFunction(1, Bar::getVolume);
                //.addMassFunction(1, bar -> bar.getMA(60, Bar.EnumValue.C));
        strategies.add(ms);

        ms = MassStrategy.build("002735", "20210322", 40, 4.5, "王子新材[SZ002735]-20210322.png")
                .addMassFunction(1, Bar::getClose)
                .addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

//        ms = MassStrategy.build("002172", "20210115", 60, 6, "澳洋健康[SZ002172]-20210115.png")
//                .addMassFunction(1, Bar::getClose)
//                .addMassFunction(1, Bar::getVolume);
//        strategies.add(ms);

        ms = MassStrategy.build("000807", "20210203", 30, 4.5, "云铝股份[SZ000807]-20210203.png")
                .addMassFunction(1, Bar::getClose)
                .addMassFunction(1, Bar::getVolume);
        strategies.add(ms);
        ms = MassStrategy.build("000807", "20201102", 40, 6, "云铝股份[SZ000807]-20201102.png")
                .addMassFunction(1, Bar::getClose)
                .addMassFunction(1, Bar::getVolume);
        strategies.add(ms);
        ms = MassStrategy.build("000807", "20200630", 80, 7, "云铝股份[SZ000807]-20200630.png")
                .addMassFunction(2, Bar::getClose)
                .addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("002813", "20210331", 35, 4.5, "路畅科技[SZ002813]-20210331.png")
                .addMassFunction(1, Bar::getClose)
                .addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("688068", "20210318", 40, 6, "热景生物[SH688068]-20210318.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("002348", "20210331", 60, 7, "高乐股份[SZ002348]-20210331.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("300253", "20200622", 15, 2.5, "卫宁健康[SZ300253]-20200622.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getHigh)
                .addMassFunction(1, Bar::getLow).addMassFunction(1, Bar::getOpen);
        strategies.add(ms);

        ms = MassStrategy.build("300278", "20200805", 50, 5, "华昌达[SZ300278]-20200805.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("300283", "20200814", 50, 50, "温州宏丰[SZ300283]-20200814.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getVolume)
                .addMassFunction(1, bar -> bar.getMA(120, Bar.EnumValue.C)-bar.getClose());
        //strategies.add(ms);

        ms = MassStrategy.build("300312", "20210219", 25, 3, "邦讯技术[SZ300312]-20210219.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getVolume);
        strategies.add(ms);

        ms = MassStrategy.build("300313", "20200812", 100, 6, "天山生物[SZ300313]-20200812.png")
                .addMassFunction(1, Bar::getClose).addMassFunction(1, Bar::getClose, 25)
                .addMassFunction(1, bar -> bar.getMA(120, Bar.EnumValue.C)-bar.getClose());
        strategies.add(ms);


        return strategies;
    }

    @SneakyThrows
    public static MassResult execute(List<Stock> stocks){
        /*Set<Field> fieldSet = ReflectionUtils.getFields(Mass.class, field -> StringUtils.startsWith(field.getName(), "a"));
        List<MassStrategy> strategies = new ArrayList<>();
        for(Field field : fieldSet){
            strategies.add((MassStrategy) field.get(null));
        }*/
        return Mass.execute(stocks, Mass.getStrategies().toArray(new MassStrategy[0]));
    }

    public static MassResult execute(List<Stock> stocks, MassStrategy... massStrategies){
        MassResult massResult = new MassResult();
        for(MassStrategy strategy : massStrategies){
            List<MassStockDistance> minDistances = strategy.getMinDistances(stocks);
            List<MassStockDistance> listStocks = minDistances.stream().filter(massStockDistance -> massStockDistance.getDistance() <= strategy.getTargetDistance()).collect(Collectors.toList());

            List<List<String>> subTable = new ArrayList<>();
            for(MassStockDistance massStockDistance : listStocks){
                List<String> row = ListUtils.createList(massStockDistance.getStock().getNameAndCodeWithLink()+"<br/>distance:" + massStockDistance.getDistance(),
                        massStockDistance.getStock().getDayBarImage(), massStockDistance.getStock().getWeekBarImage());
                subTable.add(row);
            }
            String table = CommonUtils.createHtmlTable(null, subTable);

            String imageStr = ImageUtils.getImageStr(ServiceUtils.getResourceFileAsBytes("similar_stock_image/" + strategy.getTemplateStockImage()));
            String imageHtml = CommonUtils.getImgBase64(imageStr, 450, 300);
            List<String> row = ListUtils.createList(strategy.getTemplateStock().getNameAndCodeWithLink() +"-"+strategy.getTemplateStockStartDate()+"<br/>"+ imageHtml,
                    "", table);

            massResult.getDatas().add(row);
            massResult.count(listStocks.size());
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
