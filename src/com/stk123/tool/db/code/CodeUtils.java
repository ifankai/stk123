package com.stk123.tool.db.code;

import java.util.List;

public class CodeUtils {
	
	static String toUpperFirst(String str){
		return str.substring(0,1).toUpperCase()+str.substring(1);
	}
	
	static String toUpperFirstAndLowerOthers(String str){
		return str.substring(0,1).toUpperCase()+str.substring(1).toLowerCase();
	}
	
	static String columnNameToFieldName(String str){
		str = str.toLowerCase().replaceAll("$", "");
		StringBuffer sb = new StringBuffer();
		String[] s=str.split("_");
		if(s.length>0){
			sb.append(s[0]);
			for(int i=1;i<s.length;i++){
				sb.append(CodeUtils.toUpperFirstAndLowerOthers(s[i]));
			}
		}
		return sb.toString();
	}
	
	static String tableNameToClassName(String str){
		return CodeUtils.toUpperFirst(CodeUtils.columnNameToFieldName(str));
	}
	
	static void writeField(List<String> lines,String fieldClass,String fieldName,String columnName,boolean isPK,boolean needAnnotation,boolean isUnique,String indent){
		if(needAnnotation){
			lines.add(new StringBuffer().append(indent).append("@Column(name=\""+columnName+"\""+(isPK?", pk=true":"")+")").toString());
		}
		String returnClass = isUnique?fieldClass:("List<"+fieldClass+">");
		lines.add(new StringBuffer().append(indent).append("private ").append(returnClass).append(' ').append(fieldName).append(";").toString());
	}
	
	static void writeSetGetMothod(List<String> lines,String fieldClass,String fieldName,boolean isUnique,String indent){
		String returnClass = isUnique?fieldClass:("List<"+fieldClass+">");
		lines.add(new StringBuffer().append(indent).append("public "+returnClass+" get"+CodeUtils.toUpperFirst(fieldName)+"(){").toString());
		lines.add(new StringBuffer().append(indent+indent).append("return this."+fieldName+";").toString());
		lines.add(new StringBuffer().append(indent).append("}").toString());
		lines.add(new StringBuffer().append(indent).append("public void set"+CodeUtils.toUpperFirst(fieldName)+"("+returnClass+" "+fieldName+"){").toString());
		lines.add(new StringBuffer().append(indent+indent).append("this."+fieldName+" = "+fieldName+";").toString());
		lines.add(new StringBuffer().append(indent).append("}").toString());
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(CodeUtils.columnNameToFieldName("POLICY_CT_ID"));
	}
}
