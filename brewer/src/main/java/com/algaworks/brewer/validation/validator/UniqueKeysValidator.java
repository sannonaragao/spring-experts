package com.algaworks.brewer.validation.validator;

import static com.algaworks.brewer.validation.util.ReflectionUtils.getIdField;
import static com.algaworks.brewer.validation.util.ReflectionUtils.getPropertyValue;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

import com.algaworks.brewer.validation.UniqueKeys;

/*
 * http://lucasterdev.altervista.org/wordpress/2012/07/28/unique-constraint-validation-in-jpa-part-2/
 */

public class UniqueKeysValidator implements ConstraintValidator<UniqueKeys, Serializable> {

	@PersistenceContext
	private EntityManager entityManager;
	
	private UniqueKeys constraintAnnotation;

	private String[] columnNames;

	public UniqueKeysValidator() {
	}

	@Override
	public void initialize(final UniqueKeys constraintAnnotation) {
		this.columnNames = constraintAnnotation.columnNames();
		this.constraintAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(Serializable target, ConstraintValidatorContext context) {

		final Class<?> entityClass = target.getClass();

		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		final CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();

		final Root<?> root = criteriaQuery.from(entityClass);

		List<Predicate> predicates = new ArrayList<Predicate>(columnNames.length);

		try {

			for (int i = 0; i < columnNames.length; i++) {

				String propertyName = columnNames[i];
				PropertyDescriptor desc = new PropertyDescriptor(propertyName, entityClass);
				Method readMethod = desc.getReadMethod();
				Object propertyValue = readMethod.invoke(target);

				Predicate predicate = criteriaBuilder.equal(root.get(propertyName), propertyValue);

				predicates.add(predicate);
			}

			Field idField = getIdField(entityClass);
			String idProperty = idField.getName();
			Object idValue = getPropertyValue(target, idProperty);

			if (idValue != null) {
				Predicate idNotEqualsPredicate = criteriaBuilder.notEqual(root.get(idProperty), idValue);
				predicates.add(idNotEqualsPredicate);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Object> typedQuery = entityManager.createQuery(criteriaQuery);

		List<Object> resultSet = typedQuery.getResultList();

		if (!resultSet.isEmpty()) {

			// This string will contain all column names separated by a comma.
			// Example: "title,author,editor"
			String names = "";
			for (String columnName : columnNames) {
				names += columnName;
				names += ",";
			}
			names = names.substring(0, names.length() - 1);

			ConstraintViolationBuilder cvb = context
					.buildConstraintViolationWithTemplate(constraintAnnotation.message());
			@SuppressWarnings("deprecation")
			NodeBuilderDefinedContext nbdc = cvb.addNode(names);
			ConstraintValidatorContext cvc = nbdc.addConstraintViolation();
			cvc.disableDefaultConstraintViolation();
			return false;
		}

		return true;
	}
}
