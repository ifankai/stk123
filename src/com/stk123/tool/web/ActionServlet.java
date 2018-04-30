package com.stk123.tool.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;

import com.stk123.bo.StkUser;
import com.stk123.model.User;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.util.CacheUtils;
import com.stk123.tool.web.config.ActionConfig;
import com.stk123.tool.web.config.ConfigHelper;
import com.stk123.tool.web.config.ForwardConfig;
import com.stk123.tool.web.config.MvcConfig;
import com.stk123.tool.web.util.RequestUtils;
import com.stk123.web.StkConstant;

public class ActionServlet extends HttpServlet {

    public static final String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
    public static final String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
    
    public static final String DEFAULT_METHOD = "perform";
    public static final String REQUEST_METHOD = "method";
    
    private static MvcConfig mvcConfig;
    private static final String mvcConfigFile = "mvc-config.xml";
    
    //private Pool pool;
    private String actionContext = ActionContext.class.getName();

    public void destroy() {
    	mvcConfig = null;
    	if(Pool.getPool() != null){
    		Pool.getPool().closeAllConnections();
    	}
    	CacheUtils.close();
    }
    
    public void init() throws ServletException {
    	try {
    		System.out.println("action servlet init...");
    		String ac = this.getInitParameter("action-context");
			if(ac != null && ac.length() > 0){
				actionContext = ac;
				Object obj = RequestUtils.applicationInstance(actionContext);
				if(!(obj instanceof ActionContext)){
					throw new ServletException("action-context must be subclass of ActionContext");
				}
			}
			mvcConfig = ConfigHelper.parseMvcConfig(MvcConfig.class, mvcConfigFile);
			
			//BeanUtils
			ConvertUtils.deregister();
			ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
		    ConvertUtils.register(new BigIntegerConverter(null), BigInteger.class);
		    ConvertUtils.register(new BooleanConverter(null), Boolean.class);
		    ConvertUtils.register(new ByteConverter(null), Byte.class);
		    ConvertUtils.register(new CharacterConverter(null), Character.class);
		    ConvertUtils.register(new DoubleConverter(null), Double.class);
		    ConvertUtils.register(new FloatConverter(null), Float.class);
		    ConvertUtils.register(new IntegerConverter(null), Integer.class);
		    ConvertUtils.register(new LongConverter(null), Long.class);
		    ConvertUtils.register(new ShortConverter(null), Short.class);

		    if(StkUtils.isDev()){
		    	System.out.println("This is localhost");
		    }else{
		    	System.out.println("This is stk123.cn");
		    }
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("init error.");
		}
    }

    public void doGet(HttpServletRequest request,
              HttpServletResponse response)
        throws IOException, ServletException {
        process(request, response);
    }

    public void doPost(HttpServletRequest request,
               HttpServletResponse response)
        throws IOException, ServletException {
        process(request, response);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
    	try{
    		//System.out.println("action servlet process...");
	        processNoCache(request, response);
	        String path = processPath(request, response);
	        if (path == null) {
	            return;
	        }
	        ActionConfig actionConfig = mvcConfig.getActions().get(path);
	        if(actionConfig == null){
	    		System.out.println("error: action config is null.");
	    		return;
	    	}
	        
	        ForwardConfig forwardConfig = null;
	        do{
		        Object form = createActionForm(request, response, actionConfig);
		        
		        createActionContext(request, response, form);
		        
		        if (!processForward(request, response, actionConfig)) {
		        	System.out.println("error: create action context failed.");
		            return;
		        }
		        //TODO
		        /*if (!processInclude(request, response, action)) {
		            return;
		        }*/
		        Object action = createAction(request, response, actionConfig);
		        if (action == null) {
		        	System.out.println("error: create action failed.");
		            return;
		        }
		        String forward = createActionPerform(request, response, actionConfig, action);
		        //System.out.println("forward==="+forward);
		        if(forward == null){
		        	return;
		        }
		        forwardConfig = actionConfig.getForwardConfig(forward);
		        if(forwardConfig == null){//find global forward
		        	forwardConfig = mvcConfig.getForwards().get(forward);
		        }
		        
		        if(forwardConfig == null && forward.startsWith("/")){
		        	//System.out.println(forward);
		        	forwardConfig = new ForwardConfig();
		        	forwardConfig.setPath(forward);
		        }
		        if(forwardConfig == null){
		        	throw new ServletException("forward name '"+forward+"' is not existing under action "+actionConfig.getPath()); 
		        }
		        /**
		         * 内部跳转，根据mvc-config.xml的配置
		         * <forward name="fail" redirect="true" path="index" />直接到转到path=index的action上 
		         */
		        actionConfig = mvcConfig.getActions().get(forwardConfig.getPath());
    		}while(actionConfig != null);
	        
	        processForwardConfig(request, response, forwardConfig);
	        
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		ActionContext.clear();
    	}
    }
    
    public ActionContext createActionContext(HttpServletRequest request, 
    		HttpServletResponse response, Object form) throws ServletException {
        ActionContext oldContext = ActionContext.getContext();
        if (oldContext != null) {//内部跳转
            return oldContext;
        } else {
        	ActionContext ctx = null;
            try {
				ctx = (ActionContext)RequestUtils.applicationInstance(actionContext);
			} catch (Exception e) {
				throw new ServletException("instance action context error.");
			}
            HttpSession session = request.getSession(false);
            
            ctx.setRequest(request);
            ctx.setResponse(response);
            ctx.setSession(session);
            ctx.setApplication(this.getServletContext());
            ctx.setForm(form);
            if(session != null){
            	User user = (User)session.getAttribute(StkConstant.SESSION_CURRENT_USER);
    			ctx.put(StkConstant.SESSION_CURRENT_USER, user);
            	//throw new ServletException("请重新登陆!");
            }
            if(!StkUtils.isDev()){
            	StkUser su = new StkUser();
            	su.setId(1);
            	su.setNickname("Stk123之路");
            	User user = new User(su);
            	if(session == null){
            		session = request.getSession();
            	}
            	session.setAttribute(StkConstant.SESSION_CURRENT_USER, user);
            	ctx.put(StkConstant.SESSION_CURRENT_USER, user);
            }
            ActionContext.setContext(ctx);
            return ctx;
        }
    }
    
