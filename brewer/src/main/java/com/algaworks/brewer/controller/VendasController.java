package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.session.TabelaItensVenda;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	Cervejas cervejas;
	
	@Autowired
	Vendas vendas;
	
	@Autowired
	private TabelaItensVenda tabelaItensVenda;
	
	@Autowired
	private CadastroVendaService cadastroVendaService;
	
	@Autowired
	private VendaValidator vendaValidator;

	
	@Autowired
	private Mailer mailer;
	
	@InitBinder("venda")   // incluido o argumento VENDA para validar somente a variável VENDA, não a vendaFiltro.
	public void inicializarValidador(WebDataBinder binder){
		binder.setValidator(vendaValidator);
	}
	

	@GetMapping("/novo")
	public ModelAndView novo(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		
		if( StringUtils.isEmpty(venda.getUuid())){
			venda.setUuid(UUID.randomUUID().toString());
			if ( venda.isNova() ){
				this.tabelaItensVenda.getItens().clear();
			}
				
		}
		mv.addObject("itens", venda.getItens());
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItensVenda.getValorTotal());
		return mv;
	}
	
	@PostMapping(value="/novo", params="salvar")
	public ModelAndView salvar(Venda venda, BindingResult result , RedirectAttributes attributes, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		
		validarVenda(venda, result);
		
		if(result.hasErrors()){
			return novo(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		tabelaItensVenda = new TabelaItensVenda();
		
		attributes.addFlashAttribute("mensagem","Venda salva com sucesso!");
		return new ModelAndView("redirect:/vendas/novo");
	}
	
	@PostMapping(value="/novo", params="emitir")
	public ModelAndView emitir(Venda venda, BindingResult result , RedirectAttributes attributes, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		
		validarVenda(venda, result);
		
		if(result.hasErrors()){
			return novo(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem","Venda emitida com sucesso!");
		tabelaItensVenda = new TabelaItensVenda();
		return new ModelAndView("redirect:/vendas/novo");
	}


	@PostMapping(value="/novo", params="enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result , RedirectAttributes attributes, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		
		validarVenda(venda, result);
		
		if(result.hasErrors()){
			return novo(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		mailer.enviar(venda);
		
		cadastroVendaService.salvar(venda);
		
		tabelaItensVenda = new TabelaItensVenda();
		attributes.addFlashAttribute("mensagem","Venda salva e e-enviado!");
		return new ModelAndView("redirect:/vendas/novo");
	}

	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItensVenda.getItens());
		venda.calcularValorTotal();
		
		// remove o @Valid do parametro "Venda venda" da função e chama a validação abaixo:
		vendaValidator.validate(venda, result);
	}

	
	/*
	@PostMapping("/item")
	public @ResponseBody String adicionarItem(Long codigoCerveja){
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensVenda.adicionarItem(cerveja, 1);
		System.out.println("total de itens:"+tabelaItensVenda.total());
		return "Item adicionado!";
	}*/

	//23.06 - Renderizando HTML retornado do Ajax
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja){
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensVenda.adicionarItem(cerveja, 1);
		return mvTabelaItensVenda();
	}
	
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuandidadeItem(@PathVariable("codigoCerveja") Cerveja cerveja, Integer quantidade){
		tabelaItensVenda.alterarQuantidadeItens(cerveja, quantidade);
		return mvTabelaItensVenda();
	}
	
	@PostMapping("/reload")
	public ModelAndView adicionarItem(){
		return mvTabelaItensVenda();
	}
	
	@DeleteMapping("/item/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja){
		tabelaItensVenda.excluirItem(cerveja);
		
		return mvTabelaItensVenda();
	}

	private ModelAndView mvTabelaItensVenda() {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens",tabelaItensVenda.getItens());
		mv.addObject("valorTotal", tabelaItensVenda.getValorTotal());
		return mv;
	}
	

	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter, BindingResult result, 
			@PageableDefault(size=3) Pageable pageable, HttpServletRequest httpServletRequest){ // Pageable ativa quando acrescenta @EnableSpringDataWebSupport no WebConfig.java.
		ModelAndView mv = new ModelAndView("venda/PesquisaVendas");
		mv.addObject("todosStatus", StatusVenda.values());
		
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Venda venda) {
		try {
			cadastroVendaService.excluir(venda);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Venda venda = vendas.buscarComItens(codigo);
		
		this.tabelaItensVenda.adicionarTodosItems(venda.getItens());
//		for(ItemVenda item : venda.getItens()){
//			this.tabelaItensVenda.adicionarItem(item.getCerveja(), item.getQuantidade());
//			
//		}
		
		ModelAndView mv = novo(venda);
		mv.addObject(venda);
		return mv;
	}
	
	@GetMapping("/porOrigem")
	public @ResponseBody List<VendaOrigem> vendasPorNacionalidade() {
		return this.vendas.totalPorOrigem();
	}
	
	@PostMapping(value="/novo", params="cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result , RedirectAttributes attributes, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		
		try{
			cadastroVendaService.cancelar(venda);
		}catch( AccessDeniedException e){
			return new ModelAndView("/403");
		}
		
		
		
		attributes.addFlashAttribute("mensagem","Venda cancelada com sucesso!");
		return new ModelAndView("redirect:/vendas/"+venda.getCodigo());
	}

	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalVendaPorMes(){
		return vendas.totalPorMes();
	}
	
}
