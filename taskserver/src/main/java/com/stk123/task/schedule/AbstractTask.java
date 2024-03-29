package com.stk123.task.schedule;

import com.stk123.model.enumeration.EnumMarket;
import com.stk123.service.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


@Slf4j
public abstract class AbstractTask extends Task {

    public AbstractTask(){super();}
    public AbstractTask(boolean canStop){
        super(canStop);
    }

    protected EnumMarket market = EnumMarket.CN; //default A stock

    protected Map<String, Object> params = new LinkedHashMap<>();
    protected Map<String, Runnable> methods = new LinkedHashMap<>();

    protected void runByName(String name, Runnable runnable){
        methods.put(name, runnable);
    }
    protected void run(Runnable runnable){
        methods.put("run_"+new Date().getTime(), runnable);
    }

    @Override
    public void execute(String... args) throws Exception {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if(StringUtils.contains(arg, "=")){
                    String[] p = StringUtils.split(arg, "=");
                    if(p.length == 1){
                        params.put(p[0], null);
                    }else {
                        params.put(p[0], p[1]);
                    }
                }else{
                    if ("US".equalsIgnoreCase(arg)) {
                        market = EnumMarket.US;
                    }else if ("HK".equalsIgnoreCase(arg)) {
                        market = EnumMarket.HK;
                    }
                    params.put(arg, true);
                }
            }
            setParameterToProperty();
        }
        register();

        Set<String> keySet = params.keySet();
        for(Map.Entry<String, Runnable> method : methods.entrySet()){
            if(keySet.isEmpty() || StringUtils.startsWith(method.getKey(), "run_") || keySet.stream().anyMatch(key -> StringUtils.contains(method.getKey(), key))){
                //log.info("["+this.getClass().getSimpleName()+"]" + "...start");
                try {
                    method.getValue().run();
                }catch (Exception e){
                    log.error("["+this.getClass().getSimpleName()+"] error:", e);
                }
                //log.info("["+this.getClass().getSimpleName()+"]" + "...end");
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
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("setParameterToProperty error", e);
            }
        }
    }
}
