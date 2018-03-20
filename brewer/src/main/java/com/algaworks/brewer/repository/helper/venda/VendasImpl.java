package com.algaworks.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;
import java.util.Optional;

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

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class VendasImpl implements VendasQueries{

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable ) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		
		//Faz distinct na entidade principal evitando resultado cartesiano
//		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		paginacaoUtil.preparar(criteria, pageable);
		
		adicionarFiltro(filtro, criteria);
		criteria.createAlias("cliente", "c", JoinType.LEFT_OUTER_JOIN);
		
		List<Venda> filtrados = criteria.list();
		
//		filtrados.forEach(u -> Hibernate.initialize(u.getCliente()));
		
		return new PageImpl<>( filtrados, pageable, total(filtro)) ;
	}
	

	private void adicionarFiltro(VendaFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
//			if (!StringUtils.isEmpty(filtro.getEmail())) {
//				criteria.add(Restrictions.ilike("email", filtro.getEmail(), MatchMode.START));
//			}
//			
//			/*
//			 * comentado o alias para inicializar individualmente os grupos, evitando produto cartesiano
//			//Cria um alias com LEFT_OUTER_JOIN que resolve o problema de lazy inicialization 
//			criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);   
//			*/
//			if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
//				List<Criterion> subqueries = new ArrayList<>();
//				for (Long codigoGrupo : filtro.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
//					
//					DetachedCriteria dc = DetachedCriteria.forClass(UsuarioGrupo.class);
//					dc.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
//					dc.setProjection(Projections.property("id.usuario"));
//					
//					subqueries.add(Subqueries.propertyIn("codigo", dc));
//				}
//				
//				Criterion[] criterions = new Criterion[subqueries.size()];
//				criteria.add(Restrictions.and(subqueries.toArray(criterions)));
//			}
		}
	}

	private Long total(VendaFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}


	@Transactional(readOnly = true)
	@Override
	public Venda buscarComItens(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("codigo", codigo));
		
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (Venda) criteria.uniqueResult();
		
	}

	@Override
	public BigDecimal valorTotalNoAno() {
		Optional<BigDecimal>  optional = Optional.ofNullable( manager.createQuery
				("select sum(valorTotal) from Venda where year(dataCriacao) = :ano", BigDecimal.class)
		.setParameter("ano", Year.now().getValue()).getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	@Override
	public BigDecimal valorTotalNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal) from Venda where month(dataCriacao) = :mes and status = :status", BigDecimal.class)
					.setParameter("mes", MonthDay.now().getMonthValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMes> totalPorMes(){
		List<VendaMes> vendasMes = manager.createNamedQuery("Vendas.totalPorMes").getResultList();
		
		LocalDate hoje = LocalDate.now();
		
		for(int i = 1; i <= 6; i++){
			String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());
			boolean possuiMes = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes){
				vendasMes.add(i -1, new VendaMes(mesIdeal,0));
			}
			hoje = hoje.minusMonths(1);
		
		}
		
		
		return vendasMes;
	}
	
	@Override
	public BigDecimal valorTicketMedioNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	

	@Override
	public List<VendaOrigem> totalPorOrigem() {
		List<VendaOrigem> vendasNacionalidade = manager.createNamedQuery("Vendas.porOrigem", VendaOrigem.class).getResultList();
		
		LocalDate now = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			String mesIdeal = String.format("%d/%02d", now.getYear(), now.getMonth().getValue());
			
			boolean possuiMes = vendasNacionalidade.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasNacionalidade.add(i - 1, new VendaOrigem(mesIdeal, 0, 0));
			}
			
			now = now.minusMonths(1);
		}
		
		return vendasNacionalidade;
	}	
	
	
}
