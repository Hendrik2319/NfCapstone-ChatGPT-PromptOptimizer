package net.schwarzbaer.spring.promptoptimizer.backend.security.models;

public record UserInfo(
		boolean isAuthenticated,
		boolean isUser,
		boolean isAdmin,
		String id,
		String userDbId,
		String login,
		String name,
		String location,
		String url,
		String avatar_url
) {
}
