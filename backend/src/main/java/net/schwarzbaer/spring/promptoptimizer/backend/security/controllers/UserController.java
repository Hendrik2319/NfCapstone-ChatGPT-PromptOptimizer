package net.schwarzbaer.spring.promptoptimizer.backend.security.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("me") // allowed: all
	public UserInfos getCurrentUser() {
		return userService.getCurrentUser();
	}
}