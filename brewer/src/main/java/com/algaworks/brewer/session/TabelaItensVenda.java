package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

@SessionScope
@Component
public class TabelaItensVenda {

	private List<ItemVenda> itens = new ArrayList<>();
			
	public BigDecimal getValorTotal(){
		return itens.stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
				
	}

	public void adicionarItem(Cerveja cerveja, Integer quantidade){
		
		Optional<ItemVenda> itemVendaOptional =  itens.stream()
			.filter(i -> i.getCerveja().equals(cerveja))
			.findAny();
		
		ItemVenda itemVenda = null;
		if(itemVendaOptional.isPresent()){
			itemVenda	=	itemVendaOptional.get();
			itemVenda.setQuantidade( itemVenda.getQuantidade() + quantidade );
		}else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());
			
			this.itens.add(0, itemVenda);
		}
		
	}

	public void alterarQuantidadeItens(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
		itemVenda.setQuantidade(quantidade);
	}
	
	public void excluirItem(Cerveja cerveja){
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getCerveja().equals(cerveja))
				.findAny().getAsInt();
		
		itens.remove(indice);
	}
	
	
	public int total(){
		return itens.size();
	}

	public List<ItemVenda> getItens() {
		return itens;
	}
	
	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		return itens.stream()
				.filter(i -> i.getCerveja().equals(cerveja))
				.findAny();
	}

	public void adicionarTodosItems(List<ItemVenda> itens) {
		this.itens.clear();
		
		for(ItemVenda item : itens){
			adicionarItem(item.getCerveja(), item.getQuantidade());
		
		}
		
		
	}
}
