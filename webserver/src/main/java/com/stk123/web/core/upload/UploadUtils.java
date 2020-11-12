package com.stk123.web.core.upload;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.stk123.tool.util.StkUtils;
import com.stk123.StkConstant;

public class UploadUtils {
	
	public final static int MAX_MB = 2;//	Max Upload Size in MB
	public final static String HEADER_CONTENT_TYPE = "Content-Type";
	public final static String STR_FILENAME = "filename=\"";
	
	public static UploadResult upload(String filePath, String fileName, HttpServletRequest request) throws ServletException, IOException {
		int formDataLength = request.getContentLength();
		String contentType = request.getContentType();
		int index = contentType.lastIndexOf(StkConstant.MARK_EQUALS);
		String boundary = contentType.substring(index+1);
		//System.out.println("upload - " + formDataLength + " - " + (formDataLength/1024/1024));
		if (((double)formDataLength/1024/1024) >= MAX_MB) {
			return new UploadResult(false, null, "文件大小：" + StkUtils.numberFormat(((double)formDataLength/1024/1024), 2)	+ "MB - 超过最大限制：" + MAX_MB + "MB");
		}

		DataInputStream in = new DataInputStream (request.getInputStream());
		byte[] data = new byte[formDataLength];
		int bytesRead = 0;
		int totalBytesRead = 0;
		while (totalBytesRead < formDataLength){
			bytesRead = in.read(data, totalBytesRead, formDataLength);
			totalBytesRead += bytesRead;
		}

		//	Convert to String for easy manipulation
		String m_requestDataString = new String (data, StkConstant.ENCODING_ISO_8859_1);
		if (m_requestDataString.length() != data.length){
			return new UploadResult(false, null, "Internal conversion Error");
		}

		//	Content-Disposition: form-data; name="file"; filename="C:\Documents and Settings\jjanke\My Documents\desktop.ini"
		index = m_requestDataString.indexOf(STR_FILENAME);
		String m_fileName = m_requestDataString.substring(index+10);
		index = m_fileName.indexOf(StkConstant.MARK_DOUBLE_QUOTATION);
		if (index < 1){
			return new UploadResult(false, null, "No File Name");
		}
		m_fileName = m_fileName.substring(0, index);
		//System.out.println("upload - " + m_fileName);
		String fileType = null;
		if(m_fileName.indexOf(StkConstant.MARK_DOT) > 0){
			fileType = m_fileName.substring(m_fileName.indexOf(StkConstant.MARK_DOT)+1, m_fileName.length());
			if(!StkConstant.UPLOAD_IMAGE_TYPES.contains(fileType.toLowerCase())){
				fileType = null;
			}
		}
		if(fileType == null){
			return new UploadResult(false, null, "请上传指定的图片类型："+StkConstant.UPLOAD_IMAGE_TYPES);
		}

		int posStart = m_requestDataString.indexOf(STR_FILENAME);
		posStart = m_requestDataString.indexOf(StkConstant.MARK_NEW_LINE,posStart)+1;	//	end of Context-Disposition
		posStart = m_requestDataString.indexOf(StkConstant.MARK_NEW_LINE,posStart)+1;	//	end of Content-Type
		posStart = m_requestDataString.indexOf(StkConstant.MARK_NEW_LINE,posStart)+1;	//	end of empty line
		int posEnd = m_requestDataString.indexOf(boundary, posStart)-4;
		int length = posEnd-posStart;
		//System.out.println("uploadFile - Start=" + posStart + ", End=" + posEnd + ", Length=" + length);

		//Final copy
		byte[] m_data = new byte[length];
		for (int i = 0; i < length; i++)
			m_data[i] = data[posStart+i];
		File path = new File(filePath);
		if(!path.exists()){
			path.mkdir();
		}
		if(fileName.indexOf(StkConstant.MARK_DOT) < 0){
			fileName += StkConstant.MARK_DOT + fileType;
		}
		FileOutputStream bw = new FileOutputStream(new java.io.File(filePath + fileName));
		bw.write(m_data);
		bw.flush();
		bw.close();
		return new UploadResult(true, fileName, null);
	}
	
}
