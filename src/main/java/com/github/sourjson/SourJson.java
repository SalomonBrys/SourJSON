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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.exception.SourJsonException;
import com.github.sourjson.internal.SJUtils;
import com.github.sourjson.internal.TypeAndAnnos;
import com.github.sourjson.internal.TranslaterCache;
import com.github.sourjson.translat.SJTranslater;
import com.github.sourjson.translat.def.ClassTranslater;
import com.github.sourjson.translat.def.DateTranslater;
import com.googlecode.gentyref.GenericTypeReflector;

@SuppressWarnings("javadoc")
public class SourJson /*implements Cloneable*/ {

	public static enum AllowEmpty {
		YES,
		ROOT,
		NO;

		public AllowEmpty next() {
			switch (this) {
			case YES: return YES;
			default: return NO;
			}
		}

		public boolean allow() {
			switch (this) {
			case NO: return false;
			default: return true;
			}
		}
	}

	private TranslaterCache tcache = new TranslaterCache();

	private HashMap<Class<?>, SJTranslater<?>> exactTranslaters = new HashMap<>();
	private LinkedHashMap<Class<?>, SJTranslater<?>> hierarchyTranslaters = new LinkedHashMap<>();

	@CheckForNull HashSet<Class<?>> knownClasses = new HashSet<>();
	private boolean checkKnownClasses = false;

	private boolean putTypes = true;

	private boolean checkForAllowNulls = false;

	public SourJson() {
		addTranslater(Date.class, new DateTranslater());
		addTranslater(Class.class, new ClassTranslater());
	}

	/*@Override
	public SourJson clone() {
		try {
			SourJson clone = (SourJson)super.clone();
			clone.knownClasses = (HashSet<Class<?>>)knownClasses.clone();
			clone.exactTranslaters = (HashMap<Class<?>, SJTranslater<?>>)exactTranslaters.clone();
			clone.hierarchyTranslaters = (LinkedHashMap<Class<?>, SJTranslater<?>>)hierarchyTranslaters.clone();
			return clone;
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}*/

	public <T> void addTranslater(Class<T> forClass, SJTranslater<T> translater) {
		exactTranslaters.put(forClass, translater);
		knownClasses.add(forClass);
	}

	public <T> void removeTranslater(Class<T> forClass) {
		exactTranslaters.remove(forClass);
		knownClasses.remove(forClass);
	}

