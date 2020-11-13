package com.stk123.common.ik;

public enum DocumentField {
	
	ID("id"),
	TYPE("type"), //1:stk, 2:text, 3:industry
	CODE("code"),
	TITLE("title"),
	CONTENT("content"),
	TIME("time"),
	ORDER("order"),
	
	USERID("userid"),
	KEYWORD("keyword"),
	F9("f9"),
	F9ZHUYIN("f9zhuyin"),
	COMPANYPROFILE("companyprofile"),
	
	SUMMARY("summary");
	
	private String name;
	
	private DocumentField(String name){
		this.name = name;
	}
	public String value() {
        return name;
    }
	public static DocumentField valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }
}
