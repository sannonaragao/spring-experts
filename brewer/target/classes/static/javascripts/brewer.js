//Usa o brewer existente ou (||) cria um novo
var Brewer = Brewer || {};

/* MaskMoney */
Brewer.MaskMoney = (function() {

	function MaskMoney() {
		this.decimal = $('.js-decimal');
		this.plain = $('.js-plain');
	}

	MaskMoney.prototype.enable = function() {
		this.decimal.maskMoney({
			decimal : ',',
			thousands : '.'
		});
		this.plain.maskMoney({
			precision : 0,
			thousands : '.'
		});
	}

	return MaskMoney;

}());

/* MaskProneNumber */
Brewer.MaskPhoneNumber = (function(){
	function MaskPhoneNumber(){
		this.inputPhoneNumber = $('.js-phone-number');
	}
	
	MaskPhoneNumber.prototype.enable = function() {
		var maskBehavior = function (val) {
		  return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
		};
		options = {
		  onKeyPress: function(val, e, field, options) {
		      field.mask(maskBehavior.apply({}, arguments), options);
		    }
		};
		
		this.inputPhoneNumber.mask(maskBehavior, options);
	}
	
	return MaskPhoneNumber;
}());


/* Mask Generic */
Brewer.BrewerMask = (function(){
	function BrewerMask(){
		this.inputField = $('.js-mask');
	}
	
	BrewerMask.prototype.enable = function() {
		var mascara = this.inputField.data('mascara');
		
		if ( mascara != undefined ) {
			this.inputField.mask(mascara);
		}
	
	}
		
	return BrewerMask;
}());


Brewer.MaskDate = (function(){

	function MaskDate(){
		this.inputDate = $('.js-date');
		
	}
	
	MaskDate.prototype.enable = function(){
		this.inputDate.mask('00/00/0000');
		this.inputDate.datepicker({
			orientation: 'bottom',
			language: 'pt-BR',
			autoclose:true
		});
	}
	
	return MaskDate;
	
}());

Brewer.Security = (function() {
	
	function Security() {
		this.token = $('input[name=_csrf]').val();
		this.header = $('input[name=_csrf_header]').val();
	}
	
	//Pega o código csrf da LayoutPadrão.html e envia em todas as chamadas
	Security.prototype.enable = function() {
		$(document).ajaxSend(function(event, jqxhr, settings) {
			jqxhr.setRequestHeader(this.header, this.token);
		}.bind(this));
	}
	
	return Security;
	
}());

numeral.locale('pt-br');

Brewer.formatarMoeda = function(valor){
	return numeral(valor).format('0,0.00');
}
Brewer.recuperarValor = function(valorFormatado){
	return numeral(valorFormatado).value();
}


/*  Instantiate */
$(function() {
	var maskMoney = new Brewer.MaskMoney();
	maskMoney.enable();
	
	var maskPhoneNumber = new Brewer.MaskPhoneNumber();
	maskPhoneNumber.enable();
	
	var brewerMask = new Brewer.BrewerMask();
	brewerMask.enable();
	
	var maskDate = new Brewer.MaskDate();
	maskDate.enable();
	
	var security = new Brewer.Security();
	security.enable();
	
	
});
