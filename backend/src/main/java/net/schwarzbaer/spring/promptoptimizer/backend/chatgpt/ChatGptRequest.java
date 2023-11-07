package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import org.springframework.lang.NonNull;

import java.util.List;

public record ChatGptRequest(
        @NonNull String model,
        @NonNull List<Message> messages
) {
	public record Message(
            @NonNull String role,
            @NonNull String content
    ) {}
}
