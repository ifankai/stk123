package com.stk.web;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CommonsLog
public class ThymeleafController {
	
    @RequestMapping("/hi")
	public String hello(Locale locale, Model model) {
    	log.info("hello............");
		model.addAttribute("greeting", "Hello!");

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);        
		String formattedDate = dateFormat.format(date);
		model.addAttribute("currentTime", formattedDate);

		return "hello";
	}

}