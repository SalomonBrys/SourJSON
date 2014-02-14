package com.github.sourjson.internal;

import javax.annotation.CheckForNull;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.SourJsonException;

public class SelfTranslater<T> implements InternalTranslater<T> {

	@CheckForNull Class<?> fromClass = null;

	SelfTranslater(Class<?> fromClass) {
		this.fromClass = fromClass;
	}

	@Override
	public Object serialize(T from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		return from;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(Object from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (from instanceof Number) {
			Number numFrom = (Number)from;
			if (fromClass.equals(byte.class) || fromClass.equals(Byte.class))
				return (T) Byte.valueOf(numFrom.byteValue());
			if (fromClass.equals(char.class) || fromClass.equals(Character.class))
				return (T) Character.valueOf((char)numFrom.intValue());
			if (fromClass.equals(short.class) || fromClass.equals(Short.class))
				return (T) Short.valueOf(numFrom.shortValue());
			if (fromClass.equals(int.class) || fromClass.equals(Integer.class))
				return (T) Integer.valueOf(numFrom.intValue());
			if (fromClass.equals(long.class) || fromClass.equals(Long.class))
				return (T) Long.valueOf(numFrom.longValue());
			if (fromClass.equals(float.class) || fromClass.equals(Float.class))
				return (T) Float.valueOf(numFrom.floatValue());
			if (fromClass.equals(double.class) || fromClass.equals(Double.class))
				return (T) Double.valueOf(numFrom.doubleValue());
		}
		return (T) from;
	}
}
