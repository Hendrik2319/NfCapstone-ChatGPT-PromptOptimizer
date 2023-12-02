package net.schwarzbaer.spring.promptoptimizer.backend.security.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserAttributesService.Field;
import net.schwarzbaer.spring.promptoptimizer.backend.security.services.UserAttributesService.Registration;

public class UserAttributesServiceTest {
    
	private UserAttributesService userAttributesService;

    @BeforeEach
	void setup() {
		userAttributesService = new UserAttributesService();
	}

// ############################################################################################
//      void fixAttributesIfNeeded( Map<String, Object> attributes, String registrationId )
// ############################################################################################

    @Test
    void whenFixAttributesIfNeeded_isCalledWithGoogleData() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "value");
        attributes.put("other", "value2");

        // When
        userAttributesService.fixAttributesIfNeeded(attributes, Registration.GOOGLE.id);

        // Then
        Map<Object, Object> expected = Map.of(
            "sub", "value",
            "original_Id", "value",
            "other", "value2"
        );
        assertEquals(expected, attributes);
    }

    @Test
    void whenFixAttributesIfNeeded_isCalledWithNonGoogleData() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("field1", "value1");
        attributes.put("field2", "value2");

        // When
        userAttributesService.fixAttributesIfNeeded(attributes, "other");

        // Then
        Map<Object, Object> expected = Map.of(
            "field1", "value1",
            "field2", "value2"
        );
        assertEquals(expected, attributes);
    }

// ############################################################################################
//      String getAttribute( @NonNull OAuth2AuthenticatedPrincipal user, @NonNull String field, String nullDefault )
// ############################################################################################

    @Test void whenGetAttribute_WithUserAndFieldName_isCalledWithUnkownField_returnsNullDefault() {
        whenGetAttribute_WithUserAndFieldName_isCalled("field2", "nullDefault");
    }
    @Test void whenGetAttribute_WithUserAndFieldName_isCalledNormal_returnsValue() {
        whenGetAttribute_WithUserAndFieldName_isCalled("field1", "value1");
    }
    private void whenGetAttribute_WithUserAndFieldName_isCalled(
            @NonNull String requestedFieldName, @NonNull String expectedReturnValue
    ) {
        // Given
        DefaultOAuth2User user = new DefaultOAuth2User(
            List.of(),
            Map.of(
                "id", "userId",
                "field1", "value1"
            ),
            "id"
        );

        // When
        String actual = userAttributesService.getAttribute(user, requestedFieldName, "nullDefault");

        // Then
        assertEquals(expectedReturnValue, actual);
    }

// ############################################################################################
//      String getAttribute( @NonNull Map<String, Object> userAttributes, @NonNull String field, String nullDefault )
// ############################################################################################

    @Test void whenGetAttribute_WithAttributeMapAndFieldName_isCalledWithUnkownField_returnsNullDefault() {
        whenGetAttribute_WithAttributeMapAndFieldName_isCalled("field2", "nullDefault");
    }
    @Test void whenGetAttribute_WithAttributeMapAndFieldName_isCalledNormal_returnsValue() {
        whenGetAttribute_WithAttributeMapAndFieldName_isCalled("field1", "value1");
    }
    private void whenGetAttribute_WithAttributeMapAndFieldName_isCalled(
            @NonNull String requestedFieldName, @NonNull String expectedReturnValue
    ) {
        // Given
        Map<String, Object> attributes = Objects.requireNonNull( Map.of(
            "id", "userId",
            "field1", "value1"
        ) );

        // When
        String actual = userAttributesService.getAttribute(attributes, requestedFieldName, "nullDefault");

        // Then
        assertEquals(expectedReturnValue, actual);
    }

// ############################################################################################
//      String getAttribute( @NonNull OAuth2AuthenticatedPrincipal user, String registrationId, Field field, String nullDefault )
// ############################################################################################

    @Test void whenGetAttribute_WithUserAndRegistrationAndField_isCalledNormal_returnsValue() {
        whenGetAttribute_WithUserAndRegistrationAndField_isCalled(
            Registration.GITHUB.id, Field.NAME, "value1"
        );
    }
    @Test void whenGetAttribute_WithUserAndRegistrationAndField_isCalledUnknownRegId_returnsNullDefault() {
        whenGetAttribute_WithUserAndRegistrationAndField_isCalled(
            "other", Field.NAME, "nullDefault"
        );
    }
    @Test void whenGetAttribute_WithUserAndRegistrationAndField_isCalledUnsetField_returnsNullDefault() {
        whenGetAttribute_WithUserAndRegistrationAndField_isCalled(
            Registration.GITHUB.id, Field.LOCATION, "nullDefault"
        );
    }
    private void whenGetAttribute_WithUserAndRegistrationAndField_isCalled(
            String registrationId, Field field, String expectedReturnValue
    ) {
        // Given
        DefaultOAuth2User user = new DefaultOAuth2User(
            List.of(),
            Map.of(
                "id", "userId",
                "name", "value1"
            ),
            "id"
        );

        // When
        String actual = userAttributesService.getAttribute(user, registrationId, field, "nullDefault");

        // Then
        assertEquals(expectedReturnValue, actual);
    }

// ############################################################################################
//      String getAttribute( @NonNull Map<String, Object> userAttributes, String registrationId, Field field, String nullDefault )
// ############################################################################################

    @Test void whenGetAttribute_WithAttributeMapAndRegistrationAndField_isCalledNormal_returnsValue() {
        whenGetAttribute_WithAttributeMapAndRegistrationAndField_isCalled(
            Registration.GITHUB.id, Field.NAME, "value1"
        );
    }
    @Test void whenGetAttribute_WithAttributeMapAndRegistrationAndField_isCalledUnknownRegId_returnsNullDefault() {
        whenGetAttribute_WithAttributeMapAndRegistrationAndField_isCalled(
            "other", Field.NAME, "nullDefault"
        );
    }
    @Test void whenGetAttribute_WithAttributeMapAndRegistrationAndField_isCalledUnsetField_returnsNullDefault() {
        whenGetAttribute_WithAttributeMapAndRegistrationAndField_isCalled(
            Registration.GITHUB.id, Field.LOCATION, "nullDefault"
        );
    }
    private void whenGetAttribute_WithAttributeMapAndRegistrationAndField_isCalled(
            String registrationId, Field field, String expectedReturnValue
    ) {
        // Given
        Map<String, Object> attributes = Objects.requireNonNull( Map.of(
            "id", "userId",
            "name", "value1"
        ) );

        // When
        String actual = userAttributesService.getAttribute(attributes, registrationId, field, "nullDefault");

        // Then
        assertEquals(expectedReturnValue, actual);
    }
}
