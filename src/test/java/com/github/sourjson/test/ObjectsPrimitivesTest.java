package com.github.sourjson.test;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;
import org.testng.annotations.Test;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.SourJsonException;
import com.github.sourjson.test.struct.PrimitiveBean;
import com.github.sourjson.test.struct.SimpleBean;

@SuppressWarnings({"javadoc", "static-method"})
public class ObjectsPrimitivesTest {

	public static class IncompleteBean extends SimpleBean {
		public Date date;
		public IncompleteBean(String name, int value, Date date) {
			super(name, value);
			this.date = date;
		}
	}

	public static class Dreamer {
		public Dreamer host;
		public String name;
		public Dreamer(Dreamer d, String name) {
			this.host = d;
			this.name = name;
		}
		@SuppressWarnings("unused")
		private Dreamer() {}
	}

	public static class Plane {
		public Dreamer main;
		public int passengers;
		public Plane(Dreamer d, int number) {
			this.main = d;
			this.passengers = number;
		}
		@SuppressWarnings("unused")
		private Plane() {}
	}

	@Test
	public void primitives() throws Exception {
		PrimitiveBean from = new PrimitiveBean(
				(byte)21,		Byte.valueOf((byte)42),
				(short)63,		Short.valueOf((short)84),
				105,			Integer.valueOf(126),
				147L,			Long.valueOf(168L),
				(float)189.21,	Float.valueOf((float)210.42),
				231.63,			Double.valueOf(252.84),
				true,			Boolean.FALSE,
				'!',			Character.valueOf('B'),
								"hello, world!");

		SourJson json = new SourJson();
		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.struct.PrimitiveBean");
		assert ser.containsKey("pby");
		assert ser.get("pby").equals(Byte.valueOf((byte)21));
		assert ser.containsKey("oby");
		assert ser.get("oby").equals(Byte.valueOf((byte)42));
		assert ser.containsKey("psh");
		assert ser.get("psh").equals(Short.valueOf((short)63));
		assert ser.containsKey("osh");
		assert ser.get("osh").equals(Short.valueOf((short)84));
		assert ser.containsKey("pin");
		assert ser.get("pin").equals(Integer.valueOf(105));
		assert ser.containsKey("oin");
		assert ser.get("oin").equals(Integer.valueOf(126));
		assert ser.containsKey("plo");
		assert ser.get("plo").equals(Long.valueOf(147L));
		assert ser.containsKey("olo");
		assert ser.get("olo").equals(Long.valueOf(168L));
		assert ser.containsKey("pfl");
		assert ser.get("pfl").equals(Float.valueOf((float)189.21));
		assert ser.containsKey("ofl");
		assert ser.get("ofl").equals(Float.valueOf((float)210.42));
		assert ser.containsKey("pdo");
		assert ser.get("pdo").equals(Double.valueOf(231.63));
		assert ser.containsKey("odo");
		assert ser.get("odo").equals(Double.valueOf(252.84));
		assert ser.containsKey("pbo");
		assert ser.get("pbo").equals(Boolean.valueOf(true));
		assert ser.containsKey("obo");
		assert ser.get("obo").equals(Boolean.FALSE);
		assert ser.containsKey("pch");
		assert ser.get("pch").equals("!");
		assert ser.containsKey("och");
		assert ser.get("och").equals("B");
		assert ser.containsKey("str");
		assert ser.get("str").equals("hello, world!");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		PrimitiveBean to = json.fromJSON(jsonObj, PrimitiveBean.class, 0);

		assert to.pby == (byte)21;
		assert to.oby.equals(Byte.valueOf((byte)42));
		assert to.psh == (short)63;
		assert to.osh.equals(Short.valueOf((short)84));
		assert to.pin == 105;
		assert to.oin.equals(Integer.valueOf(126));
		assert to.plo == 147L;
		assert to.olo.equals(Long.valueOf(168L));
		assert to.pfl == (float)189.21;
		assert to.ofl.equals(Float.valueOf((float)210.42));
		assert to.pdo == 231.63;
		assert to.odo.equals(Double.valueOf(252.84));
		assert to.pbo == true;
		assert to.obo.equals(Boolean.FALSE);
		assert to.pch == '!';
		assert to.och.equals(Character.valueOf('B'));
		assert to.str.equals("hello, world!");
	}

