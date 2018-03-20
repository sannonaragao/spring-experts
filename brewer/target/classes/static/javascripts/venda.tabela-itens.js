/**
 * 
 */
Brewer.TabelaItens = (function(){
	function TabelaItens(autocomplete){
		this.autocomplete = autocomplete;
		this.tabelaCervejasContainer = $(".js-tabela-cervejas-container");
		
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
		
	}
	
	TabelaItens.prototype.iniciar = function(){
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
		reloadItensDoServidor.call(this);
		bindQuantidade.call(this);
		bindTabelaItem.call(this);
	}
	
	TabelaItens.prototype.valorTotal = function(){
		return this.tabelaCervejasContainer.data('valor')
	}
	
	function onItemSelecionado(evento, item) {
		var resposta = $.ajax({
			url: 'item',
			method: 'POST',
			data: {
				codigoCerveja: item.codigo
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	
	function onItemAtualizadoNoServidor(html) {
		this.tabelaCervejasContainer.html(html);
		
		bindQuantidade.call(this);
		var tabelaItem = bindTabelaItem.call(this);
		this.emitter.trigger('tabela-itens-atualizada', tabelaItem.data('valor-total'));

		
	}
	
	function bindQuantidade(){
		var quantidadeItemInput = $('.js-tabela-cerveja-quantidade-item');
		quantidadeItemInput.on('change', onQuantidadeItemAlterado.bind(this));
		quantidadeItemInput.maskMoney({ precision: 0, thousands: ''});
	}
	
	function onQuantidadeItemAlterado(evento) {
		var input = $(evento.target);
		var quantidade = input.val();
		
		if (quantidade <= 0){
			input.val(1);
			quantidade = 1;
		}
		
		var codigoCerveja = input.data('codigo-cerveja');
		
		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'PUT',
			data: {
				quantidade: quantidade
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function reloadItensDoServidor(){
		var resposta = $.ajax({
			url: 'reload',
			method: 'POST'
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onDoubleClick(evento){
		var currentTarget = $(evento.currentTarget);
		currentTarget.toggleClass('solicitando-exclusao');
		
	}
	
	function onExclusaoItemClick(evento){
		var codigoCerveja = $(evento.target).data('codigo-cerveja');
		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'DELETE'
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function bindTabelaItem(){
		var tabelaItem = $('.js-tabela-item');
		tabelaItem.on('dblclick', onDoubleClick);
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
		return tabelaItem;
	}
	return TabelaItens;
	
}())

/*
$(function() {
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();
	
});*/