package com.github.sourjson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.CheckForNull;
import javax.annotation.meta.TypeQualifierNickname;

/**
 * Tells SourJSON that the annotated field is allowed to be null / absent.
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname @CheckForNull
public @interface SJCheckForNull {
}
