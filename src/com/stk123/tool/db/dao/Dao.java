package com.stk123.tool.db.dao;

import java.io.Serializable;
import java.util.List;

public interface Dao<E, I extends Serializable> {
	
	E save(E entity);
	
	E load(I id);
	
	List<E> findAll();
	
	void delete(I id);
	
	E update(E entity);
	
	E saveOrUpdate(E entity);
}
