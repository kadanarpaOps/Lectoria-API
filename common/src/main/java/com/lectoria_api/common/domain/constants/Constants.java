package com.lectoria_api.common.domain.constants;

import java.time.ZoneId;

public class Constants {

    private Constants() {}

    // Date Format
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final ZoneId TIME_ZONE = ZoneId.of("America/Bogota");

    // Verification Constants
    public static final long CODE_EXPIRATION_MINUTES = 5;
    public static final long VERIFICATION_EXPIRATION_MINUTES = 60;

    // Utils
    public static final String BLANK = "";

}
