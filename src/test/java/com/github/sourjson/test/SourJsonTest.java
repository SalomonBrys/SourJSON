package com.github.sourjson.test;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.sourjson.SourJson;
import com.github.sourjson.SourJson.AllowEmpty;
import com.github.sourjson.annotation.SJCheckForNull;
import com.github.sourjson.annotation.SJDisregardParent;
import com.github.sourjson.annotation.SJDisregardedParent;
import com.github.sourjson.annotation.SJExclude;
import com.github.sourjson.annotation.SJFieldName;
import com.github.sourjson.annotation.SJSince;
import com.github.sourjson.annotation.SJStrict;
import com.github.sourjson.annotation.SJUntil;
import com.github.sourjson.exception.SourJsonException;
import com.github.sourjson.exception.UnknownClassException;
import com.github.sourjson.translat.SJTranslater;


@SuppressWarnings("javadoc")
public class SourJsonTest {


	// ========================================== PRIMITIVES ==========================================

	static class PrimitiveBean {
		byte	pby;		Byte		oby;
		short	psh;		Short		osh;
		int		pin;		Integer		oin;
		long	plo;		Long		olo;
		float	pfl;		Float		ofl;
		double	pdo;		Double		odo;
		boolean	pbo;		Boolean		obo;
		char	pch;		Character	och;
							String		str;

		public PrimitiveBean() {}

		public PrimitiveBean(
				byte	pby,		Byte		oby,
				short	psh,		Short		osh,
				int		pin,		Integer		oin,
				long	plo,		Long		olo,
				float	pfl,		Float		ofl,
				double	pdo,		Double		odo,
				boolean	pbo,		Boolean		obo,
				char	pch,		Character	och,
									String		str
				) {
			this.pby = pby;		this.oby = oby;
			this.psh = psh;		this.osh = osh;
			this.pin = pin;		this.oin = oin;
			this.plo = plo;		this.olo = olo;
			this.pfl = pfl;		this.ofl = ofl;
			this.pdo = pdo;		this.odo = odo;
			this.pbo = pbo;		this.obo = obo;
			this.pch = pch;		this.och = och;
								this.str = str;
		}
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
		assert ser.get("!type").equals("com.github.sourjson.test.SourJsonTest$PrimitiveBean");
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


	// ========================================== TRANSLATERS IN OBJECT ==========================================

	static class DatedClass {
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
		assert ser.get("!type").equals("com.github.sourjson.test.SourJsonTest$DatedClass");
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
		assert ((JSONObject)ser.get("c")).get("name").equals("com.github.sourjson.test.SourJsonTest$PrimitiveBean");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		DatedClass to = json.fromJSON(jsonObj, DatedClass.class, 0);

		assert to.d.equals(date);
		assert to.c.equals(PrimitiveBean.class);
	}


	// ========================================== TRANSLATERS IN MAP ==========================================

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
		assert ((JSONObject)ser.get("c")).get("name").equals("com.github.sourjson.test.SourJsonTest$PrimitiveBean");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		@SuppressWarnings("unchecked")
		Map<String, Object> to = json.fromJSON(jsonObj, Map.class, 0);

		assert to.get("d") instanceof Date;
		assert to.get("d").equals(date);
		assert to.get("c") instanceof Class;
		assert to.get("c").equals(PrimitiveBean.class);
	}


	// ========================================== TRANSLATERS IN COLLECTION ==========================================

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
		assert ((JSONObject)ser.get(1)).get("name").equals("com.github.sourjson.test.SourJsonTest$PrimitiveBean");

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		@SuppressWarnings("unchecked")
		List<Object> to = json.fromJSON(jsonObj, List.class, 0);

