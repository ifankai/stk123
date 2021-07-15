package com.stk123.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class WebProperties {
    @Value("${com.neo.title}")
    private String title;

}
