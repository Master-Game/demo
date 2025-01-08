package com.anjin.ccc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ok {
	@RequestMapping("/ok")
	public String ok() {
		return "ok";
	}
}
