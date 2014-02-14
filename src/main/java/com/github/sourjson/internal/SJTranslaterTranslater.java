package com.github.sourjson.internal;

import java.util.Map;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;
import com.github.sourjson.SourJson.AllowEmpty;
import com.github.sourjson.exception.SourJsonException;
import com.github.sourjson.translat.SJTranslater;
import com.googlecode.gentyref.GenericTypeReflector;

public class SJTranslaterTranslater<T> implements InternalTranslater<T> {

	SJTranslater<T> translater;
	String typeName;

	public SJTranslaterTranslater(SJTranslater<T> translater, Class<?> fromClass) {
		super();
		this.translater = translater;
		typeName = GenericTypeReflector.getTypeName(fromClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object serialize(T from, TypeAndAnnos info, @CheckForNull Object enclosing, double version, AllowEmpty allowEmpty, SourJson sour) throws SourJsonException {
		JSONObject ret = translater.serialize(from, info.type, info.annos, enclosing, sour, version, allowEmpty);
		if (sour.isPutTypes())
			((Map<String, Object>)ret).put("!type", typeName);
		return ret;
	}

	@Override
	public T deserialize(Object from, TypeAndAnnos info, Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (!(from instanceof JSONObject))
			throw new SourJsonException("Cannot deserialize a " + from.getClass().getSimpleName() + " into a " + typeName);

		return translater.deserialize((JSONObject) from, info.type, info.annos, enclosing, sour, version);
	}
}