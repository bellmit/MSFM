package com.cboe.cfix.fix.util;

/**
 * FixChecksumHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public final class FixChecksumHelper
{
    private static final boolean debug = false;

    public static final int calculateFixChecksum(String str)
    {
        int checksum = 0;
        int to = str.length();

        for (int i = 0; i < to; i++)
        {
            checksum += str.charAt(i);

            if (debug) Log.information(Thread.currentThread().getName() + " Checksum[" + i + "] '" + str.charAt(i) + "' = " + checksum);
        }

        if (debug) Log.information(Thread.currentThread().getName() + " FINAL Checksum[" + to + "] = " + (checksum & 255));

        return checksum & 255;
    }

    public static final int calculateFixChecksum(String str, int offset, int length)
    {
        int checksum = 0;
        int to = offset + length;

        for (int i = offset; i < to; i++)
        {
            checksum += str.charAt(i);
            if (debug) Log.information(Thread.currentThread().getName() + " Checksum[" + i + "] '" + str.charAt(i) + "' = " + checksum);
        }

        if (debug) Log.information(Thread.currentThread().getName() + " FINAL Checksum[" + to + "] = " + (checksum & 255));

        return checksum & 255;
    }

    public static final int calculateFixChecksum(FastCharacterWriter writer) throws Exception
    {
        int checksum = 0;
        int size = writer.size();
		char[] chars = writer.toCharArray();

        for (int i = 0; i < size; i++)
        {
            checksum += chars[i];
            if (debug) Log.information(Thread.currentThread().getName() + " Checksum[" + i + "] '" + chars[i] + "' = " + checksum);
        }

        if (debug) Log.information(Thread.currentThread().getName() + " FINAL Checksum[" + size + "] = " + (checksum & 255));

        return checksum & 255;
    }

    public static final int calculateFixChecksum(StringBuffer str)
    {
        int checksum = 0;
        int to = str.length();

        for (int i = 0; i < to; i++)
        {
            checksum += str.charAt(i);
            if (debug) Log.information(Thread.currentThread().getName() + " Checksum[" + i + "] '" + str.charAt(i) + "' = " + checksum);
        }

        if (debug) Log.information(Thread.currentThread().getName() + " FINAL Checksum[" + to + "] = " + (checksum & 255));

        return checksum & 255;
    }

    public static final int calculateFixChecksum(StringBuffer str, int offset, int to)
    {
        int checksum = 0;

        for (int i = offset; i < to; i++)
        {
            checksum += str.charAt(i);
            if (debug) Log.information(Thread.currentThread().getName() + " Checksum[" + i + "] '" + str.charAt(i) + "' = " + checksum);
        }

        if (debug) Log.information(Thread.currentThread().getName() + " FINAL Checksum[" + to + "] = " + (checksum & 255));

        return checksum & 255;
    }

    public static final int calculateFixChecksum(char[] array, int offset, int length)
    {
        int checksum = 0;
        int to = offset + length;

        for (int i = offset; i < to; i++)
        {
            checksum += array[i];
            if (debug) Log.information(Thread.currentThread().getName() + " Checksum[" + i + "] '" + array[i] + "' = " + checksum);
        }

        if (debug) Log.information(Thread.currentThread().getName() + " FINAL Checksum[" + to + "] = " + (checksum & 255));

        return checksum & 255;
    }

    public static final int calculateFixChecksum(String str1, String str2)
    {
        int checksum = 0;
        int to;
        int i;

        if (debug)
        {
            int to2;

            to = str1.length();
            for (i = 0; i < to; i++)
            {
                checksum += str1.charAt(i);
                Log.information(Thread.currentThread().getName() + " Checksum[" + i + "] '" + str1.charAt(i) + "' = " + checksum);
            }

            to2 = str2.length();
            for (i = 0; i < to2; i++)
            {
                checksum += str2.charAt(i);
                Log.information(Thread.currentThread().getName() + " Checksum[" + (to+i) + "] '" + str2.charAt(i) + "' = " + checksum);
            }

            if (debug) Log.information(Thread.currentThread().getName() + " FINAL Checksum[" + (to+to2) + "] = " + (checksum & 255));
        }
        else
        {
            to = str1.length();
            for (i = 0; i < to; i++)
            {
                checksum += str1.charAt(i);
            }

            to = str2.length();
            for (i = 0; i < to; i++)
            {
                checksum += str2.charAt(i);
            }
        }

        return checksum & 255;
    }

    public static final String stringizeChecksum(int checksum)
    {
        return StringHelper.zeroPad(checksum, 3);
    }

    public static final String calculateFixChecksumToString(String str)
    {
        return stringizeChecksum(calculateFixChecksum(str));
    }

    public static final String calculateFixChecksumToString(FastCharacterWriter writer) throws Exception
    {
        return stringizeChecksum(calculateFixChecksum(writer));
    }

    public static final String calculateFixChecksumToString(FastCharacterWriter writer, int debugFlags) throws Exception
    {
        return stringizeChecksum(calculateFixChecksum(writer));
    }

    public static final String calculateFixChecksumToString(String str, int offset, int length)
    {
        return stringizeChecksum(calculateFixChecksum(str, offset, length));
    }

    public static final String calculateFixChecksumToString(StringBuffer str, int offset, int to)
    {
        return stringizeChecksum(calculateFixChecksum(str, offset, to));
    }

    public static final String calculateFixChecksumToString(StringBuffer str)
    {
        return stringizeChecksum(calculateFixChecksum(str));
    }

    public static final String calculateFixChecksumToString(char[] array, int offset, int length)
    {
        return stringizeChecksum(calculateFixChecksum(array, offset, length));
    }

    public static final String calculateFixChecksumToString(String str1, String str2)
    {
        return stringizeChecksum(calculateFixChecksum(str1, str2));
    }

/*
    public static void main(String[] args)
    {
        String fixLine;
        String originalLine;
        int startIndex;
        int endIndex;
        BufferedReader in = null;

        try
        {
            if (args.length == 0)
            {
                in = new BufferedReader(new InputStreamReader(System.in));
            }
            else
            {
                in = new BufferedReader(new FileReader(args[0]));
            }

            while ((fixLine = in.readLine()) != null)
            {
                fixLine = fixLine.trim();
                if (fixLine.length() < 1 || fixLine.startsWith("#"))
                    continue;

                originalLine = fixLine;

                startIndex = fixLine.indexOf("8=FIX.4.2" + FixFieldIF.SOHchar);
                if (startIndex < 0)
                    continue;

                endIndex = fixLine.lastIndexOf(FixFieldIF.SOHchar + "10=");
                if (endIndex == -1)
                {
                    endIndex = fixLine.length();
                }

                fixLine = fixLine.substring(startIndex, endIndex + 1);

                Log.information("Checksum[" + calculateFixChecksum(fixLine) + "] Line: [" + originalLine + "]");
            }

            in.close();
        }
        catch (Exception ex)
        {
            Log.information("Exception: " + ExceptionHelper.getStackTrace(ex));
        }
    }
*/
}