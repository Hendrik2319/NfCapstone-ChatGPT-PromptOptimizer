package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import java.util.List;

public record ChatGptResponse(
        String id,
        String object,
        int created,
        String model,
        Usage usage,
        List<Choice> choices
) {
	public record Choice(
            int index,
            Message message,
            String finish_reason
    ) {}
    public record Message(
            String role,
            String content
    ) {}
    public record Usage(
            int prompt_tokens,
            int completion_tokens,
            int total_tokens
    ) {}
}
