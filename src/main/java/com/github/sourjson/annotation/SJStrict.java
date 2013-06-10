package com.github.sourjson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tell the SourJSON to consider the declared class of the annotated field
 * and not it's real type 
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface SJStrict {
}
