package com.algaworks.brewer.config.init;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		
		/*
		 * Quantidade de segundos até expirar a seção, mesmo em atividade.
		 */
		//servletContext.getSessionCookieConfig().setMaxAge(64000);
		
		/*
		 * Evita que o JSESSIONID apareça na URL, passando ele a ser utilizado em cookie.
		 */
		servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
		
		
		/*
		 * Configuração de acentuação
		 */
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("encodingFilter",
				new CharacterEncodingFilter());
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		characterEncodingFilter.setInitParameter("forceEncoding", "true");
		characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
	}
	
}
