package com.github.sourjson.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;
import com.github.sourjson.annotation.SCheckForNull;
import com.github.sourjson.annotation.Since;
import com.github.sourjson.annotation.StrictType;
import com.github.sourjson.annotation.Until;
import com.github.sourjson.exception.SourJsonException;
import com.googlecode.gentyref.GenericTypeReflector;

@SuppressWarnings("unchecked")
public class ObjectTranslater<T> implements InternalTranslater<T> {

	static class FieldCache {
		String name;
		double since = -Double.MAX_VALUE;
		double until = Double.MAX_VALUE;
		Type type;
		boolean strictType;
		boolean checkForNull;
		Field field;
	}

	private List<FieldCache> fields = new ArrayList<>();
	private Class<T> fromClass;
	private String typeName;

	public ObjectTranslater(Type fromType) {
		fromClass = (Class<T>) GenericTypeReflector.erase(fromType);
		typeName = GenericTypeReflector.getTypeName(fromType);

		Type t = fromType;
		while (t != null) {
			Class<?> cls = GenericTypeReflector.erase(t);

			for (Field field : cls.getDeclaredFields()) {
				if (!SJUtils.IsJSONPrintable(field))
					continue ;

				FieldCache fc = new FieldCache();
				fc.name = SJUtils.getFieldName(field);

				Until until = field.getAnnotation(Until.class);
				if (until != null)
					fc.until = until.value();

				Since since = field.getAnnotation(Since.class);
				if (since != null)
					fc.since = since.value();

				fc.type = GenericTypeReflector.getExactFieldType(field, fromType);

				Class<?> fieldClass = GenericTypeReflector.erase(fc.type);

				if (fieldClass.isPrimitive()) {
					fieldClass = SJUtils.PRIMITIVES_TO_WRAPPERS.get(fieldClass);
					fc.type = fieldClass;
				}

				fc.strictType = field.isAnnotationPresent(StrictType.class) || Map.class.isAssignableFrom(fieldClass) || Collection.class.isAssignableFrom(fieldClass);

				fc.checkForNull = field.isAnnotationPresent(SCheckForNull.class);

				fc.field = field;

				fields.add(fc);
			}

			t = SJUtils.getParent(cls);
		}
	}

	@Override
	public Object serialize(T from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		JSONObject object = new JSONObject();

		for (FieldCache fc : fields) {
			if (version > fc.until)
				continue ;
			if (version < fc.since)
				continue ;

			fc.field.setAccessible(true);
			try {
				Object fieldValue = fc.field.get(from);
				if (fieldValue == null)
					continue ;

				Type fieldType = fieldValue.getClass();
				if (fc.strictType)
					fieldType = fc.type;

				Object json = sour.toJSON(fieldValue, fieldType, version, fc.field, from);
				((Map<String, Object>)object).put(fc.name, json);
			}
			catch (IllegalAccessException e) {
				throw new SourJsonException(e);
			}
			finally {
				fc.field.setAccessible(false);
			}

		}

		if (sour.isPutTypes())
			((Map<String, Object>)object).put("!type", typeName);

		return object;
	}

	@Override
	public T deserialize(Object from, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		if (!(from instanceof JSONObject))
			throw new SourJsonException("Cannot deserialize a " + from.getClass().getSimpleName() + " into a " + typeName);

		JSONObject fromObject = (JSONObject)from;

		Object ret = SJUtils.construct(fromClass);

		for (FieldCache fc : fields) {

			if (version > fc.until)
				continue ;
			if (version < fc.since)
				continue ;

			if (fromObject.get(fc.name) == null) {
				if (!sour.isCheckForAllowNulls() || fc.checkForNull)
					continue ;

				throw new SourJsonException("Missing (or null) JSON : " + fc.name);
			}

			Object value = sour.fromJSON(fromObject.get(fc.name), fc.type, version, fc.field, ret);
			fc.field.setAccessible(true);
			try {
				fc.field.set(ret, value);
			}
			catch (IllegalAccessException e) {
				throw new SourJsonException(e);
			}
			finally {
				fc.field.setAccessible(false);
			}
		}
		return (T)ret;
	}


}