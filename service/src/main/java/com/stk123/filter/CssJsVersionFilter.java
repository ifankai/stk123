package com.stk123.filter;

import javax.servlet.*;
import java.io.IOException;
import java.util.Date;

public class CssJsVersionFilter implements Filter {

    private final static String VERSION = String.valueOf(new Date().getTime());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setAttribute("_version", VERSION);
        filterChain.doFilter(request, response);
    }

}
