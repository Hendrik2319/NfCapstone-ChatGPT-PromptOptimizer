package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.controllers;

import lombok.extern.slf4j.Slf4j;
import net.schwarzbaer.spring.promptoptimizer.backend.ErrorMessage;
import net.schwarzbaer.spring.promptoptimizer.backend.security.models.UserIsNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(IllegalArgumentException ex)
	{
		return getErrorMessageAndDoLog("IllegalArgumentException", ex);
	}

	@ExceptionHandler(UserIsNotAllowedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessage handleException(UserIsNotAllowedException ex)
	{
		return getErrorMessageAndDoLog("UserIsNotAllowedException", ex);
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException(NoSuchElementException ex)
	{
		return getErrorMessageAndDoLog("NoSuchElementException", ex);
	}

	private static ErrorMessage getErrorMessageAndDoLog(String exception, Exception ex)
	{
		String message = "%s: %s".formatted(exception, ex.getMessage());
		log.error(message);
		return new ErrorMessage(message);
	}
}
