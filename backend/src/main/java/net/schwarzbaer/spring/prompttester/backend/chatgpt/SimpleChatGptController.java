package net.schwarzbaer.spring.prompttester.backend.chatgpt;

import lombok.RequiredArgsConstructor;
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
	public String aksChatGPT(@RequestBody String prompt){
		return chatGptService.askChatGPT(prompt);
	}

}
