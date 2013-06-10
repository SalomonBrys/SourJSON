package com.github.sourjson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.CheckForNull;
import javax.annotation.meta.TypeQualifierNickname;

/**
 * Tells SourJSON to ignore a field of the (de)serializer version is superior to the given value
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.FIELD)
@TypeQualifierNickname @CheckForNull
public @interface SJUntil {
	/**
	 * @return Maximum version for the annotated field
	 */
	double value();
}
