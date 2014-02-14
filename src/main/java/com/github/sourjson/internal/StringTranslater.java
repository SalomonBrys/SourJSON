package com.github.sourjson.internal;

import javax.annotation.CheckForNull;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.SourJsonException;

class StringTranslater<T> implements InternalTranslater<T> {

	Class<T> fromClass;

	public StringTranslater(Class<T> fromClass) {
		this.fromClass = fromClass;
	}

	@Override
	public Object serialize(T from, TypeAndAnnos info, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		return from.toString();
	}

	@Override
	public T deserialize(Object from, TypeAndAnnos info, Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (fromClass.equals(char.class) || fromClass.equals(Character.class))
			return (T)Character.valueOf(((String)from).charAt(0));

		T c = SJUtils.constructWithValueOf(fromClass, from);
		if (c != null)
			return c;
		return SJUtils.constructWithConstructor(fromClass, from);
	}
}
