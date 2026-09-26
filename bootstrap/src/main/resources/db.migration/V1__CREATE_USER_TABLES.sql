CREATE TABLE IF NOT EXISTS T_ROLES (
                                        ROLE_ID UUID NOT NULL PRIMARY KEY,
                                        ROLE_NAME VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS T_USERS (
                                        USER_ID UUID NOT NULL PRIMARY KEY,
                                        USER_USERNAME VARCHAR(100) NOT NULL,
                                        USER_EMAIL VARCHAR(50) NOT NULL UNIQUE,
                                        USER_ENABLED BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS T_USER_DETAILS (
                                       USER_ID UUID NOT NULL,
                                       USER_DETAILS_ID UUID NOT NULL PRIMARY KEY,
                                       USER_CREATED_AT TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                       USER_UPDATED_AT TIMESTAMP WITHOUT TIME ZONE,
                                       USER_REGISTERED_VERIFIED BOOLEAN DEFAULT FALSE,
                                       USER_PROFILE_IMAGE_URL VARCHAR(100),
                                       FOREIGN KEY (USER_ID) REFERENCES T_USERS(USER_ID)
                                           ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS T_USER_ROLES (
                                        USER_ID UUID NOT NULL,
                                        ROLE_ID UUID NOT NULL,
                                        FOREIGN KEY (USER_ID) REFERENCES T_USERS(USER_ID)
                                            ON DELETE CASCADE,
                                        FOREIGN KEY (ROLE_ID) REFERENCES T_ROLES(ROLE_ID)
                                            ON DELETE RESTRICT
);
