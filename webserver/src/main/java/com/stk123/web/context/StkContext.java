package com.stk123.web.context;

import java.util.List;

import com.stk123.bo.StkFnType;
import com.stk123.model.Fn;
import com.stk123.model.Index;
import com.stk123.model.User;
import com.stk123.tool.web.ActionContext;
import com.stk123.web.StkConstant;


public class StkContext extends ActionContext {
	
	private static final long serialVersionUID = -3327995644423262347L;
	
	private Index index;
	
	public static StkContext getContext(){
		return (StkContext)ActionContext.getContext();
	}

	public List<StkFnType> getFnTypes(int market) throws Exception{
		/*List<StkFnType> result = (List<StkFnType>)this.getApplication().getAttribute("fn_type_"+market); 
		if(result == null){
			result = JdbcUtils.list(ActionContext.getConnection(), "select * from stk_fn_type where market="+market+" and disp_order <> 1000 order by disp_order asc", StkFnType.class);
			this.getApplication().setAttribute("fn_type_"+market, result);
		}
		return result;*/
		return Fn.getFnTypesForDisplay(market);
	}

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}
	
	public User getUser(){
		return (User)this.get(StkConstant.SESSION_CURRENT_USER);
	}
	
	
}
