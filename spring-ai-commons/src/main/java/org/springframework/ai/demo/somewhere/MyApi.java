/*
 * Copyright 2026-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.demo.somewhere;

import org.springframework.ai.demo.somewhere.MyOptions.Builder;
import org.springframework.ai.demo.common.SuperOptions;
import org.springframework.ai.demo.common.Prompt;

public class MyApi {

	private MyOptions defaultOptions;

	public MyApi(MyOptions defaultOptions) {
		this.defaultOptions = defaultOptions;
	}

	public void createRequest(Prompt prompt) {
		SuperOptions options = prompt.options().combineWith(defaultOptions).build();
		// construire le merge des options
		System.out.println(options);
	}

}
