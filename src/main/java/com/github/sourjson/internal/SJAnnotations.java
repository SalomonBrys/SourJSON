package com.github.sourjson.internal;

import java.lang.reflect.AnnotatedElement;

import javax.annotation.CheckForNull;

import com.github.sourjson.annotation.StrictType;

public class SJAnnotations {

	private boolean strictType;

	public SJAnnotations(@CheckForNull AnnotatedElement el) {
		if (el == null)
			return ;

		strictType = el.isAnnotationPresent(StrictType.class);
	}

	public boolean isStrictType() {
		return strictType;
	}
}
