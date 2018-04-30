package com.stk123.tool.ik;

public enum DocumentType {
	STK("1"), 
	TEXT("2"),
	INDUSTRY("3"),
	INDEX("4");
	
	private String value;
	
	private DocumentType(String value){
		this.value = value;
	}
	public String value() {
        return value;
    }
	public static DocumentType valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }
}
