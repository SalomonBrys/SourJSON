/**
 * Copyright 2013 Salomon BRYS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public class DateTranslater implements SJTranslater<Date> {

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
