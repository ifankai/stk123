package com.stk123.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.stk123.web.StkConstant;

/**
 * ����css��js�汾��Ϣ
 * @author kevin.fan
 *
 */
public class VersionTag extends TagSupport {
	
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			out.print(StkConstant.VERSION);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
}
