package com.stk123.spring.control;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
@CommonsLog
public class HomeController {

    @RequestMapping("/")
    public String home(Device device) {
        if (device.isMobile()) {
            log.info("Hello mobile user!");
            return "forward:/m";
        } else if (device.isTablet()) {
            log.info("Hello tablet user!");
        } else {
            log.info("Hello desktop user!");
        }
        return "/";
    }

}
