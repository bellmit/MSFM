package com.cboe.client.util;

/**
 * ExceptionHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * Helper for working with Java Exceptions
 *
 */

import java.io.*;
import java.util.*;

public final class ExceptionHelper
{
    public static String getStackTrace()
    {
        Exception exception = null;

        try
        {
            throw new Exception();
        }
        catch (Exception ex)
        {
            exception = ex;
        }

        String s = getStackTrace(exception, "\n");

        return s.substring(s.indexOf(',', s.indexOf(',') + 1) + 1);
    }

    public static String getStackTrace(String delimiter)
    {
        Exception exception = null;

        try
        {
            throw new Exception();
        }
        catch (Exception ex)
        {
            exception = ex;
        }

        String s = getStackTrace(exception, delimiter);

        return s.substring(s.indexOf(',', s.indexOf(',') + 1) + 1);
    }

    public static String getStackTrace(Throwable e)
    {
        return getStackTrace(e, "\n");
    }

    public static String getStackTrace(Throwable e, char delimiter)
    {
        return getStackTrace(e, "" + delimiter);
    }

    public static String getStackTrace(Throwable e, String delimiter)
    {
        StringWriter sw = new StringWriter();
        PrintWriter  pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        String s = sw.toString();

        StringTokenizer tokenizer = new StringTokenizer(s, "\n");
        StringBuilder buffer = new StringBuilder(s.length());

        boolean b = tokenizer.hasMoreTokens();
        while (b)
        {
            s = tokenizer.nextToken().trim();

            if (s.startsWith("at "))
            {
                buffer.append(s.substring(3));
            }
            else
            {
                buffer.append(s);
            }

            b = tokenizer.hasMoreTokens();
            if (b == true)
            {
                buffer.append(delimiter);
            }
        }

        return buffer.toString();
    }
}