	public void removeTranslater(SJTranslater<?> tr) {
		Iterator<Entry<Class<?>, SJTranslater<?>>> it = exactTranslaters.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Class<?>, SJTranslater<?>> entry = it.next();
			if (entry.getValue().equals(tr)) {
				knownClasses.remove(entry.getKey());
				it.remove();
			}
		}
	}

	public <T> void addHierarchyTranslater(Class<T> forClass, SJTranslater<T> translater) {
		hierarchyTranslaters.put(forClass, translater);
		knownClasses.add(forClass);
	}

	public <T> void removeHierarchyTranslater(Class<T> forClass) {
		hierarchyTranslaters.remove(forClass);
		knownClasses.remove(forClass);
	}

	public void removeHierarchyTranslater(SJTranslater<?> tr) {
		Iterator<Entry<Class<?>, SJTranslater<?>>> it = hierarchyTranslaters.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Class<?>, SJTranslater<?>> entry = it.next();
			if (entry.getValue().equals(tr)) {
				knownClasses.remove(entry.getKey());
				it.remove();
			}
		}
	}

	public @CheckForNull <T> SJTranslater<T> getTranslater(Class<T> forClass) {
		if (exactTranslaters.containsKey(forClass))
			return (SJTranslater<T>)exactTranslaters.get(forClass);
		for (Map.Entry<Class<?>, SJTranslater<?>> entry : hierarchyTranslaters.entrySet())
			if (entry.getKey().isAssignableFrom(forClass))
				return (SJTranslater<T>)entry.getValue();
		return null;
	}

	public void setCheckKnownClasses(boolean checkKnownClasses) {
		this.checkKnownClasses = checkKnownClasses;
	}

	public @CheckForNull void checkKnownClasses(@CheckForNull Collection<Class<?>> classes) {
		this.knownClasses.addAll(classes);
		setCheckKnownClasses(true);
	}

	public @CheckForNull void checkKnownClasses(Class<?>... classes) {
		checkKnownClasses(Arrays.asList(classes));
	}

	public boolean isClassKnown(Class<?> cls) {
		if (!checkKnownClasses)
			return true;
		return knownClasses.contains(cls);
	}

	public void setPutTypes(boolean putTypes) {
		this.putTypes = putTypes;
	}

	public boolean isPutTypes() {
		return putTypes;
	}

	public void setCheckForAllowNulls(boolean checkForAllowNulls) {
		this.checkForAllowNulls = checkForAllowNulls;
	}

	public boolean isCheckForAllowNulls() {
		return checkForAllowNulls;
	}

	public @CheckForNull Object toJSON(@CheckForNull Object from, Type fromType, double version, @CheckForNull AnnotatedElement fromAnnos, AllowEmpty allowEmpty, @CheckForNull Object enclosing) throws SourJsonException {

		if (from == null)
			return null;

		Class<?> fromClass = GenericTypeReflector.erase(fromType);

		if (Void.class.isAssignableFrom(fromClass))
			return null;

		if (!fromClass.isAssignableFrom(from.getClass()))
			throw new SourJsonException(from.toString() + " is not an instance of " + fromType);

		TypeAndAnnos info = new TypeAndAnnos(fromType, fromAnnos);

		return tcache.getTranslater(info, this).serialize(from, info, enclosing, version, allowEmpty, this);
	}

	public @CheckForNull Object toJSON(Object from, Type fromType, double version, AllowEmpty allowEmpty) throws SourJsonException {
		return toJSON(from, fromType, version, null, allowEmpty, null);
	}

	public @CheckForNull Object toJSON(Object from, Type fromType, double version) throws SourJsonException {
		return toJSON(from, fromType, version, null, AllowEmpty.NO, null);
	}

	public @CheckForNull Object toJSON(Object from, double version) throws SourJsonException {
		if (from == null)
			return null;
		return toJSON(from, from.getClass(), version, null, AllowEmpty.NO, null);
	}

	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Type toType, double version, @CheckForNull AnnotatedElement toAnnos, @CheckForNull Object enclosing) throws SourJsonException {
		if (from == null)
			return null;

		Class<?> toClass = GenericTypeReflector.erase(toType);

		if (Void.class.isAssignableFrom(toClass))
			return null;

		TypeAndAnnos info = new TypeAndAnnos(toType, toAnnos);

		if (toClass.isPrimitive()) {
			toClass = SJUtils.PRIMITIVES_TO_WRAPPERS.get(toClass);
			toType = toClass;
		}

		if (from instanceof JSONObject && ((JSONObject)from).containsKey("!type")) {
			String className = (String)((JSONObject)from).get("!type");
			try {
				info.type = Class.forName(className);
			}
			catch (ClassNotFoundException e) {
				throw new SourJsonException("Could not find class " + className);
			}
		}

		return (T)tcache.getTranslater(info, this).deserialize(from, info, enclosing, version, this);

	}

	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Class<T> toClass, double version, @CheckForNull Object enclosing) throws SourJsonException {
		return fromJSON(from, (Type)toClass, version, null, enclosing);
	}

	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Type toType, double version) throws SourJsonException {
		return fromJSON(from, toType, version, null, null);
	}

	public @CheckForNull <T> T fromJSON(@CheckForNull Object from, Class<T> toClass, double version) throws SourJsonException {
		return fromJSON(from, (Type)toClass, version, null, null);
	}
}
