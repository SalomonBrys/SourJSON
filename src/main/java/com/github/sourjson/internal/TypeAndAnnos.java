package com.github.sourjson.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import javax.annotation.CheckForNull;

public class TypeAndAnnos {

	private static class EmptyAnnos implements AnnotatedElement {
		@Override public boolean isAnnotationPresent( Class<? extends Annotation> annotationClass) { return false; }
		@Override public <T extends Annotation> T getAnnotation(Class<T> annotationClass) { return null; }
		@Override public Annotation[] getAnnotations() { return new Annotation[0]; }
		@Override public Annotation[] getDeclaredAnnotations() { return new Annotation[0]; }

	}
	private static final EmptyAnnos emptyAnnos = new EmptyAnnos();

	public Type type;
	public AnnotatedElement annos;

	public TypeAndAnnos(Type type, @CheckForNull AnnotatedElement annos) {
		super();
		this.type = type;
		this.annos = annos != null ? annos : emptyAnnos;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TypeAndAnnos))
			return false;
		TypeAndAnnos info = (TypeAndAnnos)obj;
		return type.equals(info.type) && annos.equals(info.annos);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + type.hashCode();
		hash = hash * 31 + annos.hashCode();
		return hash;
	}
}