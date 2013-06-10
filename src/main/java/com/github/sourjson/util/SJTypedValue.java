//package com.github.sourjson.util;
//
//import java.lang.reflect.Type;
//
//import com.github.sourjson.translat.SJTranslater;
//
///**
// * Used by {@link SJTranslater}s to tell SourJSON to serialize the value according to a given type
// * and not according to the real type of the value 
// * 
// * @author Salomon BRYS <salomon.brys@gmail.com>
// */
//public class SJTypedValue {
//
//	/**
//	 * The type to use for serialization
//	 */
//	private Type type;
//	
//	/**
//	 * The value to serialize
//	 */
//	private Object value;
//
//	/**
//	 * @param strictClass The type to use for serialization
//	 * @param value The value to serialize
//	 */
//	public <T, V extends T> SJTypedValue(Class<T> strictClass, V value) {
//		this((Type)strictClass, (Object)value);
//	}
//	
//	/**
//	 * @param type The type to use for serialization
//	 * @param value The value to serialize
//	 */
//	public SJTypedValue(Type type, Object value) {
//		this.type = type;
//		this.value = value;
//	}
//
//	/**
//	 * @return The type to use for serialization
//	 */
//	public Type getType() {
//		return type;
//	}
//	
//	/**
//	 * @return The value to serialize
//	 */
//	public Object getValue() {
//		return value;
//	}
//}
