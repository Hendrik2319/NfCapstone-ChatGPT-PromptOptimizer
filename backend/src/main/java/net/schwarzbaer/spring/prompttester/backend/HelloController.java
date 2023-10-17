package net.schwarzbaer.spring.prompttester.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api")
public class HelloController {

	@GetMapping("/hello")
	public String getHelloWorld(){
		return "Hello World :)  [ %s ]".formatted(ZonedDateTime.now().toString());
	}

}
