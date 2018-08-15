package com.bb.vivria.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ChatController {
	@RequestMapping(value = "/chat", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		return "chat/test";
	}
}
