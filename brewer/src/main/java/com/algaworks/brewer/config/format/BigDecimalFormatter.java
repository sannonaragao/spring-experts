package com.algaworks.brewer.config.format;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

public class BigDecimalFormatter implements Formatter<BigDecimal> {

	private DecimalFormat decimalFormat;
	
	public BigDecimalFormatter(String pattern) {
		NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
		decimalFormat = (DecimalFormat) format;
		decimalFormat.setParseBigDecimal(true);
		decimalFormat.applyPattern(pattern);
	}
	
	/* Função chamada quando quer imprimir na tela o valor, recebe BigDecimal e devolve String */
	@Override
	public String print(BigDecimal object, Locale locale) {
		return decimalFormat.format(object);
	}

	/* Função chamada quando quer receber da tela o valor e enviar para o servidor,
	   recebe String Big e devolve Decimal */
	@Override
	public BigDecimal parse(String text, Locale locale) throws ParseException {
		return (BigDecimal) decimalFormat.parse(text);
	}

}
