package com.github.sourjson.translat;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;

/**
 * Translater for SourJson
 * 
 * @param <F> The class to transform

 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
public abstract class SJTranslater<F> {
	/**
	 * Converts FROM the original type TO an object more easily serializable by SourJSON
	 * 
	 * @param obj The object to convert
	 * @param type The real type of the object to convert (can be used for generics reflexivity)
	 * @param el All annotations annotating the element to translate
	 * @param enclosing The enclosing object, if this is a field being translated
	 * @param json The SourJson instance, should you need to serialize with it
	 * @return The object to serialize
	 */
	public abstract @CheckForNull JSONObject serialize(F obj, Type type, AnnotatedElement el, @CheckForNull Object enclosing, SourJson json);
	
	/**
	 * Converts FROM an object deserialized by SourJSON TO the original type
	 * 
	 * @param obj The object to convert
	 * @param type The real type of the object to obtain (can be used for generics reflexivity)
	 * @param el All annotations annotating the element to obtain
	 * @param enclosing The enclosing object, if this is a field being translated
	 * @param json The SourJson instance, should you need to deserialize with it
	 * @return The object deserialized
	 */
	public abstract @CheckForNull F deserialize(JSONObject obj, Type type, AnnotatedElement el, @CheckForNull Object enclosing, SourJson json);
}
