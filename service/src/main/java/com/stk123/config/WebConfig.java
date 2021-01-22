package com.stk123.config;

import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "DELETE", "PUT","PATCH","OPTIONS")
                        .maxAge(3600);
            }
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                converters.add(customJackson2HttpMessageConverter());
            }
        };
    }

    /**
     * 解决低版本ie responsebody返回json的时候提示下载问题
     * @return
     */
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        MediaType media = new MediaType(MediaType.TEXT_HTML, Charset.forName("UTF-8"));
        supportedMediaTypes.add(media);
        media = new MediaType(MediaType.APPLICATION_JSON_UTF8, Charset.forName("UTF-8"));
        supportedMediaTypes.add(media);
        jsonConverter.setSupportedMediaTypes(supportedMediaTypes);
        return jsonConverter;
    }


    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }

    @Bean
    public FilterRegistrationBean testFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MyFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("MyFilter");
        registration.setOrder(1);
        return registration;
    }

    public class MyFilter implements Filter {
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
				throws IOException, ServletException {
			//System.out.println("this is MyFilter,url :"+request.getRequestURI());
			filterChain.doFilter(request, response);
		}

		@Override
		public void init(FilterConfig arg0) throws ServletException {
			// TODO Auto-generated method stub
		}
    }
}



