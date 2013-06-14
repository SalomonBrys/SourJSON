/**
 * Copyright 2013 Salomon BRYS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sourjson.translat;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.SourJsonException;

/**
 * Translater for SourJson
 * 
 * @param <F> The class to transform

 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
public interface SJTranslater<F> {
	/**
	 * Converts FROM the original type TO an object more easily serializable by SourJSON
	 * 
	 * @param obj The object to convert
	 * @param type The real type of the object to convert (can be used for generics reflexivity)
	 * @param el All annotations annotating the element to translate
	 * @param enclosing The enclosing object, if this is a field being translated
	 * @param json The SourJson instance, should you need to serialize with it
	 * @param version The curent version
	 * @return The object to serialize
	 */
	public abstract @CheckForNull JSONObject serialize(F obj, Type type, AnnotatedElement el, @CheckForNull Object enclosing, SourJson json, double version) throws SourJsonException;
	
	/**
	 * Converts FROM an object deserialized by SourJSON TO the original type
	 * 
	 * @param obj The object to convert
	 * @param type The real type of the object to obtain (can be used for generics reflexivity)
	 * @param el All annotations annotating the element to obtain
	 * @param enclosing The enclosing object, if this is a field being translated
	 * @param json The SourJson instance, should you need to deserialize with it
	 * @param version The curent version
	 * @return The object deserialized
	 */
	public abstract @CheckForNull F deserialize(JSONObject obj, Type type, AnnotatedElement el, @CheckForNull Object enclosing, SourJson json, double version) throws SourJsonException;
}
