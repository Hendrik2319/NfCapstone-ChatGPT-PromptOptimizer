package net.schwarzbaer.spring.promptoptimizer.backend.security;

public class UserIsNotAllowedException extends Exception {
	public UserIsNotAllowedException(String message) {
		super(message);
	}
}
