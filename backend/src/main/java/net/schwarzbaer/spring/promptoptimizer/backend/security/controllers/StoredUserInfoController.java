package net.schwarzbaer.spring.promptoptimizer.backend.security.controllers;

import lombok.RequiredArgsConstructor;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.StoredUserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class StoredUserInfoController {

	private final StoredUserInfoService storedUserInfoService;

	@GetMapping() // allowed: admin
	public List<StoredUserInfo> getAllStoredUsers()
			throws UserIsNotAllowedException
	{
		return storedUserInfoService.getAllStoredUsers();
	}

	@PutMapping("{id}") // allowed: admin
	public ResponseEntity<StoredUserInfo> updateStoredUser(@NonNull @PathVariable String id, @NonNull @RequestBody StoredUserInfo storedUserInfo)
			throws UserIsNotAllowedException
	{
		return ResponseEntity.of(storedUserInfoService.updateStoredUser(id, storedUserInfo));
	}

	@DeleteMapping("{id}") // allowed: admin
	public void deleteStoredUser(@NonNull @PathVariable String id)
			throws UserIsNotAllowedException
	{
		storedUserInfoService.deleteStoredUser(id);
	}

	@GetMapping("reason") // allowed: authorized   | called by: unknown accounts
	public String getDenialReasonForCurrentUser()
			throws UserIsNotAllowedException
	{
		return storedUserInfoService.getDenialReasonForCurrentUser();
	}
}
