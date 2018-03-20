package com.algaworks.brewer.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.algaworks.brewer.validation.AtributoConfirmacao;
import com.algaworks.brewer.validation.UniqueKey;

@UniqueKey(property = "email", message = "Email já cadastrado")
@AtributoConfirmacao(atributo = "senha", atributoConfirmacao = "confirmacaoSenha", message = "A senha e sua confirmação devem ser iguais.")
@Entity
@Table(name = "usuario")
@DynamicUpdate
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean ativo;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@Transient
	private String confirmacaoSenha;

	// @NotNull(message = "Data de nascimento é obrigatório")
	@Column(name = "data_nascimento")
	private LocalDate dataNascimento;

	@NotBlank(message = "E-mail é obrigatório")
	@Email(message = "E-mail inválido")
	private String email;

//	@Size(min = 1, message = "Selecione pelo menos um grupo")
	@ManyToMany
	@JoinTable(name = "usuario_grupo", joinColumns = @JoinColumn(name = "codigo_usuario"), inverseJoinColumns = @JoinColumn(name = "codigo_grupo"))
	private List<Grupo> grupos;

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	private String senha;

	@PreUpdate
	private void preUpdate(){
		this.confirmacaoSenha = senha;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public Long getCodigo() {
		return codigo;
	}

	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public String getEmail() {
		return email;
	}

	public List<Grupo> getGrupos() {
		return grupos;
	}

	public String getNome() {
		return nome;
	}

	public String getSenha() {
		return senha;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public boolean isNovo(){
		return this.codigo == null;
	}
}
