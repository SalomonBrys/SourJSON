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

package com.github.sourjson;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import com.github.sourjson.annotation.SJCheckForNull;
import com.github.sourjson.annotation.SJDisregardParent;
import com.github.sourjson.annotation.SJDisregardedParent;
import com.github.sourjson.annotation.SJExclude;
import com.github.sourjson.annotation.SJFieldName;
import com.github.sourjson.annotation.SJSince;
import com.github.sourjson.annotation.SJStrict;
import com.github.sourjson.annotation.SJUntil;
import com.github.sourjson.exception.SourJsonException;
import com.github.sourjson.exception.UnknownClassException;
import com.github.sourjson.translat.SJTranslater;
import com.github.sourjson.translat.def.ClassTranslater;
import com.github.sourjson.translat.def.DateTranslater;
import com.googlecode.gentyref.GenericTypeReflector;

@SuppressWarnings("javadoc")
public class SourJson {

	public static enum AllowEmpty {
		YES,
		ROOT,
		NO
	}
	
	private @CheckForNull Set<Class<?>> knownClasses = new HashSet<>();
	private boolean checkKnownClasses = false;
	
	private boolean checkForNulls = false;
	
	@SuppressWarnings("serial")
	private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>() {
		{
	      this.put(boolean.class, Boolean.class);
	      this.put(byte.class, Byte.class);
	      this.put(char.class, Character.class);
	      this.put(double.class, Double.class);
	      this.put(float.class, Float.class);
	      this.put(int.class, Integer.class);
	      this.put(long.class, Long.class);
	      this.put(short.class, Short.class);
	      this.put(void.class, Void.class);
		}
	};

	@SuppressWarnings("serial")
	private static final Map<Class<?>, Class<?>> WRAPPERS_TO_PRIMITIVES = new HashMap<Class<?>, Class<?>>() {
		{
			for (Entry<Class<?>, Class<?>> entry : PRIMITIVES_TO_WRAPPERS.entrySet())
				this.put(entry.getValue(), entry.getKey());
		}
	};


	private Map<Class<?>, SJTranslater<?>> exactTranslaters = new HashMap<>();
	private Map<Class<?>, SJTranslater<?>> hierarchyTranslaters = new LinkedHashMap<>();

	public SourJson() {
		addHierarchyTranslater(Date.class, new DateTranslater());
		addHierarchyTranslater(Class.class, new ClassTranslater());
	}
	
	public <T> void addTranslater(Class<T> forClass, SJTranslater<T> translater) {
		exactTranslaters.put(forClass, translater);
		knownClasses.add(forClass);
	}

	public <T> void addHierarchyTranslater(Class<T> forClass, SJTranslater<T> translater) {
		hierarchyTranslaters.put(forClass, translater);
	}
	
	@SuppressWarnings("unchecked")
	private @CheckForNull <T> SJTranslater<T> getTranslater(Class<T> forClass) {
		if (exactTranslaters.containsKey(forClass))
			return (SJTranslater<T>)exactTranslaters.get(forClass);
		for (Map.Entry<Class<?>, SJTranslater<?>> entry : hierarchyTranslaters.entrySet())
			if (entry.getKey().isAssignableFrom(forClass))
				return (SJTranslater<T>)entry.getValue();
		return null;
	}

	public @CheckForNull void checkKnownClasses() {
		this.checkKnownClasses = true;
	}

	public @CheckForNull void checkKnownClasses(@CheckForNull Collection<Class<?>> classes) {
		this.knownClasses.addAll(classes);
		checkKnownClasses();
	}

	public @CheckForNull void checkKnownClasses(Class<?>... classes) {
		checkKnownClasses(Arrays.asList(classes));
	}
	
	public void checkForNulls() {
		this.checkForNulls = true;
	}
	
	private boolean isSystem(Class<?> cls) {
		Package objectPackage = cls.getPackage();
		String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
		return objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || cls.getClassLoader() == null;
	}

