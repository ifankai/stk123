package com.stk123.tool.web.upload;

public class UploadResult {
	
	private boolean successful;
	private String fileName;
	private String errMsg;
	
	public UploadResult(boolean successful, String uploadFileName, String errMsg){
		this.successful = successful;
		this.fileName = uploadFileName;
		this.errMsg = errMsg;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	
}
