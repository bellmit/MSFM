package com.cboe.client.util;

/**
 * IntegerHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Helper file for integer manipulation
 *
 */

public final class IntegerHelper
{
    public static final int   INVALID_VALUE   = Integer.MIN_VALUE;

    public static final int   LOG_10_INT_WHOLE_MAX_VALUE                = 10; //  1234567890
    public static final int   LOG_10_INT_WHOLE_MIN_VALUE                = 11; // -1234567890
    public static final int   LOG_10_INT_WHOLE_WITH_FRACTION_MAX_VALUE  = 22; //  1234567890.0000000001
    public static final int   LOG_10_INT_WHOLE_WITH_FRACTION_MIN_VALUE  = 23; // -1234567890.0000000001

    public static final int   LOG_10_LONG_WHOLE_MAX_VALUE               = 64;  //
    public static final int   LOG_10_LONG_WHOLE_MIN_VALUE               = 65;  //
    public static final int   LOG_10_LONG_WHOLE_WITH_FRACTION_MAX_VALUE = 128; //
    public static final int   LOG_10_LONG_WHOLE_WITH_FRACTION_MIN_VALUE = 129; //

    public static final int NUMBER_10            = 10;
    public static final int NUMBER_100           = 100;
    public static final int NUMBER_1_000         = 1000;
    public static final int NUMBER_10_000        = 10000;
    public static final int NUMBER_100_000       = 100000;
    public static final int NUMBER_1_000_000     = 1000000;
    public static final int NUMBER_10_000_000    = 10000000;
    public static final int NUMBER_100_000_000   = 100000000;
    public static final int NUMBER_1_000_000_000 = 1000000000;

    public static final Integer[] integers = new Integer[500];

    static
    {
        for (int i = 0; i < integers.length; i++)
        {
            integers[i] = Integer.valueOf(i);
        }
    }

    public static Integer createInteger(int integer)
    {
        if (integer >= 0 && integer < integers.length)
        {
            return integers[integer];
        }

        return Integer.valueOf(integer);
    }

    public static Integer incInteger(Integer param)
    {
        int integer = param.intValue() + 1;

        if (integer >= 0 && integer < integers.length)
        {
            return integers[integer];
        }

        return Integer.valueOf(integer);
    }

    public static Integer decInteger(Integer param)
    {
        int integer = param.intValue() - 1;

        if (integer >= 0 && integer < integers.length)
        {
            return integers[integer];
        }

        return Integer.valueOf(integer);
    }

    public static int parseInt(byte[] bytes, int offset, int length)
    {
        if (bytes == null || length == 0 || offset >= bytes.length)
        {
            return INVALID_VALUE;
        }

        length += offset;

        if (length > bytes.length)
        {
            return INVALID_VALUE;
        }

        byte b;
        int  number = 0;

        for (int i = offset; i < length; i++)
        {
            b = bytes[i];

            if (b < '0' || b > '9')
            {
                return INVALID_VALUE;
            }

            number = number * 10 + (b - '0');
        }

        return number;
    }

    public static int parseInt(char[] chars, int offset, int length)
    {
        if (chars == null || length == 0 || offset >= chars.length)
        {
            return INVALID_VALUE;
        }

        length += offset;

        if (length > chars.length)
        {
            return INVALID_VALUE;
        }

        char ch;
        int  number = 0;

        for (int i = offset; i < length; i++)
        {
            ch = chars[i];

            if (ch < '0' || ch > '9')
            {
                return INVALID_VALUE;
            }

            number = number * 10 + (ch - '0');
        }

        return number;
    }

    public static int parseInt(String string)
    {
        if (string == null)
        {
            return INVALID_VALUE;
        }

        return internalParseInt(string, 0, string.length());
    }

    public static int parseInt(String string, int offset)
    {
        if (string == null || offset >= string.length())
        {
            return INVALID_VALUE;
        }

        return internalParseInt(string, offset, string.length() - offset);
    }

    public static int parseInt(String string, int offset, int length)
    {
        if (string == null || offset >= string.length() || length == 0)
        {
            return INVALID_VALUE;
        }

        if (length < 0)
        {
            length = string.length();
        }
        else if ((length + offset) > string.length())
        {
            return INVALID_VALUE;
        }

        return internalParseInt(string, offset, length);
    }

    public static int parseInts(String string, int[] ints)
    {
        int current = 0;
        char ch;
        int i = 0;

        for (i = 0; i < ints.length; i++)
        {
            ints[i] = 0;
        }

        for (i = 0; i < string.length(); i++)
        {
            ch = string.charAt(i);

            if (ch >= '0' && ch <= '9')
            {
                ints[current] = ints[current] * 10 + (ch - '0');
            }
            else if (ch == ' ')
            {
                for (; i < string.length() && ' ' == string.charAt(i); i++)
                {

                }

                if (i != string.length())
                {
                    current++;
                    if (current >= ints.length)
                    {
                        return INVALID_VALUE;
                    }
                    i--;
                }
            }
            else
            {
                return INVALID_VALUE;
            }
        }

        return current + 1;
    }

    private static int internalParseInt(String string, int offset, int length)
    {
        char b;
        int  wall  = offset+length;
        int  number = 0;

        for (int i = offset; i < wall; i++)
        {
            b = string.charAt(i);

            if (b < '0' || b > '9')
            {
                return INVALID_VALUE;
            }

            number = number * 10 + (b - '0');
        }

        return number;
    }

    public static int zeroIfNull(Integer integer)
    {
        if (integer != null)
        {
            return integer.intValue();
        }

        return 0;
    }

    public static int zeroIfNull(String string)
    {
        if (string != null)
        {
            return parseInt(string);
        }

        return 0;
    }

    public static int countDigits(int num)
    {
        if (num < 0)
        {
            return (num == Integer.MIN_VALUE) ?
                    LOG_10_INT_WHOLE_MIN_VALUE : 1+countDigits(-num);
        }

        if (num < NUMBER_100) // most common
        {
            if (num < NUMBER_10)
            {
               return 1;
            }

            return 2;
        }

        if (num < NUMBER_1_000) // next most common
        {
            return 3;
        }

        if (num < NUMBER_1_000_000)
        {
            if (num < NUMBER_10_000)
            {
                return 4;
            }

            if (num < NUMBER_100_000)
            {
               return 5;
            }

            return 6;
        }

        if (num < NUMBER_100_000_000)
        {
            if (num < NUMBER_10_000_000)
            {
                return 7;
            }

            return 8;
        }

        if (num < NUMBER_1_000_000_000)
        {
            return 9;
        }

        return 10;
    }

    public static int countDigits(int num, boolean minus)
    {
        if (minus)
        {
            return countDigits(num) + 1;
        }

        return countDigits(num);
    }

    public static int higherPowerOf2(int num)
    {
        int power;

        for (power = 1; power < num; power <<= 1)
        {

        }

        return power;
    }
}
