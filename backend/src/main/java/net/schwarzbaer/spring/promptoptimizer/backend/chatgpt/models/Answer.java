package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.models;

import org.springframework.lang.NonNull;

public record Answer(
		@NonNull String answer,
		int promptTokens,
		int completionTokens,
		int totalTokens
) {
	public Answer(@NonNull String answer) {
		this(answer, 0, 0, 0);
	}
}
