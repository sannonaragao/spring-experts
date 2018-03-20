package com.algaworks.brewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ErrosController {

	@GetMapping("/404")
	public String paginaNaoEncontrada(){
		return "404";
	}
	@PostMapping("/500")
	public String erroInternoServidor(){
		return "500";
	}
}
