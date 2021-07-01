package com.bb.vivria.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TestController {
	
	@RequestMapping(value = "/test/test", method = RequestMethod.GET)
	public String test(Locale locale, Model model) {
		return "test/test.jsp";
	}
}