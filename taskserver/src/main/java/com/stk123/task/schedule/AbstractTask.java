package com.stk123.task.schedule;

import com.stk123.model.core.Stock;
import com.stk123.service.task.Task;
import com.stk123.task.tool.TaskUtils;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


@CommonsLog
public abstract class AbstractTask extends Task {

    protected final Date now = new Date();
    @Setter
    protected String today = TaskUtils.getToday();//"20160923";
    protected int dayOfWeek = TaskUtils.getDayOfWeek(now);
    protected boolean isWorkingDay = (dayOfWeek == 1 || dayOfWeek == 2 || dayOfWeek == 3 || dayOfWeek == 4 || dayOfWeek == 5)?true:false;;
    protected Stock.EnumMarket market = Stock.EnumMarket.CN; //default A stock

    protected Map<String, Object> params = new LinkedHashMap<>();
    protected Map<String, Runnable> methods = new LinkedHashMap<>();

    protected void register(String name, Runnable runnable){
        methods.put(name, runnable);
    }

    @Override
    public void execute(String... args) throws Exception {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if(StringUtils.contains(arg, "=")){
                    String[] p = StringUtils.split(arg, "=");
                    params.put(p[0], p[1]);
                }else{
                    if ("US".equalsIgnoreCase(arg)) {
                        market = Stock.EnumMarket.US;
                    }else if ("HK".equalsIgnoreCase(arg)) {
                        market = Stock.EnumMarket.HK;
                    }
                    params.put(arg, true);
                }
            }
            setParameterToProperty();
        }
        register();

        Set<String> keySet = params.keySet();
        for(Map.Entry<String, Runnable> method : methods.entrySet()){
            if(keySet.stream().anyMatch(key -> StringUtils.contains(method.getKey(), key))){
                log.info(method.getKey() + "...start");
                method.getValue().run();
                log.info(method.getKey() + "...end");
            }
        }
    }

    public String getParameterAsString(String name) {
        return String.valueOf(params.get(name));
    }

    public abstract void register();

    private void setParameterToProperty(){
        for(Map.Entry<String, Object> param : params.entrySet()){
            try {
                if(PropertyUtils.isWriteable(this, param.getKey()))
                    PropertyUtils.setProperty(this, param.getKey(), param.getValue());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
