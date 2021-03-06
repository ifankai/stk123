package com.stk123.service.support;

import lombok.extern.apachecommons.CommonsLog;
import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@CommonsLog
public class CustomJpaResultTransformer extends AliasToBeanResultTransformer {

    private static final long serialVersionUID = 1L;

    @Autowired
    private SessionFactory sessionFactory;

    private NativeQuery query;


    private final Class resultClass;
    private boolean isInitialized;
    private String aliases[];
    private Setter setters[];


    public <T> CustomJpaResultTransformer(NativeQuery query, Class<T> resultClass) {
        super(resultClass);
        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        } else {
            isInitialized = false;
            this.query = query;
            this.resultClass = resultClass;
            return;
        }
    }

    public boolean isTransformedValueATupleElement(String aliases[], int tupleLength) {
        return false;
    }

    public Object transformTuple(Object tuple[], String aliases[]) {
        Object result;
        try {
            if (!isInitialized)
                initialize(aliases);
//            else
//                check(aliases);
            result = resultClass.newInstance();
            for (int i = 0; i < this.aliases.length; i++)
                if (setters[i] != null) {
                    // To fix issue: Expected type: java.lang.Long, actual value: java.math.BigDecimal
//                    if(tuple[i] instanceof BigDecimal && setters[i].getMethod().getParameterTypes()[0] == Long.class) {
//                        setters[i].set(result, new Long(tuple[i].toString()), null);
//                    }else {

//                    try {
//                        Annotation scalar = result.getClass().getDeclaredField(aliases[i]).getAnnotation(Scalar.class);
//                        if(scalar != null){
//                            Class clazz = result.getClass().getDeclaredField(aliases[i]).getType();
//                            Type type = sessionFactory.getTypeHelper().heuristicType(clazz.getName());
//                            query.addScalar(aliases[i], type);
//                        }
//                    } catch (NoSuchFieldException e) {
//                        throw new HibernateException((new StringBuilder()).append("Could not find ").append(aliases[i]).append(" in resultclass: ").append(resultClass.getName()).toString());
//                    }

                    if (tuple[i] == null && ReflectHelper.findField( resultClass, this.aliases[i]).getType().isPrimitive()) continue;

                    setters[i].set(result, tuple[i], null);
//                    }
                }

        } catch (InstantiationException e) {
            throw new HibernateException((new StringBuilder()).append("Could not instantiate resultclass: ").append(resultClass.getName()).toString());
        } catch (IllegalAccessException e) {
            throw new HibernateException((new StringBuilder()).append("Could not instantiate resultclass: ").append(resultClass.getName()).toString());
        }
        return result;
    }

    private void initialize(String aliases[]) {
        PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(
                PropertyAccessStrategyBasicImpl.INSTANCE,
                PropertyAccessStrategyFieldImpl.INSTANCE,
                PropertyAccessStrategyMapImpl.INSTANCE
        );
        this.aliases = new String[aliases.length];
        setters = new Setter[aliases.length];
        for (int i = 0; i < aliases.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                alias = UnderlineToCamelUtils.underlineToCamel(alias.toLowerCase());
                this.aliases[i] = alias;
                try {
                    setters[i] = propertyAccessStrategy.buildPropertyAccess(resultClass, alias).getSetter();
                }catch (PropertyNotFoundException e){
                    log.warn(e.getMessage());
                }
            }
        }
        isInitialized = true;
    }

    private void check(String aliases[]) {
        if (!Arrays.equals(aliases, this.aliases))
            throw new IllegalStateException((new StringBuilder()).append("aliases are different from what is cached; aliases=").append(Arrays.asList(aliases)).append(" cached=").append(Arrays.asList(this.aliases)).toString());
        else
            return;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomJpaResultTransformer that = (CustomJpaResultTransformer) o;
        if (!resultClass.equals(that.resultClass))
            return false;
        return Arrays.equals(aliases, that.aliases);
    }

    public int hashCode() {
        int result = resultClass.hashCode();
        result = 31 * result + (aliases == null ? 0 : Arrays.hashCode(aliases));
        return result;
    }


}