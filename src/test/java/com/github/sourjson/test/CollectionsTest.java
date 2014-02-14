package com.github.sourjson.test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.SourJsonException;
import com.github.sourjson.test.struct.SimpleBean;
import com.googlecode.gentyref.TypeToken;

@SuppressWarnings({"javadoc", "static-method"})
public class CollectionsTest {

	@Test
	public void primitiveArrays() throws Exception {
		int[] from_i = {21, 42, 63, 84};
		String[][] from_s = {
			{"foo", "bar"},
			{"Salomon", "So", "Schlomo", null},
			null
		};

		SourJson json = new SourJson();
		JSONArray ser_i = (JSONArray)json.toJSON(from_i, 0);
		JSONArray ser_s = (JSONArray)json.toJSON(from_s, 0);

		assert ser_i.size() == 4;
		assert !ser_i.contains("!type");
		assert ser_i.get(0).equals(Integer.valueOf(21));
		assert ser_i.get(1).equals(Integer.valueOf(42));
		assert ser_i.get(2).equals(Integer.valueOf(63));
		assert ser_i.get(3).equals(Integer.valueOf(84));

		assert ser_s.size() == 3;
		assert !ser_s.contains("!type");

		assert ((JSONArray)ser_s.get(0)).size() == 2;
		assert !((JSONArray)ser_s.get(0)).contains("!type");
		assert ((JSONArray)ser_s.get(0)).get(0).equals("foo");
		assert ((JSONArray)ser_s.get(0)).get(1).equals("bar");

		assert ((JSONArray)ser_s.get(1)).size() == 4;
		assert !((JSONArray)ser_s.get(1)).contains("!type");
		assert ((JSONArray)ser_s.get(1)).get(0).equals("Salomon");
		assert ((JSONArray)ser_s.get(1)).get(1).equals("So");
		assert ((JSONArray)ser_s.get(1)).get(2).equals("Schlomo");
		assert ((JSONArray)ser_s.get(1)).get(3) == null;

		assert ser_s.get(2) == null;

		String jsonStr_i = ser_i.toJSONString();
		Object jsonObj_i = JSONValue.parse(jsonStr_i);
		int[] to_i = json.fromJSON(jsonObj_i, int[].class, 0);

		String jsonStr_s = ser_s.toJSONString();
		Object jsonObj_s = JSONValue.parse(jsonStr_s);
		String[][] to_s = json.fromJSON(jsonObj_s, String[][].class, 0);

		assert to_i.length == 4;
		assert to_i[0] == 21;
		assert to_i[1] == 42;
		assert to_i[2] == 63;
		assert to_i[3] == 84;

		assert to_s.length == 3;

		assert to_s[0].length == 2;
		assert to_s[0][0].equals("foo");
		assert to_s[0][1].equals("bar");

		assert to_s[1].length == 4;
		assert to_s[1][0].equals("Salomon");
		assert to_s[1][1].equals("So");
		assert to_s[1][2].equals("Schlomo");
		assert to_s[1][3] == null;
		assert to_s[2] == null;
	}


	@Test
	public void objectArrays() throws Exception {
		SimpleBean[] from = new SimpleBean[] {
			new SimpleBean(null, 21),
			new SimpleBean("Salomon", 42),
			new SimpleBean("Brys", 63),
			null
		};

		SourJson json = new SourJson();
		JSONArray ser = (JSONArray)json.toJSON(from, 0);

		assert ser.size() == 4;
		assert !((JSONObject)ser.get(0)).containsKey("name");
		assert ((JSONObject)ser.get(0)).get("count").equals(Integer.valueOf(21));
		assert ((JSONObject)ser.get(1)).get("name").equals("Salomon");
		assert ((JSONObject)ser.get(1)).get("count").equals(Integer.valueOf(42));
		assert ((JSONObject)ser.get(2)).get("name").equals("Brys");
		assert ((JSONObject)ser.get(2)).get("count").equals(Integer.valueOf(63));
		assert ser.get(3) == null;

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		SimpleBean[] to = json.fromJSON(jsonObj, SimpleBean[].class, 0);

		assert to.length == 4;
		assert to[0].name == null;
		assert to[0].value == 21;
		assert to[1].name.equals("Salomon");
		assert to[1].value == 42;
		assert to[2].name.equals("Brys");
		assert to[2].value == 63;
		assert to[3] == null;
	}

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot deserialize .*")
	public void arrayNotCorrect() throws Exception {
		SourJson json = new SourJson();
		json.fromJSON(JSONValue.parse("42"), int[].class, 0);
	}

	@DataProvider(name = "collections")
	public Object[][] collectionsProvider() {
		SimpleBean[] from = new SimpleBean[] {
				new SimpleBean(null, 21),
				new SimpleBean("Salomon", 42),
				new SimpleBean("Brys", 63)
			};

		List<SimpleBean> l = new ArrayList<>(Arrays.asList(from));
		l.add(null);
		return new Object[][] {
			{ l, List.class },
			{ l, ArrayList.class },
			{ new LinkedHashSet<>(Arrays.asList(from)), Set.class },
			{ new ArrayDeque<>(Arrays.asList(from)), Queue.class }
		};
	}

