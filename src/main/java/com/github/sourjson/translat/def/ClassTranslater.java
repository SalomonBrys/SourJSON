package com.github.sourjson.translat.def;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;
import com.github.sourjson.translat.SJTranslater;

/**
 * Class default translater
 * Translate from class to string and back
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@SuppressWarnings("rawtypes")
public class ClassTranslater extends SJTranslater<Class> {
	
	@SuppressWarnings("unchecked")
	@Override
	public @CheckForNull JSONObject serialize(Class obj, Type typeOnServer, AnnotatedElement el, @CheckForNull Object enclosing, SourJson json) {
		JSONObject ret = new JSONObject();
		ret.put("name", obj.getName());
		return ret;
	}

	@Override
	public @CheckForNull Class deserialize(JSONObject obj, Type typeOnServer, AnnotatedElement el, @CheckForNull Object enclosing, SourJson json) {
		try {
			return Class.forName((String)obj.get("name"));
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
