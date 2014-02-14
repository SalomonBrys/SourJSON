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
import com.github.sourjson.annotation.Exclude;
import com.github.sourjson.annotation.Since;
import com.github.sourjson.annotation.StrictType;
import com.github.sourjson.annotation.Until;
import com.github.sourjson.exception.SourJsonException;
import com.github.sourjson.test.struct.DatedSimpleBean;
import com.github.sourjson.test.struct.SimpleBean;

@SuppressWarnings({"javadoc", "static-method"})
public class FieldAnnotationsTest {

	public static class IgnoredBean {
		public static String first = "...";
		public @Exclude String second;
		public transient String third;
		public String fourth;
		public IgnoredBean(String second, String third, String fourth) {
			super();
			this.second = second;
			this.third = third;
			this.fourth = fourth;
		}
		@SuppressWarnings("unused")
		private IgnoredBean() {}
	}

	public static class StrictTypeContainer {
		@StrictType SimpleBean bean;

		@StrictType Map<String, SimpleBean> stringMap = new HashMap<>();

		@StrictType Map<Integer, SimpleBean> intMap = new HashMap<>();

		@StrictType List<SimpleBean> list = new ArrayList<>();

		@StrictType SimpleBean[] array = new SimpleBean[1];

		@SuppressWarnings("unused")
		private StrictTypeContainer() {}

		public StrictTypeContainer(SimpleBean bean, SimpleBean inStringMap, SimpleBean inIntMap, SimpleBean inList, SimpleBean inArray) {
			super();
			this.bean = bean;
			this.stringMap.put("one", inStringMap);
			this.intMap.put(Integer.valueOf(42), inIntMap);
			this.list.add(inList);
			array[0] = inArray;
		}
	}

	@Test
	public void ignoredFields() throws Exception {
		SourJson json = new SourJson();
		json.setCheckForAllowNulls(true);

		JSONObject ser = (JSONObject)json.toJSON(new IgnoredBean("I'm exculded", "I'm transient", "I'm here!"), 0);

		assert !ser.containsKey("first");
		assert !ser.containsKey("second");
		assert !ser.containsKey("third");
		assert ser.containsKey("fourth");

		IgnoredBean.first = "I'm a static";

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		IgnoredBean to = json.fromJSON(jsonObj, IgnoredBean.class, 0);

		assert IgnoredBean.first.equals("I'm a static");
		assert to.second == null;
		assert to.third == null;
		assert to.fourth.equals("I'm here!");
	}

	public static class Versionned {
		@Until(0.9)
		private int first;

		@Until(1.0)
		private int second;

		@Since(1.0)
		private int third;

		@Since(1.1)
		private int fourth;

		@SuppressWarnings("unused")
		private Versionned() {}

