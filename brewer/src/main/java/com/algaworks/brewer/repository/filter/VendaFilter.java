package com.algaworks.brewer.repository.filter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.algaworks.brewer.model.StatusVenda;

public class VendaFilter {
	
	private Long codigo;

	private LocalDateTime dataCriacaoInicial;
	private LocalDateTime dataCriacaoFinal;

	private BigDecimal valorMinimo;
	private BigDecimal valorMaximo;
	private StatusVenda status = StatusVenda.ORCAMENTO;
	
	private String nome;
	
	private String cpfOuCnpj;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public LocalDateTime getDataCriacaoInicial() {
		return dataCriacaoInicial;
	}

	public void setDataCriacaoInicial(LocalDateTime dataCriacaoInicial) {
		this.dataCriacaoInicial = dataCriacaoInicial;
	}

	public LocalDateTime getDataCriacaoFinal() {
		return dataCriacaoFinal;
	}

	public void setDataCriacaoFinal(LocalDateTime dataCriacaoFinal) {
		this.dataCriacaoFinal = dataCriacaoFinal;
	}


	public BigDecimal getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(BigDecimal valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	public BigDecimal getValorMaximo() {
		return valorMaximo;
	}

	public void setValorMaximo(BigDecimal valorMaximo) {
		this.valorMaximo = valorMaximo;
	}

	public StatusVenda getStatus() {
		return status;
	}

	public void setStatus(StatusVenda status) {
		this.status = status;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}

	public void setCpfOuCnpj(String cpfOuCnpj) {
		this.cpfOuCnpj = cpfOuCnpj;
	}

}