		assert to.get(0) instanceof Date;
		assert to.get(0).equals(date);
		assert to.get(1) instanceof Class;
		assert to.get(1).equals(PrimitiveBean.class);
	}


	// ========================================== EMBEDDING ==========================================

	static class Dreamer {
		Dreamer host;
		String name;
		public Dreamer(Dreamer d, String name) {
			this.host = d;
			this.name = name;
		}
		@SuppressWarnings("unused")
		private Dreamer() {}
	}
	
	static class Plane {
		Dreamer main;
		int passengers;
		public Plane(Dreamer d, int number) {
			this.main = d;
			this.passengers = number;
		}
		@SuppressWarnings("unused")
		private Plane() {}
	}

	@Test
	public void embeding() throws Exception {
		Plane from = new Plane(new Dreamer(new Dreamer(new Dreamer(null, "Eames"), "Arthur"), "Yusuf"), 7);
		
		SourJson json = new SourJson();
		JSONObject ser = (JSONObject)json.toJSON(from, 0);
		
		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.SourJsonTest$Plane");
		assert ser.containsKey("passengers");
		assert ser.get("passengers").equals(Integer.valueOf(7));
		assert ser.containsKey("main");
		JSONObject dreamer = (JSONObject)ser.get("main");
		assert dreamer.containsKey("!type");
		assert dreamer.get("!type").equals("com.github.sourjson.test.SourJsonTest$Dreamer");
		assert dreamer.containsKey("name");
		assert dreamer.get("name").equals("Yusuf");
		assert dreamer.containsKey("host");
		dreamer = (JSONObject)dreamer.get("host");
		assert dreamer.containsKey("!type");
		assert dreamer.get("!type").equals("com.github.sourjson.test.SourJsonTest$Dreamer");
		assert dreamer.containsKey("name");
		assert dreamer.get("name").equals("Arthur");
		assert dreamer.containsKey("host");
		dreamer = (JSONObject)dreamer.get("host");
		assert dreamer.containsKey("!type");
		assert dreamer.get("!type").equals("com.github.sourjson.test.SourJsonTest$Dreamer");
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


	// ========================================== PRIMITIVE ARRAYS ==========================================
	
	@Test
	public void primitiveArrays() throws Exception {
		int[] from_i = {21, 42, 63, 84};
		String[][] from_s = {
			{"foo", "bar"},
			{"Salomon", "So", "Schlomo"}
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
		
		assert ser_s.size() == 2;
		assert !ser_s.contains("!type");

		assert ((JSONArray)ser_s.get(0)).size() == 2;
		assert !((JSONArray)ser_s.get(0)).contains("!type");
		assert ((JSONArray)ser_s.get(0)).get(0).equals("foo");
		assert ((JSONArray)ser_s.get(0)).get(1).equals("bar");
		
		assert ((JSONArray)ser_s.get(1)).size() == 3;
		assert !((JSONArray)ser_s.get(1)).contains("!type");
		assert ((JSONArray)ser_s.get(1)).get(0).equals("Salomon");
		assert ((JSONArray)ser_s.get(1)).get(1).equals("So");
		assert ((JSONArray)ser_s.get(1)).get(2).equals("Schlomo");

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
		
		assert to_s.length == 2;
		
		assert to_s[0].length == 2;
		assert to_s[0][0].equals("foo");
		assert to_s[0][1].equals("bar");

		assert to_s[1].length == 3;
		assert to_s[1][0].equals("Salomon");
		assert to_s[1][1].equals("So");
		assert to_s[1][2].equals("Schlomo");
	}


	// ========================================== OBJECT ARRAYS ==========================================
	
	static class SimpleBean {
		String name;
		@SJFieldName("count")
		int value;
		public SimpleBean(String name, int value) {
			this.name = name;
			this.value = value;
		}
		@SuppressWarnings("unused")
		protected SimpleBean() {}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SimpleBean))
				return false;
			SimpleBean sbo = (SimpleBean)obj;
			return ((this.name != null && this.name.equals(sbo.name)) || sbo.name == null) && this.value == sbo.value;
		}
		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 17 + (name != null ? name.hashCode() : -1);
			hash = hash * 31 + value;
			return hash;
		}
	}
	
	@Test
	public void objectArrays() throws Exception {
		SimpleBean[] from = new SimpleBean[] {
			new SimpleBean(null, 21),
			new SimpleBean("Salomon", 42),
			new SimpleBean("Brys", 63)
		};
		
		SourJson json = new SourJson();
		JSONArray ser = (JSONArray)json.toJSON(from, 0);
		
		assert ser.size() == 3;
		assert !((JSONObject)ser.get(0)).containsKey("name");
		assert ((JSONObject)ser.get(0)).get("count").equals(Integer.valueOf(21));
		assert ((JSONObject)ser.get(1)).get("name").equals("Salomon");
		assert ((JSONObject)ser.get(1)).get("count").equals(Integer.valueOf(42));
		assert ((JSONObject)ser.get(2)).get("name").equals("Brys");
		assert ((JSONObject)ser.get(2)).get("count").equals(Integer.valueOf(63));
		
		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		SimpleBean[] to = json.fromJSON(jsonObj, SimpleBean[].class, 0);
		
		assert to.length == 3;
		assert to[0].name == null;
		assert to[0].value == 21;
		assert to[1].name.equals("Salomon");
		assert to[1].value == 42;
		assert to[2].name.equals("Brys");
		assert to[2].value == 63;
	}


	// ========================================== COLLECTIONS ==========================================

	@DataProvider(name = "collections")
	public Object[][] collectionsProvider() {
		SimpleBean[] from = new SimpleBean[] {
				new SimpleBean(null, 21),
				new SimpleBean("Salomon", 42),
				new SimpleBean("Brys", 63)
			};
		
		return new Object[][] {
			{ Arrays.asList(from), List.class },
			{ new LinkedHashSet<>(Arrays.asList(from)), Set.class },
			{ new ArrayDeque<>(Arrays.asList(from)), Queue.class }
		};
	}
	
	@Test(dataProvider = "collections")
	public void collections(Collection<Plane> from, Class<? extends Collection<SimpleBean>> cls) throws Exception {
		SourJson json = new SourJson();
		JSONArray ser = (JSONArray)json.toJSON(from, 0);
		
		assert ser.size() == 3;
		assert !((JSONObject)ser.get(0)).containsKey("name");
		assert ((JSONObject)ser.get(0)).get("count").equals(Integer.valueOf(21));
		assert ((JSONObject)ser.get(1)).get("name").equals("Salomon");
		assert ((JSONObject)ser.get(1)).get("count").equals(Integer.valueOf(42));
		assert ((JSONObject)ser.get(2)).get("name").equals("Brys");
		assert ((JSONObject)ser.get(2)).get("count").equals(Integer.valueOf(63));
		
		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		Collection<SimpleBean> to = json.fromJSON(jsonObj, cls, 0);

		assert to.size() == 3;
		assert to.contains(new SimpleBean(null, 21));
		assert to.contains(new SimpleBean("Salomon", 42));
		assert to.contains(new SimpleBean("Brys", 63));
	}


	// ========================================== MANUAL TRANSLATER ==========================================
	
	static class SimpleBeanTranslater extends SJTranslater<SimpleBean> {
		@SuppressWarnings("unchecked")
		@Override public @CheckForNull
		JSONObject serialize(SimpleBean obj, Type type, AnnotatedElement el, Object enclosing, SourJson json) {
			JSONObject ret = new JSONObject();
			ret.put("n", obj.name);
			ret.put("v", Long.valueOf(obj.value));
			return ret;
		}
		@Override public @CheckForNull SimpleBean deserialize(JSONObject obj, Type type, AnnotatedElement el, Object enclosing, SourJson json) {
			return new SimpleBean((String)obj.get("n"), ((Long)obj.get("v")).intValue());
		}
		
	}
	
	@Test
	public void manualTranslater() throws Exception {
		SourJson json = new SourJson();
		json.addTranslater(SimpleBean.class, new SimpleBeanTranslater());
		
		JSONObject ser = (JSONObject)json.toJSON(new SimpleBean("Salomon", 42), 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.SourJsonTest$SimpleBean");
		assert ser.containsKey("n");
		assert ser.get("n").equals("Salomon");
		assert ser.containsKey("v");
		assert ser.get("v").equals(Long.valueOf(42));
		
		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		SimpleBean to = json.fromJSON(jsonObj, SimpleBean.class, 0);
		
		assert to.name.equals("Salomon");
		assert to.value == 42;
	}


	// ========================================== MANUAL TRANSLATER ==========================================

	static class DatedSimpleBean extends SimpleBean {
		@SJCheckForNull Date date;
		public DatedSimpleBean(String name, int value, Date date) {
			super(name, value);
			this.date = date;
		}
		@SuppressWarnings("unused")
		private DatedSimpleBean() {}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof DatedSimpleBean))
				return false;
			DatedSimpleBean sbo = (DatedSimpleBean)obj;
			return super.equals(obj) && date.equals(sbo.date);
		}
		@Override
		public int hashCode() {
			int hash = super.hashCode();
			hash = hash * 7 + date.hashCode();
			return hash;
		}
	}
	
	@Test
	public void manualHierarchyTranslater() throws Exception {
		SourJson json = new SourJson();
		json.addHierarchyTranslater(SimpleBean.class, new SimpleBeanTranslater());
		
		JSONObject ser = (JSONObject)json.toJSON(new DatedSimpleBean("Salomon", 42, new Date()), 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.SourJsonTest$DatedSimpleBean");
		assert ser.containsKey("n");
		assert ser.get("n").equals("Salomon");
		assert ser.containsKey("v");
		assert ser.get("v").equals(Long.valueOf(42));
		assert !ser.containsKey("date");
		
		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		SimpleBean to = json.fromJSON(jsonObj, SimpleBean.class, 0);
		
		assert to.name.equals("Salomon");
		assert to.value == 42;
		assert !(to instanceof DatedSimpleBean);
	}


	// ========================================== UNREACHABLE TRANSLATER ==========================================

	@Test
	public void unreachableTranslater() throws Exception {
		Date date = new Date(534990102000L);

		SourJson json = new SourJson();
		json.addTranslater(SimpleBean.class, new SimpleBeanTranslater());

		JSONObject ser = (JSONObject)json.toJSON(new DatedSimpleBean("Salomon", 42, date), 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.SourJsonTest$DatedSimpleBean");
		assert ser.containsKey("name");
		assert ser.get("name").equals("Salomon");
		assert ser.containsKey("count");
		assert ser.get("count").equals(Integer.valueOf(42));
		assert ser.containsKey("date");
		assert ((JSONObject)ser.get("date")).get("GMT").equals("1986-12-15 00:21:42");
		assert !ser.containsKey("n");
		assert !ser.containsKey("v");
		
		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		DatedSimpleBean to = json.fromJSON(jsonObj, DatedSimpleBean.class, 0);
		
		assert to.name.equals("Salomon");
		assert to.value == 42;
		assert to.date.equals(date);
	}


	// ========================================== ACCEPTED KNOWN CLASSES ==========================================

	@Test
	public void acceptedKnownClasses() throws Exception {
		Date date = new Date(534990102000L);

		SourJson json = new SourJson();
		json.checkKnownClasses(DatedSimpleBean.class);

		JSONObject ser = (JSONObject)json.toJSON(new DatedSimpleBean("Salomon", 42, date), 0);

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		DatedSimpleBean to = json.fromJSON(jsonObj, DatedSimpleBean.class, 0);
		
		assert to.name.equals("Salomon");
		assert to.value == 42;
		assert to.date.equals(date);
	}


	// ========================================== NOT ACCEPTED KNOWN CLASSES ==========================================

	@Test(expectedExceptions = UnknownClassException.class)
	public void notAcceptedKnownClasses() throws Exception {
		Date date = new Date(534990102000L);

		SourJson json = new SourJson();
		json.checkKnownClasses();

		json.toJSON(new DatedSimpleBean("Salomon", 42, date), 0);
	}


	// ========================================== CHECK FOR NULL SUCCEEDED ==========================================

	@Test
	public void checkForNullSuccess() throws Exception {
		SourJson json = new SourJson();
		json.checkForNulls();

		JSONObject ser = (JSONObject)json.toJSON(new DatedSimpleBean("Salomon", 42, null), 0);

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		DatedSimpleBean to = json.fromJSON(jsonObj, DatedSimpleBean.class, 0);
		
		assert to.name.equals("Salomon");
		assert to.value == 42;
		assert to.date == null;
	}


	// ========================================== CHECK FOR NULL FAIL ==========================================

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Missing \\(or null\\) JSON : .*")
	public void checkForNullFail() throws Exception {
		SourJson json = new SourJson();
		json.checkForNulls();

		JSONObject ser = (JSONObject)json.toJSON(new DatedSimpleBean(null, 42, null), 0);

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		json.fromJSON(jsonObj, DatedSimpleBean.class, 0);
	}


	// ========================================== IGNORED FIELDS ==========================================

	static class IgnoredBean {
		static String first = "...";
		@SJExclude String second;
		transient String third;
		String fourth;
		public IgnoredBean(String second, String third, String fourth) {
			super();
			this.second = second;
			this.third = third;
			this.fourth = fourth;
		}
		@SuppressWarnings("unused")
		private IgnoredBean() {}
	}
	
	@Test
	public void ignoredFields() throws Exception {
		SourJson json = new SourJson();
		json.checkForNulls();

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


	// ========================================== CANNOT CONSTRUCT ==========================================

	static class IncompleteBean extends SimpleBean {
		Date date;
		public IncompleteBean(String name, int value, Date date) {
			super(name, value);
			this.date = date;
		}
	}
	
	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot construct class .*")
	public void cannotConstruct() throws Exception {
		SourJson json = new SourJson();
		json.checkForNulls();

		JSONObject ser = (JSONObject)json.toJSON(new IncompleteBean("Salomon", 26, new Date()), 0);

		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		json.fromJSON(jsonObj, IncompleteBean.class, 0);
	}


	// ========================================== DISREGARD PARENT ==========================================
	
	static class A1 {
		int a;
		public A1(int a) {
			super();
			this.a = a;
		}
		private A1() {}
	}
	
	@SJDisregardParent
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
		json.checkForNulls();

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


	// ========================================== DISREGARDED PARENT ==========================================
	
	@SJDisregardedParent
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
		json.checkForNulls();

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

	
	// ========================================== NULLS ==========================================

	@Test
	public void nulls() throws Exception {
		SourJson json = new SourJson();

		assert json.toJSON(null, 0) == null;
		assert json.toJSON(null, SimpleBean.class, 0) == null;
		assert json.fromJSON(null, SimpleBean.class, 0) == null;
		
		assert json.toJSON(new Object(), Void.class, 0) == null;
	}

	
	// ========================================== JSONOBJECTS ==========================================

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


	// ========================================== ALLOW EMPTY IN OBJECT ==========================================

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
	
	@Test
	public void allowEmptyInObject() throws Exception {
		SourJson json = new SourJson();

		JSONObject ser_y = (JSONObject)json.toJSON(new EmptyCollections(new HashMap<>(0), new ArrayList<>(0), new String[0]), EmptyCollections.class, 0, AllowEmpty.YES);
		
		assert ser_y.containsKey("map");
		assert ser_y.containsKey("list");
		
		String jsonStr_y = ser_y.toJSONString();
		Object jsonObj_y = JSONValue.parse(jsonStr_y);
		EmptyCollections to_y = json.fromJSON(jsonObj_y, EmptyCollections.class, 0);
		
		assert to_y.map != null;
		assert to_y.map.isEmpty();
		assert to_y.list != null;
		assert to_y.list.isEmpty();
		assert to_y.array != null;
		assert to_y.array.length == 0;

		JSONObject ser_r = (JSONObject)json.toJSON(new EmptyCollections(new HashMap<>(0), new ArrayList<>(0), new String[0]), EmptyCollections.class, 0, AllowEmpty.ROOT);
		
		assert !ser_r.containsKey("map");
		assert !ser_r.containsKey("list");
		
		String jsonStr_r = ser_r.toJSONString();
		Object jsonObj_r = JSONValue.parse(jsonStr_r);
		EmptyCollections to_r = json.fromJSON(jsonObj_r, EmptyCollections.class, 0);
		
		assert to_r.map == null;
		assert to_r.list == null;
		assert to_r.array == null;

		JSONObject ser_n = (JSONObject)json.toJSON(new EmptyCollections(new HashMap<>(0), new ArrayList<>(0), new String[0]), EmptyCollections.class, 0, AllowEmpty.NO);
		
		assert !ser_n.containsKey("map");
		assert !ser_n.containsKey("list");
		
		String jsonStr_n = ser_n.toJSONString();
		Object jsonObj_n = JSONValue.parse(jsonStr_n);
		EmptyCollections to_n = json.fromJSON(jsonObj_n, EmptyCollections.class, 0);
		
		assert to_n.map == null;
		assert to_n.list == null;
		assert to_n.array == null;
	}

	
	// ========================================== ALLOW EMPTY IN COLLECTIONS ==========================================
	
	@SuppressWarnings("serial")
	@Test
	public void allowEmptyInCollections() throws Exception {
		SourJson json = new SourJson();
		
		assert json.toJSON(new ArrayList<String>(0), List.class, 0, AllowEmpty.NO) == null;
		assert json.toJSON(new HashMap<String, String>(0), HashMap.class, 0, AllowEmpty.NO) == null;
		assert json.toJSON(new String[0], String[].class, 0, AllowEmpty.NO) == null;

		assert json.toJSON(new ArrayList<String>(0), List.class, 0, AllowEmpty.YES) != null;
		assert json.toJSON(new HashMap<String, String>(0), HashMap.class, 0, AllowEmpty.YES) != null;
		assert json.toJSON(new String[0], String[].class, 0, AllowEmpty.YES) != null;

		JSONArray ser_l = (JSONArray)json.toJSON(new ArrayList<List<String>>(){{ add(new ArrayList<String>(0)); }}, List.class, 0, AllowEmpty.ROOT);
		assert ser_l != null;
		assert ser_l.isEmpty();

		JSONObject ser_m = (JSONObject)json.toJSON(new HashMap<String, List<String>>(){{ put("test", new ArrayList<String>(0)); }}, Map.class, 0, AllowEmpty.ROOT);
		assert ser_m != null;
		assert ser_m.isEmpty();

		JSONArray ser_a = (JSONArray)json.toJSON(new String[0], String[].class, 0, AllowEmpty.ROOT);
		assert ser_a != null;
		assert ser_a.isEmpty();

		JSONArray ser_ne_l = (JSONArray)json.toJSON(new ArrayList<String>(){{ add("hi"); add(null); }}, List.class, 0, AllowEmpty.NO);
		assert ser_ne_l != null;
		assert ser_ne_l.size() == 1;

		JSONObject ser_ne_m = (JSONObject)json.toJSON(new HashMap<String, String>(){{ put("hi", "hello"); put("yo", null); }}, Map.class, 0, AllowEmpty.NO);
		assert ser_ne_m != null;
		assert ser_ne_m.size() == 1;

		JSONArray ser_ne_a = (JSONArray)json.toJSON(new String[] { "hi", null }, String[].class, 0, AllowEmpty.NO);
		assert ser_ne_a != null;
		assert ser_ne_a.size() == 1;

		JSONArray ser_n_l = (JSONArray)json.toJSON(new ArrayList<String>(){{ add("hi"); add(null); }}, List.class, 0, AllowEmpty.YES);
		assert ser_n_l != null;
		assert ser_n_l.size() == 2;
		assert ser_n_l.get(1) == null;

		JSONObject ser_n_m = (JSONObject)json.toJSON(new HashMap<String, String>(){{ put("hi", null); }}, Map.class, 0, AllowEmpty.YES);
		assert ser_n_m != null;
		assert ser_n_m.size() == 1;
		assert ser_n_m.get("hi") == null;

		JSONArray ser_n_a = (JSONArray)json.toJSON(new String[] { "hi", null }, String[].class, 0, AllowEmpty.YES);
		assert ser_n_a != null;
		assert ser_n_a.size() == 2;
		assert ser_n_l.get(1) == null;
	}
	

	// ========================================== ENUM ==========================================

	static enum Hand {
		ONE, TWO, THREE, FOUR, FIVE
	}
	
	@Test
	public void enums() throws Exception {
		SourJson json = new SourJson();
		
		JSONObject ser = (JSONObject)json.toJSON(Hand.TWO, 0);
		
		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.SourJsonTest$Hand");
		assert ser.containsKey("!enum");
		assert ser.get("!enum").equals("TWO");
		
		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		Hand to = json.fromJSON(jsonObj, Hand.class, 0);

		assert to == Hand.TWO;
	}
	
	
	// ========================================== NUMBER TO CHAR ==========================================

	@Test
	public void numberToChar() throws Exception {
		SourJson json = new SourJson();
		
		char to = json.fromJSON(Integer.valueOf(42), char.class, 0).charValue();
		
		assert to == '*';
	}


	// ========================================== OBJECT CONSTRUCTION ==========================================

	static class ValueOfTest {
		int a = 0;

		private ValueOfTest() {}
		
		@SuppressWarnings("unused")
		public ValueOfTest(int a) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unused")
		public ValueOfTest(String a) {
			throw new UnsupportedOperationException();
		}
		
		public static ValueOfTest valueOf(int a) {
			ValueOfTest ret = new ValueOfTest();
			ret.a = a;
			return ret;
		}

		public static ValueOfTest valueOf(String a) {
			ValueOfTest ret = new ValueOfTest();
			ret.a = Integer.valueOf(a).intValue();
			return ret;
		}
	}

	static class ConstructorTest {
		int a = 0;
		
		public ConstructorTest(int a) {
			this.a = a;
		}

		public ConstructorTest(String a) {
			this.a = Integer.valueOf(a).intValue();
		}
	}

	@Test
	public void objectContruction() throws Exception {
		SourJson json = new SourJson();
		
		assert json.fromJSON(Integer.valueOf(21), ValueOfTest.class, 0).a == 21;
		assert json.fromJSON(Long.valueOf(42), ValueOfTest.class, 0).a == 42;

		assert json.fromJSON(Integer.valueOf(21), ConstructorTest.class, 0).a == 21;
		assert json.fromJSON(Long.valueOf(42), ConstructorTest.class, 0).a == 42;
	}
	
	
	// ========================================== SINCE / UNTIL ==========================================

	public static class Versionned {
		@SJUntil(0.9)
		private int first;
		
		@SJUntil(1.0)
		private int second;
		
		@SJSince(1.0)
		private int third;

		@SJSince(1.1)
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


	// ========================================== SINCE / UNTIL ==========================================

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Cannot transform .*")
	public void cannotTransform() throws Exception {
		SourJson json = new SourJson();
		json.fromJSON(Integer.valueOf(42), SimpleBean.class, 0);
	}


	// ========================================== COLLECTION ERROR ==========================================

	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Can only unserialize an array into a Collection, not .*")
	public void collectionError() throws Exception {
		SourJson json = new SourJson();
		json.fromJSON(json.toJSON(new String[] { "a", "b" }, 0), SimpleBean.class, 0);
	}


	// ========================================== CLASS NOT FOUND ==========================================

	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = SourJsonException.class, expectedExceptionsMessageRegExp = "Could not find .*")
	public void classNotFound() throws Exception {
		SourJson json = new SourJson();
		JSONObject obj = new JSONObject();
		obj.put("!type", "un.known.type.Class");
		json.fromJSON(obj, SimpleBean.class, 0);
	}


	// ========================================== STRICT TYPING ==========================================
	
	public static class StrictTypeContainer {
		@SJStrict SimpleBean bean;

		@SJStrict Map<String, SimpleBean> map = new HashMap<>();

		@SJStrict List<SimpleBean> list = new ArrayList<>();

		@SJStrict SimpleBean[] array = new SimpleBean[1];

		@SuppressWarnings("unused")
		private StrictTypeContainer() {}
		
		public StrictTypeContainer(SimpleBean bean, SimpleBean inMap, SimpleBean inList, SimpleBean inArray) {
			super();
			this.bean = bean;
			this.map.put("one", inMap);
			this.list.add(inList);
			array[0] = inArray;
		}
	}
	
	@Test
	public void strictTyping() throws Exception {
		SourJson json = new SourJson();
		
		StrictTypeContainer from = new StrictTypeContainer(
				new DatedSimpleBean("Salomon", 21, new Date()),
				new DatedSimpleBean("Salomon", 42, new Date()),
				new DatedSimpleBean("Salomon", 63, new Date()),
				new DatedSimpleBean("Salomon", 84, new Date())
		);
		
		JSONObject ser = (JSONObject)json.toJSON(from, 0);

		assert ((JSONObject)ser.get("bean")).get("!type").equals("com.github.sourjson.test.SourJsonTest$SimpleBean");
		assert ((JSONObject)((JSONObject)ser.get("map")).get("one")).get("!type").equals("com.github.sourjson.test.SourJsonTest$SimpleBean");
		assert ((JSONObject)((JSONArray)ser.get("list")).get(0)).get("!type").equals("com.github.sourjson.test.SourJsonTest$SimpleBean");
		assert ((JSONObject)((JSONArray)ser.get("array")).get(0)).get("!type").equals("com.github.sourjson.test.SourJsonTest$SimpleBean");
		
		String jsonStr = ser.toJSONString();
		Object jsonObj = JSONValue.parse(jsonStr);
		StrictTypeContainer to = json.fromJSON(jsonObj, StrictTypeContainer.class, 0);
		
		assert !(to.bean instanceof DatedSimpleBean);
		assert to.bean.getClass().equals(SimpleBean.class);
		assert !(to.map.get("one") instanceof DatedSimpleBean);
		assert to.map.get("one").getClass().equals(SimpleBean.class);
		assert !(to.list.get(0) instanceof DatedSimpleBean);
		assert to.list.get(0).getClass().equals(SimpleBean.class);
		assert !(to.array[0] instanceof DatedSimpleBean);
		assert to.array[0].getClass().equals(SimpleBean.class);
	}
}


