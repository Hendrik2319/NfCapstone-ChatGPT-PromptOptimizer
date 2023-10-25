package net.schwarzbaer.spring.promptoptimizer.backend.security;

public record UserInfos(
		boolean isAuthenticated,
		boolean isUser,
		boolean isAdmin,
		String id,
		String login,
		String name,
		String location,
		String url,
		String avatar_url
) {
}
