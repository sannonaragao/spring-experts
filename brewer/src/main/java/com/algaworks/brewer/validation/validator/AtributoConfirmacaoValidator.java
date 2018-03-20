package com.algaworks.brewer.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.apache.commons.beanutils.BeanUtils;

import com.algaworks.brewer.validation.AtributoConfirmacao;

public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object>{

	
	private String atributo;
	private String atributoConfirmacao;
	
	@Override
	public void initialize(AtributoConfirmacao constraintAnnotation) {
		this.atributo  = constraintAnnotation.atributo();
		this.atributoConfirmacao = constraintAnnotation.atributoConfirmacao();
			
		
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		boolean valido = false;
		try {
			Object valorAtributo = BeanUtils.getProperty(object, this.atributo);
			Object valorConfirmacao = BeanUtils.getProperty(object, this.atributoConfirmacao); 
			
			valido = ambosSaoNull(valorAtributo, valorConfirmacao)  || ambosSaoIguais(valorAtributo, valorConfirmacao);
			
			
		} catch (Exception e) {
			throw new RuntimeException("Erro recuperado valores dos atributos", e);
			
		}
		
		if (!valido){
			context.disableDefaultConstraintViolation();  //Desabilita a mensagem default que estava duplicando
			String mensagem = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);
			violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}
		
		return valido;
	}

	private boolean ambosSaoIguais(Object valorAtributo, Object valorConfirmacao) {
		return valorAtributo != null && valorAtributo.equals(valorConfirmacao) ;
	}

	private boolean ambosSaoNull( Object valorAtributo, Object valorConfirmacao){
		return valorAtributo == null && valorConfirmacao == null;
	}
			
}
