package com.github.sourjson.exception;

import com.github.sourjson.SourJson;

/**
 * Thrown when {@link SourJson#checkForKnownClasses(java.util.Collection)} is previously called and the
 * JSON (de)serializer is handling a class that is not declared as known
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@SuppressWarnings("javadoc")
public class UnknownClassException extends RuntimeException {
	private static final long serialVersionUID = 3590054085580608330L;

	public UnknownClassException(Class<?> cls) {
		super(cls.getName() + " is not known");
	}
}
