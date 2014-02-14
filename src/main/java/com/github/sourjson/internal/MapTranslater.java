package com.github.sourjson.internal;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;
import com.github.sourjson.annotation.StrictType;
import com.github.sourjson.exception.SourJsonException;
import com.googlecode.gentyref.GenericTypeReflector;

@SuppressWarnings({"unchecked"})
public class MapTranslater<T> implements InternalTranslater<T> {

	private @CheckForNull Class<T> mapClass;
	private @CheckForNull Type mapKType;
	private Type mapVType;
	private boolean strictType;

	public MapTranslater(TypeAndAnnos info) {
		mapClass = (Class<T>) GenericTypeReflector.erase(info.type);
		mapKType = GenericTypeReflector.getTypeParameter(info.type, Map.class.getTypeParameters()[0]);
		mapVType = GenericTypeReflector.getTypeParameter(info.type, Map.class.getTypeParameters()[1]);
		if (mapVType == null)
			mapVType = Object.class;
		strictType = info.annos.isAnnotationPresent(StrictType.class);
	}

	@Override
	public Object serialize(T from, TypeAndAnnos info, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {
		Map<?, ?> fromMap = (Map<?, ?>)from;

		if (fromMap.isEmpty())
			return new JSONObject();

		Type fromKType = mapKType;
		if (fromKType == null)
			fromKType = SJUtils.findGenType(((Map<?, ?>)from).keySet());

		if (fromKType.equals(String.class)) {
			JSONObject obj = new JSONObject();
			Iterator<?> it = fromMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<?, ?> e = (Entry<?, ?>) it.next();
				Object json = null;
				if (e.getValue() != null) {
					Type valueType = strictType ? mapVType : e.getValue().getClass();
					json = sour.toJSON(e.getValue(), valueType, version, info.annos, from);
				}
				obj.put(e.getKey().toString(), json);
			}
			return obj;
		}

		JSONArray list = new JSONArray();
		Iterator<?> it = fromMap.entrySet().iterator();
		while (it.hasNext()) {
			JSONObject obj = new JSONObject();
			Map.Entry<?, ?> e = (Entry<?, ?>) it.next();
			Type keyType = strictType ? fromKType : e.getKey().getClass();
			Object json = sour.toJSON(e.getKey(), keyType, version, info.annos, from);
			obj.put("!k", json);
			json = null;
			if (e.getValue() != null) {
				Type valueType = strictType ? mapVType : e.getValue().getClass();
				json = sour.toJSON(e.getValue(), valueType, version, info.annos, from);
			}
			obj.put("!v", json);
			list.add(obj);
		}
		return list;
	}

	@Override
	public T deserialize(Object from, TypeAndAnnos info, @CheckForNull Object enclosing, double version, SourJson sour) throws SourJsonException {

		Map<?, ?> map;
		if (Map.class.equals(mapClass))
			map = new HashMap<>();
		else
			map = (Map<?, ?>) SJUtils.construct(mapClass);

		if (from instanceof JSONObject) {
			JSONObject fromObject = (JSONObject)from;

			for (Entry<String, ?> entry : (Set<Map.Entry<String, ?>>)fromObject.entrySet()) {
				String key = String.valueOf(entry.getKey());
				((Map<String, Object>)map).put(key, sour.fromJSON(entry.getValue(), mapVType, version, info.annos, map));
			}

			return (T)map;
		}
		else if (from instanceof JSONArray) {
			JSONArray fromArray = (JSONArray)from;

			for (Object entry : fromArray) {
				if (!(entry instanceof JSONObject))
					throw new SourJsonException("Cannot deserialize a " + entry.getClass().getSimpleName() + " into a map entry" + mapKType);
				JSONObject entryObj = (JSONObject)entry;
				if (!entryObj.containsKey("!k"))
					throw new SourJsonException("Cannot deserialize a map: JSONObject map entry is missing !k property of type " + mapKType);
				Object key = sour.fromJSON(entryObj.get("!k"), mapKType, version, info.annos, map);
				Object value = sour.fromJSON(entryObj.get("!v"), mapVType, version, info.annos, map);
				((Map<Object, Object>)map).put(key, value);
			}

			return (T)map;

		}
		else
			throw new SourJsonException("Cannot deserialize a " + from.getClass().getSimpleName() + " into a map");
	}

}