package com.algaworks.brewer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Grupos;
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.service.CadastroUsuarioService;
import com.algaworks.brewer.service.StatusUsuario;
import com.algaworks.brewer.service.exception.EmailUsuarioJaCadastradoException;
import com.algaworks.brewer.service.exception.SenhaObrigatoriaUsuarioException;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {
	
	@Autowired
	private CadastroUsuarioService cadastroUsuarioService;

	@Autowired
	private Grupos grupos;
	
	@Autowired
	private Usuarios usuarios;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Usuario usuario) {
		ModelAndView mv = new ModelAndView("usuario/CadastroUsuario");
		mv.addObject("grupos", grupos.findAll());
		return mv;
	}
	
	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(usuario);
		}
		
		try {
			cadastroUsuarioService.salvar(usuario);
		} catch (EmailUsuarioJaCadastradoException e) {
			result.rejectValue("email", e.getMessage(), e.getMessage());
			return novo(usuario);
		} catch (SenhaObrigatoriaUsuarioException e) {
			result.rejectValue("senha", e.getMessage(), e.getMessage());
			return novo(usuario);
		}
		
		attributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso");
		return new ModelAndView("redirect:/usuarios/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(UsuarioFilter usuarioFilter,
			@PageableDefault(size=3) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/usuario/PesquisaUsuarios");
//		mv.addObject("usuarios", usuarios.filtrar(usuarioFilter));
		mv.addObject("grupos", grupos.findAll());
		
		PageWrapper<Usuario>  paginaWrapper = new PageWrapper<>(usuarios.filtrar(usuarioFilter, pageable), httpServletRequest);
		
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Usuario usuario = usuarios.buscarComGrupos(codigo);
		ModelAndView mv = novo(usuario);
		mv.addObject(usuario);
		return mv;
	}
	
	/*
	 * O Spring MVC bloqueia, recebimento de parâmetros via PUT, para habilitar é necessário
	 * override do getServletFilters() no AppInitializer.java
	 * 
	 * HttpPutFormContentFilter	httpPutFormContentFilter = new HttpPutFormContentFilter();
		return new Filter[]{ httpPutFormContentFilter };
	 * 
	 */
	@PutMapping("/status")  //Atualizando
	@ResponseStatus(HttpStatus.OK)
	public void atualizarStatus(@RequestParam("codigos[]") Long[] codigos, @RequestParam("status") StatusUsuario statusUsuario){

		cadastroUsuarioService.alterarStatus(codigos, statusUsuario);
		
	}
	
}
