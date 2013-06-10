package com.github.sourjson.exception;

/**
 * Exception thrown when something went wrong in the (de)serialization process
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@SuppressWarnings({ "serial", "javadoc" })
public class SourJsonException extends Exception {
	public SourJsonException(String message) {
		super(message);
	}

	public SourJsonException(Exception exc) {
		super(exc);
	}
}