	static boolean IsJSONPrintable(Field field) {
		return	!field.isSynthetic()
			&&	!field.isAnnotationPresent(SJExclude.class)
			&&	!Modifier.isStatic(field.getModifiers())
			&&	!Modifier.isTransient(field.getModifiers())
		;
	}
	
	private String getFieldName(Field field) {
		SJFieldName wsFieldName = field.getAnnotation(SJFieldName.class);
		if (wsFieldName != null)
			return wsFieldName.value();
		return field.getName();
	}
	
	private @CheckForNull Type getParent(Class<?> cls) {
		if (cls.getAnnotation(SJDisregardParent.class) != null)
			return null;

		Class<?> superClass = cls.getSuperclass();
		if (superClass == null)
			return null;

		if (superClass.getAnnotation(SJDisregardedParent.class) != null)
			return null;
		
		if (isSystem(superClass))
			return null;
		
		return cls.getGenericSuperclass();
	}

	private AnnotatedElement emptyAnno = new AnnotatedElement() {
		private Annotation[] array = new Annotation[0];
		@Override public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) { return false; }
		@Override public Annotation[] getDeclaredAnnotations() { return array; }
		@Override public Annotation[] getAnnotations() { return array; }
		@Override public <T extends Annotation>T getAnnotation(Class<T> annotationClass) { return null; }
	};

	@SuppressWarnings({"unchecked", "rawtypes"})
	public @CheckForNull Object toJSON(@CheckForNull Object from, Type fromType, double version, @CheckForNull AnnotatedElement fromAnno, AllowEmpty allowEmpty, @CheckForNull Object enclosing) throws SourJsonException {

		if (from == null || Void.class.isAssignableFrom(GenericTypeReflector.erase(fromType)))
			return null;
		
		AllowEmpty nextAllowEmpty = (allowEmpty == AllowEmpty.ROOT ? AllowEmpty.NO : allowEmpty);
		
		if (fromAnno == null)
			fromAnno = emptyAnno;

		SJTranslater translater = getTranslater(GenericTypeReflector.erase(fromType));
		if (translater != null) {
			JSONObject ret = translater.serialize(from, fromType, fromAnno, enclosing, this);
			ret.put("!type", GenericTypeReflector.getTypeName(fromType));
			return ret;
		}

		if (	   from instanceof JSONObject   || from instanceof JSONArray
				|| from instanceof Number	    || from instanceof Boolean
				|| from instanceof JSONAware    || from instanceof JSONStreamAware
				|| from instanceof String
				) {
			return from;
		}

		Class<?> fromClass = GenericTypeReflector.erase(fromType);

		if (Collection.class.isAssignableFrom(fromClass)) {
			if (allowEmpty == AllowEmpty.NO && ((Collection<?>)from).isEmpty())
				return null;
			Type colType = GenericTypeReflector.getTypeParameter(fromType, Collection.class.getTypeParameters()[0]);
			if (colType == null)
				colType = Object.class;
			JSONArray array = new JSONArray();
			Iterator<?> it = ((Collection<?>)from).iterator();
			while (it.hasNext()) {
				Object value = it.next();
				Object json = null;
				if (value != null) {
					Type valueType = fromAnno.isAnnotationPresent(SJStrict.class) ? colType : value.getClass();
					json = toJSON(value, valueType, version, fromAnno, nextAllowEmpty, from);
				}
				if (json != null || nextAllowEmpty != AllowEmpty.NO)
					array.add(json);
			}
			return array;
		}

		if (fromClass.isArray()) {
			JSONArray array = new JSONArray();
			int length = Array.getLength(from);
			if (allowEmpty == AllowEmpty.NO && length == 0)
				return null;
			Type arrayComponentType = GenericTypeReflector.getArrayComponentType(fromType);
			for (int i = 0; i < length; ++i) {
				Object element = Array.get(from, i);
				Object json = null;
				if (element != null) {
					Type elementType = fromAnno.isAnnotationPresent(SJStrict.class) ? arrayComponentType : element.getClass();
					json = toJSON(element, elementType, version, fromAnno, nextAllowEmpty, from);
				}
				if (json != null || nextAllowEmpty != AllowEmpty.NO)
					array.add(json);
			}
			return array;
		}

		if (Map.class.isAssignableFrom(fromClass)) {
			if (allowEmpty == AllowEmpty.NO && ((Map<?, ?>)from).isEmpty())
				return null;
			Type mapType = GenericTypeReflector.getTypeParameter(fromType, Map.class.getTypeParameters()[1]);
			if (mapType == null)
				mapType = Object.class;
			JSONObject obj = new JSONObject();
			Iterator<?> it = ((Map<?, ?>)from).entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<?, ?> e = (Map.Entry<?, ?>) it.next();
				Object value = e.getValue();
				Object json = null;
				if (value != null) {
					Type valueType = fromAnno.isAnnotationPresent(SJStrict.class) ? mapType : value.getClass();
					json = toJSON(value, valueType, version, fromAnno, nextAllowEmpty, from);
				}
				if (json != null || nextAllowEmpty != AllowEmpty.NO)
					obj.put(e.getKey().toString(), json);
			}
			return obj;
		}
		
		if (fromClass.isEnum()) {
			JSONObject object = new JSONObject();
			object.put("!type", GenericTypeReflector.getTypeName(fromType));
			object.put("!enum", from.toString());
			return object;
		}

		if (isSystem(fromClass))
			return from.toString();

		if (checkKnownClasses && !knownClasses.contains(fromClass))
			throw new UnknownClassException(fromClass);

		JSONObject object = new JSONObject();

		Type typeOnServer = fromType;

		// TODO: Should be Cached
		while (fromType != null) {
			fromClass = GenericTypeReflector.erase(fromType);

			for (Field field : fromClass.getDeclaredFields()) {
				if (!IsJSONPrintable(field))
					continue ;

				SJUntil until = field.getAnnotation(SJUntil.class);
				if (until != null && version > until.value())
					continue ;

				SJSince since = field.getAnnotation(SJSince.class);
				if (since != null && version < since.value())
					continue ;

				field.setAccessible(true);
				try {
					Object fieldValue = field.get(from);
					if (fieldValue == null)
						continue ;

					Type fieldType = fieldValue.getClass();
					Class<?> fieldClass = GenericTypeReflector.erase(fieldType);
					if (Map.class.isAssignableFrom(fieldClass) || Collection.class.isAssignableFrom(fieldClass) || field.getAnnotation(SJStrict.class) != null)
						fieldType = GenericTypeReflector.getExactFieldType(field, fromType);

					Object json = toJSON(fieldValue, fieldType, version, field, nextAllowEmpty, from);
					if (json != null)
						object.put(getFieldName(field), json);
				}
				catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				finally {
					field.setAccessible(false);
				}
			}
			
			fromType = getParent(fromClass);
		}

		object.put("!type", GenericTypeReflector.getTypeName(typeOnServer));
		
		return object;
	}
	
	public @CheckForNull Object toJSON(Object from, Type fromType, double version, AllowEmpty allowEmpty) throws SourJsonException {
		return toJSON(from, fromType, version, null, allowEmpty, null);
	}

	public @CheckForNull Object toJSON(Object from, Type fromType, double version) throws SourJsonException {
		return toJSON(from, fromType, version, null, AllowEmpty.YES, null);
	}

	public @CheckForNull Object toJSON(Object from, double version) throws SourJsonException {
		if (from == null)
			return null;
		return toJSON(from, from.getClass(), version, null, AllowEmpty.YES, null);
	}

	private Object construct(Class<?> cls) throws SourJsonException {
		try {
			Constructor<?> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			try {
				return constructor.newInstance();
			}
			finally {
				constructor.setAccessible(false);
			}
		}
		catch (Exception e) {
			throw new SourJsonException("Cannot construct " + cls);
		}
	}
	
	private @CheckForNull Object constructWithValueOf(Class<?> toClass, Object ret) {
		try {
			Method valueOf = toClass.getMethod("valueOf", ret.getClass());
			valueOf.setAccessible(true);
			try {
				return valueOf.invoke(null, ret);
			}
			finally {
				valueOf.setAccessible(false);
			}
		}
		catch (Exception e) {
			if (WRAPPERS_TO_PRIMITIVES.containsKey(ret.getClass())) {
				Class<?> primitive = WRAPPERS_TO_PRIMITIVES.get(ret.getClass());
				try {
					Method valueOf = toClass.getMethod("valueOf", primitive);
					valueOf.setAccessible(true);
					try {
						return valueOf.invoke(null, ret);
					}
					finally {
						valueOf.setAccessible(false);
					}
				}
				catch (Exception e2) {}
			}
		}
		return null;
	}

	private @CheckForNull Object constructWithConstructor(Class<?> toClass, Object ret) {
		try {
			Constructor<?> constructor = toClass.getConstructor(ret.getClass());
			constructor.setAccessible(true);
			try {
				return constructor.newInstance(ret);
			}
			finally {
				constructor.setAccessible(false);
			}
		}
		catch (Exception e) {
			if (WRAPPERS_TO_PRIMITIVES.containsKey(ret.getClass())) {
				Class<?> primitive = WRAPPERS_TO_PRIMITIVES.get(ret.getClass());
				try {
					Constructor<?> constructor = toClass.getConstructor(primitive);
					constructor.setAccessible(true);
					try {
						return constructor.newInstance(ret);
					}
					finally {
						constructor.setAccessible(false);
					}
				}
				catch (Exception e2) {}
			}
		}
		return null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Type toType, double version, @CheckForNull AnnotatedElement toAnno, @CheckForNull Object enclosing) throws SourJsonException {
		if (from == null)
			return null;
		
		Class<?> toClass = GenericTypeReflector.erase(toType);

		if (toAnno == null)
			toAnno = emptyAnno;

		if (from instanceof JSONObject) {
			JSONObject fromObject = (JSONObject)from;

			if (fromObject.containsKey("!type"))
				try {
					toType = Class.forName(String.valueOf(fromObject.get("!type")));
					toClass = (Class<?>)toType;
				}
				catch (ClassNotFoundException e1) {
					throw new SourJsonException("Could not find " + String.valueOf(fromObject.get("!type")));
				}

			SJTranslater<T> translater = (SJTranslater<T>)getTranslater(toClass);
			if (translater != null)
				return translater.deserialize(fromObject, toType, toAnno, enclosing, this);

			if (Map.class.isAssignableFrom(toClass)) {
				Map<String, Object> map; 
				if (Map.class.equals(toClass))
					map = new HashMap<String, Object>();
				else
					map = (Map<String, Object>)construct(toClass);
				
				Type mapToType = GenericTypeReflector.getTypeParameter(toType, Map.class.getTypeParameters()[1]);
				if (mapToType == null)
					mapToType = Object.class;
				
				for (Entry<Object, Object> entry : (Set<Map.Entry<Object, Object>>)fromObject.entrySet()) {
					String key = String.valueOf(entry.getKey());
					map.put(key, fromJSON(entry.getValue(), mapToType, version, toAnno, map));
				}

				return (T)map;
			}
			else if (toClass.isEnum()) {
				return (T)Enum.valueOf((Class<? extends Enum>)toClass, (String)((JSONObject)from).get("!enum"));
			}

			T ret = (T)construct(toClass);

			// TODO: Should be Cached
			while (toType != null) {
				toClass = GenericTypeReflector.erase(toType);

				for (Field field : toClass.getDeclaredFields()) {
					if (!IsJSONPrintable(field))
						continue ;

					String fieldName = getFieldName(field);

					SJUntil until = field.getAnnotation(SJUntil.class);
					if (until != null && version > until.value())
						continue ;

					SJSince since = field.getAnnotation(SJSince.class);
					if (since != null && version < since.value())
						continue ;

					if (!fromObject.containsKey(fieldName) || fromObject.get(fieldName) == null) {
						if (!checkForNulls || field.isAnnotationPresent(SJCheckForNull.class))
							continue ;

						throw new SourJsonException("Missing (or null) JSON : " + field);
					}
					
					Object json = fromJSON(fromObject.get(fieldName), GenericTypeReflector.getExactFieldType(field, toType), version, field, ret);
					field.setAccessible(true);
					try {
						field.set(ret, json);
					}
					catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					finally {
						field.setAccessible(false);
					}
				}
				
				toType = getParent(toClass);
			}
			
			return ret;
		}
		
		else if (from instanceof JSONArray) {
			JSONArray fromArray = (JSONArray)from;
			
			if (!Collection.class.isAssignableFrom(toClass) && !toClass.isArray())
				throw new SourJsonException("Can only unserialize an array into a Collection, not " + toClass);
			Collection<Object> col;
			if (toClass.isArray())
				col = new ArrayList<Object>();
			else if (List.class.equals(toClass))
				col = new LinkedList<Object>();
			else if (Set.class.equals(toClass))
				col = new HashSet<Object>();
			else if (Queue.class.equals(toClass))
				col = new ArrayDeque<Object>();
			else
				col = (Collection<Object>)construct(toClass);
			
			Type colToType;
			if (toClass.isArray())
				colToType = GenericTypeReflector.getArrayComponentType(toType);
			else {
				colToType = GenericTypeReflector.getTypeParameter(toType, Collection.class.getTypeParameters()[0]);
				if (colToType == null)
					colToType = Object.class;
			}
			
			for (int i = 0; i < fromArray.size(); ++i)
				col.add(fromJSON(fromArray.get(i), colToType, version, toAnno, col));
			
			Object ret = col;
			
			if (toClass.isArray()) {
				ret = Array.newInstance(toClass.getComponentType(), col.size());
				Iterator<Object> it = col.iterator();
				for (int i = 0; i < col.size(); ++i)
					Array.set(ret, i, it.next());
			}
			
			return (T)ret;
			
		}
		else {
			
			if (toClass.isPrimitive())
				toClass = PRIMITIVES_TO_WRAPPERS.get(toClass);

			if (toClass.isAssignableFrom(from.getClass()))
				return (T)from;
			
			if (Character.class.isAssignableFrom(toClass)) {
				if (Number.class.isAssignableFrom(from.getClass()))
					return (T)Character.valueOf((char)((Number)from).intValue());
				return (T)Character.valueOf(from.toString().charAt(0));
			}

			Object c = constructWithValueOf(toClass, from);
			if (c != null)
				return (T)c;
			c = constructWithValueOf(toClass, from.toString());
			if (c != null)
				return (T)c;
			c = constructWithConstructor(toClass, from);
			if (c != null)
				return (T)c;
			c = constructWithConstructor(toClass, from.toString());
			if (c != null)
				return (T)c;
			throw new SourJsonException("Cannot transform " + from.getClass() + " to " + toClass);
		}
	}
	
	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Class<T> toClass, double version, AnnotatedElement toAnno, @CheckForNull Object enclosing) throws SourJsonException {
		return fromJSON(from, (Type)toClass, version, toAnno, enclosing);
	}

	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Type toType, double version, AnnotatedElement toAnno) throws SourJsonException {
		return fromJSON(from, toType, version, toAnno, null);
	}

	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Class<T> toClass, double version, AnnotatedElement toAnno) throws SourJsonException {
		return fromJSON(from, (Type)toClass, version, toAnno, null);
	}

	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Type toType, double version) throws SourJsonException {
		return fromJSON(from, toType, version, null, null);
	}

	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Class<T> toClass, double version) throws SourJsonException {
		return fromJSON(from, (Type)toClass, version, null, null);
	}

}
