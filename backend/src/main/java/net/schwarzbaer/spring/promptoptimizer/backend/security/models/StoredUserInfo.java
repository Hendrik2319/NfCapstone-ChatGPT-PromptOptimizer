package net.schwarzbaer.spring.promptoptimizer.backend.security.models;

public record StoredUserInfo(
		String id,             // == UserInfos.userDbId
		String registrationId, // registration service: github, ...
		String originalId,     // == UserInfos.id
		Role   role,
		String login,
		String name,
		String location,
		String url,
		String avatar_url
) {}
