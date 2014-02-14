package com.github.sourjson.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.annotations.Test;

import com.github.sourjson.SourJson;
import com.github.sourjson.test.struct.PrimitiveBean;

@SuppressWarnings({"javadoc", "static-method"})
public class DefaultTranslatersTest {

	public static class DatedClass {
		public Date d;
		public Class<?> c;
		public DatedClass(Date d, Class<?> c) {
			this.d = d;
			this.c = c;
		}
		@SuppressWarnings("unused")
		private DatedClass() {}
	}

	@Test
	public void translatersInObject() throws Exception {
		Date date = new Date(534990102000L);
		DatedClass from = new DatedClass(date, PrimitiveBean.class);

		SourJson json = new SourJson();
		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.DefaultTranslatersTest$DatedClass");
		assert ser.containsKey("d");
		assert ser.get("d") instanceof JSONObject;
		assert ((JSONObject)ser.get("d")).containsKey("!type");
		assert ((JSONObject)ser.get("d")).get("!type").equals("java.util.Date");
		assert ((JSONObject)ser.get("d")).containsKey("GMT");
		assert ((JSONObject)ser.get("d")).get("GMT").equals("1986-12-15 00:21:42");
		assert ser.containsKey("c");
		assert ((JSONObject)ser.get("c")).containsKey("!type");
		assert ((JSONObject)ser.get("c")).get("!type").equals("java.lang.Class");
		assert ((JSONObject)ser.get("c")).containsKey("name");
		assert ((JSONObject)ser.get("c")).get("name").equals("com.github.sourjson.test.struct.PrimitiveBean");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		DatedClass to = json.fromJSON(jsonObj, DatedClass.class, 0);

		assert to.d.equals(date);
		assert to.c.equals(PrimitiveBean.class);
	}

	@Test
	public void translatersInMap() throws Exception {
		Map<String, Object> from = new HashMap<>();
		Date date = new Date(534990102000L);
		from.put("d", date);
		from.put("c", PrimitiveBean.class);

		SourJson json = new SourJson();
		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ser.containsKey("d");
		assert ser.get("d") instanceof JSONObject;
		assert ((JSONObject)ser.get("d")).containsKey("!type");
		assert ((JSONObject)ser.get("d")).get("!type").equals("java.util.Date");
		assert ((JSONObject)ser.get("d")).containsKey("GMT");
		assert ((JSONObject)ser.get("d")).get("GMT").equals("1986-12-15 00:21:42");
		assert ser.containsKey("c");
		assert ((JSONObject)ser.get("c")).containsKey("!type");
		assert ((JSONObject)ser.get("c")).get("!type").equals("java.lang.Class");
		assert ((JSONObject)ser.get("c")).containsKey("name");
		assert ((JSONObject)ser.get("c")).get("name").equals("com.github.sourjson.test.struct.PrimitiveBean");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);

		Map<String, Object> to = json.fromJSON(jsonObj, Map.class, 0);

		assert to.get("d") instanceof Date;
		assert to.get("d").equals(date);
		assert to.get("c") instanceof Class;
		assert to.get("c").equals(PrimitiveBean.class);
	}

	@Test
	public void translatersInCollection() throws Exception {
		List<Object> from = new ArrayList<>(2);
		Date date = new Date(534990102000L);
		from.add(date);
		from.add(PrimitiveBean.class);

		SourJson json = new SourJson();
		JSONArray ser = (JSONArray)json.toJSON(from, 0);

		assert ser.size() == 2;
		assert ser.get(0) instanceof JSONObject;
		assert ((JSONObject)ser.get(0)).containsKey("!type");
		assert ((JSONObject)ser.get(0)).get("!type").equals("java.util.Date");
		assert ((JSONObject)ser.get(0)).containsKey("GMT");
		assert ((JSONObject)ser.get(0)).get("GMT").equals("1986-12-15 00:21:42");
		assert ((JSONObject)ser.get(1)).containsKey("!type");
		assert ((JSONObject)ser.get(1)).get("!type").equals("java.lang.Class");
		assert ((JSONObject)ser.get(1)).containsKey("name");
		assert ((JSONObject)ser.get(1)).get("name").equals("com.github.sourjson.test.struct.PrimitiveBean");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);

		List<Object> to = json.fromJSON(jsonObj, List.class, 0);

		assert to.get(0) instanceof Date;
		assert to.get(0).equals(date);
		assert to.get(1) instanceof Class;
		assert to.get(1).equals(PrimitiveBean.class);
	}

}
