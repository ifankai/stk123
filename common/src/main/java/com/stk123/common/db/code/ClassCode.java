package com.stk123.common.db.code;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stk123.common.db.Column;
import com.stk123.common.db.FK;
import com.stk123.common.db.Table;
import com.stk123.common.util.JdbcUtils;



public class ClassCode {
	
	private static final String EMPTY_LINE = "";
	
	private String packName;
	private String clazzName;
	private Map<String,String> importClazz = new HashMap<String,String>();
	private List<FieldCode> fields = new ArrayList<FieldCode>();
	
	private String indent;
	private Table table;
	
	private boolean needAnnotation;

	public ClassCode(String packName, Table table){
		this(packName,null,table, false,false,false, "    ");
	}
	
	public ClassCode(String packName,String clazzName, Table table, boolean needAnnotation,boolean codeChildTables,boolean codeParentTables){
		this(packName,clazzName,table,needAnnotation, codeChildTables,codeParentTables, "    ");
	}
	
	public ClassCode(String packName,String clazzName, Table table,boolean needAnnotation,boolean codeChildTables,boolean codeParentTables,String indent){
		this.packName = packName;
		this.table = table;
		this.clazzName = clazzName==null?CodeUtils.tableNameToClassName(table.getName()):clazzName;
		for(Column column:table.getColumns()){
			FieldCode field = new FieldCode(column.getName(),CodeUtils.columnNameToFieldName(column.getName()),
					importClass(column.getType()),column.isPK(),true,needAnnotation,indent);
			fields.add(field);
		}
		this.needAnnotation = needAnnotation;
		this.indent = indent;
		
		if(codeChildTables){
			if(this.table.getPK() != null){
				for(Table tab:this.table.getChildTables()){
					importClass(List.class);
					if(!tab.getName().equals(this.table.getName())){
						boolean isUniqueIdx = false;
						for(FK fk:tab.getFKsByParentTable(this.table.getName())){
							for(Column col:fk.getColumns()){
								isUniqueIdx = col.isIndexColumn(true);
							}
						}
						FieldCode field = new FieldCode(null,CodeUtils.columnNameToFieldName(tab.getName()),
								CodeUtils.tableNameToClassName(tab.getName()),false,isUniqueIdx,false,indent);
						fields.add(field);
					}
				}
			}
		}
		
		if(codeParentTables){
			List<Table> parentTables = new ArrayList<Table>();
			Set<Table> dupTabs = new HashSet<Table>();
			for(FK fk:this.table.getFKs()){
				Table tab = fk.getParentTable();
				if(!tab.getName().equals(this.table.getName())){
					if(parentTables.contains(tab)){
						dupTabs.add(tab);
					}
					parentTables.add(tab);
				}
			}

			Collections.sort(this.table.getFKs(), new Comparator<FK>(){
				public int compare(FK o1, FK o2) {
			        return o1.getParentTable().getName().compareTo(o2.getParentTable().getName());
				}
			});
			
			for(FK fk:this.table.getFKs()){
				Table tab = fk.getParentTable();
				if(!tab.getName().equals(this.table.getName())){
					String fieldName = CodeUtils.columnNameToFieldName(tab.getName());
					if(dupTabs.contains(tab)){
						fieldName = CodeUtils.columnNameToFieldName(tab.getName())+CodeUtils.columnNameToFieldName(getColumnsAsString(fk.getColumns()));
					}
					FieldCode field = new FieldCode(null,fieldName,
							CodeUtils.tableNameToClassName(tab.getName()),false,true,false,indent);
					fields.add(field);
				}
			}	
		}
	}
	
	private FieldCode getFieldCode(String fieldName){
		for(FieldCode field:this.fields){
			if(field.getName().equals(fieldName)){
				return field;
			}
		}
		return null;
	}

	private static final String $ = JdbcUtils.Query.$;
	
	private String getColumnsAsString(List<Column> columns){
		StringBuffer sb = new StringBuffer($);
		for(Column column:columns){
			sb.append(column.getName()).append($);
		}
		return sb.toString().substring(0,sb.length()-$.length());
	}
	
	private String importClass(Class clazz){
		String clazzName = clazz.getName();
		String simpleClazzName = clazzName.substring(clazzName.lastIndexOf(".")+1);
		if(clazzName.indexOf("java.lang") >= 0){
			return simpleClazzName;
		}else{
			if(importClazz.containsKey(simpleClazzName)){
				if(importClazz.get(simpleClazzName).equals(clazzName)){
					return simpleClazzName;
				}else{
					return clazzName;
				}
			}
			importClazz.put(simpleClazzName,clazzName.replace('$', '.'));
		}
		return simpleClazzName;
	}
	
	public List<String> writeClass(){
		//------------
		importClass(Serializable.class);
		if(this.needAnnotation){
			importClass(com.stk123.common.util.JdbcUtils.Table.class);
			importClass(com.stk123.common.util.JdbcUtils.Column.class);
		}
		//------------
		List<String> lines = new ArrayList<String>();
		//-----------------package xx.xx.xx
		lines.add("package "+packName+";");
		lines.add(EMPTY_LINE);
		//-----------------import xx.xx.XXXX
		for(String s:importClazz.keySet()){
			lines.add("import "+importClazz.get(s)+";");
		}
		lines.add(EMPTY_LINE);
		//-----------------public class XXXX {
		if(this.needAnnotation){
			lines.add("@SuppressWarnings(\"serial\")");
			lines.add("@Table(name=\""+table.getName()+"\")");
		}
		lines.add("public class "+clazzName+" implements Serializable {");
		lines.add(EMPTY_LINE);
		//----------------------private XXXX xxx
		for(FieldCode field:fields){
			field.writeField(lines);
			lines.add(EMPTY_LINE);
		}
		
		lines.add(EMPTY_LINE);
		//----------------------public set/getXXXX
		for(FieldCode field:fields){
			field.writeMethod(lines);
			lines.add(EMPTY_LINE);
		}
		lines.add(EMPTY_LINE);
		//----------------------public String toString(){
		lines.add(indent+"public String toString(){");
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<fields.size();i++){
			FieldCode field = fields.get(i);
			if(i==fields.size()-1){
				sb.append(field.getName()).append("=\"+").append(field.getName());
			}else if(i==0){
				sb.append("\"").append(field.getName()).append("=\"+").append(field.getName()).append("+\",");
			}else{
				sb.append(field.getName()).append("=\"+").append(field.getName()).append("+\",");
			}
		}
		lines.add(indent+indent+"return "+sb+";");
		lines.add(indent+"}");
		lines.add(EMPTY_LINE);
		//---------------}//end
		lines.add("}");
		return lines;
	}
	
	public String getClassName(){
		return this.clazzName;
	}
	
}
