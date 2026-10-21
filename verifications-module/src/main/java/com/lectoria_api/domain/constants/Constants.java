package com.lectoria_api.domain.constants;

import com.lectoria_api.verifications.domain.model.enums.VerificationType;

import java.util.Set;

public class Constants {

    private Constants() {}

    // Messages
    public static final String NO_AVAILABLE_ATTEMPTS_MESSAGE = "Reached Max Numbers of Attempts, the Code is no longer available";
    public static final String VERIFICATION_CODE_HAS_EXPIRED_MESSAGE = "The Verification Code has expired, request a new one!";
    public static final String VERIFICATION_HAS_EXPIRED_MESSAGE = "The Time to Complete your Verification has ended, request a new one!";
    public static final String WRONG_CODE_MESSAGE = "Wrong Code";
    public static final String NOT_FOUND_VERIFICATION_MESSAGE = "The Verification Request was not found";
    public static final String EXISTING_VERIFICATION_IS_PENDING_MESSAGE = "An Existing Verification Request is pending";
    public static final String WRONG_VERIFICATION_CREATION_SCOPE = "This Verification Type just can be created from the %s Scope";

    // Scopes
    public static final String PUBLIC_SCOPE = "Logged Out";
    public static final String AUTH_SCOPE = "Authenticated";

    // Verification Types Constraints
    public static final Set<VerificationType> ALLOWED_VERIFICATION_TYPES_PUBLIC_CREATION = Set.of(
            VerificationType.FORGOT_PASSWORD
    );

    public static final Set<VerificationType> ALLOWED_VERIFICATION_TYPES_AUTHENTICATED_CREATION = Set.of(
            VerificationType.CHANGE_PASSWORD,
            VerificationType.CHANGE_EMAIL,
            VerificationType.DISABLE_ACCOUNT
    );

}
