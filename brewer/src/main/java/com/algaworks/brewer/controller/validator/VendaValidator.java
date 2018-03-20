package com.algaworks.brewer.controller.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.algaworks.brewer.model.Venda;

@Component
public class VendaValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Venda.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "cliente.codigo", "", "Selecione um cliente na pesquisa rápida");
		
		Venda venda = (Venda)target;
		validarSeInformouApenasHorarioDeEntrega(errors, venda);
		validarSeInformouItens(errors, venda);
		validarValorTotalNegativo(errors, venda);
		
	}

	private void validarValorTotalNegativo(Errors errors, Venda venda) {
		if (venda.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
			errors.reject("", "Valor total não pode ser negativo");
		}
	}
	private void validarSeInformouItens(Errors errors, Venda venda) {
		if(venda.getItens().isEmpty()){
			errors.reject("","Adicione pelo menos uma cerveja na venda.");
		}
	}

	private void validarSeInformouApenasHorarioDeEntrega(Errors errors, Venda venda) {
		if(venda.getHorarioEntrega() != null && venda.getDataEntrega() == null ){
			// Segundo parâmetro é um código, provavelmente aponta para um arquivo de erro _pt-br
			errors.rejectValue("dataEntrega", "","Informe uma data de entrega para um horário.");
		}
	}

}
