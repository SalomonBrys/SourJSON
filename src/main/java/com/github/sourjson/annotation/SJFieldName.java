package com.github.sourjson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Give SourJSON the name to use for the annotated field instead of its java name
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface SJFieldName {
	/**
	 * @return name to use for this field
	 */
	String value();
}
