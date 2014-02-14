package com.github.sourjson.test;

import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.annotations.Test;

import com.github.sourjson.SourJson;
import com.github.sourjson.exception.UnknownClassException;
import com.github.sourjson.test.struct.DatedSimpleBean;

@SuppressWarnings({"javadoc", "static-method"})
public class KnownClassesTest {

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

	@Test(expectedExceptions = UnknownClassException.class)
	public void notAcceptedKnownClasses() throws Exception {
		Date date = new Date(534990102000L);

		SourJson json = new SourJson();
		json.checkKnownClasses();

		json.toJSON(new DatedSimpleBean("Salomon", 42, date), 0);
	}


}
