CREATE TABLE IF NOT EXISTS T_NOTIFICATION_TEMPLATES (
                                                    NOTIFICATION_TEMPLATE_ID UUID NOT NULL PRIMARY KEY,
                                                    NOTIFICATION_TEMPLATE_NAME VARCHAR(100) NOT NULL UNIQUE,
                                                    NOTIFICATION_TEMPLATE_SUBJECT TEXT NOT NULL UNIQUE,
                                                    NOTIFICATION_TEMPLATE_LOCATION VARCHAR(100) NOT NULL UNIQUE,
                                                    NOTIFICATION_TEMPLATE_CREATED_AT TIMESTAMP WITHOUT TIME ZONE,
                                                    NOTIFICATION_TEMPLATE_UPDATED_AT TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS T_SENT_NOTIFICATIONS (
                                                    NOTIFICATION_ID UUID NOT NULL PRIMARY KEY,
                                                    USER_ID UUID NOT NULL,
                                                    NOTIFICATION_CONTENT TEXT,
                                                    NOTIFICATION_SHOWN BOOLEAN DEFAULT FALSE,
                                                    NOTIFICATION_CREATED_AT TIMESTAMP WITHOUT TIME ZONE,
                                                    NOTIFICATION_SHOWN_AT TIMESTAMP WITHOUT TIME ZONE,
                                                    FOREIGN KEY (USER_ID) REFERENCES T_USERS(USER_ID)
                                                        ON DELETE CASCADE
);
