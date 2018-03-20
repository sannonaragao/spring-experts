package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.algaworks.brewer.security.AppUserDetailsService;

@EnableWebSecurity
@ComponentScan(basePackageClasses = AppUserDetailsService.class)
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService	userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

/*		auth.inMemoryAuthentication()
			.withUser("admin").password("admin").roles("CADASTRO_CLIENTE");*/
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/cidades/novo").hasRole("CADASTRAR_CIDADE")
				.antMatchers("/usuarios/**").hasRole("CADASTRAR_USUARIO")
				.anyRequest().authenticated()
//				.anyRequest().denyAll()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
			.and()
				.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.and()
				.exceptionHandling()
				.accessDeniedPage("/403");

		/*
		 * Opção abaixo impede que seja aberto o mesmo login em mais de 1 tela 
		 * 

		 .and()
		 .sessionManagement()
		 	.mnaximumSessions(1)
		 	.expiredUrl("/login")  <-- Redireciona para outra tela caso tenham feito um "login por cima"
		 	;
		  
		 */
		
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/layout/**")
			.antMatchers("/images/**");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
