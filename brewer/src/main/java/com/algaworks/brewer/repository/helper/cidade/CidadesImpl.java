package com.algaworks.brewer.repository.helper.cidade;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class CidadesImpl implements CidadesQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);

		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		criteria.createAlias("estado", "e", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("c.estado",  "e", JoinType.LEFT_OUTER_JOIN);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	private Long total(CidadeFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(CidadeFilter filtro, Criteria criteria) {
		if (filtro != null) {

			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike ("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}

			if (  !StringUtils.isEmpty(filtro.getEstado()) ) {
				criteria.add(Restrictions.eq("estado", filtro.getEstado()));
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
 * CidadesImpl será automaticamente associado ao repositório Cidades.
 * 
 * @Configuration
 * 
 * @EnableJpaRepositories(basePackageClasses = Cidades.class,
 * enableDefaultTransactions=false ) // repositoryImplementationPostfix="Impl"
 * 
 * @EnableTransactionManagement public class JPAConfig {
 * 
 */