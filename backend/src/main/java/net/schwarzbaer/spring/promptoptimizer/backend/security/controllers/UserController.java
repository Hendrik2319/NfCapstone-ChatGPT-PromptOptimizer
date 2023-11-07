package net.schwarzbaer.spring.promptoptimizer.backend.security.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserService;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserInfos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("me")
	public UserInfos getCurrentUser() {
		return userService.getCurrentUser();
	}

	@GetMapping()
	public List<StoredUserInfo> getAllStoredUsers()
			throws UserIsNotAllowedException
	{
		return userService.getAllStoredUsers();
	}

	@PutMapping("{id}")
	public ResponseEntity<StoredUserInfo> updateStoredUser(@PathVariable String id, @RequestBody StoredUserInfo storedUserInfo)
			throws UserIsNotAllowedException
	{
		return ResponseEntity.of(userService.updateStoredUser(id, storedUserInfo));
	}

	@DeleteMapping("{id}")
	public void deleteStoredUser(@PathVariable String id)
			throws UserIsNotAllowedException
	{
		userService.deleteStoredUser(id);
	}

	@GetMapping("{id}/reason")
	public String getDenialReason(@PathVariable String id)
	{
		return userService.getDenialReason(id);
	}
}