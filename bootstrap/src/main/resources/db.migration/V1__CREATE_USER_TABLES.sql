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

CREATE TABLE IF NOT EXISTS T_USER_ROLES (
                                        USER_ID UUID NOT NULL,
                                        ROLE_ID UUID NOT NULL,
                                        FOREIGN KEY (USER_ID) REFERENCES T_USERS(USER_ID)
                                            ON DELETE CASCADE,
                                        FOREIGN KEY (ROLE_ID) REFERENCES T_ROLES(ROLE_ID)
                                            ON DELETE RESTRICT
);
