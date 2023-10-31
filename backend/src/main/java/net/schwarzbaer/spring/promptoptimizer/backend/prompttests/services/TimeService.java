package net.schwarzbaer.spring.promptoptimizer.backend.prompttests.services;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class TimeService {

	ZonedDateTime getNow() {
		return ZonedDateTime.now(); // currently untestable
	}

}
