package com.github.sourjson.internal;

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
import com.github.sourjson.SourJson.AllowEmpty;
import com.github.sourjson.annotation.StrictType;
import com.github.sourjson.exception.SourJsonException;
import com.googlecode.gentyref.GenericTypeReflector;

@SuppressWarnings("unchecked")
public class CollectionTranslater<T> implements InternalTranslater<T> {

	private Class<?> colType;
	private Type colParamType;
	private boolean strictType;

	public CollectionTranslater(TypeAndAnnos info) {
		colType = GenericTypeReflector.erase(info.type);
		colParamType = GenericTypeReflector.getTypeParameter(info.type, Collection.class.getTypeParameters()[0]);
		if (colParamType == null)
			colParamType = Object.class;
		strictType = info.annos.isAnnotationPresent(StrictType.class);
	}

	@Override
	public Object serialize(T from, TypeAndAnnos info, @CheckForNull Object enclosing, double version, AllowEmpty allowEmpty, SourJson sour) throws SourJsonException {
		if (!allowEmpty.allow() && ((Collection<?>)from).isEmpty())
			return null;
		JSONArray array = new JSONArray();
		Iterator<?> it = ((Collection<?>)from).iterator();
		while (it.hasNext()) {
			Object value = it.next();
			Object json = null;
			if (value != null) {
				Type valueType = strictType ? colParamType : value.getClass();
				json = sour.toJSON(value, valueType, version, info.annos, allowEmpty.next(), from);
			}
			if (json != null || allowEmpty.next().allow())
				array.add(json);
		}
		return array;
	}

	@Override
	public T deserialize(Object from, TypeAndAnnos info, Object enclosing, double version, SourJson sour) throws SourJsonException {
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
			col.add(sour.fromJSON(fromArray.get(i), colParamType, version, info.annos, col));

		return (T)col;
	}
}