	@Test(dataProvider = "collections")
	public void collections(Collection<SimpleBean> from, Class<? extends Collection<SimpleBean>> cls) throws Exception {
		int size = from.size();

		SourJson json = new SourJson();
		JSONArray ser = (JSONArray)json.toJSON(from, 0);

		assert ser.size() == size;
		assert !((JSONObject)ser.get(0)).containsKey("name");
		assert ((JSONObject)ser.get(0)).get("count").equals(Integer.valueOf(21));
		assert ((JSONObject)ser.get(1)).get("name").equals("Salomon");
		assert ((JSONObject)ser.get(1)).get("count").equals(Integer.valueOf(42));
		assert ((JSONObject)ser.get(2)).get("name").equals("Brys");
		assert ((JSONObject)ser.get(2)).get("count").equals(Integer.valueOf(63));
		if (size == 4)
			assert ser.get(3) == null;

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		Collection<SimpleBean> to = json.fromJSON(jsonObj, cls, 0);

		assert to.size() == size;
		assert to.contains(new SimpleBean(null, 21));
		assert to.contains(new SimpleBean("Salomon", 42));
		assert to.contains(new SimpleBean("Brys", 63));
		if (size == 4)
			assert to.contains(null);
	}

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot deserialize .*")
	public void collectionNotCorrect() throws Exception {
		SourJson json = new SourJson();
		json.fromJSON(JSONValue.parse("42"), new TypeToken<List<Integer>>(){/**/}.getType(), 0);
	}

	@DataProvider(name = "maps")
	public Object[][] mapsProvider() {
		HashMap<String, SimpleBean> map = new HashMap<>();
		map.put("first", new SimpleBean(null, 21));
		map.put("second", new SimpleBean("Salomon", 42));
		map.put("third", new SimpleBean("Brys", 63));
		map.put("fourth", null);

		return new Object[][] {
			{ map, Map.class },
			{ new LinkedHashMap<>(map), LinkedHashMap.class },
			{ new TreeMap<>(map), TreeMap.class }
		};
	}

	@Test(dataProvider = "maps")
	public void maps(Map<String, SimpleBean> from, Class<? extends Map<String, SimpleBean>> cls) throws Exception {
		SourJson json = new SourJson();
		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ser.size() == 4;
		assert !((JSONObject)ser.get("first")).containsKey("name");
		assert ((JSONObject)ser.get("first")).get("count").equals(Integer.valueOf(21));
		assert ((JSONObject)ser.get("second")).get("name").equals("Salomon");
		assert ((JSONObject)ser.get("second")).get("count").equals(Integer.valueOf(42));
		assert ((JSONObject)ser.get("third")).get("name").equals("Brys");
		assert ((JSONObject)ser.get("third")).get("count").equals(Integer.valueOf(63));
		assert ser.containsKey("fourth");
		assert ser.get("fourth") == null;

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		Map<String, SimpleBean> to = json.fromJSON(jsonObj, cls, 0);

		assert to.size() == 4;
		assert to.get("first").equals(new SimpleBean(null, 21));
		assert to.get("second").equals(new SimpleBean("Salomon", 42));
		assert to.get("third").equals(new SimpleBean("Brys", 63));
		assert to.containsKey("fourth");
		assert to.get("fourth") == null;
	}

	@Test
	public void primitiveMap() throws Exception {
		SourJson json = new SourJson();

		Map<Integer, String> from = new HashMap<>();
		from.put(Integer.valueOf(21), null);
		from.put(Integer.valueOf(42), "Salomon");
		from.put(Integer.valueOf(63), "Brys");

		JSONArray ser = (JSONArray)json.toJSON(from, 0);

		assert ser.size() == 3;
		assert ((JSONObject)ser.get(0)).get("!k").equals(Integer.valueOf(21));
		assert ((JSONObject)ser.get(0)).get("!v") == null;
		assert ((JSONObject)ser.get(1)).get("!k").equals(Integer.valueOf(42));
		assert ((JSONObject)ser.get(1)).get("!v").equals("Salomon");
		assert ((JSONObject)ser.get(2)).get("!k").equals(Integer.valueOf(63));
		assert ((JSONObject)ser.get(2)).get("!v").equals("Brys");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		Map<Integer, String> to = json.fromJSON(jsonObj, new TypeToken<Map<Integer, String>>(){/**/}.getType(), 0);

		assert to.size() == 3;
		assert to.containsKey(Integer.valueOf(21));
		assert to.get(Integer.valueOf(21)) == null;
		assert to.get(Integer.valueOf(42)).equals("Salomon");
		assert to.get(Integer.valueOf(63)).equals("Brys");
	}

	@Test
	public void emptyMap() throws Exception {
		SourJson json = new SourJson();
		JSONObject ser = (JSONObject)json.toJSON(new HashMap<>(), 0);
		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		Map<Object, Object> to = json.fromJSON(jsonObj, new TypeToken<Map<Object, Object>>(){/**/}.getType(), 0);
		assert to != null;
		assert to.isEmpty();
	}

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot deserialize .*")
	public void mapArrayNotCorrect() throws Exception {
		SourJson json = new SourJson();
		json.fromJSON(JSONValue.parse("[\"a\", \"b\"]"), new TypeToken<Map<Integer, String>>(){/**/}.getType(), 0);
	}

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot deserialize .*")
	public void mapArrayNoKey() throws Exception {
		SourJson json = new SourJson();
		json.fromJSON(JSONValue.parse("[ { \"test\": \"value\"} ]"), new TypeToken<Map<Integer, String>>(){/**/}.getType(), 0);
	}

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot deserialize .*")
	public void mapNotCorrect() throws Exception {
		SourJson json = new SourJson();
		json.fromJSON("Coucou", new TypeToken<Map<Integer, String>>(){/**/}.getType(), 0);
	}
}
