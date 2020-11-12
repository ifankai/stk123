package com.stk123.web.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.stk123.tool.db.connection.Pool;


public class ActionContext implements Serializable {
	
	private static final long serialVersionUID = 3593467518246194206L;
	private static final String CONTENT_TYPE = "text/html;charset=gbk";

	public static final String KEY_WORD = ActionContext.class.getName()+"_context";
	
	private static ThreadLocal<ActionContext> actionContext = new ThreadLocal<ActionContext>();
	private Map<String, Object> context;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ServletContext application;
    private Object form;
    private Connection conn;

    private static final String LOCALE = ActionContext.class.getName()+".locale";
    
    public ActionContext(){
    	this.context = new HashMap<String, Object>();
    }
    
    private Connection getConnection0() throws SQLException{
    	if(conn != null)return conn;
    	if(Pool.getPool() != null){
    		conn = Pool.getPool().getConnection();
    	}
    	return conn;
    }
    
    public void freeConnection0(){
    	if(Pool.getPool() != null && conn != null){
    		Pool.getPool().free(conn);
    	}
    }
    
    public void setResponse(Object obj) throws IOException{
    	response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
        out.println(obj); 
        out.flush();
    }
    
    public void setResponseAsJson(String json) throws IOException{
    	this.setResponse(json);
    }
    
    public static Connection getConnection() throws SQLException{
    	return ActionContext.getContext().getConnection0();
    }
    
    public static void freeConnection(){
    	ActionContext.getContext().freeConnection0();
    }
    
    //clear connection and context
    public static void clear(){
    	if(ActionContext.getContext() != null){
    		ActionContext.freeConnection();
    		ActionContext.setContext(null);
    	}
    }
    
    /**
     * Sets the action context for the current thread.
     *
     * @param context the action context.
     */
    public static void setContext(ActionContext context) {
        actionContext.set(context);
    }

    /**
     * Returns the ActionContext specific to the current thread.
     *
     * @return the ActionContext for the current thread, is never <tt>null</tt>.
     */
    public static ActionContext getContext() {
        return (ActionContext) actionContext.get();
    }

    /**
     * Sets the action's context map.
     *
     * @param contextMap the context map.
     */
    protected void setContextMap(Map<String, Object> contextMap) {
        getContext().context = contextMap;
    }

    /**
     * Gets the context map.
     *
     * @return the context map.
     */
    public Map<String, Object> getContextMap() {
        return context;
    }


    /**
     * Sets the Locale for the current action.
     *
     * @param locale the Locale for the current action.
     */
    public void setLocale(Locale locale) {
        put(LOCALE, locale);
    }

    /**
     * Gets the Locale of the current action. If no locale was ever specified the platform's
     * {@link java.util.Locale#getDefault() default locale} is used.
     *
     * @return the Locale of the current action.
     */
    public Locale getLocale() {
        Locale locale = (Locale) get(LOCALE);

        if (locale == null) {
            locale = Locale.getDefault();
            setLocale(locale);
        }

        return locale;
    }

    /**
     * Returns a value that is stored in the current ActionContext by doing a lookup using the value's key.
     *
     * @param key the key used to find the value.
     * @return the value that was found using the key or <tt>null</tt> if the key was not found.
     */
    public Object get(String key) {
        return context.get(key);
    }

    /**
     * Stores a value in the current ActionContext. The value can be looked up using the key.
     *
     * @param key   the key of the value.
     * @param value the value to be stored.
     */
    public void put(String key, Object value) {
        context.put(key, value);
    }


	public HttpServletRequest getRequest() {
		return request;
	}


	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public HttpSession getSession() {
		return session;
	}


	public void setSession(HttpSession session) {
		this.session = session;
	}


	public void setApplication(ServletContext application) {
		this.application = application;
	}
	
	public ServletContext getApplication() {
		return this.application;
	}

	public Object getForm() {
		return form;
	}

	public void setForm(Object form) {
		this.form = form;
	}
    
}
