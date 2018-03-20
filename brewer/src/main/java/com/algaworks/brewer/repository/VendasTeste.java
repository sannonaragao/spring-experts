package com.algaworks.brewer.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.algaworks.brewer.model.Venda;

public interface VendasTeste extends PagingAndSortingRepository<Venda, Long> {

	//List<Post> findByTitleContaining(@Param("title") String title);
}
