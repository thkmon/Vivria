package com.bb.vivria.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GameController {
	@RequestMapping(value = "/game/vivria/room", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		return "game/vivria/room";
	}
}
