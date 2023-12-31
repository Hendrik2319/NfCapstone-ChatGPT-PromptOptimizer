package net.schwarzbaer.spring.promptoptimizer.backend.security.models;

public record StoredUserInfo(
		String id,             // == UserInfo.userDbId
		Role   role,
		String registrationId, // registration service: github, ...
		String originalId,     // == UserInfo.id
		String login,
		String name,
		String location,
		String url,
		String avatar_url,
		String denialReason    // defined by Admin
) {}
