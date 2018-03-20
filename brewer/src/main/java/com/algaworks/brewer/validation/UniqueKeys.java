package com.algaworks.brewer.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.algaworks.brewer.validation.validator.UniqueKeysValidator;

/*
 * http://lucasterdev.altervista.org/wordpress/2012/07/28/unique-constraint-validation-in-jpa-part-2/
 */

@Constraint(validatedBy = { UniqueKeysValidator.class })
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueKeys {

	String[] columnNames();

	String message() default "Values are not unique";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		UniqueKeys[] value();
	}
}