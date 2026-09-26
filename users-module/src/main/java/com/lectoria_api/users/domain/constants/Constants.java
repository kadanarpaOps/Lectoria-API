package com.lectoria_api.users.domain.constants;

public class Constants {

    private Constants() {}

    // Messages
    public static final String USER_WITH_FIELD_ALREADY_EXISTS_MESSAGE = "User with that %s already exists";
    public static final String USER_WITH_FIELD_NOT_EXISTS_MESSAGE = "User with that %s does not exists";
    public static final String SAME_VALUE_ON_UPDATE_MESSAGE = "New %s can't be the same";
    public static final String ROLE_WITH_NAME_NOT_EXISTS_MESSAGE = "Role %s does not exist";
    public static final String USER_ALREADY_HAVE_THAT_ROLE_MESSAGE = "The user already have the role '%s'";
    public static final String USER_DONT_HAVE_THAT_ROLE_MESSAGE = "The user don't have the role '%s'";
    public static final String USER_ROLES_LIST_CANT_BE_EMPTY_MESSAGE = "The role list of the user can't be empty";
    public static final String FAILED_OP_MESSAGE = "Failed %s because %s";
    public static final String ROLE_NOT_FOUND_IN_KEYCLOAK_MESSAGE = "Role %s does not exist in Authentication Server";

    public static final String USER_INVALID_USERNAME_MESSAGE = "Invalid USCO code";
    public static final String USER_INVALID_EMAIL_MESSAGE = "Invalid email";
    public static final String USER_INVALID_NAME_MESSAGE = "Invalid name";
    public static final String USER_INVALID_PASSWORD_MESSAGE = "Invalid password";

    // Operations
    public static final String CREATION_OP = "Creation";
    public static final String UPDATE_OP = "Update";
    public static final String SEARCH_OP = "Searching";
    public static final String DELETION_OP = "Deletion";

    // Causes
    public static final String ROLE_IS_REQUIRED = "Role is Required";
    public static final String AT_LEAST_ONE_FIELD_REQUIRED = "At least one field is required";
    public static final String FIELD_WITH_VALUE_NOT_EXIST = "%s with value %s does not exist";
    public static final String KEYCLOAK_ERR_CONNECTION = "Errors in the Network Connection with Keycloak";
    public static final String INVALID_TOKEN = "There was errors in the Token Validation";
    public static final String DATABASE_ERR_CONNECTION = "Errors in the Network Connection with Database";
    public static final String USER_WITH_ROLE_INVALID = "User has invalid role %s";

    // Fields
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    // Admin Role
    public static final String ADMINISTRATOR = "ADMINISTRADOR";

}