	protected String createActionPerform(HttpServletRequest request,
			HttpServletResponse response,ActionConfig actionConfig, Object action) throws IOException, ServletException {
		try {       
			String methodName = actionConfig.getMethod();
			String methodRequest = request.getParameter(REQUEST_METHOD);
			if(methodRequest != null && methodRequest.length() > 0){
				methodName = methodRequest;
			}
			if(methodName == null || methodName.length() == 0){
				methodName = DEFAULT_METHOD;
			}
			System.out.println("action="+action.getClass().getName()+",method="+methodName);
			Method method = action.getClass().getMethod(methodName); 
			Object methodResult =  method.invoke(action);
			return (String) methodResult;       
		} catch (Exception e) {
			e.printStackTrace();
			return (processException(request, response, e));
		}

	}
    
	protected String processException(HttpServletRequest request,
			HttpServletResponse response, Exception exception) throws IOException, ServletException {
		exception.printStackTrace();
		/*// Is there a defined handler for this exception?
		ExceptionConfig config = mapping.findException(exception.getClass());
		if (config == null) {
			if (exception instanceof IOException) {
				throw (IOException) exception;
			} else if (exception instanceof ServletException) {
				throw (ServletException) exception;
			} else {
				throw new ServletException(exception);
			}
		}

		// Use the configured exception handling
		try {
			ExceptionHandler handler = (ExceptionHandler) RequestUtils
					.applicationInstance(config.getHandler());
			return (handler.execute(exception, config, mapping, form, request,
					response));
		} catch (Exception e) {
			throw new ServletException(e);
		}*/
		return StkConstant.ACTION_FAIL;
	}
    
    
    protected Object createActionForm(HttpServletRequest request,
            HttpServletResponse response, ActionConfig action) throws ServletException, Exception {
    	String type = action.getForm();
    	if(type == null){
    		return null;
    	}
    	Object form = formBeanClass(type).newInstance();
    	RequestUtils.populate(form, request);
    	return form;
    }
    
    protected Class formBeanClass(String type) {
        ClassLoader classLoader =
            Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        try {
            return (classLoader.loadClass(type));
        } catch (Exception e) {
            return (null);
        }
    }
            
    protected void processNoCache(HttpServletRequest request,
            HttpServletResponse response) {
		if (true) {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache,no-store,max-age=0");
			response.setDateHeader("Expires", 1);
		}
	}
    
	protected String processPath(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String path = request.getPathInfo();
		
		// For prefix matching, match on the path info (if any)
		/*path = (String) request.getAttribute(INCLUDE_PATH_INFO);
		if (path == null) {
			path = request.getPathInfo();
		}
		if ((path != null) && (path.length() > 0)) {
			int slash = path.indexOf("/");
			return (path);
		}

		// For extension matching, strip the module prefix and extension
		path = (String) request.getAttribute(INCLUDE_SERVLET_PATH);*/
		if (path == null) {
			path = request.getServletPath();
		}
		int slash = path.indexOf(StkConstant.MARK_SLASH);
		int period = path.lastIndexOf(StkConstant.MARK_DOT);
		if(period == -1)period = path.length();
		if ((period >= 0) && (period > slash)) {
			path = path.substring(slash+1, period);
		}
		return (path);

	}
	
	protected boolean processForward(HttpServletRequest request,
			HttpServletResponse response, ActionConfig action)
			throws IOException, ServletException {

		// Are we going to processing this request?
		String forward = action.getForward();
		if (forward == null) {
			return (true);
		}
		doForward(forward, request, response);
		return false;
	}
	
	protected void doForward(String uri, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		RequestDispatcher rd = getServletContext().getRequestDispatcher(uri);
		if (rd == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"forward requestDispatcher error:"+uri);
			return;
		}
		rd.forward(request, response);
	}
	
	protected Object createAction(HttpServletRequest request,
            HttpServletResponse response,
            ActionConfig actionConfig)	throws IOException {
		Object instance = null;
		try {
            instance =  RequestUtils.applicationInstance(actionConfig.getType());
        } catch (Exception e) {
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "actionCreate error:" + actionConfig.getPath());
            e.printStackTrace();
            return (null);
        }
		return (instance);
	}
	
	protected void processForwardConfig(HttpServletRequest request,
			HttpServletResponse response, ForwardConfig forward)
			throws IOException, ServletException {

		if (forward == null) {
			return;
		}
		String uri = forward.getPath();

		/*if (forwardPath.startsWith("/")) {
			uri = RequestUtils.forwardURL(request, forward, null); 
		} else {
			uri = forwardPath;
		}*/
		if (forward.isRedirect()) {
			// only prepend context path for relative uri
			if (uri.startsWith(StkConstant.MARK_SLASH)) {
				uri = request.getContextPath() + uri;
			}
			response.sendRedirect(response.encodeRedirectURL(uri));
		} else {
			doForward(uri, request, response);
		}

	}
}

