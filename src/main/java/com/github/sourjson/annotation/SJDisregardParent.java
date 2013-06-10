package com.github.sourjson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells SourJSON to stop looking up the inheritance tree for field to (de)serialize
 * It will still consider the annotated class
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface SJDisregardParent {
}
