package com.stk123.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class WebProperties {
    @Value("${stk.environment}")
    private String environment;

}
