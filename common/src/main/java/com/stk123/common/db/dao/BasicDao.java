package com.stk123.common.db.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class BasicDao<E, I extends Serializable> implements Dao<E, I> {
	
	private Class<E> entityClass;
	private Table table;
	private List<String> columns = new ArrayList<String>(100);
	//private Map<String,POJOPropertyDescriptor> pds = new HashMap<String,POJOPropertyDescriptor>(100) ;
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface Table {
		public String name();
		public String alias() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface Column {
		public String name();
		public boolean pk() default false;
	}
	
	@SuppressWarnings("unchecked")
	public BasicDao() {
        entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        table = (Table)this.entityClass.getAnnotation(Table.class);
		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(entityClass);
		} catch (IntrospectionException e) {
			throw new RuntimeException("fail to contruct beanwrapper:" + entityClass, e) ;
		}
		Field[] fields = entityClass.getDeclaredFields();
		for(Field field:fields){
			Column column = field.getAnnotation(Column.class);
			if(column != null){
				columns.add(column.name());
			}
		}
    }

	public E save(E entity) {
		// TODO Auto-generated method stub
		Class clazz = entity.getClass();
		return null;
	}

	public E load(I id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<E> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(I id) {
		// TODO Auto-generated method stub
		
	}

	public E update(E entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public E saveOrUpdate(E entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
