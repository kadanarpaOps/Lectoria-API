CREATE TABLE IF NOT EXISTS T_ROLES (
                                        ROLE_ID UUID NOT NULL PRIMARY KEY,
                                        ROLE_NAME VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS T_USER_DETAILS (
                                              USER_DETAILS_ID UUID NOT NULL PRIMARY KEY,
                                              USER_CREATED_AT TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                              USER_UPDATED_AT TIMESTAMP WITHOUT TIME ZONE,
                                              USER_REGISTERED_VERIFIED BOOLEAN DEFAULT FALSE,
                                              USER_PROFILE_IMAGE_URL VARCHAR(200)
);


CREATE TABLE IF NOT EXISTS T_USERS (
                                        USER_ID UUID NOT NULL PRIMARY KEY,
                                        USER_USERNAME VARCHAR(100) NOT NULL,
                                        USER_EMAIL VARCHAR(50) NOT NULL,
                                        USER_ENABLED BOOLEAN DEFAULT FALSE,
                                        USER_DETAILS_ID UUID NOT NULL UNIQUE,
                                        FOREIGN KEY (USER_DETAILS_ID) REFERENCES T_USER_DETAILS(USER_DETAILS_ID)
);

CREATE TABLE IF NOT EXISTS T_USER_ROLES (
                                        USER_ID UUID NOT NULL,
                                        ROLE_ID UUID NOT NULL,
                                        PRIMARY KEY (USER_ID, ROLE_ID),
                                        FOREIGN KEY (USER_ID) REFERENCES T_USERS(USER_ID)
                                            ON DELETE CASCADE,
                                        FOREIGN KEY (ROLE_ID) REFERENCES T_ROLES(ROLE_ID)
                                            ON DELETE RESTRICT
);
