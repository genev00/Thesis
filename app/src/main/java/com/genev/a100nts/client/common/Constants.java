package com.genev.a100nts.client.common;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public final class Constants {

    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=[^A-Z\\n]*[A-Z])(?=[^a-z\\n]*[a-z])(?=[^0-9\\n]*[0-9])(?=[^#?!@$%^&*\\n-]*[#?!@$%^&*-]).{8,}$");
    public static final Pattern NON_LETTER_SYMBOLS_PATTERN = Pattern.compile("[^a-zA-Zа-яА-Я]");
    public static final Pattern SECURITY_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]{10}$");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    public static final float MAX_DISTANCE_IN_METERS = 1000.0F;
    public final static String ID_AND_DATETIME_SEPARATOR = "\\+";

    public static final String ARGUMENT_SITE_ID = "ARGUMENT_SITE_ID";
    public static final String ARGUMENT_COMMENTS = "ARGUMENT_COMMENTS";
    public static final String ARGUMENT_SITE = "ARGUMENT_SITE";
    public static final String ARGUMENT_EMAIL = "ARGUMENT_EMAIL";
    public static final String ARGUMENT_PASSWORD = "ARGUMENT_PASSWORD";
    public static final String ARGUMENT_IS_RANKING_VIEW = "ARGUMENT_IS_RANKING_ENABLED";
    public static final String ARGUMENT_IS_USER_SITES_VIEW = "ARGUMENT_IS_USER_SITES_VIEW";
    public static final String ARGUMENT_IS_USER_FAVOURITE_SITES_VIEW = "ARGUMENT_IS_USER_FAVOURITE_SITES_VIEW";
    public static final String ARGUMENT_IS_NEAR_TO_USER_SITES_VIEW = "ARGUMENT_IS_NEAR_TO_USER_SITES_VIEW";
    public static final String ARGUMENT_IS_EDIT_SITE_VIEW = "ARGUMENT_IS_ADD_SITE_VIEW";
    public static final String ARGUMENT_USER_LATITUDE = "ARGUMENT_USER_LATITUDE";
    public static final String ARGUMENT_USER_LONGITUDE = "ARGUMENT_USER_LONGITUDE";
    public static final String ARGUMENT_ERROR_MESSAGE = "ARGUMENT_ERROR_MESSAGE";

    private Constants() {
    }

}
