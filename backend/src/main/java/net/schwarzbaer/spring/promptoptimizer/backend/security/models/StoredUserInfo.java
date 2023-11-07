package net.schwarzbaer.spring.promptoptimizer.backend.security.models;

public record StoredUserInfo(
		String id,             // == UserInfos.userDbId
		Role   role,
		String registrationId, // registration service: github, ...
		String originalId,     // == UserInfos.id
		String login,
		String name,
		String location,
		String url,
		String avatar_url
) {}
