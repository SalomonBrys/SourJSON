package com.github.sourjson.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;

import com.github.sourjson.annotation.DisregardParent;
import com.github.sourjson.annotation.DisregardedParent;
import com.github.sourjson.annotation.Exclude;
import com.github.sourjson.annotation.FieldName;
import com.github.sourjson.exception.SourJsonException;

public class SJUtils {

	static boolean isSystem(Class<?> cls) {
		Package objectPackage = cls.getPackage();
		String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
		return objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || cls.getClassLoader() == null;
	}

	static boolean IsJSONPrintable(Field field) {
		return	!field.isSynthetic()
			&&	!field.isAnnotationPresent(Exclude.class)
			&&	!Modifier.isStatic(field.getModifiers())
			&&	!Modifier.isTransient(field.getModifiers())
		;
	}

	static String getFieldName(Field field) {
		FieldName wsFieldName = field.getAnnotation(FieldName.class);
		if (wsFieldName != null)
			return wsFieldName.value();
		return field.getName();
	}

	static @CheckForNull Type getParent(Class<?> cls) {
		if (cls.getAnnotation(DisregardParent.class) != null)
			return null;

		Class<?> superClass = cls.getSuperclass();
		if (superClass == null)
			return null;

		if (superClass.getAnnotation(DisregardedParent.class) != null)
			return null;

		if (isSystem(superClass))
			return null;

		return cls.getGenericSuperclass();
	}

	static Class<?> findGenType(Collection<?> c) {
		Class<?> ret = null;
		for (Object k : c) {
			if (ret == null) {
				ret = k.getClass();
				continue;
			}
			if (k.getClass().equals(ret))
				continue ;
			while (!ret.isAssignableFrom(k.getClass()))
				ret = ret.getSuperclass();
		}
		if (ret == null)
			return Object.class;
		return ret;
	}

	static <T> T construct(Class<T> cls) throws SourJsonException {
		try {
			Constructor<?> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			try {
				return (T) constructor.newInstance();
			}
			finally {
				constructor.setAccessible(false);
			}
		}
		catch (Exception e) {
			throw new SourJsonException("Cannot construct " + cls);
		}
	}

	@SuppressWarnings("serial")
	public static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>() {
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
	public static final Map<Class<?>, Class<?>> WRAPPERS_TO_PRIMITIVES = new HashMap<Class<?>, Class<?>>() {
		{
			for (Entry<Class<?>, Class<?>> entry : PRIMITIVES_TO_WRAPPERS.entrySet())
				this.put(entry.getValue(), entry.getKey());
		}
	};

	static  @CheckForNull <T> T constructWithValueOf(Class<T> toClass, Object ret) {
		try {
			Method valueOf = toClass.getMethod("valueOf", ret.getClass());
			valueOf.setAccessible(true);
			try {
				return (T) valueOf.invoke(null, ret);
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
						return (T) valueOf.invoke(null, ret);
					}
					finally {
						valueOf.setAccessible(false);
					}
				}
				catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
					// None of those exceptions should happen as we have checked everything before
				}
			}
		}
		return null;
	}

	static @CheckForNull <T> T constructWithConstructor(Class<T> toClass, Object ret) {
		try {
			Constructor<?> constructor = toClass.getConstructor(ret.getClass());
			constructor.setAccessible(true);
			try {
				return (T) constructor.newInstance(ret);
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
						return (T) constructor.newInstance(ret);
					}
					finally {
						constructor.setAccessible(false);
					}
				}
				catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e1) {
					// None of those exceptions should happen as we have checked everything before
				}
			}
		}
		return null;
	}
}
