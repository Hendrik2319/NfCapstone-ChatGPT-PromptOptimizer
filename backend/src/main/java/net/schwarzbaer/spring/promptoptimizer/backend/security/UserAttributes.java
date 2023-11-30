package net.schwarzbaer.spring.promptoptimizer.backend.security;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

public class UserAttributes {

	public static final String ATTR_USER_DB_ID      = "UserDbId";
	public static final String ATTR_REGISTRATION_ID = "RegistrationId";
	public static final String REGID_GITHUB = "github";
	public static final String REGID_GOOGLE = "google";
	
	public enum Field {
		ORIGINAL_ID,
		LOGIN      ,
		NAME       ,
		LOCATION   ,
		URL        ,
		AVATAR_URL ,
	}


	private static final Map<String, Map<Field, String>> config = createConfig();

	private static Map<String, Map<Field, String>> createConfig() {
		HashMap<String, Map<Field, String>> newConfig = new HashMap<>();
		EnumMap<Field, String> fields;

		newConfig.put(REGID_GITHUB, fields = new EnumMap<>(Field.class));
		fields.put( Field.ORIGINAL_ID, "id"         );
		fields.put( Field.LOGIN      , "login"      );
		fields.put( Field.NAME       , "name"       );
		fields.put( Field.LOCATION   , "location"   );
		fields.put( Field.URL        , "html_url"   );
		fields.put( Field.AVATAR_URL , "avatar_url" );

		newConfig.put(REGID_GOOGLE, fields = new EnumMap<>(Field.class));
		fields.put( Field.ORIGINAL_ID, "sub"        );
		fields.put( Field.LOGIN      , "email"      );
		fields.put( Field.NAME       , "name"       );
		fields.put( Field.LOCATION   , "locale"     );
	//	fields.put( Field.URL        , "html_url"   );
		fields.put( Field.AVATAR_URL , "picture"    );

		return newConfig;
	}


	public static String getAttribute( @NonNull OAuth2AuthenticatedPrincipal user, @NonNull String field, String nullDefault )
	{
		return Objects.toString( user.getAttribute(field), nullDefault );
	}

	public static String getAttribute( @NonNull Map<String, Object> userAttributes, @NonNull String field, String nullDefault )
	{
		return Objects.toString( userAttributes.get(field), nullDefault );
	}

    public static String getAttribute( @NonNull OAuth2AuthenticatedPrincipal user, String registrationId, Field field, String nullDefault )
	{
        return getAttribute( user, registrationId, field, nullDefault, UserAttributes::getAttribute );
    }

    public static String getAttribute( @NonNull Map<String, Object> userAttributes, String registrationId, Field field, String nullDefault )
	{
        return getAttribute( userAttributes, registrationId, field, nullDefault, UserAttributes::getAttribute );
    }

	
	private interface GetAttributeFunction<Source> {
		String getAttribute( @NonNull Source source, @NonNull String field, String nullDefault);
	}

    private static <Source> String getAttribute( @NonNull Source source, String registrationId, Field field, String nullDefault, GetAttributeFunction<Source> getAttribute )
	{
		Map<Field, String> attrNames = config.get(registrationId);
		if (attrNames==null) return nullDefault;
		
		String attrName = attrNames.get(field);
		if (attrName==null) return nullDefault;

        return getAttribute.getAttribute( source, attrName, nullDefault );
    }
}
