package net.schwarzbaer.spring.prompttester.backend.chatgpt;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public record ChatGptResponse(
        String id,
        String object,
        int created,
        String model,
        Usage usage,
        List<Choice> choices
) {
    public void showContent(PrintStream out, String label) {
        out.printf("%s: ChatGPTResponse%n", label);
        out.printf("    id: %s%n", id);
        out.printf("    object: %s%n", object);
        out.printf("    created: %s -> %s%n", created, Instant.ofEpochMilli(1000L * created).atZone(ZoneId.systemDefault()));
        out.printf("    model: %s%n", model);
        if (usage!=null)
            out.printf(
                    "    usage:   (%d tokens for prompt)  +  (%d tokens for answer)  ->  (%d tokens total)%n",
                    usage.prompt_tokens, usage.completion_tokens, usage.total_tokens
            );

        if (choices!=null) {
            out.printf("    choices: [%d]%n", choices.size());
            for (int i=0; i<choices.size(); i++) {
                Choice choice = choices.get(i);
                if (choice==null)
                    out.printf("        [%d] null%n", i);
                else {
                    out.printf("        [%d] index: %d,  finish reason: \"%s\"%n", i, choice.index, choice.finish_reason);
                    if (choice.message!=null)
                        out.printf("             as \"%s\": %s%n", choice.message.role, choice.message.content);
                }
            }
        }
    }

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
