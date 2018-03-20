package com.algaworks.brewer.validation.validator;

import static com.algaworks.brewer.validation.util.ReflectionUtils.getIdField;
import static com.algaworks.brewer.validation.util.ReflectionUtils.getPropertyValue;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

import com.algaworks.brewer.validation.UniqueKey; 

public class UniqueKeyValidator implements ConstraintValidator<UniqueKey, Serializable>{

	@PersistenceContext
	private EntityManager entityManager;

	
	private UniqueKey constraintAnnotation;

	public UniqueKeyValidator() {
	}

	public UniqueKeyValidator(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public void initialize(final UniqueKey constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(final Serializable target, final ConstraintValidatorContext context) {

		if (entityManager == null) {
			// eclipselink may be configured with a BeanValidationListener that
			// validates an entity on prePersist
			// In this case we don't want to and we cannot check anything (the
			// entityManager is not set)
			//
			// Alternatively, you can disable bean validation during jpa
			// operations
			// by adding the property "javax.persistence.validation.mode" with
			// value "NONE" to persistence.xml
			return true;
		}

		final Class<?> entityClass = target.getClass();

		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		final CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();

		final Root<?> root = criteriaQuery.from(entityClass);

		try {
			final Object propertyValue = getPropertyValue(target, constraintAnnotation.property());
			final Predicate uniquePropertyPredicate = criteriaBuilder.equal(root.get(constraintAnnotation.property()),
					propertyValue);

			final Field idField = getIdField(entityClass);
			final String idProperty = idField.getName();
			final Object idValue = getPropertyValue(target, idProperty);

			if (idValue != null) {
				final Predicate idNotEqualsPredicate = criteriaBuilder.notEqual(root.get(idProperty), idValue);
				criteriaQuery.select(root).where(uniquePropertyPredicate, idNotEqualsPredicate);
			} else {
				criteriaQuery.select(root).where(uniquePropertyPredicate);
			}

		} catch (final Exception e) {
			throw new RuntimeException("An error occurred when trying to create the jpa predicate for the @UniqueKey '"
					+ constraintAnnotation.property() + "' on bean " + entityClass + ".", e);
		}

		final List<Object> resultSet = entityManager.createQuery(criteriaQuery).getResultList();

		if (!resultSet.isEmpty()) {
			ConstraintViolationBuilder cvb = context
					.buildConstraintViolationWithTemplate(constraintAnnotation.message());
			@SuppressWarnings("deprecation")
			NodeBuilderDefinedContext nbdc = cvb.addNode(constraintAnnotation.property());
			ConstraintValidatorContext cvc = nbdc.addConstraintViolation();
			cvc.disableDefaultConstraintViolation();
			return false;
		}

		return true;
	}


}