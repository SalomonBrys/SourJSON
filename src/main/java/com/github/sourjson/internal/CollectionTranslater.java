package com.github.sourjson.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.json.simple.JSONArray;

import com.github.sourjson.SourJson;
import com.github.sourjson.annotation.StrictType;
import com.github.sourjson.exception.SourJsonException;
import com.googlecode.gentyref.GenericTypeReflector;

@SuppressWarnings("unchecked")
public class CollectionTranslater<T> implements InternalTranslater<T> {

	private Class<?> colType;
	private Type colParamType;
	private boolean strictType;
	private AnnotatedElement el;

	public CollectionTranslater(TypeAndAnnos info) {
		colType = GenericTypeReflector.erase(info.type);
		colParamType = GenericTypeReflector.getTypeParameter(info.type, Collection.class.getTypeParameters()[0]);
		if (colParamType == null)
			colParamType = Object.class;
		strictType = info.annos.isAnnotationPresent(StrictType.class);
		el = info.annos;
	}

	@Override
	public Object serialize(T from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		JSONArray array = new JSONArray();
		Iterator<?> it = ((Collection<?>)from).iterator();
		while (it.hasNext()) {
			Object value = it.next();
			Object json = null;
			if (value != null) {
				Type valueType = strictType ? colParamType : value.getClass();
				json = sour.toJSON(value, valueType, version, el, from);
			}
			array.add(json);
		}
		return array;
	}

	@Override
	public T deserialize(Object from, Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (!(from instanceof JSONArray))
			throw new SourJsonException("Cannot deserialize a " + from.getClass().getSimpleName() + " into a collection");

		Collection<Object> col;
		if (List.class.equals(colType))
			col = new LinkedList<>();
		else if (Set.class.equals(colType))
			col = new HashSet<>();
		else if (Queue.class.equals(colType))
			col = new ArrayDeque<>();
		else
			col = (Collection<Object>)SJUtils.construct(colType);

		JSONArray fromArray = (JSONArray)from;

		for (int i = 0; i < fromArray.size(); ++i)
			col.add(sour.fromJSON(fromArray.get(i), colParamType, version, el, col));

		return (T)col;
	}
}