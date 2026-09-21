package com.lectoria_api.auth.domain.constants;

public class Constants {

    private Constants() {}

    // Messages
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid Email or Password";
    public static final String INVALID_REFRESH_ACCESS_ATTEMPT = "Incorrect Refresh Session Attempt, Refresh Token Required";
    public static final String NOT_AUTH_FOUND = "There is not an Authenticated User";
    // Operations
    public static final String AUTH_OP = "Authentication";
    public static final String LOGT_OP = "Logging Out";
    public static final String INTR_OP = "Introspection";
    public static final String REFR_OP = "Refresh";

    // Token Claims
    public static final String SUB = "sub";

    // Keycloak Form Request
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String PASSWORD = "password";
    public static final String SCOPE_OPENID = "openid";
    public static final String TOKEN = "token";
    // Keycloak Login Response
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ACCESS_EXPIRES_IN = "expires_in";
    public static final String REFRESH_EXPIRES_IN = "refresh_expires_in";

    // COOKIES Constants
    public static final boolean HTTP_ONLY = true;
    public static final String SAME_SITE_DEV = "Lax";
    //public static final String NONE_SITE_PROD = "None";
    public static final boolean COOKIE_SECURE_DEV = false;
    public static final String COOKIE_PATH = "/";
    // Delete Cookies
    public static final String BLANK = "";
    public static final Long ZERO = 0L;


}
