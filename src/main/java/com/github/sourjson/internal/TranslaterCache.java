package com.github.sourjson.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.UnknownClassException;
import com.github.sourjson.translat.SJTranslater;
import com.googlecode.gentyref.GenericTypeReflector;

public class TranslaterCache implements Cloneable {

	private HashMap<TypeAndAnnos, InternalTranslater<?>> serializers = new HashMap<>();

	private static <T> InternalTranslater<T> makeSerializer(TypeAndAnnos info, SourJson sour) {

		Class<T> fromClass = (Class<T>) GenericTypeReflector.erase(info.type);

		SJTranslater<T> translater = sour.getTranslater(fromClass);
		if (translater != null)
			return new SJTranslaterTranslater<>(translater, info);

		if (	   JSONObject.class.isAssignableFrom(fromClass)   || JSONArray.class.isAssignableFrom(fromClass)
				|| Number.class.isAssignableFrom(fromClass)	      || Boolean.class.isAssignableFrom(fromClass)
				|| JSONAware.class.isAssignableFrom(fromClass)    || JSONStreamAware.class.isAssignableFrom(fromClass)
				|| String.class.isAssignableFrom(fromClass)	      || fromClass.isPrimitive()
				) {
			return new SelfTranslater<>(fromClass);
		}

		if (Collection.class.isAssignableFrom(fromClass))
			return new CollectionTranslater<>(info);

		if (fromClass.isArray())
			return new ArrayTranslater<>(info);

		if (Map.class.isAssignableFrom(fromClass))
			return new MapTranslater<>(info);

		if (fromClass.isEnum())
			return new EnumTranslater<>(info.type);

		if (SJUtils.isSystem(fromClass))
			return new StringTranslater<>(fromClass);

		if (!sour.isClassKnown(fromClass))
			throw new UnknownClassException(fromClass);

		return new ObjectTranslater<>(info.type);
	}

	@SuppressWarnings("unchecked")
	public <T> InternalTranslater<T> getTranslater(TypeAndAnnos info, SourJson sour) {
		InternalTranslater<T> ser = (InternalTranslater<T>) serializers.get(info);

		if (ser == null) {
			ser = (InternalTranslater<T>) makeSerializer(info, sour);
			serializers.put(info, ser);
		}

		return ser;
	}

	@Override
	public Object clone() {
		try {
			TranslaterCache clone = (TranslaterCache) super.clone();
			clone.serializers = (HashMap<TypeAndAnnos, InternalTranslater<?>>) serializers.clone();
			return clone;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
}
