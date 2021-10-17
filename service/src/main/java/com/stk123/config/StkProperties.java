package com.stk123.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "stk")
public class StkProperties {

    private Integer barRowsDefault;
}
