package com.stk123.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class StkProperties {

    @Value("${stk.barRowsDefault:500}")
    private Integer barRowsDefault;

    @Value("${stk.barImageLazyload:true}")
    private Boolean barImageLazyload;

}
