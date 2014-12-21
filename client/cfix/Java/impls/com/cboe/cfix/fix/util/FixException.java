package com.cboe.cfix.fix.util;

/**
 * FixException.java
 *
 * @author Dmitry Volpyansky
 *
 */

public class FixException extends Exception
{
    public final static byte TAG_OUT_OF_ORDER_WITHIN_GROUP            = 1;  public final static String string_TAG_OUT_OF_ORDER_WITHIN_GROUP            = "TAG_OUT_OF_ORDER_WITHIN_GROUP";
    public final static byte DUPLICATE_TAG_WITHIN_GROUP               = 2;  public final static String string_DUPLICATE_TAG_WITHIN_GROUP               = "DUPLICATE_TAG_WITHIN_GROUP";
    public final static byte FEWER_GROUPS_THAN_SPECIFIED              = 3;  public final static String string_FEWER_GROUPS_THAN_SPECIFIED              = "FEWER_GROUPS_THAN_SPECIFIED";
    public final static byte DUPLICATE_TAG                            = 4;  public final static String string_DUPLICATE_TAG                            = "DUPLICATE_TAG";
    public final static byte TAG_IN_INVALID_POSITION                  = 5;  public final static String string_TAG_IN_INVALID_POSITION                  = "TAG_IN_INVALID_POSITION";
    public final static byte INVALID_TAG                              = 6;  public final static String string_INVALID_TAG                              = "INVALID_TAG";
    public final static byte INVALID_TAG_VALUE                        = 7;  public final static String string_INVALID_TAG_VALUE                        = "INVALID_TAG_VALUE";
    public final static byte UNEXPECTED_USER_DEFINED_TAG_WITHIN_GROUP = 8;  public final static String string_UNEXPECTED_USER_DEFINED_TAG_WITHIN_GROUP = "UNEXPECTED_USER_DEFINED_TAG_WITHIN_GROUP";
    public final static byte HEADER_TAG_OUTSIDE_OF_HEADER             = 9;  public final static String string_HEADER_TAG_OUTSIDE_OF_HEADER             = "HEADER_TAG_OUTSIDE_OF_HEADER";
    public final static byte MISSING_CONDITIONALLY_REQUIRED_TAG       = 10; public final static String string_MISSING_CONDITIONALLY_REQUIRED_TAG       = "MISSING_CONDITIONALLY_REQUIRED_TAG";
    public final static byte MISSING_REQUIRED_TAG                     = 11; public final static String string_MISSING_REQUIRED_TAG                     = "MISSING_REQUIRED_TAG";

    public byte error;
    public int position;

    public FixException(byte error, int position)
    {
        this.error    = error;
        this.position = position;
    }

    public byte getError()
    {
        return error;
    }

    public int getPosition()
    {
        return position;
    }

    public String toString()
    {
        return toString(error, position);
    }

    public static boolean isPositionATag(byte error, int position)
    {
        switch (error)
        {
            case MISSING_CONDITIONALLY_REQUIRED_TAG:
            case MISSING_REQUIRED_TAG:
                return true;
        }

        return position < 0;
    }

    public static String toString(byte error, int position)
    {
        String string = "???";
        String s = "[offset ";

        switch (error)
        {
            case TAG_OUT_OF_ORDER_WITHIN_GROUP:            string = string_TAG_OUT_OF_ORDER_WITHIN_GROUP; break;
            case DUPLICATE_TAG_WITHIN_GROUP:               string = string_DUPLICATE_TAG_WITHIN_GROUP; break;
            case FEWER_GROUPS_THAN_SPECIFIED:              string = string_FEWER_GROUPS_THAN_SPECIFIED; break;
            case DUPLICATE_TAG:                            string = string_DUPLICATE_TAG; break;
            case TAG_IN_INVALID_POSITION:                  string = string_TAG_IN_INVALID_POSITION; break;
            case INVALID_TAG:                              string = string_INVALID_TAG; break;
            case INVALID_TAG_VALUE:                        string = string_INVALID_TAG_VALUE; break;
            case UNEXPECTED_USER_DEFINED_TAG_WITHIN_GROUP: string = string_UNEXPECTED_USER_DEFINED_TAG_WITHIN_GROUP; break;
            case HEADER_TAG_OUTSIDE_OF_HEADER:             string = string_HEADER_TAG_OUTSIDE_OF_HEADER; break;
            case MISSING_CONDITIONALLY_REQUIRED_TAG:       string = string_MISSING_CONDITIONALLY_REQUIRED_TAG; break;
            case MISSING_REQUIRED_TAG:                     string = string_MISSING_REQUIRED_TAG; break;
        }

        if (isPositionATag(error, position))
        {
            s = "[tag ";
        }

        StringBuilder result = new StringBuilder(string.length()+s.length()+10);
        result.append(string).append(s).append(position).append("]");
        return result.toString();
    }
}
