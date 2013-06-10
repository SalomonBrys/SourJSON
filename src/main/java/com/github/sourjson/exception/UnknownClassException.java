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

package com.github.sourjson.exception;

import com.github.sourjson.SourJson;

/**
 * Thrown when {@link SourJson#checkForKnownClasses(java.util.Collection)} is previously called and the
 * JSON (de)serializer is handling a class that is not declared as known
 * 
 * @author Salomon BRYS <salomon.brys@gmail.com>
 */
@SuppressWarnings("javadoc")
public class UnknownClassException extends RuntimeException {
	private static final long serialVersionUID = 3590054085580608330L;

	public UnknownClassException(Class<?> cls) {
		super(cls.getName() + " is not known");
	}
}
