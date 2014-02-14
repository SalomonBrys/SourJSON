package com.github.sourjson.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

import javax.annotation.CheckForNull;

import org.json.simple.JSONArray;

import com.github.sourjson.SourJson;
import com.github.sourjson.annotation.StrictType;
import com.github.sourjson.exception.SourJsonException;
import com.googlecode.gentyref.GenericTypeReflector;

@SuppressWarnings({"unchecked"})
public class ArrayTranslater<T> implements InternalTranslater<T> {

	private Type arrayComponentType;
	private Class<T[]> arrayComponentClass;
	private boolean strictType;
	private AnnotatedElement el;

	public ArrayTranslater(TypeAndAnnos info) {
		arrayComponentType = GenericTypeReflector.getArrayComponentType(info.type);
		arrayComponentClass = (Class<T[]>) GenericTypeReflector.erase(arrayComponentType);
		strictType = info.annos.isAnnotationPresent(StrictType.class);
		el = info.annos;
	}

	@Override
	public Object serialize(T from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		JSONArray array = new JSONArray();
		int length = Array.getLength(from);
		for (int i = 0; i < length; ++i) {
			Object element = Array.get(from, i);
			Object json = null;
			if (element != null) {
				Type elementType = strictType ? arrayComponentType : element.getClass();
				json = sour.toJSON(element, elementType, version, el, from);
			}
			array.add(json);
		}
		return array;
	}

	@Override
	public T deserialize(Object from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (!(from instanceof JSONArray))
			throw new SourJsonException("Cannot deserialize a " + from.getClass().getSimpleName() + " into an array");

		JSONArray fromArray = (JSONArray)from;

		T ret = (T) Array.newInstance(arrayComponentClass, fromArray.size());

		for (int i = 0; i < fromArray.size(); ++i)
			Array.set(ret, i, sour.fromJSON(fromArray.get(i), arrayComponentType, version, el, ret));

		return ret;
	}
}
