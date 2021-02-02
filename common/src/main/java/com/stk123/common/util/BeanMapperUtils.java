package com.stk123.common.util;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;

/**
 * modelmapper
 * http://modelmapper.org/getting-started/
 */
public class BeanMapperUtils {

    public static <S,D,V> D map(S source, Class<D> destClass, SourceGetter<S> sourceGetter, DestinationSetter<D, V> destinationSetter){
        ModelMapper modelMapper = new ModelMapper();
        TypeMap typeMap = modelMapper.createTypeMap(source.getClass(), destClass);
        typeMap.addMapping(sourceGetter, destinationSetter);
        return modelMapper.map(source, destClass);
    }

    public static <S,D,V> D map(S source, Class<D> destClass, PropertyMap<S, D> propertyMap){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        return modelMapper.map(source, destClass);
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

    public static void main(String[] args) {
        A a = new A();
        a.setCode("5000");
        a.setAge(10);


        B b = BeanMapperUtils.map(a, B.class, src -> src.getCode(), B::setCodeName);
        System.out.println(b);

        a.setCode("hahah");
        b = BeanMapperUtils.map(a, B.class, new PropertyMap<A, B>() {
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
