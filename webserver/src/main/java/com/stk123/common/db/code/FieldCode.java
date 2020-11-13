package com.stk123.common.db.code;

import java.util.List;

public class FieldCode {
	private String indent;
	
	private String columnName;
	private String fieldName;
	private String className;
	private boolean isPK;
	private boolean isUnique;
	private boolean needAnnotation;
	
	public FieldCode(String columnName,String fieldName,String className,boolean isPK, boolean isUnique,boolean needAnnotation,String indent){
		this.columnName = columnName;
		this.fieldName = fieldName;
		this.className = className;
		this.isPK = isPK;
		this.isUnique = isUnique;
		this.needAnnotation = needAnnotation;
		this.indent = indent;
	}
	
	String getName(){
		return this.fieldName;
	}
	String getColumnName(){
		return this.columnName;
	}
	
	void writeField(List<String> lines){
		CodeUtils.writeField(lines, className, fieldName,columnName, isPK, needAnnotation, isUnique, indent);
	}
	
	void writeMethod(List<String> lines){
		CodeUtils.writeSetGetMothod(lines, className, fieldName, isUnique, indent);
	}
	
}
