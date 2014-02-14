package com.github.sourjson.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.github.sourjson.SourJson;
import com.github.sourjson.test.struct.SimpleBean;

@SuppressWarnings({"javadoc", "static-method"})
public class NullEmptyTest {

	@Test
	public void nulls() throws Exception {
		SourJson json = new SourJson();

		assert json.toJSON(null, 0) == null;
		assert json.toJSON(null, SimpleBean.class, 0) == null;
		assert json.fromJSON(null, SimpleBean.class, 0) == null;

		assert json.toJSON(new Object(), Void.class, 0) == null;
	}


	static class EmptyCollections {
		HashMap<Object, Object> map;
		ArrayList<Object> list;
		String[] array;
		public EmptyCollections(HashMap<Object, Object> map, ArrayList<Object> list, String[] array) {
			super();
			this.map = map;
			this.list = list;
			this.array = array;
		}
		@SuppressWarnings("unused")
		private EmptyCollections() {}
	}

//	@Test
//	public void allowEmptyInObject() throws Exception {
//		SourJson json = new SourJson();
//
//		JSONObject ser_y = (JSONObject)json.toJSON(new EmptyCollections(new HashMap<>(0), new ArrayList<>(0), new String[0]), EmptyCollections.class, 0, AllowEmpty.YES);
//
//		assert ser_y.containsKey("map");
//		assert ser_y.containsKey("list");
//
//		String jsonStr_y = ser_y.toJSONString();
//		Object jsonObj_y = JSONValue.parse(jsonStr_y);
//		EmptyCollections to_y = json.fromJSON(jsonObj_y, EmptyCollections.class, 0);
//
//		assert to_y.map != null;
//		assert to_y.map.isEmpty();
//		assert to_y.list != null;
//		assert to_y.list.isEmpty();
//		assert to_y.array != null;
//		assert to_y.array.length == 0;
//
//		JSONObject ser_r = (JSONObject)json.toJSON(new EmptyCollections(new HashMap<>(0), new ArrayList<>(0), new String[0]), EmptyCollections.class, 0, AllowEmpty.ROOT);
//
//		assert !ser_r.containsKey("map");
//		assert !ser_r.containsKey("list");
//
//		String jsonStr_r = ser_r.toJSONString();
//		Object jsonObj_r = JSONValue.parse(jsonStr_r);
//		EmptyCollections to_r = json.fromJSON(jsonObj_r, EmptyCollections.class, 0);
//
//		assert to_r.map == null;
//		assert to_r.list == null;
//		assert to_r.array == null;
//
//		JSONObject ser_n = (JSONObject)json.toJSON(new EmptyCollections(new HashMap<>(0), new ArrayList<>(0), new String[0]), EmptyCollections.class, 0, AllowEmpty.NO);
//
//		assert !ser_n.containsKey("map");
//		assert !ser_n.containsKey("list");
//
//		String jsonStr_n = ser_n.toJSONString();
//		Object jsonObj_n = JSONValue.parse(jsonStr_n);
//		EmptyCollections to_n = json.fromJSON(jsonObj_n, EmptyCollections.class, 0);
//
//		assert to_n.map == null;
//		assert to_n.list == null;
//		assert to_n.array == null;
//	}
//
//
//	@SuppressWarnings("serial")
//	@Test
//	public void allowEmptyInCollections() throws Exception {
//		SourJson json = new SourJson();
//
//		assert json.toJSON(new ArrayList<String>(0), List.class, 0, AllowEmpty.NO) == null;
//		assert json.toJSON(new HashMap<String, String>(0), HashMap.class, 0, AllowEmpty.NO) == null;
//		assert json.toJSON(new HashMap<Integer, String>(0), HashMap.class, 0, AllowEmpty.NO) == null;
//		assert json.toJSON(new String[0], String[].class, 0, AllowEmpty.NO) == null;
//
//		assert json.toJSON(new ArrayList<String>(0), List.class, 0, AllowEmpty.YES) != null;
//		assert json.toJSON(new HashMap<String, String>(0), HashMap.class, 0, AllowEmpty.YES) != null;
//		assert json.toJSON(new HashMap<Integer, String>(0), HashMap.class, 0, AllowEmpty.YES) != null;
//		assert json.toJSON(new String[0], String[].class, 0, AllowEmpty.YES) != null;
//
//		JSONArray ser_l = (JSONArray)json.toJSON(new ArrayList<List<String>>(){{ add(new ArrayList<String>(0)); }}, List.class, 0, AllowEmpty.ROOT);
//		assert ser_l != null;
//		assert ser_l.isEmpty();
//
//		JSONObject ser_ms = (JSONObject)json.toJSON(new HashMap<String, List<String>>(){{ put("test", new ArrayList<String>(0)); }}, Map.class, 0, AllowEmpty.ROOT);
//		assert ser_ms != null;
//		assert ser_ms.isEmpty();
//
//		JSONArray ser_mi = (JSONArray)json.toJSON(new HashMap<Integer, List<String>>(){{ put(Integer.valueOf(42), new ArrayList<String>(0)); }}, Map.class, 0, AllowEmpty.ROOT);
//		assert ser_mi != null;
//		assert ser_mi.isEmpty();
//
//		JSONArray ser_a = (JSONArray)json.toJSON(new String[0], String[].class, 0, AllowEmpty.ROOT);
//		assert ser_a != null;
//		assert ser_a.isEmpty();
//
//		JSONArray ser_ne_l = (JSONArray)json.toJSON(new ArrayList<String>(){{ add("hi"); add(null); }}, List.class, 0, AllowEmpty.NO);
//		assert ser_ne_l != null;
//		assert ser_ne_l.size() == 1;
//
//		JSONObject ser_ne_ms = (JSONObject)json.toJSON(new HashMap<String, String>(){{ put("hi", "hello"); put("yo", null); }}, Map.class, 0, AllowEmpty.NO);
//		assert ser_ne_ms != null;
//		assert ser_ne_ms.size() == 1;
//
//		JSONArray ser_ne_mi = (JSONArray)json.toJSON(new HashMap<Integer, String>(){{ put(Integer.valueOf(21), "hello"); put(Integer.valueOf(42), null); }}, Map.class, 0, AllowEmpty.NO);
//		assert ser_ne_mi != null;
//		assert ser_ne_mi.size() == 1;
//
//		JSONArray ser_ne_a = (JSONArray)json.toJSON(new String[] { "hi", null }, String[].class, 0, AllowEmpty.NO);
//		assert ser_ne_a != null;
//		assert ser_ne_a.size() == 1;
//
//		JSONArray ser_n_l = (JSONArray)json.toJSON(new ArrayList<String>(){{ add("hi"); add(null); }}, List.class, 0, AllowEmpty.YES);
//		assert ser_n_l != null;
//		assert ser_n_l.size() == 2;
//		assert ser_n_l.get(1) == null;
//
//		JSONObject ser_n_ms = (JSONObject)json.toJSON(new HashMap<String, String>(){{ put("hi", null); }}, Map.class, 0, AllowEmpty.YES);
//		assert ser_n_ms != null;
//		assert ser_n_ms.size() == 1;
//		assert ser_n_ms.get("hi") == null;
//
//		JSONArray ser_n_mi = (JSONArray)json.toJSON(new HashMap<Integer, String>(){{ put(Integer.valueOf(42), null); }}, Map.class, 0, AllowEmpty.YES);
//		assert ser_n_mi != null;
//		assert ser_n_mi.size() == 1;
//		assert ((JSONObject)ser_n_mi.get(0)).size() == 2;
//		assert ((JSONObject)ser_n_mi.get(0)).get("!v") == null;
//
//		JSONArray ser_n_a = (JSONArray)json.toJSON(new String[] { "hi", null }, String[].class, 0, AllowEmpty.YES);
//		assert ser_n_a != null;
//		assert ser_n_a.size() == 2;
//		assert ser_n_l.get(1) == null;
//	}


}
