package net.schwarzbaer.spring.promptoptimizer.backend.chatgpt;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apistate")
@RequiredArgsConstructor
public class ApiStateController {

	private final ChatGptService chatGptService;

	@GetMapping
	public ApiState getApiState(){
		return chatGptService.getApiState();
	}

}
