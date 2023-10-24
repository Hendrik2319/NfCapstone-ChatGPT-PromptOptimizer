package net.schwarzbaer.spring.promptoptimizer.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("me")
	public UserInfos getCurrentUser() {
		return userService.getCurrentUser();
	}

	@GetMapping("restricted")
	public String getRestricted() {
		return "You got access to a restricted endpoint";
	}
}