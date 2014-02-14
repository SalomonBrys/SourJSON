package com.github.sourjson.internal;

import javax.annotation.CheckForNull;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.SourJsonException;

public interface InternalTranslater<T> {

	public abstract @CheckForNull Object serialize(T from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException;

	public abstract @CheckForNull T deserialize(Object from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException;
}