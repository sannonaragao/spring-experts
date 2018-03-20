package com.algaworks.brewer.validation.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * A utility class for reflection tasks.
 *
 * @author Martin Grotzke
 */
public class ReflectionUtils {

	private ReflectionUtils() {
		// utility class
	}

	/**
	 * Determines which field of the given class is annotated with {@link Id},
	 * including the inheritence tree up to {@link Object}.
	 *
	 * @return the Id field, never <code>null</code>.
	 * @throws IllegalArgumentException
	 *             thrown if there's no field annotated with {@link Id}.
	 */
	public static Field getIdField(final Class<?> entityClass) throws IllegalArgumentException {
		final Field[] fields = entityClass.getDeclaredFields();
		for (final Field field : fields) {
			final Id annotation = field.getAnnotation(Id.class);
			if (annotation != null) {
				return field;
			}
		}
		if (entityClass.getSuperclass() != Object.class) {
			return getIdField(entityClass.getSuperclass());
		}
		throw new IllegalArgumentException("No id field found on class " + entityClass);
	}

	/**
	 * Read the value of the specified propertyName from the given object. This
	 * implementation allows properties/fields with only getters (immutable
	 * beans) and does not rely on java beans standard (PropertyDescriptor)
	 * which would require also a setter method (the alternative would be s.th.
	 * like
	 * <code>new PropertyDescriptor(propertyName, entityClass).getReadMethod().invoke(target);</code>
	 * ).
	 */
	@NotNull
	public static Object getPropertyValue(@NotNull final Object object, @NotNull final String propertyName)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		final Method method = getGetter(object.getClass(), propertyName);
		return method.invoke(object, (Object[]) null);
	}

	@NotNull
	private static String toMethodName(@NotNull final String methodPrefix, @NotNull final String propertyName) {
		return methodPrefix + propertyName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propertyName.substring(1);
	}

	@NotNull
	private static Method getGetter(@NotNull final Class<?> clazz, @NotNull final String property) {
		final String get = toMethodName("get", property);
		final String is = toMethodName("is", property);
		Method getter;
		try {
			getter = clazz.getMethod(get);
		} catch (final NoSuchMethodException noSuchMethodException) {
			try {
				getter = clazz.getMethod(is);
			} catch (final NoSuchMethodException noSuchMethodException2) {
				throw new RuntimeException("Getter for '" + clazz.getSimpleName() + '#' + property + "' not found.",
						noSuchMethodException2);
			} catch (final SecurityException securityException) {
				throw new RuntimeException(
						"Method '" + clazz.getSimpleName() + '#' + is + "()' is protected by the security manager.");
			}
		} catch (final SecurityException securityException) {
			try {
				getter = clazz.getMethod(is);
			} catch (final NoSuchMethodException noSuchMethodException) {
				throw new RuntimeException(
						"Method '" + clazz.getSimpleName() + '#' + get + "()' is protected by the security manager.");
			} catch (final SecurityException securityException2) {
				throw new RuntimeException("Methods '" + clazz.getSimpleName() + '#' + get + "()' and "
						+ clazz.getSimpleName() + '#' + is + "()' are protected by the security manager.");
			}
		}
		return getter;
	}

}