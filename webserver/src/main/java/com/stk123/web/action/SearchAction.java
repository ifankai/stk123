package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import com.stk123.model.Index;
import com.stk123.model.User;
import com.stk123.service.ServiceUtils;
import com.stk123.common.ik.DocumentField;
import com.stk123.common.ik.DocumentType;
import com.stk123.web.ik.Search;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.util.collection.Name2ListSet;
import com.stk123.web.core.util.RequestUtils;
import com.stk123.common.CommonConstant;
import com.stk123.web.WebUtils;
import com.stk123.web.context.StkContext;


public class SearchAction {
	
	public String perform() throws Exception {
		return CommonConstant.ACTION_SUCC;
	}
	
	public void search() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		if(user == null){
			return;
		}
		HttpServletRequest request = sc.getRequest();
		Connection conn = StkContext.getConnection();
		String searchword = request.getParameter(CommonConstant.PARAMETER_Q);
		int page = RequestUtils.getInt(request, CommonConstant.PARAMETER_PAGE);
		
		int perPage = 20;
		int start = (page-1)*perPage;
		int end = page * perPage;
		
		Search search = new Search(user.getStkUser().getId());
		Search.TotalCount totalCount = new Search.TotalCount();
		List<Document> docs = search.searchAll(searchword, false, totalCount, start, end);

		List<Map> results = new ArrayList<Map>();
		Name2ListSet set = new Name2ListSet();
		List<String> codeExisting = new ArrayList<String>();
		for(Document doc : docs){
			Map map = new HashMap();
			List<IndexableField> fields = doc.getFields();
			String id = doc.get(DocumentField.ID.value());
			String type = doc.get(DocumentField.TYPE.value());
			//title field
			if(DocumentType.STK.value().equals(type)){
				Index index = new Index(conn, id);
				map.put(DocumentField.TITLE.value(), "<a target=\"_blank\" href=\"/stk?s="+index.getCode()+"\">"+doc.get(DocumentField.TITLE.value())+"</a>");
				map.put(DocumentField.CONTENT.value(), WebUtils.display(doc.get(DocumentField.CONTENT.value()), doc.get(DocumentField.SUMMARY.value()), 330));
				codeExisting.add(index.getCode());
			}else if(DocumentType.INDUSTRY.value().equals(type)){
				String title = "[行业] <a target=\"_blank\" href=\"/industry?id="+id+"\">"+doc.get(DocumentField.TITLE.value())+"</a>";
				map.put(DocumentField.TITLE.value(), title);
				set.addIfNotExist(DocumentType.INDUSTRY.value(), title);
			}else if(DocumentType.TEXT.value().equals(type)){
				String title = doc.get(DocumentField.TITLE.value());
				String code = doc.get(DocumentField.CODE.value());
				if(title == null && code != null){
					Index index = new Index(conn, code);
					title = ServiceUtils.wrapCodeAndNameAsHtml(index);
					codeExisting.add(index.getCode());
				}else if(code != null && title != null){
					Index index = new Index(conn, code);
					title = "<a target=\"_blank\" href=\"/stk?s="+index.getCode()+"\">"+index.getName()+"["+index.getCode()+"]</a>&nbsp;"+title;
					codeExisting.add(index.getCode());
				}else{
					title = "[文档] <a target=\"_blank\" href=\"/article?id="+id+"\">"+title+"</a>";
					set.addIfNotExist(DocumentType.TEXT.value(), title);
				}
				map.put(DocumentField.TITLE.value(), title);
				map.put(DocumentField.CONTENT.value(), WebUtils.display(doc.get(DocumentField.CONTENT.value()), doc.get(DocumentField.SUMMARY.value()), 330));
			}else if(DocumentType.INDEX.value().equals(type)){
				String title = "[指标] <a target=\"_blank\" href=\"/data?id="+id+"\">"+doc.get(DocumentField.TITLE.value())+"</a>";
				map.put(DocumentField.TITLE.value(), title);
				set.addIfNotExist(DocumentType.INDEX.value(), title);
			}
			map.put(DocumentField.ID.value(), doc.get(DocumentField.ID.value()));
			String time = doc.get(DocumentField.TIME.value());
			if(time != null){
				map.put(DocumentField.TIME.value(), ServiceUtils.formatDate(time, ServiceUtils.sf_ymd12, ServiceUtils.sf_ymd9) );
			}
			results.add(map);
		}
		//出现N次的股票后面加上“N次”
		List<String> stkTitles = new ArrayList<String>();
		for(String code : codeExisting){
			int cnt = Collections.frequency(codeExisting, code);
			Index index = new Index(conn, code);
			if(cnt > 1){
				stkTitles.add(ServiceUtils.wrapCodeAndNameAsHtml(index) + cnt + "次");
			}else{
				stkTitles.add(ServiceUtils.wrapCodeAndNameAsHtml(index));
			}
		}
		for(String title : stkTitles){
			set.addIfNotExist(DocumentType.STK.value(), title);
		}
		
		set.sort();
		String json = JsonUtils.getJsonString4JavaPOJO(results);
		json = "{\"left\":"+json+", \"right\":"+JsonUtils.getJsonString4JavaPOJO(set)+CommonConstant.MARK_BRACE_RIGHT;
		
		StringBuffer sb = new StringBuffer(CommonConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(totalCount.totalCount).append(CommonConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(CommonConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(CommonConstant.MARK_BRACE_RIGHT);

		sc.setResponse(sb.toString());
	}
	
}
