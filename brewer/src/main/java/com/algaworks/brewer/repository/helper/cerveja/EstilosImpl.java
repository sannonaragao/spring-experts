package com.algaworks.brewer.repository.helper.cerveja;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class EstilosImpl implements EstilosQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Estilo.class);

		
		paginacaoUtil.preparar(criteria, pageable);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	private Long total(EstiloFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Estilo.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(EstiloFilter filtro, Criteria criteria) {
		if (filtro != null && filtro.getNome() != null) {
			if (!StringUtils.isEmpty(filtro.getNome()) ) {
				criteria.add(Restrictions.like("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}

		}
	}

}

/*
 * 
 * O "scan" da implementação de interface é definido em JPAConfig. Final Impl é
 * o final default. Para mudar para final Xpto, acrescentar
 * repositoryImplementationPostfix="Xpto"
 * 
 * CervejasImpl será automaticamente associado ao repositório Cervejas.
 * 
 * @Configuration
 * 
 * @EnableJpaRepositories(basePackageClasses = Cervejas.class,
 * enableDefaultTransactions=false ) // repositoryImplementationPostfix="Impl"
 * 
 * @EnableTransactionManagement public class JPAConfig {
 * 
 */