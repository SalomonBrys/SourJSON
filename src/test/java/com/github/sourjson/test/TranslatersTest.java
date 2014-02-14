package com.github.sourjson.test;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Date;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.annotations.Test;

import com.github.sourjson.SourJson;
import com.github.sourjson.SourJson.AllowEmpty;
import com.github.sourjson.test.struct.DatedSimpleBean;
import com.github.sourjson.test.struct.SimpleBean;
import com.github.sourjson.translat.SJTranslater;

@SuppressWarnings({"javadoc", "static-method"})
public class TranslatersTest {

	static class SimpleBeanTranslater implements SJTranslater<SimpleBean> {
		@SuppressWarnings("unchecked")
		@Override public @CheckForNull
		JSONObject serialize(SimpleBean obj, Type type, AnnotatedElement el, Object enclosing, SourJson json, double version, AllowEmpty allowEmpty) {
			JSONObject ret = new JSONObject();
			ret.put("n", obj.name);
			ret.put("v", Long.valueOf(obj.value));
			return ret;
		}
		@Override public @CheckForNull SimpleBean deserialize(JSONObject obj, Type type, AnnotatedElement el, Object enclosing, SourJson json, double version) {
			return new SimpleBean((String)obj.get("n"), ((Long)obj.get("v")).intValue());
		}

	}

	@Test
	public void manualTranslater() throws Exception {
		SourJson json = new SourJson();
		json.addTranslater(SimpleBean.class, new SimpleBeanTranslater());

		JSONObject ser = (JSONObject)json.toJSON(new SimpleBean("Salomon", 42), 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.struct.SimpleBean");
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

	@Test
	public void manualHierarchyTranslater() throws Exception {
		SourJson json = new SourJson();
		json.addHierarchyTranslater(SimpleBean.class, new SimpleBeanTranslater());

		JSONObject ser = (JSONObject)json.toJSON(new DatedSimpleBean("Salomon", 42, new Date()), 0);

		assert ser.containsKey("!type");
		assert ser.get("!type").equals("com.github.sourjson.test.struct.DatedSimpleBean");
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
		assert ser.get("!type").equals("com.github.sourjson.test.struct.DatedSimpleBean");
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

}
