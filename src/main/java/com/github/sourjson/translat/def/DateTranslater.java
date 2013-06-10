package com.github.sourjson.translat.def;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.CheckForNull;

import org.json.simple.JSONObject;

import com.github.sourjson.SourJson;
import com.github.sourjson.translat.SJTranslater;

/**
 * Date default translater
 * Translate from date to string and back
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
public class DateTranslater extends SJTranslater<Date> {

	/**
	 * Formatter that will be used to (de)serialize dates
	 */
	private SimpleDateFormat formater;
	
	/** */
	public DateTranslater() {
		formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formater.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public @CheckForNull
	JSONObject serialize(Date obj, Type typeOnServer, AnnotatedElement el, @CheckForNull Object enclosing, SourJson json) {
		JSONObject ret = new JSONObject();
		ret.put("GMT", formater.format(obj));
		return ret;
	}

	@Override
	public @CheckForNull
	Date deserialize(JSONObject obj, Type typeOnServer, AnnotatedElement el, @CheckForNull Object enclosing, SourJson json) {
		try {
			return formater.parse((String)obj.get("GMT"));
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
