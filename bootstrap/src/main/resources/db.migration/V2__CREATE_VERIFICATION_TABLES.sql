CREATE TABLE IF NOT EXISTS T_USER_VERIFICATIONS (
                                        VERIFICATION_ID UUID NOT NULL PRIMARY KEY,
                                        USER_ID UUID NOT NULL,
                                        VERIFICATION_CODE VARCHAR(100),
                                        VERIFICATION_ATTEMPTS INT,
                                        VERIFICATION_CREATION_DATE TIMESTAMP WITHOUT TIME ZONE,
                                        VERIFICATION_EXPIRATION_DATE TIMESTAMP WITHOUT TIME ZONE,
                                        VERIFICATION_TYPE VARCHAR(50),
                                        VERIFICATION_STATUS VARCHAR(50),
                                        FOREIGN KEY (USER_ID) REFERENCES T_USERS(USER_ID)
                                            ON DELETE CASCADE
);
