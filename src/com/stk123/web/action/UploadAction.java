package com.stk123.web.action;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.stk123.model.User;
import com.stk123.tool.web.upload.UploadResult;
import com.stk123.tool.web.upload.UploadUtils;
import com.stk123.web.context.StkContext;

public class UploadAction {
	
	public void perform() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		HttpServletRequest request = sc.getRequest();
		String filePath = user.getUserUploadImagePath();
		String fileName = UUID.randomUUID().toString();
		String callback = request.getParameter("CKEditorFuncNum");
		UploadResult result = UploadUtils.upload(filePath, fileName, request);
		StringBuffer sb = new StringBuffer();
		sb.append("<script type=\"text/javascript\">");
		if(result.isSuccessful()){
			sb.append("window.parent.CKEDITOR.tools.callFunction("+ callback + ",'" + user.getUserUploadImageRelativePath() + result.getFileName() + "','')");
		}else{
			sb.append("window.parent.alertAndClose(20,350,3000,'"+result.getErrMsg()+"');"); 
			sb.append("window.parent.CKEDITOR.tools.callFunction("+ callback + ",'','')");
		}
		sb.append("</script>");
		sc.setResponse(sb.toString());
	}
}
