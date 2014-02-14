package com.github.sourjson.test;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.annotations.Test;

import com.github.sourjson.SourJson;
import com.github.sourjson.annotation.DisregardParent;
import com.github.sourjson.annotation.DisregardedParent;

@SuppressWarnings({"javadoc", "static-method"})
public class ParentTest {

	static class A1 {
		int a;
		public A1(int a) {
			super();
			this.a = a;
		}
		private A1() {}
	}

	@DisregardParent
	static class B1 extends A1 {
		int b;
		public B1(int a, int b) {
			super(a);
			this.b = b;
		}
		@SuppressWarnings("unused")
		private B1() {}
	}

	@Test
	public void disregardParentFields() throws Exception {
		SourJson json = new SourJson();
		json.setCheckForAllowNulls(true);

		JSONObject ser = (JSONObject)json.toJSON(new B1(21, 42), 0);

		assert !ser.containsKey("a");
		assert ser.containsKey("b");
		assert ser.get("b").equals(Integer.valueOf(42));

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		B1 to = json.fromJSON(jsonObj, B1.class, 0);

		assert to.a == 0;
		assert to.b == 42;
	}

	@DisregardedParent
	static class A2 {
		int a;
		public A2(int a) {
			super();
			this.a = a;
		}
		private A2() {}
	}

	static class B2 extends A2 {
		int b;
		public B2(int a, int b) {
			super(a);
			this.b = b;
		}
		@SuppressWarnings("unused")
		private B2() {}
	}

	@Test
	public void disregardedParentFields() throws Exception {
		SourJson json = new SourJson();
		json.setCheckForAllowNulls(true);

		JSONObject ser = (JSONObject)json.toJSON(new B2(21, 42), 0);

		assert !ser.containsKey("a");
		assert ser.containsKey("b");
		assert ser.get("b").equals(Integer.valueOf(42));

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		B2 to = json.fromJSON(jsonObj, B2.class, 0);

		assert to.a == 0;
		assert to.b == 42;
	}

}
