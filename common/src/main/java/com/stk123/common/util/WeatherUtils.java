package com.stk123.common.util;

import com.stk123.common.CommonUtils;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@CommonsLog
public class WeatherUtils {

    public static void check() {
        try {
            String today = CommonUtils.formatDate(new Date());
            String tomorrow = CommonUtils.formatDate(CommonUtils.addDay(new Date(), 1));
            //"http://weathernew.pae.baidu.com/weathernew/pc?query=上海天气&srcid=4982&city_name=上海&province_name=上海&forecast="+tomorrow
            String url = "http://weathernew.pae.baidu.com/weathernew/pc?query=%E4%B8%8A%E6%B5%B7%E5%A4%A9%E6%B0%94&srcid=4982&city_name=%E4%B8%8A%E6%B5%B7&province_name=%E4%B8%8A%E6%B5%B7&forecast=" + tomorrow;
            String page = CommonHttpUtils.get(url, "utf-8");
            if (page != null) {
                if (StringUtils.contains(page, "data[\"longDayForecast\"]=")) {
                    //System.out.println(page);
                    String json = StringUtils.substringBetween(page, "data[\"longDayForecast\"]=", ";");
                    Map root = JsonUtils.testJson(json);
                    List<Map> info = (List<Map>) root.get("info");
                    /*for(Map map : info){
                        System.out.println(map);
                    }*/
                    Map todayMap = info.stream().filter(map -> today.equals(map.get("date"))).findFirst().get();
                    Map tomorrowMap = info.stream().filter(map -> tomorrow.equals(map.get("date"))).findFirst().get();
                    log.info("today temp:" + todayMap.get("temperature_day"));
                    log.info("tomorrow:" + tomorrowMap.get("temperature_day"));
                    double todayTemp = Double.parseDouble(String.valueOf(todayMap.get("temperature_day")));
                    double tomorrowTemp = Double.parseDouble(String.valueOf(tomorrowMap.get("temperature_day")));
                    if (todayTemp - tomorrowTemp >= 6) {
                        String title = "";
                        if (todayTemp - tomorrowTemp >= 8) {
                            title = "[降温红色警告]";
                        } else {
                            title = "[降温黄色警告]";
                        }
                        EmailUtils.send(title + "今天最高" + todayTemp + "℃, 明天最高" + tomorrowTemp + "℃, 降温" + (todayTemp - tomorrowTemp) + "℃", "");
                    }
                }
            }
        }catch (Exception e){
            EmailUtils.send("天气检查报错", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public static void main(String[] args) {
        check();
    }
}
