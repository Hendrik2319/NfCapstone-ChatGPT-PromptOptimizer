package net.schwarzbaer.spring.promptoptimizer.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintStream;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private static final PrintStream DEBUG_OUT = System.out;

	@GetMapping("me")
	public UserInfos getMe() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		DEBUG_OUT.println("Authentication: "+authentication);
		Object principal = authentication.getPrincipal();
		if (principal!=null) DEBUG_OUT.println("   principal: "+principal.getClass()+" -> "+principal);

		if (principal instanceof OAuth2AuthenticatedPrincipal user) {
			DEBUG_OUT.println("User Attributes:");
			user.getAttributes().forEach((key, value) ->
					DEBUG_OUT.println("   ["+key+"]: "+value+ (value==null ? "" : " { Class:"+value.getClass().getName()+" }"))
			);
			return new UserInfos(
					true,
					Objects.toString( user.getAttribute("id"), null ),
					Objects.toString( user.getAttribute("login"), null ),
					Objects.toString( user.getAttribute("name"), null ),
					Objects.toString( user.getAttribute("location"), null ),
					Objects.toString( user.getAttribute("html_url"), null ),
					Objects.toString( user.getAttribute("avatar_url"), null )
			);
		}

		return new UserInfos( false, authentication.getName(), null,null, null,null,null);
	}
	@GetMapping("restricted")
	public String getRestricted() {
		return "You got access to a restricted endpoint";
	}
}