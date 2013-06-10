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