	@Test
	public void numberToChar() throws Exception {
		SourJson json = new SourJson();

		char to = json.fromJSON(Integer.valueOf(42), char.class, 0).charValue();

		assert to == '*';
	}

	@Test
	public void embeding() throws Exception {
		Plane from = new Plane(new Dreamer(new Dreamer(new Dreamer(null, "Eames"), "Arthur"), "Yusuf"), 7);

		SourJson json = new SourJson();
		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.ObjectsPrimitivesTest$Plane");
		assert ser.containsKey("passengers");
		assert ser.get("passengers").equals(Integer.valueOf(7));
		assert ser.containsKey("main");
		JSONObject dreamer = (JSONObject)ser.get("main");
		assert dreamer.containsKey("!type");
		assert dreamer.get("!type").equals("com.github.sourjson.test.ObjectsPrimitivesTest$Dreamer");
		assert dreamer.containsKey("name");
		assert dreamer.get("name").equals("Yusuf");
		assert dreamer.containsKey("host");
		dreamer = (JSONObject)dreamer.get("host");
		assert dreamer.containsKey("!type");
		assert dreamer.get("!type").equals("com.github.sourjson.test.ObjectsPrimitivesTest$Dreamer");
		assert dreamer.containsKey("name");
		assert dreamer.get("name").equals("Arthur");
		assert dreamer.containsKey("host");
		dreamer = (JSONObject)dreamer.get("host");
		assert dreamer.containsKey("!type");
		assert dreamer.get("!type").equals("com.github.sourjson.test.ObjectsPrimitivesTest$Dreamer");
		assert dreamer.containsKey("name");
		assert dreamer.get("name").equals("Eames");
		assert !dreamer.containsKey("host");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		Plane to = json.fromJSON(jsonObj, Plane.class, 0);

		assert to.passengers == 7;
		assert to.main.name.equals("Yusuf");
		assert to.main.host.name.equals("Arthur");
		assert to.main.host.host.name.equals("Eames");
		assert to.main.host.host.host == null;
	}

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot construct class .*")
	public void cannotConstruct() throws Exception {
		SourJson json = new SourJson();
		json.setCheckForAllowNulls(true);

		JSONObject ser = (JSONObject)json.toJSON(new IncompleteBean("Salomon", 26, new Date()), 0);

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		json.fromJSON(jsonObj, IncompleteBean.class, 0);
	}

	static class JA implements JSONAware {
		@Override public String toJSONString() {
			return "{}";
		}
	}

	static class JSA implements JSONStreamAware {
		@Override public void writeJSONString(Writer out) throws IOException {
			out.write("{}");
		}
	}

	static class JSONContainer {
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		JA ja = new JA();
		JSA jsa = new JSA();
	}

	@Test
	public void jsonObjects() throws Exception {
		SourJson json = new SourJson();
		JSONContainer from = new JSONContainer();
		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ser.containsKey("obj");
		assert ser.get("obj") == from.obj;
		assert ser.containsKey("array");
		assert ser.get("array") == from.array;
		assert ser.containsKey("ja");
		assert ser.get("ja") == from.ja;
		assert ser.containsKey("jsa");
		assert ser.get("jsa") == from.jsa;
	}

	static enum Hand {
		ONE, TWO, THREE, FOUR, FIVE
	}

	@Test
	public void enums() throws Exception {
		SourJson json = new SourJson();

		JSONObject ser = (JSONObject)json.toJSON(Hand.TWO, 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.ObjectsPrimitivesTest$Hand");
		assert ser.containsKey("!enum");
		assert ser.get("!enum").equals("TWO");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		Hand to = json.fromJSON(jsonObj, Hand.class, 0);

		assert to == Hand.TWO;
	}

	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Could not find .*")
	public void classNotFound() throws Exception {
		SourJson json = new SourJson();
		JSONObject obj = new JSONObject();
		obj.put("!type", "un.known.type.Class");
		json.fromJSON(obj, SimpleBean.class, 0);
	}

}
