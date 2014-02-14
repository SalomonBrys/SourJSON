package com.github.sourjson.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.SourJsonException;
import com.github.sourjson.translat.SJTranslater;
import com.googlecode.gentyref.GenericTypeReflector;

public class SJTranslaterTranslater<T> implements InternalTranslater<T> {

	SJTranslater<T> translater;
	String typeName;
	Type type;
	private AnnotatedElement el;

	public SJTranslaterTranslater(SJTranslater<T> translater, TypeAndAnnos info) {
		super();
		this.translater = translater;
		typeName = GenericTypeReflector.getTypeName(info.type);
		type = info.type;
		el = info.annos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object serialize(T from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		JSONObject ret = translater.serialize(from, type, el, enclosing, sour, version);
		if (sour.isPutTypes())
			((Map<String, Object>)ret).put("!type", typeName);
		return ret;
	}

	@Override
	public T deserialize(Object from, Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (!(from instanceof JSONObject))
			throw new SourJsonException("Cannot deserialize a " + from.getClass().getSimpleName() + " into a " + typeName);

		return translater.deserialize((JSONObject) from, type, el, enclosing, sour, version);
	}
}