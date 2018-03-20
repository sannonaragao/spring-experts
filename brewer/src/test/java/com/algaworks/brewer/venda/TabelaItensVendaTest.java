package com.algaworks.brewer.venda;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.session.TabelaItensVenda;


public class TabelaItensVendaTest {

	private TabelaItensVenda tabelaItensVenda;

	@Before
	public void setUp() {
		this.tabelaItensVenda = new TabelaItensVenda();
	}

	@Test
	public void deveCalcularValorTotalSemItens() throws Exception {
		assertEquals(BigDecimal.ZERO, tabelaItensVenda.getValorTotal());
	}

	@Test
	public void deveCalcularValorTotalcomUmItem() throws Exception {
		Cerveja cerveja = new Cerveja();
		BigDecimal valor = new BigDecimal("8.90");
		cerveja.setValor(valor);
		
		tabelaItensVenda.adicionarItem(cerveja, 1);
		assertEquals(valor, tabelaItensVenda.getValorTotal());
		
	}
	
	@Test
	public void deveCalcularValorTotalcomVariosItens() throws Exception {
		Cerveja c1 = new Cerveja();
		BigDecimal v1 = new BigDecimal("8.90"); //17.8
		c1.setCodigo(1L);
		c1.setValor(v1);
		
		Cerveja c2 = new Cerveja();
		BigDecimal v2 = new BigDecimal("4.90"); //14.7
		c2.setCodigo(2L);
		c2.setValor(v2);
		
		Cerveja c3 = new Cerveja();
		BigDecimal v3 = new BigDecimal("5.11"); // 10.22
		c3.setCodigo(3L);
		c3.setValor(v3);
		
		
		tabelaItensVenda.adicionarItem(c1, 2);
		tabelaItensVenda.adicionarItem(c2, 3);
		tabelaItensVenda.adicionarItem(c3, 2);
		
		assertEquals(new BigDecimal("42.72"), tabelaItensVenda.getValorTotal());
		
	}
	
	@Test
	public void naoInsereCervejaDuplicada() throws Exception{
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		BigDecimal v1 = new BigDecimal("8.90"); //17.8
		c1.setValor(v1);
		
		tabelaItensVenda.adicionarItem(c1, 1);
		tabelaItensVenda.adicionarItem(c1, 1);
		assertEquals(1, tabelaItensVenda.total());
		
		
	}
	
	@Test
	public void deveAlterarQuantidadeDoItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		c1.setValor(new BigDecimal("4.50"));
		
		tabelaItensVenda.adicionarItem(c1, 1);
		tabelaItensVenda.alterarQuantidadeItens(c1, 3);
		
		assertEquals(1, tabelaItensVenda.total());
		assertEquals(new BigDecimal("13.50"), tabelaItensVenda.getValorTotal());
	}

	@Test
	public void deveExcluirItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1L);
		c1.setValor(new BigDecimal("8.90"));
		
		Cerveja c2 = new Cerveja();
		c2.setCodigo(2L);
		c2.setValor(new BigDecimal("4.99"));
		
		Cerveja c3 = new Cerveja();
		c3.setCodigo(3L);
		c3.setValor(new BigDecimal("2.00"));
		
		tabelaItensVenda.adicionarItem(c1, 1);
		tabelaItensVenda.adicionarItem(c2, 2);
		tabelaItensVenda.adicionarItem(c3, 1);
		
		tabelaItensVenda.excluirItem(c2);
		
		assertEquals(2, tabelaItensVenda.total());
		assertEquals(new BigDecimal("10.90"), tabelaItensVenda.getValorTotal());
	}
	

}
