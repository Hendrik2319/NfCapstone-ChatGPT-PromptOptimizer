package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.services.ChatGptService;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.models.Answer;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.models.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ask")
@RequiredArgsConstructor
public class SimpleChatGptController {

	private final ChatGptService chatGptService;

	@PostMapping
	public Answer aksChatGPT(@RequestBody Prompt prompt){
		return chatGptService.askChatGPT(prompt);
	}

}
