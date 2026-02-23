/*
 * Copyright 2023-2025 the original author or authors.
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

package org.springframework.ai.chat.prompt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.ModelRequest;
import org.springframework.util.Assert;

/**
 * The Prompt class represents a prompt used in AI model requests. A prompt consists of
 * one or more messages and additional chat options.
 *
 * @author Mark Pollack
 * @author luocongqiu
 * @author Thomas Vitale
 */
public class Prompt implements ModelRequest<List<Message>> {

	private final List<Message> messages;

	@Deprecated
	private final @Nullable ChatOptions chatOptions;

	private final ChatOptions.@Nullable Builder<?> optionsCustomizer;

	// TODO: remove. For debugging purposes only, shows how Prompts were constructed
	private final Exception ctorCause;

	public Prompt(String contents) {
		this(new UserMessage(contents));
	}

	public Prompt(Message message) {
		this(Collections.singletonList(message));
	}

	public Prompt(List<Message> messages) {
		this(messages, (ChatOptions) null);
	}

	public Prompt(Message... messages) {
		this(Arrays.asList(messages), (ChatOptions) null);
	}

	// Only used in tests
	public Prompt(String contents, @Nullable ChatOptions chatOptions) {
		this(new UserMessage(contents), chatOptions);
	}

	// Only uszd in tests (and above)
	public Prompt(Message message, @Nullable ChatOptions chatOptions) {
		this(Collections.singletonList(message), chatOptions);
	}

	public Prompt(List<Message> messages, @Nullable ChatOptions chatOptions) {
		Assert.notNull(messages, "messages cannot be null");
		Assert.noNullElements(messages, "messages cannot contain null elements");
		this.messages = messages;
		this.chatOptions = chatOptions;
		this.optionsCustomizer = null;
		this.ctorCause = new Exception();
		this.ctorCause.fillInStackTrace();
	}

	private Prompt(List<Message> messages, ChatOptions.@Nullable Builder<?> optionsCustomizer,
			@Nullable ChatOptions chatOptions) {
		Assert.notNull(messages, "messages cannot be null");
		Assert.noNullElements(messages, "messages cannot contain null elements");
		Assert.state(!(optionsCustomizer != null && chatOptions != null),
				"Only one of optionsCustomizer and chatOptions cannot be null");
		this.messages = messages;
		this.chatOptions = chatOptions;
		this.optionsCustomizer = optionsCustomizer;
		this.ctorCause = new Exception();
		this.ctorCause.fillInStackTrace();
	}

	public String getContents() {
		StringBuilder sb = new StringBuilder();
		for (Message message : getInstructions()) {
			sb.append(message.getText());
		}
		return sb.toString();
	}

	@Deprecated
	@Override
	public @Nullable ChatOptions getOptions() {
		if (this.optionsCustomizer != null) {
			throw new IllegalStateException("Prompt using customizer should not need to access getOptions()",
					this.ctorCause);
		}
		return this.chatOptions;
	}

	public ChatOptions.@Nullable Builder<?> getOptionsCustomizer() {
		// TODO remove (this.optionsCustomizer != null) part
		if (this.chatOptions != null && this.optionsCustomizer != null) {
			throw new IllegalStateException(
					"Prompt accessing customizer should not have been built with options in the first place",
					this.ctorCause);
		}
		return this.optionsCustomizer;
	}

	@Override
	public List<Message> getInstructions() {
		return this.messages;
	}

	/**
	 * Get the first system message in the prompt. If no system message is found, an empty
	 * SystemMessage is returned.
	 */
	public SystemMessage getSystemMessage() {
		for (int i = 0; i <= this.messages.size() - 1; i++) {
			Message message = this.messages.get(i);
			if (message instanceof SystemMessage systemMessage) {
				return systemMessage;
			}
		}
		return new SystemMessage("");
	}

	/**
	 * Get the last user message in the prompt. If no user message is found, an empty
	 * UserMessage is returned.
	 */
	public UserMessage getUserMessage() {
		for (int i = this.messages.size() - 1; i >= 0; i--) {
			Message message = this.messages.get(i);
			if (message instanceof UserMessage userMessage) {
				return userMessage;
			}
		}
		return new UserMessage("");
	}

	/**
	 * Get the last user or tool response message in the prompt. If no user or tool
	 * response message is found, an empty UserMessage is returned.
	 */
	public Message getLastUserOrToolResponseMessage() {
		for (int i = this.messages.size() - 1; i >= 0; i--) {
			Message message = this.messages.get(i);
			if (message instanceof UserMessage || message instanceof ToolResponseMessage) {
				return message;
			}
		}
		return new UserMessage("");
	}

	/**
	 * Get all system messages in the prompt.
	 * @return a list of all system messages in the prompt
	 */
	public List<SystemMessage> getSystemMessages() {
		List<SystemMessage> systemMessages = new ArrayList<>();
		for (Message message : this.messages) {
			if (message instanceof SystemMessage systemMessage) {
				systemMessages.add(systemMessage);
			}
		}
		return systemMessages;
	}

	/**
	 * Get all user messages in the prompt.
	 * @return a list of all user messages in the prompt
	 */
	public List<UserMessage> getUserMessages() {
		List<UserMessage> userMessages = new ArrayList<>();
		for (Message message : this.messages) {
			if (message instanceof UserMessage userMessage) {
				userMessages.add(userMessage);
			}
		}
		return userMessages;
	}

