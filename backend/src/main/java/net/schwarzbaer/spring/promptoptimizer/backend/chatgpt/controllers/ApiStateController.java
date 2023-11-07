package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.services.ChatGptService;
import net.schwarzbaer.spring.promptoptimizer.backend.chatgpt.models.ApiState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apistate")
@RequiredArgsConstructor
public class ApiStateController {

	private final ChatGptService chatGptService;

	@GetMapping // allowed: all
	public ApiState getApiState(){
		return chatGptService.getApiState();
	}

}
