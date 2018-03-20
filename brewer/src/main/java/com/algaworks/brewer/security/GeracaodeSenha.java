package com.algaworks.brewer.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeracaodeSenha {

	public static void main(String[] args){
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("teste"));
	}
}
