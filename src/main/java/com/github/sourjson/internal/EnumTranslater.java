package com.github.sourjson.internal;

import java.lang.reflect.Type;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.SourJsonException;
import com.googlecode.gentyref.GenericTypeReflector;

@SuppressWarnings({"unchecked"})
public class EnumTranslater<T> implements InternalTranslater<T> {

	private Class<T> enumClass;
	private String typeName;

	public EnumTranslater(Type fromType) {
		typeName = GenericTypeReflector.getTypeName(fromType);
		enumClass = (Class<T>) GenericTypeReflector.erase(fromType);
	}

	@Override
	public Object serialize(T from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (sour.isPutTypes()) {
			JSONObject object = new JSONObject();
			object.put("!type", typeName);
			object.put("!enum", from.toString());
			return object;
		}
		return from.toString();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public T deserialize(Object from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (from instanceof String)
			return (T) Enum.valueOf((Class<Enum>)enumClass, (String)from);
		else if (from instanceof JSONObject)
			return (T) Enum.valueOf((Class<Enum>)enumClass, (String)((JSONObject)from).get("!enum"));
		throw new SourJsonException("Cannot deserialize a " + from.getClass().getSimpleName() + " into an enum");
	}
}