		public Versionned(int first, int second, int third, int fourth) {
			super();
			this.first = first;
			this.second = second;
			this.third = third;
			this.fourth = fourth;
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void sinceUntil() throws Exception {
		SourJson json = new SourJson();

		JSONObject ser_08 = (JSONObject)json.toJSON(new Versionned(21, 42, 63, 84), 0.8);
		assert ser_08.containsKey("first");
		assert ser_08.containsKey("second");
		assert !ser_08.containsKey("third");
		assert !ser_08.containsKey("fourth");
		ser_08.put("third", Integer.valueOf(63));
		ser_08.put("fourth", Integer.valueOf(84));
		String jsonStr_08 = ser_08.toJSONString();
		Object jsonObj_08 = JSONValue.parse(jsonStr_08);
		Versionned to_08 = json.fromJSON(jsonObj_08, Versionned.class, 0.8);
		assert to_08.first == 21;
		assert to_08.second == 42;
		assert to_08.third == 0;
		assert to_08.fourth == 0;

		JSONObject ser_10 = (JSONObject)json.toJSON(new Versionned(21, 42, 63, 84), 1.0);
		assert !ser_10.containsKey("first");
		assert ser_10.containsKey("second");
		assert ser_10.containsKey("third");
		assert !ser_10.containsKey("fourth");
		ser_10.put("first", Integer.valueOf(21));
		ser_10.put("fourth", Integer.valueOf(84));
		String jsonStr_10 = ser_10.toJSONString();
		Object jsonObj_10 = JSONValue.parse(jsonStr_10);
		Versionned to_10 = json.fromJSON(jsonObj_10, Versionned.class, 1.0);
		assert to_10.first == 0;
		assert to_10.second == 42;
		assert to_10.third == 63;
		assert to_10.fourth == 0;

		JSONObject ser_12 = (JSONObject)json.toJSON(new Versionned(21, 42, 63, 84), 1.2);
		assert !ser_12.containsKey("first");
		assert !ser_12.containsKey("second");
		assert ser_12.containsKey("third");
		assert ser_12.containsKey("fourth");
		ser_12.put("first", Integer.valueOf(21));
		ser_12.put("second", Integer.valueOf(42));
		String jsonStr_12 = ser_12.toJSONString();
		Object jsonObj_12 = JSONValue.parse(jsonStr_12);
		Versionned to_12 = json.fromJSON(jsonObj_12, Versionned.class, 1.2);
		assert to_12.first == 0;
		assert to_12.second == 0;
		assert to_12.third == 63;
		assert to_12.fourth == 84;
	}


	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot deserialize .*")
	public void cannotTransform() throws Exception {
		SourJson json = new SourJson();
		json.fromJSON(Integer.valueOf(42), SimpleBean.class, 0);
	}

	@Test
	public void strictTyping() throws Exception {
		SourJson json = new SourJson();

		StrictTypeContainer from = new StrictTypeContainer(
				new DatedSimpleBean("Salomon", 21, new Date()),
				new DatedSimpleBean("Salomon", 42, new Date()),
				new DatedSimpleBean("Salomon", 63, new Date()),
				new DatedSimpleBean("Salomon", 84, new Date()),
				new DatedSimpleBean("Salomon", 105, new Date())
		);

		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ((JSONObject)ser.get("bean")).get("!type").equals("com.github.sourjson.test.struct.SimpleBean");
		assert ((JSONObject)((JSONObject)ser.get("stringMap")).get("one")).get("!type").equals("com.github.sourjson.test.struct.SimpleBean");
		assert ((JSONObject)((JSONObject)((JSONArray)ser.get("intMap")).get(0)).get("!v")).get("!type").equals("com.github.sourjson.test.struct.SimpleBean");
		assert ((JSONObject)((JSONArray)ser.get("list")).get(0)).get("!type").equals("com.github.sourjson.test.struct.SimpleBean");
		assert ((JSONObject)((JSONArray)ser.get("array")).get(0)).get("!type").equals("com.github.sourjson.test.struct.SimpleBean");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		StrictTypeContainer to = json.fromJSON(jsonObj, StrictTypeContainer.class, 0);

		assert !(to.bean instanceof DatedSimpleBean);
		assert to.bean.getClass().equals(SimpleBean.class);
		assert !(to.stringMap.get("one") instanceof DatedSimpleBean);
		assert to.stringMap.get("one").getClass().equals(SimpleBean.class);
		assert !(to.intMap.get(Integer.valueOf(42)) instanceof DatedSimpleBean);
		assert to.intMap.get(Integer.valueOf(42)).getClass().equals(SimpleBean.class);
		assert !(to.list.get(0) instanceof DatedSimpleBean);
		assert to.list.get(0).getClass().equals(SimpleBean.class);
		assert !(to.array[0] instanceof DatedSimpleBean);
		assert to.array[0].getClass().equals(SimpleBean.class);
	}

	public static class UnstrictTypeContainer {
		SimpleBean bean;

		Map<String, SimpleBean> map = new HashMap<>();

		List<SimpleBean> list = new ArrayList<>();

		SimpleBean[] array = new SimpleBean[1];

		@SuppressWarnings("unused")
		private UnstrictTypeContainer() {}

		public UnstrictTypeContainer(SimpleBean bean, SimpleBean inMap, SimpleBean inList, SimpleBean inArray) {
			super();
			this.bean = bean;
			this.map.put("one", inMap);
			this.list.add(inList);
			array[0] = inArray;
		}
	}

	@Test
	public void unstrictTyping() throws Exception {
		SourJson json = new SourJson();

		UnstrictTypeContainer from = new UnstrictTypeContainer(
				new DatedSimpleBean("Salomon", 21, new Date()),
				new DatedSimpleBean("Salomon", 42, new Date()),
				new DatedSimpleBean("Salomon", 63, new Date()),
				new DatedSimpleBean("Salomon", 84, new Date())
		);

		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ((JSONObject)ser.get("bean")).get("!type").equals("com.github.sourjson.test.struct.DatedSimpleBean");
		assert ((JSONObject)((JSONObject)ser.get("map")).get("one")).get("!type").equals("com.github.sourjson.test.struct.DatedSimpleBean");
		assert ((JSONObject)((JSONArray)ser.get("list")).get(0)).get("!type").equals("com.github.sourjson.test.struct.DatedSimpleBean");
		assert ((JSONObject)((JSONArray)ser.get("array")).get(0)).get("!type").equals("com.github.sourjson.test.struct.DatedSimpleBean");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		UnstrictTypeContainer to = json.fromJSON(jsonObj, UnstrictTypeContainer.class, 0);

		assert to.bean.getClass().equals(DatedSimpleBean.class);
		assert to.map.get("one").getClass().equals(DatedSimpleBean.class);
		assert to.list.get(0).getClass().equals(DatedSimpleBean.class);
		assert to.array[0].getClass().equals(DatedSimpleBean.class);
	}

	@Test
	public void checkForNullSuccess() throws Exception {
		SourJson json = new SourJson();
		json.setCheckForAllowNulls(true);

		JSONObject ser = (JSONObject)json.toJSON(new DatedSimpleBean("Salomon", 42, null), 0);

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		DatedSimpleBean to = json.fromJSON(jsonObj, DatedSimpleBean.class, 0);

		assert to.name.equals("Salomon");
		assert to.value == 42;
		assert to.date == null;
	}

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Missing \\(or null\\) JSON : .*")
	public void checkForNullFail() throws Exception {
		SourJson json = new SourJson();
		json.setCheckForAllowNulls(true);

		JSONObject ser = (JSONObject)json.toJSON(new DatedSimpleBean(null, 42, null), 0);

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		json.fromJSON(jsonObj, DatedSimpleBean.class, 0);
	}

}
