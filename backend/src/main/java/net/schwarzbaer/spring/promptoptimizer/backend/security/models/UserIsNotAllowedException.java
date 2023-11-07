package net.schwarzbaer.spring.promptoptimizer.backend.security.models;

public class UserIsNotAllowedException extends Exception {
	public UserIsNotAllowedException(String message) {
		super(message);
	}
}
