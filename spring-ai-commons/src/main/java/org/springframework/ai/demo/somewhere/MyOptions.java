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

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.ai.demo.common.SuperOptions;
import org.springframework.util.Assert;

public final class MyOptions extends SuperOptions {

	private final String foo;

	private final Integer bar;

	private final @Nullable String wizz;

	public MyOptions(String foo, Integer bar, @Nullable String wizz) {
		this.foo = foo;
		this.bar = bar;
		this.wizz = wizz;
	}

	public static Builder builder() {
		return new Builder(null, null, null);
	}

	public Builder mutate() {
		return new Builder(this.foo, this.bar, this.wizz);
	}

	public String foo() {
		return foo;
	}

	public Integer bar() {
		return bar;
	}

	public @Nullable String wizz() {
		return wizz;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (MyOptions) obj;
		return Objects.equals(this.foo, that.foo) && Objects.equals(this.bar, that.bar)
				&& Objects.equals(this.wizz, that.wizz);
	}

	@Override
	public int hashCode() {
		return Objects.hash(foo, bar, wizz);
	}

	@Override
	public String toString() {
		return "MyOptions[" + "foo=" + foo + ", " + "bar=" + bar + ", " + "wizz=" + wizz + ']';
	}

	public static final class Builder extends SuperOptions.Builder {

		private @Nullable String foo;

		private @Nullable Integer bar;

		private @Nullable String wizz;

		public Builder(@Nullable String foo, @Nullable Integer bar, @Nullable String wizz) {
			this.foo = foo;
			this.bar = bar;
			this.wizz = wizz;
		}

		public Builder foo(String foo) {
			this.foo = foo;
			return this;
		}

		public Builder bar(Integer bar) {
			this.bar = bar;
			return this;
		}

		public Builder wizz(@Nullable String wizz) {
			this.wizz = wizz;
			return this;
		}


		@Override
		public Builder combineWith(SuperOptions soptions) {
			if (soptions instanceof MyOptions options) {
				Assert.notNull(options, "MyOptions must not be null");
				if (this.foo == null)
					this.foo = options.foo;
				if (this.bar == null)
					this.bar = options.bar;
				if (this.wizz == null)
					this.wizz = options.wizz;
			}
			return this;
		}

		public MyOptions build() {
			Assert.state(foo != null, "foo must not be null");
			Assert.state(bar != null, "bar must not be null");
			return new MyOptions(foo, bar, wizz);
		}

		/*
		public @Nullable String foo() {
			return foo;
		}

		public @Nullable Integer bar() {
			return bar;
		}

		public @Nullable String wizz() {
			return wizz;
		}
*/
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj == null || obj.getClass() != this.getClass())
				return false;
			var that = (Builder) obj;
			return Objects.equals(this.foo, that.foo) && Objects.equals(this.bar, that.bar)
					&& Objects.equals(this.wizz, that.wizz);
		}

		@Override
		public int hashCode() {
			return Objects.hash(foo, bar, wizz);
		}

		@Override
		public String toString() {
			return "Builder[" + "foo=" + foo + ", " + "bar=" + bar + ", " + "wizz=" + wizz + ']';
		}

	}

}
