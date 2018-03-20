package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.CadastroCidadeService;
import com.algaworks.brewer.service.exception.CidadeJaCadastradaException;

@Controller
@RequestMapping("/cidades")
public class CidadesController {

	@Autowired
	private Cidades cidades;
	
	@Autowired
	private Estados estados;
	
	@Autowired
	private CadastroCidadeService cadastroCidadeService;
	
	@CacheEvict(value="cache_cidades", key="#cidade.estado.codigo", condition="#cidade.temEstado()")
	@RequestMapping("/novo")
	public ModelAndView novo(Cidade cidade) {
		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados", estados.findAll());
		return mv;
		
	}

	@RequestMapping(value = "/novo", method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(cidade);
		}
		try{
			cadastroCidadeService.salvar(cidade);
		}catch (CidadeJaCadastradaException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(cidade);
			
		}
		
			
		attributes.addFlashAttribute("mensagem", "Cidade salva com sucesso!");
		return new ModelAndView("redirect:/cidades/novo");
	}
	
	@Cacheable(value="cache_cidades", key="#codigoEstado" ) //Define o nome do cache
	@RequestMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade>  pesquisaPorCodigoEstado(
			@RequestParam(name="estado", defaultValue="-1") Long codigoEstado){
		
		return cidades.findByEstadoCodigo(codigoEstado);
		
	}
	
//	@RequestMapping
//	public String pesquisar(){
//		return "cidade/CadastroCidade";
//	}
	
	@GetMapping
	public ModelAndView pesquisar(CidadeFilter CidadeFilter, BindingResult result, 
			@PageableDefault(size=4) Pageable pageable, HttpServletRequest httpServletRequest){ // Pageable ativa quando acrescenta @EnableSpringDataWebSupport no WebConfig.java.
		ModelAndView mv = new ModelAndView("cidade/PesquisaCidade");
		mv.addObject("estados", estados.findAll());
		PageWrapper<Cidade> paginaWrapper = new PageWrapper<>(cidades.filtrar(CidadeFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	
}
