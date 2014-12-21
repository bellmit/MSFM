package com.cboe.interfaces.domain;

import java.util.regex.Pattern;

public interface Delimeter
{
    public final static char PROPERTY_DELIMETER = '\u0001';
    public static final String PROP_DELIMITER_AS_STRING = String.valueOf(PROPERTY_DELIMETER);
    public static final Pattern PATTERN = Pattern.compile(Delimeter.PROP_DELIMITER_AS_STRING);
}

