package com.stk123.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class ApplicationFilter implements Filter {

    private final static String CSS_JS_VERSION = '?'+String.valueOf(new Date().getTime());

    private static final Pattern STATIC_RESOURCES = Pattern.compile("(^/js/.*)|(^/css/.*)|(^/img/.*)|(^/fonts/.*)|(^/adminlte/.*)|(^/dist/.*)");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String uri = httpServletRequest.getServletPath();
        if (STATIC_RESOURCES.matcher(uri).matches()) {
            filterChain.doFilter(request, response);
            return;
        }
        request.setAttribute("_version", CSS_JS_VERSION);
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        }finally {
            log.info("==> uri: {}, {}, cost: {}ms", uri, getParametersAsMap(httpServletRequest), System.currentTimeMillis() - start);
        }
    }

    private Map getParametersAsMap(HttpServletRequest request){
        Enumeration<String> enumeration = request.getParameterNames();
        Map<String, Object> modelMap = new HashMap<>();
        while(enumeration.hasMoreElements()){
            String parameterName = enumeration.nextElement();
            modelMap.put(parameterName, request.getParameter(parameterName));
        }
        return modelMap;
    }

}
