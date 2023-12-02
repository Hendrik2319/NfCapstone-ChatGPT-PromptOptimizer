package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAttributesService {

	public static final String ATTR_USER_DB_ID      = "UserDbId";
	public static final String ATTR_REGISTRATION_ID = "RegistrationId";
	
	public enum Field {
		ORIGINAL_ID,
		LOGIN      ,
		NAME       ,
		LOCATION   ,
		URL        ,
		AVATAR_URL ,
	}

	public enum Registration {
		GITHUB("github"),
		GOOGLE("google"),
		;
		public final String id;
		private Registration(String id) {
			this.id = id;
		}
	}


	private static final Map<String, Map<Field, String>> config = createConfig();

	private static Map<String, Map<Field, String>> createConfig() {
		HashMap<String, Map<Field, String>> newConfig = new HashMap<>();
		EnumMap<Field, String> fields;

		fields = new EnumMap<>(Field.class);
		fields.put( Field.ORIGINAL_ID, "id"         );
		fields.put( Field.LOGIN      , "login"      );
		fields.put( Field.NAME       , "name"       );
		fields.put( Field.LOCATION   , "location"   );
		fields.put( Field.URL        , "html_url"   );
		fields.put( Field.AVATAR_URL , "avatar_url" );
		newConfig.put(Registration.GITHUB.id, fields);

		fields = new EnumMap<>(Field.class);
		fields.put( Field.ORIGINAL_ID, "original_Id");
		fields.put( Field.LOGIN      , "email"      );
		fields.put( Field.NAME       , "name"       );
		fields.put( Field.LOCATION   , "locale"     );
	//	fields.put( Field.URL        , "html_url"   );
		fields.put( Field.AVATAR_URL , "picture"    );
		newConfig.put(Registration.GOOGLE.id, fields);

		return newConfig;
	}


	public void fixAttributesIfNeeded(Map<String, Object> attributes, String registrationId) {
		if (Registration.GOOGLE.id.equals(registrationId)) {
			attributes.put("original_Id", attributes.get("sub"));
		}
	}


	public String getAttribute( @NonNull OAuth2AuthenticatedPrincipal user, @NonNull String field, String nullDefault )
	{
		return Objects.toString( user.getAttribute(field), nullDefault );
	}

	public String getAttribute( @NonNull Map<String, Object> userAttributes, @NonNull String field, String nullDefault )
	{
		return Objects.toString( userAttributes.get(field), nullDefault );
	}

	public String getAttribute( @NonNull OAuth2AuthenticatedPrincipal user, String registrationId, Field field, String nullDefault )
	{
		return getAttribute( user, registrationId, field, nullDefault, this::getAttribute );
	}

	public String getAttribute( @NonNull Map<String, Object> userAttributes, String registrationId, Field field, String nullDefault )
	{
		return getAttribute( userAttributes, registrationId, field, nullDefault, this::getAttribute );
	}

	
	private interface GetAttributeFunction<S> {
		String getAttribute( @NonNull S source, @NonNull String field, String nullDefault);
	}

	private <S> String getAttribute( @NonNull S source, String registrationId, Field field, String nullDefault, GetAttributeFunction<S> getAttribute )
	{
		Map<Field, String> attrNames = config.get(registrationId);
		if (attrNames==null) return nullDefault;
		
		String attrName = attrNames.get(field);
		if (attrName==null) return nullDefault;

		return getAttribute.getAttribute( source, attrName, nullDefault );
	}
}