	@Override
	public String toString() {
		return "Prompt{" + "messages=" + this.messages + ", optionsCustomizer=" + this.optionsCustomizer + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Prompt prompt)) {
			return false;
		}
		return Objects.equals(this.messages, prompt.messages) && Objects.equals(this.chatOptions, prompt.chatOptions)
				&& Objects.equals(this.optionsCustomizer, prompt.optionsCustomizer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.messages, this.chatOptions, this.optionsCustomizer);
	}

	public Prompt copy() {
		return mutate().build();
	}

	private List<Message> instructionsCopy() {
		List<Message> messagesCopy = new ArrayList<>();
		this.messages.forEach(message -> {
			if (message instanceof UserMessage userMessage) {
				messagesCopy.add(userMessage.copy());
			}
			else if (message instanceof SystemMessage systemMessage) {
				messagesCopy.add(systemMessage.copy());
			}
			else if (message instanceof AssistantMessage assistantMessage) {
				messagesCopy.add(AssistantMessage.builder()
					.content(Objects.requireNonNullElse(assistantMessage.getText(), ""))
					.properties(assistantMessage.getMetadata())
					.toolCalls(assistantMessage.getToolCalls())
					.build());
			}
			else if (message instanceof ToolResponseMessage toolResponseMessage) {
				messagesCopy.add(ToolResponseMessage.builder()
					.responses(new ArrayList<>(toolResponseMessage.getResponses()))
					.metadata(new HashMap<>(toolResponseMessage.getMetadata()))
					.build());
			}
			else {
				throw new IllegalArgumentException("Unsupported message type: " + message.getClass().getName());
			}
		});

		return messagesCopy;
	}

	/**
	 * Augments the first system message in the prompt with the provided function. If no
	 * system message is found, a new one is created with the provided text.
	 * @return a new {@link Prompt} instance with the augmented system message.
	 */
	public Prompt augmentSystemMessage(Function<SystemMessage, SystemMessage> systemMessageAugmenter) {
		var messagesCopy = new ArrayList<>(this.messages);
		boolean found = false;
		for (int i = 0; i < messagesCopy.size(); i++) {
			Message message = messagesCopy.get(i);
			if (message instanceof SystemMessage systemMessage) {
				messagesCopy.set(i, systemMessageAugmenter.apply(systemMessage));
				found = true;
				break;
			}
		}
		if (!found) {
			// If no system message is found, create a new one with the provided text
			// and add it as the first item in the list.
			messagesCopy.add(0, systemMessageAugmenter.apply(new SystemMessage("")));
		}
		return new Prompt(messagesCopy, this.optionsCustomizer, this.chatOptions);
	}

	/**
	 * Augments the last system message in the prompt with the provided text. If no system
	 * message is found, a new one is created with the provided text.
	 * @return a new {@link Prompt} instance with the augmented system message.
	 */
	public Prompt augmentSystemMessage(String newSystemText) {
		return augmentSystemMessage(systemMessage -> systemMessage.mutate().text(newSystemText).build());
	}

	/**
	 * Augments the last user message in the prompt with the provided function. If no user
	 * message is found, a new one is created with the provided text.
	 * @return a new {@link Prompt} instance with the augmented user message.
	 */
	public Prompt augmentUserMessage(Function<UserMessage, UserMessage> userMessageAugmenter) {
		var messagesCopy = new ArrayList<>(this.messages);
		for (int i = messagesCopy.size() - 1; i >= 0; i--) {
			Message message = messagesCopy.get(i);
			if (message instanceof UserMessage userMessage) {
				messagesCopy.set(i, userMessageAugmenter.apply(userMessage));
				break;
			}
			if (i == 0) {
				messagesCopy.add(userMessageAugmenter.apply(new UserMessage("")));
			}
		}

		return new Prompt(messagesCopy, this.optionsCustomizer, this.chatOptions);
	}

	/**
	 * Augments the last user message in the prompt with the provided text. If no user
	 * message is found, a new one is created with the provided text.
	 * @return a new {@link Prompt} instance with the augmented user message.
	 */
	public Prompt augmentUserMessage(String newUserText) {
		return augmentUserMessage(userMessage -> userMessage.mutate().text(newUserText).build());
	}

	public Builder mutate() {
		Builder builder = new Builder().messages(instructionsCopy());
		if (this.chatOptions != null) {
			builder.chatOptions(this.chatOptions.copy());
		}
		if (this.optionsCustomizer != null) {
			builder.chatOptionsNew(this.optionsCustomizer.build().mutate());
		}
		return builder;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private @Nullable List<Message> messages;

		private ChatOptions.@Nullable Builder<?> chatOptionsCustomizer;

		private @Nullable ChatOptions chatOptions;

		public Builder content(@Nullable String content) {
			this.messages = List.of(new UserMessage(content));
			return this;
		}

		public Builder messages(Message... messages) {
			if (messages != null) {
				this.messages = Arrays.asList(messages);
			}
			return this;
		}

		public Builder messages(List<Message> messages) {
			this.messages = messages;
			return this;
		}

		public Builder chatOptions(@Nullable ChatOptions chatOptions) {
			this.chatOptions = chatOptions;
			return this;
		}

		public Builder chatOptionsNew(ChatOptions.@Nullable Builder<?> customizer) {
			this.chatOptionsCustomizer = customizer;
			return this;
		}

		public Prompt build() {
			Assert.state(this.messages != null, "either messages or content needs to be set");
			return new Prompt(this.messages, this.chatOptionsCustomizer, this.chatOptions);
		}

	}

}
