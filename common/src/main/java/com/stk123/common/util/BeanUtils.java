package com.stk123.common.util;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * modelmapper
 * http://modelmapper.org/getting-started/
 */
public class BeanUtils {

    private final static ModelMapper modelMapper = new ModelMapper();
    public static <S,D> D map(S source, Class<D> destClass){
        if(source == null) return null;
        return modelMapper.map(source, destClass);
    }

    public static <S,D,V> D map(S source, Class<D> destClass, SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter){
        if(source == null) return null;
        ModelMapper modelMapper = new ModelMapper();
        TypeMap typeMap = modelMapper.createTypeMap(source.getClass(), destClass);
        typeMap.addMapping(sourceGetter, destinationSetter);
        return modelMapper.map(source, destClass);
    }

    public static <S,D> D map(S source, Class<D> destClass, PropertyMap<S, D> propertyMap){
        if(source == null) return null;
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        return modelMapper.map(source, destClass);
    }

    public static <S,D> void map(S source, D dest, PropertyMap<S, D> propertyMap){
        if(source == null || dest == null) return;
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        modelMapper.map(source, dest);
    }

    public static <S,D> void mapIgnoreNull(S source, D dest){
        if(source == null || dest == null) return;
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(source, dest);
    }

    /*public static <S,D,V> D map(Object source, Class<D> destClass, Map<SourceGetter, DestinationSetter> mapper) {
        ModelMapper modelMapper = new ModelMapper();
        if(mapper != null)
            modelMapper.typeMap(source.getClass(), dClass).addMappings(map -> {
                mapper.entrySet().stream().forEach(m -> {
                    map.map(m.getKey(), m.getValue());
                });
            });
        return modelMapper.map(source, destination);
    }*/

    @SneakyThrows
    public static Map toMap(Object object, String... properties){
        Map map = new HashMap();
        for(String property : properties) {
            Object obj = PropertyUtils.getProperty(object, property);
            map.put(property, obj);
        }
        return map;
    }

    public static void main(String[] args) {
        A a = new A();
        a.setCode("5000");
        a.setAge(10);


        B b = BeanUtils.map(a, B.class, src -> src.getCode(), B::setCodeName);
        System.out.println(b);

        a.setCode("hahah");
        b = BeanUtils.map(a, B.class, new PropertyMap<A, B>() {
            @Override
            protected void configure() {
                map().setCodeName(source.getCode());
                map().setAgeStr(String.valueOf(source.getAge()));
            }
        });
        System.out.println(b);
    }

}
@Data
class A {
    private String code;
    private int age;
}

@Data
class B {
    private String codeName;
    private String ageStr;
}
