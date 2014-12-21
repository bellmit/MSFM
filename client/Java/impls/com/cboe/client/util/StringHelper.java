package com.cboe.client.util;

/**
 * StringHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;

import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;

public final class StringHelper
{
    public static final String[]   chars;
    public static final String[]   numbers;
    public static final char[][]   numbersChars;
    public static final String[][] prices;
    public static final String[]   zeroes          = new String[] {"", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000", "000000000", "0000000000", "00000000000"};
    public static final char[][]   zeroesChars     = {{'0'}, {'0'}, {'0','0'}, {'0','0','0'}, {'0','0','0','0'}, {'0','0','0','0','0'}, {'0','0','0','0','0','0'}, {'0','0','0','0','0','0','0'}, {'0','0','0','0','0','0','0','0'}, {'0','0','0','0','0','0','0','0','0'}, {'0','0','0','0','0','0','0','0','0','0'}, {'0','0','0','0','0','0','0','0','0','0','0'}};
    public static final String[]   spaces          = new String[] {"", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          ", "           "};
    public static final String[]   zeroPaddedStringValues_3;
    public static final char[][]   zeroPaddedCharsValues_3;
    public static final String[]   zeroPaddedStringValues_2 = new String[] {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"};
    public static final char[][]   penniesChars = new char[100][];
    public static final String[]   pennies = new String[] {".00", ".01", ".02", ".03", ".04", ".05", ".06", ".07", ".08", ".09", ".10", ".11", ".12", ".13", ".14", ".15", ".16", ".17", ".18", ".19", ".20", ".21", ".22", ".23", ".24", ".25", ".26", ".27", ".28", ".29", ".30", ".31", ".32", ".33", ".34", ".35", ".36", ".37", ".38", ".39", ".40", ".41", ".42", ".43", ".44", ".45", ".46", ".47", ".48", ".49", ".50", ".51", ".52", ".53", ".54", ".55", ".56", ".57", ".58", ".59", ".60", ".61", ".62", ".63", ".64", ".65", ".66", ".67", ".68", ".69", ".70", ".71", ".72", ".73", ".74", ".75", ".76", ".77", ".78", ".79", ".80", ".81", ".82", ".83", ".84", ".85", ".86", ".87", ".88", ".89", ".90", ".91", ".92", ".93", ".94", ".95", ".96", ".97", ".98", ".99"};

    public static final int NUMBER_1             = 1;
    public static final int NUMBER_10            = 10;
    public static final int NUMBER_100           = 100;
    public static final int NUMBER_1_000         = 1000;
    public static final int NUMBER_10_000        = 10000;
    public static final int NUMBER_100_000       = 100000;
    public static final int NUMBER_1_000_000     = 1000000;
    public static final int NUMBER_10_000_000    = 10000000;
    public static final int NUMBER_100_000_000   = 100000000;
    public static final int NUMBER_1_000_000_000 = 1000000000;

    private static final String INT_MIN = Integer.toString(Integer.MIN_VALUE);
    private static final String INT_MIN_COMMA = "-2,147,483,648";
    private static final String LONG_MIN_COMMA = "-9,223,372,036,854,775,808";

    static
    {
        chars                    = new String[256];
        numbers                  = new String[NUMBER_10_000]; // don't change it below 10000 -- otherwise you have to change all the calculations in this file
        numbersChars             = new char[numbers.length][];
        prices                   = new String[NUMBER_1_000][NUMBER_100];
        zeroPaddedStringValues_3 = new String[NUMBER_1_000];
        zeroPaddedCharsValues_3  = new char[zeroPaddedStringValues_3.length][];

        int i;
        int j;

        for (i = 0; i < chars.length; i++)
        {
            chars[i] = new Character((char) i).toString();
        }

        int magnitude = 1;
        char[]  temp;

        for (i = 0; i < numbers.length; i++)
        {
            if (i == NUMBER_10)
            {
                magnitude++;
            }
            else if (i == NUMBER_100)
            {
                magnitude++;
            }
            else if (i == NUMBER_1_000)
            {
                magnitude++;
            }
            else if (i == NUMBER_10_000)
            {
                magnitude++;
            }

            temp = numbersChars[i] = new char[magnitude];

            switch (magnitude)
            {
                case 0:
                    break;
                case 1:
                    temp[0] = (char) ('0' + i);
                    numbers[i]                  = new String(temp, 0, magnitude);
                    zeroPaddedCharsValues_3[i]  = new char[] {'0', '0', temp[0]};
                    zeroPaddedStringValues_3[i] = new String(zeroPaddedCharsValues_3[i]);
                    break;
                case 2:
                    temp[0] = (char) ('0' + (i / 10));
                    temp[1] = (char) ('0' + (i % 10));
                    numbers[i]                  = new String(temp, 0, magnitude);
                    zeroPaddedCharsValues_3[i]  = new char[] {'0', temp[0], temp[1]};
                    zeroPaddedStringValues_3[i] = new String(zeroPaddedCharsValues_3[i]);
                    break;
                case 3:
                    temp[0] = (char) ('0' + (i / 100));
                    temp[1] = (char) ('0' + (i / 10) % 10);
                    temp[2] = (char) ('0' + (i % 10));
                    numbers[i]                  = new String(temp, 0, magnitude);
                    zeroPaddedCharsValues_3[i]  = temp;
                    zeroPaddedStringValues_3[i] = numbers[i];
                    break;
                case 4:
                    temp[0] = (char) ('0' + (i / 1000));
                    temp[1] = (char) ('0' + (i / 100) % 10);
                    temp[2] = (char) ('0' + (i / 10)  % 10);
                    temp[3] = (char) ('0' + (i % 10));
                    numbers[i]                  = new String(temp);
                    break;
                case 5:
                    temp[0] = (char) ('0' + (i / 10000));
                    temp[1] = (char) ('0' + (i / 1000) % 10);
                    temp[2] = (char) ('0' + (i / 100)  % 10);
                    temp[3] = (char) ('0' + (i / 10)   % 10);
                    temp[4] = (char) ('0' + (i % 10));
                    numbers[i]                  = new String(temp);
                    break;
            }
        }

        for (i = 0; i < 100; i++)
        {
            penniesChars[i] = stringGetChars(pennies[i]);
        }

        StringBuilder buffer = new StringBuilder(8); // 19999.99
        for (i = 0; i < prices.length; i++)
        {
            for (j = 0; j < 100; j++)
            {
                buffer.append(numbers[i]);
                buffer.append(pennies[j]);
                prices[i][j] = buffer.toString();
                buffer.setLength(0);
            }
        }
    }

    public static int populateBufFromInt(char[] buf, int number, boolean minus)
    {
        int i;

        // write the number into buf[], starting at the end and moving towards the front
        for (i = buf.length - 1; i >= 0; i--)
        {
           buf[i] = (char) ('0' + (number % 10));
           number /= 10;
           if (number == 0) // keep this check here, don't move it to the for loop
           {
               if (minus)
               {
                  buf[--i] = '-';
               }
               break;
           }
        }

        return i;
    }

    public static int populateBufFromInt(char[] buf, int whole, int fraction, boolean minus)
    {
        int  i;
        char fractionByte;
        int  magnitude = 0;
        byte nonZero = 0;
        
        if (fraction < 0) {fraction = -fraction;}

        // write the fraction first into buf[], starting at the end and moving towards the front
        for (i = buf.length; i >= 0; )
        {
            magnitude++;

            fractionByte = (char) ('0' + (fraction % 10));
            nonZero |= fractionByte - '0';
            if (nonZero != 0)
            {
                buf[--i] = fractionByte;
            }

            fraction /= 10;
            if (fraction == 0) // keep this check here, don't move it to the for loop
            {
                break;
            }
        }

        switch (magnitude)
        {
            case 1: buf[--i] = '0'; // no break
            case 2: buf[--i] = '0'; // no break
            case 3: buf[--i] = '0'; // no break
            case 4: buf[--i] = '0'; // no break
            case 5: buf[--i] = '0'; // no break
            case 6: buf[--i] = '0'; // no break
            case 7: buf[--i] = '0'; // no break
            case 8: buf[--i] = '0'; // no break
        }

        buf[--i] = '.';

        // write the whole into buf[], starting at the end and moving towards the front
        for (--i; i >= 0; i--)
        {
           buf[i] = (char) ('0' + (whole % 10));
           whole /= 10;
           if (whole == 0) // keep this check here, don't move it to the for loop
           {
               if (minus)
               {
                  buf[--i] = '-';
               }
               break;
           }
        }

        return i;
    }

    public static String intToString(int number)
    {
        if (number < 0)
        {
            if (number == Integer.MIN_VALUE)
            {
                // Can't force this value positive, handle as special case.
                return INT_MIN;
            }
            number = -number;

            // see if the number is already pre-defined in the lookup table
            if (number < numbers.length)
            {
                return "-" + numbers[number];
            }
            number = -number;   // restore original value
        }
        else
        {
            // see if the number is already pre-defined in the lookup table
            if (number < numbers.length)
            {
                return numbers[number];
            }
        }

        return Integer.toString(number);
    }

    public static String priceFractionToString(int whole, int fraction)
    {
        boolean minus = whole < 0;
        if (minus)
        {
           whole = -whole;
        }

        if (fraction < 0) {fraction = -fraction;}

        // see if the number is already pre-defined in the lookup table
        if (whole < numbers.length && fraction == 0)
        {
            if (minus)
            {
                return new StringBuilder("-").append(numbers[whole]).toString();
            }

            return numbers[whole];
        }

        final int cents = fraction / (PriceScale.DEFAULT_SCALE / 100);
        final int subCents = fraction % (PriceScale.DEFAULT_SCALE / 100);
        if (whole < prices.length && subCents == 0)
        {
            if (minus)
            {
                return new StringBuilder("-").append(prices[whole][cents]).toString();
            }
            return prices[whole][cents];
        }

        if (subCents == 0)
        {
            StringBuilder buffer = new StringBuilder();

            if (minus)
            {
                buffer.append('-');
            }

            appendInt(buffer, whole);
            buffer.append(penniesChars[cents]);

            return buffer.toString();
        }

        char[] buf = new char[IntegerHelper.LOG_10_INT_WHOLE_WITH_FRACTION_MIN_VALUE];

        int startingDigit = populateBufFromInt(buf, whole, fraction, minus);

        // create new string with buf[] starting at the startingDigit
        return new String(buf, startingDigit, buf.length - startingDigit);
    }

    public static Writer appendPriceStruct(Writer writer, PriceStruct priceStruct) throws Exception
    {
        int whole    = priceStruct.whole;
        int fraction = priceStruct.fraction;
        /*
        PriceStruct:
        for an order with price -5.30, it looks like
        price.whole = -5
        price.fraction = -300000000

        for an order with price -0.05, it looks like
        price.whole = 0
        price.fraction = -50000000

        for an order with price -5.00, it looks like
        price.whole = -5
        price.fraction = 0
        */
        if (whole == 0)
        {
            if (fraction < 0)
            {
                fraction = -fraction;
                writer.write('-');
            }
        }else
        {
            if (whole < 0)
            {
                whole = -whole;
                writer.write('-');
                if (fraction < 0)
                    fraction = -fraction;
            }
        }

        // see if the number is already pre-defined in the lookup table
        if (whole < numbersChars.length && fraction == 0)
        {
            writer.write(numbersChars[whole]);
            return writer;
        }

        int rem = fraction % (PriceScale.DEFAULT_SCALE / 100);
        if (rem == 0)
        {
            if (whole < numbersChars.length)
            {
                writer.write(numbersChars[whole]);
                writer.write(penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else if (whole < NUMBER_1_000_000) // 123,456
            {
                writer.write(trimNumberCharsThousand_x_xxx_000_xxx(whole));
                writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(whole));
                writer.write(penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else if (whole < NUMBER_1_000_000_000) // 123,456,789
            {
                writer.write(trimNumberCharsThousand_x_000_xxx_xxx(whole));
                writer.write(zeroNumberCharsThousand_x_xxx_000_xxx(whole));
                writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(whole));
                writer.write(penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else // 1,234,567,890
            {
                writer.write(trimNumberCharsThousand_0_xxx_xxx_xxx(whole));
                writer.write(zeroNumberCharsThousand_x_000_xxx_xxx(whole));
                writer.write(zeroNumberCharsThousand_x_xxx_000_xxx(whole));
                writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(whole));
                writer.write(penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }

            return writer;
        }

        char[] buf = new char[IntegerHelper.LOG_10_INT_WHOLE_WITH_FRACTION_MAX_VALUE];

        int startingDigit = populateBufFromInt(buf, whole, fraction, false);

        writer.write(buf, startingDigit, buf.length - startingDigit);
        return writer;
    }

    public static FastCharacterWriter appendPriceStruct(FastCharacterWriter writer, PriceStruct priceStruct)
    {
        int whole    = priceStruct.whole;
        int fraction = priceStruct.fraction;
        /*
        PriceStruct:
        for an order with price -5.30, it looks like
        price.whole = -5
        price.fraction = -300000000

        for an order with price -0.05, it looks like
        price.whole = 0
        price.fraction = -50000000

        for an order with price -5.00, it looks like
        price.whole = -5
        price.fraction = 0
        */
        boolean minus = false;
        if (whole == 0)
        {
            if (fraction < 0)
            {
                fraction = -fraction;
                minus = true;
                writer.write('-');
            }
        }else
        {
            if (whole < 0)
            {
                whole = -whole;
                minus = true;
                writer.write('-');
                if (fraction < 0)
                    fraction = -fraction;
            }
        }

        // see if the number is already pre-defined in the lookup table
        if (whole < numbersChars.length && fraction == 0)
        {
            writer.write(numbersChars[whole]);
            return writer;
        }

        int rem = fraction % (PriceScale.DEFAULT_SCALE / 100);
        if (rem == 0)
        {
            if (whole < numbersChars.length)
            {
                writer.write(numbersChars[whole],
                             penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else if (whole < NUMBER_1_000_000) // 123,456
            {
                writer.write(trimNumberCharsThousand_x_xxx_000_xxx(whole),
                             zeroNumberCharsThousand_x_xxx_xxx_000(whole),
                             penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else if (whole < NUMBER_1_000_000_000) // 123,456,789
            {
                writer.write(trimNumberCharsThousand_x_000_xxx_xxx(whole),
                             zeroNumberCharsThousand_x_xxx_000_xxx(whole),
                             zeroNumberCharsThousand_x_xxx_xxx_000(whole),
                             penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else // 1,234,567,890
            {
                writer.write(trimNumberCharsThousand_0_xxx_xxx_xxx(whole),
                             zeroNumberCharsThousand_x_000_xxx_xxx(whole),
                             zeroNumberCharsThousand_x_xxx_000_xxx(whole),
                             zeroNumberCharsThousand_x_xxx_xxx_000(whole),
                             penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }

            return writer;
        }

        char[] buf = new char[IntegerHelper.LOG_10_INT_WHOLE_WITH_FRACTION_MIN_VALUE];

        int startingDigit = populateBufFromInt(buf, whole, fraction, minus);

        writer.write(buf, startingDigit, buf.length - startingDigit);
        return writer;
    }

    public static String mapPriceStructToPriceString(PriceStruct priceStruct)
    {
        int whole    = priceStruct.whole;
        int fraction = priceStruct.fraction;
        StringBuilder strB = new StringBuilder();
        /*
        PriceStruct:
        for an order with price -5.30, it looks like
        price.whole = -5
        price.fraction = -300000000

        for an order with price -0.05, it looks like
        price.whole = 0
        price.fraction = -50000000

        for an order with price -5.00, it looks like
        price.whole = -5
        price.fraction = 0
        */
        boolean minus = false;
        if (whole == 0)
        {
            if (fraction < 0)
            {
                fraction = -fraction;
                minus = true;
                strB.append('-');
            }
        }else
        {
            if (whole < 0)
            {
                whole = -whole;
                minus = true;
                strB.append('-');
                if (fraction < 0)
                    fraction = -fraction;
            }
        }

        // see if the number is already pre-defined in the lookup table
        if (whole < numbersChars.length && fraction == 0)
        {
            strB.append(numbersChars[whole]);
            return strB.toString();
        }

        int rem = fraction % (PriceScale.DEFAULT_SCALE / 100);
        if (rem == 0)
        {
            if (whole < numbersChars.length)
            {
                strB.append(numbersChars[whole]).append(penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else if (whole < NUMBER_1_000_000) // 123,456
            {
                strB.append(trimNumberCharsThousand_x_xxx_000_xxx(whole))
                        .append(zeroNumberCharsThousand_x_xxx_xxx_000(whole))
                        .append(penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else if (whole < NUMBER_1_000_000_000) // 123,456,789
            {
                strB.append(trimNumberCharsThousand_x_000_xxx_xxx(whole))
                         .append(zeroNumberCharsThousand_x_xxx_000_xxx(whole))
                         .append(zeroNumberCharsThousand_x_xxx_xxx_000(whole))
                         .append(penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }
            else // 1,234,567,890
            {
                strB.append(trimNumberCharsThousand_0_xxx_xxx_xxx(whole))
                         .append(zeroNumberCharsThousand_x_000_xxx_xxx(whole))
                         .append(zeroNumberCharsThousand_x_xxx_000_xxx(whole))
                         .append(zeroNumberCharsThousand_x_xxx_xxx_000(whole))
                         .append(penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)]);
            }

            return strB.toString();
        }

        char[] srcBuf = new char[IntegerHelper.LOG_10_INT_WHOLE_WITH_FRACTION_MIN_VALUE];
        int startingDigit = populateBufFromInt(srcBuf, whole, fraction, minus);
        strB.append(srcBuf, startingDigit, srcBuf.length - startingDigit);

        return strB.toString();
    }

    public static char[] mapPriceStructToPriceCharArray(PriceStruct priceStruct)
    {
        int whole    = priceStruct.whole;
        int fraction = priceStruct.fraction;
        char[] returnCharBuffer = new char[8];
        /*
        PriceStruct:
        for an order with price -5.30, it looks like
        price.whole = -5
        price.fraction = -300000000

        for an order with price -0.05, it looks like
        price.whole = 0
        price.fraction = -50000000

        for an order with price -5.00, it looks like
        price.whole = -5
        price.fraction = 0
        */
        int size = 0;
        boolean minus = false;
        if (whole == 0)
        {
            if (fraction < 0)
            {
                fraction = -fraction;
                minus = true;
                returnCharBuffer[size++] = '-';
            }
        }else
        {
            if (whole < 0)
            {
                whole = -whole;
                minus = true;
                returnCharBuffer[size++] = '-';
                if (fraction < 0)
                    fraction = -fraction;
            }
        }

        // see if the number is already pre-defined in the lookup table
        if (whole < numbersChars.length && fraction == 0)
        {
            char[] buf1 = numbersChars[whole];
            ensureCapacity(returnCharBuffer, size, buf1.length);
            for (int i = 0; i < buf1.length; i++)
            {
                returnCharBuffer[size++] = buf1[i];
            }
            return returnCharBuffer;
        }

        int rem = fraction % (PriceScale.DEFAULT_SCALE / 100);
        if (rem == 0)
        {
            if (whole < numbersChars.length)
            {
                char[] buf1 = numbersChars[whole];
                char[] buf2 = penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)];
                ensureCapacity(returnCharBuffer, size, buf1.length + buf2.length);
                for (int i = 0; i < buf1.length; i++)
                {
                    returnCharBuffer[size++] = buf1[i];
                }
                for (int i = 0; i < buf2.length; i++)
                {
                    returnCharBuffer[size++] = buf2[i];
                }
            }
            else if (whole < NUMBER_1_000_000) // 123,456
            {
                char[] buf1 = trimNumberCharsThousand_x_xxx_000_xxx(whole);
                char[] buf2 = zeroNumberCharsThousand_x_xxx_xxx_000(whole);
                char[] buf3 = penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)];
                ensureCapacity(returnCharBuffer, size, buf1.length + buf2.length + buf3.length);
                for (int i = 0; i < buf1.length; i++)
                {
                    returnCharBuffer[size++] = buf1[i];
                }
                for (int i = 0; i < buf2.length; i++)
                {
                    returnCharBuffer[size++] = buf2[i];
                }
                for (int i = 0; i < buf3.length; i++)
                {
                    returnCharBuffer[size++] = buf3[i];
                }
            }
            else if (whole < NUMBER_1_000_000_000) // 123,456,789
            {
                char[] buf1 = trimNumberCharsThousand_x_000_xxx_xxx(whole);
                char[] buf2 = trimNumberCharsThousand_x_xxx_000_xxx(whole);
                char[] buf3 = zeroNumberCharsThousand_x_xxx_xxx_000(whole);
                char[] buf4 = penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)];
                ensureCapacity(returnCharBuffer, size, buf1.length + buf2.length + buf3.length + buf4.length);
                for (int i = 0; i < buf1.length; i++)
                {
                    returnCharBuffer[size++] = buf1[i];
                }
                for (int i = 0; i < buf2.length; i++)
                {
                    returnCharBuffer[size++] = buf2[i];
                }
                for (int i = 0; i < buf3.length; i++)
                {
                    returnCharBuffer[size++] = buf3[i];
                }
                for (int i = 0; i < buf4.length; i++)
                {
                    returnCharBuffer[size++] = buf4[i];
                }
            }
            else // 1,234,567,890
            {
                char[] buf1 = trimNumberCharsThousand_0_xxx_xxx_xxx(whole);
                char[] buf2 = trimNumberCharsThousand_x_000_xxx_xxx(whole);
                char[] buf3 = trimNumberCharsThousand_x_xxx_000_xxx(whole);
                char[] buf4 = zeroNumberCharsThousand_x_xxx_xxx_000(whole);
                char[] buf5 = penniesChars[fraction / (PriceScale.DEFAULT_SCALE / 100)];
                ensureCapacity(returnCharBuffer, size, buf1.length + buf2.length + buf3.length + buf4.length + buf5.length);
                for (int i = 0; i < buf1.length; i++)
                {
                    returnCharBuffer[size++] = buf1[i];
                }
                for (int i = 0; i < buf2.length; i++)
                {
                    returnCharBuffer[size++] = buf2[i];
                }
                for (int i = 0; i < buf3.length; i++)
                {
                    returnCharBuffer[size++] = buf3[i];
                }
                for (int i = 0; i < buf4.length; i++)
                {
                    returnCharBuffer[size++] = buf4[i];
                }
                for (int i = 0; i < buf5.length; i++)
                {
                    returnCharBuffer[size++] = buf5[i];
                }
            }

            return returnCharBuffer;
        }

        // New srcBuf - is a character array that will be copied into charB
        char[] srcBuf = new char[IntegerHelper.LOG_10_INT_WHOLE_WITH_FRACTION_MIN_VALUE];
        int startingDigit = populateBufFromInt(srcBuf, whole, fraction, minus);

        // Ensure capacity of original array charB
        ensureCapacity(returnCharBuffer, size, srcBuf.length - startingDigit);

        // Copy srcBuf to charB
        System.arraycopy(srcBuf, startingDigit, returnCharBuffer, size, srcBuf.length - startingDigit);
        size += srcBuf.length;

        return returnCharBuffer;
    }

    public static void ensureCapacity(char[] charArray, int size, int additional)
    {
        if (size + additional >= charArray.length)
        {
            charArray = CollectionHelper.arrayclone(charArray, 0, size, Math.max(size + additional, size << 1));
        }
    }

    public static Writer appendPriceWithScale(Writer writer, int price, byte priceScale) throws Exception
    {
        int divScale;
        int divScalePennies;
        switch (priceScale)
        {
            case 0:
            {
                divScale = StringHelper.NUMBER_1;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 1:
            {
                divScale = StringHelper.NUMBER_10;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 2:
            {
                divScale = StringHelper.NUMBER_100;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 3:
            {
                divScale = StringHelper.NUMBER_1_000;
                divScalePennies = StringHelper.NUMBER_10;
                break;
            }
            case 4:
            {
                divScale = StringHelper.NUMBER_10_000;
                divScalePennies = StringHelper.NUMBER_100;
                break;
            }
            case 5:
            {
                divScale = StringHelper.NUMBER_100_000;
                divScalePennies = StringHelper.NUMBER_1_000;
                break;
            }
            case 6:
            {
                divScale = StringHelper.NUMBER_1_000_000;
                divScalePennies = StringHelper.NUMBER_10_000;
                break;
            }
            case 7:
            {
                divScale = StringHelper.NUMBER_10_000_000;
                divScalePennies = StringHelper.NUMBER_100_000;
                break;
            }
            case 8:
            {
                divScale = StringHelper.NUMBER_100_000_000;
                divScalePennies = StringHelper.NUMBER_1_000_000;
                break;
            }
            case 9:
            {
                divScale = StringHelper.NUMBER_1_000_000_000;
                divScalePennies = StringHelper.NUMBER_10_000_000;
                break;
            }
            default:
            {
                divScale = 10^priceScale;
                divScalePennies = 10^(priceScale - 2);
                break;
            }
        }

        int whole;
        int tmpFraction;
        int fraction;
        if (price < 0)
        {
            writer.write('-');
            int tmpPrice = -price;
            whole = tmpPrice/divScale;
            tmpFraction = tmpPrice % divScale;
            fraction = tmpFraction/divScalePennies;
        } else
        {
            whole = price/divScale;
            tmpFraction = price % divScale;
            fraction = tmpFraction/divScalePennies;
        }

        if (whole < numbersChars.length && fraction == 0)
        {
            writer.write(numbersChars[whole]);
            return writer;
        } else if (whole < numbersChars.length )
        {
            writer.write(numbersChars[whole]);
            writer.write(penniesChars[fraction]);
            return writer;
        }else if (whole < NUMBER_1_000_000) // 123,456
        {
            writer.write(trimNumberCharsThousand_x_xxx_000_xxx(whole));
            writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(whole));
            writer.write(penniesChars[fraction]);
            return writer;
        }
        else if (whole < NUMBER_1_000_000_000) // 123,456,789
        {
            writer.write(trimNumberCharsThousand_x_000_xxx_xxx(whole));
            writer.write(zeroNumberCharsThousand_x_xxx_000_xxx(whole));
            writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(whole));
            writer.write(penniesChars[fraction]);
            return writer;
        }
        else // 1,234,567,890
        {
            writer.write(trimNumberCharsThousand_0_xxx_xxx_xxx(whole));
            writer.write(zeroNumberCharsThousand_x_000_xxx_xxx(whole));
            writer.write(zeroNumberCharsThousand_x_xxx_000_xxx(whole));
            writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(whole));
            writer.write(penniesChars[fraction]);
            return writer;
        }
    }

    public static FastCharacterWriter appendPriceWithScale(FastCharacterWriter writer, int price, byte priceScale)
    {
        int divScale;
        int divScalePennies;
        switch (priceScale)
        {
            case 0:
            {
                divScale = StringHelper.NUMBER_1;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 1:
            {
                divScale = StringHelper.NUMBER_10;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 2:
            {
                divScale = StringHelper.NUMBER_100;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 3:
            {
                divScale = StringHelper.NUMBER_1_000;
                divScalePennies = StringHelper.NUMBER_10;
                break;
            }
            case 4:
            {
                divScale = StringHelper.NUMBER_10_000;
                divScalePennies = StringHelper.NUMBER_100;
                break;
            }
            case 5:
            {
                divScale = StringHelper.NUMBER_100_000;
                divScalePennies = StringHelper.NUMBER_1_000;
                break;
            }
            case 6:
            {
                divScale = StringHelper.NUMBER_1_000_000;
                divScalePennies = StringHelper.NUMBER_10_000;
                break;
            }
            case 7:
            {
                divScale = StringHelper.NUMBER_10_000_000;
                divScalePennies = StringHelper.NUMBER_100_000;
                break;
            }
            case 8:
            {
                divScale = StringHelper.NUMBER_100_000_000;
                divScalePennies = StringHelper.NUMBER_1_000_000;
                break;
            }
            case 9:
            {
                divScale = StringHelper.NUMBER_1_000_000_000;
                divScalePennies = StringHelper.NUMBER_10_000_000;
                break;
            }
            default:
            {
                divScale = 10^priceScale;
                divScalePennies = 10^(priceScale - 2);
                break;
            }
        }

        int whole;
        int tmpFraction;
        int fraction;
        if (price < 0)
        {
            writer.write('-');
            int tmpPrice = -price;
            whole = tmpPrice/divScale;
            tmpFraction = tmpPrice % divScale;
            fraction = tmpFraction/divScalePennies;
        } else
        {
            whole = price/divScale;
            tmpFraction = price % divScale;
            fraction = tmpFraction/divScalePennies;
        }

        if (whole < numbersChars.length && fraction == 0)
        {
            writer.write(numbersChars[whole]);
            return writer;
        } else if (whole < numbersChars.length)
        {
            writer.write(numbersChars[whole],
                         penniesChars[fraction]);
            return writer;
        }
        else if (whole < NUMBER_1_000_000) // 123,456
        {
            writer.write(trimNumberCharsThousand_x_xxx_000_xxx(whole),
                         zeroNumberCharsThousand_x_xxx_xxx_000(whole),
                         penniesChars[fraction]);
            return writer;
        }
        else if (whole < NUMBER_1_000_000_000) // 123,456,789
        {
            writer.write(trimNumberCharsThousand_x_000_xxx_xxx(whole),
                         zeroNumberCharsThousand_x_xxx_000_xxx(whole),
                         zeroNumberCharsThousand_x_xxx_xxx_000(whole),
                         penniesChars[fraction]);
            return writer;
        }
        else // 1,234,567,890
        {
            writer.write(trimNumberCharsThousand_0_xxx_xxx_xxx(whole),
                         zeroNumberCharsThousand_x_000_xxx_xxx(whole),
                         zeroNumberCharsThousand_x_xxx_000_xxx(whole),
                         zeroNumberCharsThousand_x_xxx_xxx_000(whole),
                         penniesChars[fraction]);
            return writer;
        }

    }

    public static char[] appendPriceWithScale(int price, byte priceScale)
    {
        int divScale;
        int divScalePennies;
        switch (priceScale)
        {
            case 0:
            {
                divScale = StringHelper.NUMBER_1;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 1:
            {
                divScale = StringHelper.NUMBER_10;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 2:
            {
                divScale = StringHelper.NUMBER_100;
                divScalePennies = StringHelper.NUMBER_1;
                break;
            }
            case 3:
            {
                divScale = StringHelper.NUMBER_1_000;
                divScalePennies = StringHelper.NUMBER_10;
                break;
            }
            case 4:
            {
                divScale = StringHelper.NUMBER_10_000;
                divScalePennies = StringHelper.NUMBER_100;
                break;
            }
            case 5:
            {
                divScale = StringHelper.NUMBER_100_000;
                divScalePennies = StringHelper.NUMBER_1_000;
                break;
            }
            case 6:
            {
                divScale = StringHelper.NUMBER_1_000_000;
                divScalePennies = StringHelper.NUMBER_10_000;
                break;
            }
            case 7:
            {
                divScale = StringHelper.NUMBER_10_000_000;
                divScalePennies = StringHelper.NUMBER_100_000;
                break;
            }
            case 8:
            {
                divScale = StringHelper.NUMBER_100_000_000;
                divScalePennies = StringHelper.NUMBER_1_000_000;
                break;
            }
            case 9:
            {
                divScale = StringHelper.NUMBER_1_000_000_000;
                divScalePennies = StringHelper.NUMBER_10_000_000;
                break;
            }
            default:
            {
                divScale = 10^priceScale;
                divScalePennies = 10^(priceScale - 2);
                break;
            }
        }

        char[] returnCharBuffer = new char[8];
        int size = 0;
        int whole;
        int tmpFraction;
        int fraction;

        if (price < 0)
        {
            returnCharBuffer[size++] = '-';
            int tmpPrice = -price;
            whole = tmpPrice/divScale;
            tmpFraction = tmpPrice % divScale;
            fraction = tmpFraction/divScalePennies;
        } else
        {
            whole = price/divScale;
            tmpFraction = price % divScale;
            fraction = tmpFraction/divScalePennies;
        }

        if (whole < numbersChars.length && fraction == 0)
        {
            char[] buf1 = numbersChars[whole];
            ensureCapacity(returnCharBuffer, size, buf1.length);
            for (int i = 0; i < buf1.length; i++)
            {
                returnCharBuffer[size++] = buf1[i];
            }
            return returnCharBuffer;
        }
        // Fraction is non-zero
        else if (whole < numbersChars.length)
        {
            char[] buf1 = numbersChars[whole];
            char[] buf2 = penniesChars[fraction];
            ensureCapacity(returnCharBuffer, size, buf1.length + buf2.length);
            for (int i = 0; i < buf1.length; i++)
            {
                returnCharBuffer[size++] = buf1[i];
            }
            for (int i = 0; i < buf2.length; i++)
            {
                returnCharBuffer[size++] = buf2[i];
            }
            return returnCharBuffer;
        }
        else if (whole < NUMBER_1_000_000) // 123,456
        {
            char[] buf1 = trimNumberCharsThousand_x_xxx_000_xxx(whole);
            char[] buf2 = zeroNumberCharsThousand_x_xxx_xxx_000(whole);
            char[] buf3 = penniesChars[fraction];
            ensureCapacity(returnCharBuffer, size, buf1.length + buf2.length + buf3.length);
            for (int i = 0; i < buf1.length; i++)
            {
                returnCharBuffer[size++] = buf1[i];
            }
            for (int i = 0; i < buf2.length; i++)
            {
                returnCharBuffer[size++] = buf2[i];
            }
            for (int i = 0; i < buf3.length; i++)
            {
                returnCharBuffer[size++] = buf3[i];
            }
            return returnCharBuffer;
        }
        else if (whole < NUMBER_1_000_000_000) // 123,456,789
        {
            char[] buf1 = trimNumberCharsThousand_x_000_xxx_xxx(whole);
            char[] buf2 = trimNumberCharsThousand_x_xxx_000_xxx(whole);
            char[] buf3 = zeroNumberCharsThousand_x_xxx_xxx_000(whole);
            char[] buf4 = penniesChars[fraction];
            ensureCapacity(returnCharBuffer, size, buf1.length + buf2.length + buf3.length + buf4.length);
            for (int i = 0; i < buf1.length; i++)
            {
                returnCharBuffer[size++] = buf1[i];
            }
            for (int i = 0; i < buf2.length; i++)
            {
                returnCharBuffer[size++] = buf2[i];
            }
            for (int i = 0; i < buf3.length; i++)
            {
                returnCharBuffer[size++] = buf3[i];
            }
            for (int i = 0; i < buf4.length; i++)
            {
                returnCharBuffer[size++] = buf4[i];
            }
            return returnCharBuffer;
        }
        else // 1,234,567,890
        {
            char[] buf1 = trimNumberCharsThousand_0_xxx_xxx_xxx(whole);
            char[] buf2 = trimNumberCharsThousand_x_000_xxx_xxx(whole);
            char[] buf3 = trimNumberCharsThousand_x_xxx_000_xxx(whole);
            char[] buf4 = zeroNumberCharsThousand_x_xxx_xxx_000(whole);
            char[] buf5 = penniesChars[fraction];
            ensureCapacity(returnCharBuffer, size, buf1.length + buf2.length + buf3.length + buf4.length + buf5.length);
            for (int i = 0; i < buf1.length; i++)
            {
                returnCharBuffer[size++] = buf1[i];
            }
            for (int i = 0; i < buf2.length; i++)
            {
                returnCharBuffer[size++] = buf2[i];
            }
            for (int i = 0; i < buf3.length; i++)
            {
                returnCharBuffer[size++] = buf3[i];
            }
            for (int i = 0; i < buf4.length; i++)
            {
                returnCharBuffer[size++] = buf4[i];
            }
            for (int i = 0; i < buf5.length; i++)
            {
                returnCharBuffer[size++] = buf5[i];
            }
            return returnCharBuffer;
        }
    }


    public static StringBuilder appendInt(StringBuilder buffer, int number)
    {
        boolean minus = number < 0;

        if (minus) // if negative, just add a minus sign
        {
           if (number == Integer.MIN_VALUE)
           {
               // Can't force this value positive, handle as special case.
               return buffer.append(INT_MIN);
           }
           number = -number;
           buffer.append('-');
        }

        // see if the number is already pre-defined in the lookup table
        if (number < numbers.length)
        {
            buffer.append(numbers[number]);
        }
        else if (number < NUMBER_1_000_000) // 123,456
        {
            buffer.append(trimNumberCharsThousand_x_xxx_000_xxx(number))
                  .append(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else if (number < NUMBER_1_000_000_000) // 123,456,789
        {
            buffer.append(trimNumberCharsThousand_x_000_xxx_xxx(number))
                  .append(zeroNumberCharsThousand_x_xxx_000_xxx(number))
                  .append(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else // 1,234,567,890
        {
            buffer.append(trimNumberCharsThousand_0_xxx_xxx_xxx(number))
                  .append(zeroNumberCharsThousand_x_000_xxx_xxx(number))
                  .append(zeroNumberCharsThousand_x_xxx_000_xxx(number))
                  .append(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }

        return buffer;
    }

    public static Writer appendInt(Writer writer, int number) throws Exception
    {
        boolean minus = number < 0;

        if (minus) // if negative, just add a minus sign
        {
            if (number == Integer.MIN_VALUE)
            {
                // Can't force this value positive, handle as special case.
                writer.write(INT_MIN);
                return writer;
            }
            writer.write('-');
            number = -number;
        }

        // see if the number is already pre-defined in the lookup table
        if (number < numbers.length)
        {
            writer.write(numbers[number]);
        }
        else if (number < NUMBER_1_000_000) // 123,456
        {
            writer.write(trimNumberCharsThousand_x_xxx_000_xxx(number));
            writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else if (number < NUMBER_1_000_000_000) // 123,456,789
        {
            writer.write(trimNumberCharsThousand_x_000_xxx_xxx(number));
            writer.write(zeroNumberCharsThousand_x_xxx_000_xxx(number));
            writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else // 1,234,567,890
        {
            writer.write(trimNumberCharsThousand_0_xxx_xxx_xxx(number));
            writer.write(zeroNumberCharsThousand_x_000_xxx_xxx(number));
            writer.write(zeroNumberCharsThousand_x_xxx_000_xxx(number));
            writer.write(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }

        return writer;
    }

    public static FastCharacterWriter appendInt(FastCharacterWriter writer, int number)
    {
        boolean minus = number < 0;

        if (minus) // if negative, just add a minus sign
        {
           if (number == Integer.MIN_VALUE)
           {
               // Can't force this value positive, handle as special case.
               writer.write(INT_MIN);
               return writer;
           }
           number = -number;
           writer.write('-');
        }

        // see if the number is already pre-defined in the lookup table
        if (number < numbersChars.length)
        {
            writer.write(numbersChars[number]);
        }
        else if (number < NUMBER_1_000_000) // 123,456
        {
            writer.write(trimNumberCharsThousand_x_xxx_000_xxx(number),
                         zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else if (number < NUMBER_1_000_000_000) // 123,456,789
        {
            writer.write(trimNumberCharsThousand_x_000_xxx_xxx(number),
                         zeroNumberCharsThousand_x_xxx_000_xxx(number),
                         zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else // 1,234,567,890
        {
            writer.write(trimNumberCharsThousand_0_xxx_xxx_xxx(number),
                         zeroNumberCharsThousand_x_000_xxx_xxx(number),
                         zeroNumberCharsThousand_x_xxx_000_xxx(number),
                         zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }

        return writer;
    }

    public static String breakString(String s, int sectionSize, char separatorChar)
    {
        int len = s.length();

        if (len <= sectionSize)
        {
            return s;
        }

        int nSeparators = (len-1)/sectionSize;
        StringBuilder buffer = new StringBuilder(len+nSeparators);
        int i;
        for (i = 0; i + sectionSize < len; i += sectionSize)
        {
            buffer.append(s.substring(i, i+sectionSize))
                  .append(separatorChar);
        }
        buffer.append(s.substring(i));

        return buffer.toString();
    }

    public static String intToStringWithCommas(int number)
    {
        if (number == 0)
        {
            return numbers[0];
        }

        StringBuilder buffer = new StringBuilder();

        if (number < 0)
        {
            if (number == Integer.MIN_VALUE)
            {
                // Can't force this value positive, handle as special case.
                return INT_MIN_COMMA;
            }
            number = -number;
            buffer.append('-');
        }

        if (number < NUMBER_1_000) // 123
        {
            buffer.append(numbersChars[number]);
        }
        else if (number < NUMBER_1_000_000) // 123,456
        {
            buffer.append(trimNumberCharsThousand_x_xxx_000_xxx(number))
                  .append(',')
                  .append(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else if (number < NUMBER_1_000_000_000) // 123,456,789
        {
            buffer.append(trimNumberCharsThousand_x_000_xxx_xxx(number))
                  .append(',')
                  .append(zeroNumberCharsThousand_x_xxx_000_xxx(number))
                  .append(',')
                  .append(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }
        else // 1,234,567,890
        {
            buffer.append(trimNumberCharsThousand_0_xxx_xxx_xxx(number))
                  .append(',')
                  .append(zeroNumberCharsThousand_x_000_xxx_xxx(number))
                  .append(',')
                  .append(zeroNumberCharsThousand_x_xxx_000_xxx(number))
                  .append(',')
                  .append(zeroNumberCharsThousand_x_xxx_xxx_000(number));
        }

        return buffer.toString();
    }

    public static String longToStringWithCommas(long number)
    {
        if (number == 0)
        {
            return numbers[0];
        }

        char[]  buf   = new char[IntegerHelper.LOG_10_LONG_WHOLE_WITH_FRACTION_MIN_VALUE];
        int     i     = buf.length - 1;
        boolean minus = number < 0L;
        if (minus)
        {
            if (number == Long.MIN_VALUE)
            {
                // Can't force this value positive, handle as special case.
                return LONG_MIN_COMMA;
            }
            number = -number;
        }

        for (int j = 1; number > 0; j++)
        {
            buf[i--] = (char) ('0' + ((int) (number % 10)));

            number /= 10;

            if (number == 0)
            {
                break;
            }

            if ((j % 3) == 0)
            {
                buf[i--] = ',';
            }
        }

        if (minus)
        {
            buf[i--] = '-';
        }

        return new String(buf, i + 1, buf.length - i - 1);
    }

    public static String zeroPad(int number, int required)
    {
        if (number >= 0)
        {
            switch (required)
            {
                case 1:
                    return numbers(number);
                case 2:
                    if (number < NUMBER_100)
                        return zeroPaddedStringValues_2[number];
                    return numbers(number);
                case 3:
                    if (number < NUMBER_1_000)
                        return zeroPaddedStringValues_3[number];
                    return numbers(number);
            }
        }

        return Integer.toString(number);
    }

    public static String zeroes(int number)
    {
        if (number >= 0 && number < zeroes.length)
        {
            return zeroes[number];
        }

        return "";
    }

    public static String charToString(char ch)
    {
        if (ch < chars.length)
        {
            return chars[ch];
        }

        return "" + ch;
    }

    public static String numbers(int number)
    {
        if (number >= 0 && number < numbers.length)
        {
            return numbers[number];
        }

        return Integer.toString(number);
    }

    public static char[] numbersChars(int number)
    {
        if (number >= 0 && number < numbers.length)
        {
            return numbersChars[number];
        }

        boolean minus = number < 0;
        if (minus)
        {
            if (number == Integer.MIN_VALUE)
            {
                // Can't force this value positive, handle as special case.
                return INT_MIN.toCharArray();
            }
            number = -number;
        }

        char[] buf = new char[IntegerHelper.countDigits(number, minus)];

        populateBufFromInt(buf, number, minus);

        return buf;
    }

    public static String numbers(long number)
    {
        if (number >= 0 && number < numbers.length)
        {
            return numbers[(int) number];
        }

        return Long.toString(number);
    }

    public static String zeroPaddedStringValues_2(int number)
    {
        if (number >= 0 && number < zeroPaddedStringValues_2.length)
        {
            return zeroPaddedStringValues_2[number];
        }

        if (number >= 0 && number < numbers.length)
        {
            return numbers[number];
        }

        return Integer.toString(number);
    }

    public static String zeroPaddedStringValues_3(int number)
    {
        if (number >= 0 && number < zeroPaddedStringValues_3.length)
        {
            return zeroPaddedStringValues_3[number];
        }

        if (number >= 0 && number < numbers.length)
        {
            return numbers[number];
        }

        return Integer.toString(number);
    }

    public static String pennies(int number)
    {
        if (number >= 0 && number < pennies.length)
        {
            return pennies[number];
        }

        return "." + Integer.toString(number);
    }

    public static String leftPad(String s, int required, String padString)
    {
        int strlen = s.length();

        if (strlen >= required)
        {
            return s;
        }

        StringBuilder buffer = new StringBuilder(required);
        int padlen = padString.length();
        int remaining;

        for (remaining = required - strlen; remaining >= padlen; remaining -= padlen)
        {
            buffer.append(padString);
        }
        if (remaining > 0)
        {
            buffer.append(padString, 0, remaining);
        }

        buffer.append(s);

        return buffer.toString();
    }
    public static String leftPad(String s, int required, char padChar)
    {
        int strlen = s.length();

        if (strlen >= required)
        {
            return s;
        }

        StringBuilder buffer = new StringBuilder(required);

        for (int i = required - strlen; i > 0; i--)
        {
            buffer.append(padChar);
        }

        buffer.append(s);

        return buffer.toString();
    }
    public static String rightPad(String s, int required, String padString)
    {
        int strlen = s.length();

        if (strlen >= required)
        {
            return s;
        }

        StringBuilder buffer = new StringBuilder(required);

        buffer.append(s);
        int padlen = padString.length();
        int remaining;

        for (remaining = required - strlen; remaining >= padlen; remaining -= padlen)
        {
            buffer.append(padString);
        }
        if (remaining > 0)
        {
            buffer.append(padString, 0, remaining);
        }

        return buffer.toString();
    }
    public static String rightPad(String s, int required)
    {
        return rightPad(s, required, ' ');
    }
    public static String rightPad(String s, int required, char padChar)
    {
        int strlen = s.length();

        if (strlen >= required)
        {
            return s;
        }

        StringBuilder buffer = new StringBuilder(required);

        buffer.append(s);

        for (int i = required - strlen; i > 0; i--)
        {
            buffer.append(padChar);
        }

        return buffer.toString();
    }
    public static String copies(String padString, int number)
    {
        StringBuilder buffer = new StringBuilder(number * padString.length());

        for (int i = 0; i < number; i++)
        {
            buffer.append(padString);
        }

        return buffer.toString();
    }
    public static String toBase36(long number)
    {
        return Long.toString(number, 36).toUpperCase();
    }
    public static String toBase36(String prefix, long number)
    {
        String num = Long.toString(number, 36).toUpperCase();
        StringBuilder result = new StringBuilder(prefix.length()+num.length());
        result.append(prefix).append(num);
        return result.toString();
    }
    public static String toBase36(long number, int required)
    {
        if (required == 0)
        {
            return Long.toString(number, 36).toUpperCase();
        }
        else
        {
            return StringHelper.leftPad(Long.toString(number, 36), required, "0").toUpperCase();
        }
    }
    public static String toBase36(String prefix, long number, int required)
    {
        if (required == 0)
        {
            String num = Long.toString(number, 36).toUpperCase();
            StringBuilder result = new StringBuilder(prefix.length()+num.length());
            result.append(prefix).append(num);
            return result.toString();
        }
        else
        {
            String num = StringHelper.leftPad(Long.toString(number, 36), required, "0").toUpperCase();
            StringBuilder result = new StringBuilder(prefix.length()+num.length());
            result.append(prefix).append(num);
            return result.toString();
        }
    }
    public static String spaces(int numSpaces)
    {
        if (numSpaces < spaces.length)
        {
            return spaces[numSpaces];
        }

        StringBuilder buffer = new StringBuilder(numSpaces);

        for ( ; numSpaces >= spaces.length; numSpaces -= spaces.length - 1)
        {
            buffer.append(spaces[spaces.length - 1]);
        }

        if (numSpaces > 0)
        {
            buffer.append(spaces[numSpaces]);
        }

        return buffer.toString();
    }

    public static char[] stringGetChars(String string)
    {
        return string.toCharArray();
    }

    public static byte[] stringGetBytes(String string)
    {
        int count = string.length();

        byte[] bytes = new byte[count];

        for (int i = 0; i < count; i++)
        {
            bytes[i] = (byte) string.charAt(i);
        }

        return bytes;
    }

    public static int copyStringToCharArray(char[] array, int arrayOffset, String string, int stringOffset, int stringLength)
    {
        System.arraycopy(string.toCharArray(), stringOffset, array, arrayOffset, stringLength);

        return stringLength;
    }

    public static int copyStringToCharArray(char[] array, int arrayOffset, String string, int stringLength)
    {
        System.arraycopy(string.toCharArray(), 0, array, arrayOffset, stringLength);

        return stringLength;
    }

    public static int copyStringToCharArray(char[] array, int arrayOffset, String string1)
    {
        int string1Length = string1.length();

        for (int i = 0; i < string1Length; i++)
        {
            array[arrayOffset++] = string1.charAt(i);
        }

        return string1Length;
    }

    public static int copyStringToCharArray(char[] array, int arrayOffset, String string1, String string2)
    {
        int string1Length = string1.length();
        int string2Length = string2.length();

        for (int i = 0; i < string1Length; i++)
        {
            array[arrayOffset++] = string1.charAt(i);
        }

        for (int i = 0; i < string2Length; i++)
        {
            array[arrayOffset++] = string2.charAt(i);
        }

        return string1Length + string2Length;
    }

    public static int copyStringToCharArray(char[] array, int arrayOffset, String string1, String string2, String string3)
    {
        int string1Length = string1.length();
        int string2Length = string2.length();
        int string3Length = string3.length();

        for (int i = 0; i < string1Length; i++)
        {
            array[arrayOffset++] = string1.charAt(i);
        }

        for (int i = 0; i < string2Length; i++)
        {
            array[arrayOffset++] = string2.charAt(i);
        }

        for (int i = 0; i < string3Length; i++)
        {
            array[arrayOffset++] = string3.charAt(i);
        }

        return string1Length + string2Length + string3Length;
    }

    public static int copyStringToCharArray(char[] array, int arrayOffset, String string1, String string2, String string3, String string4)
    {
        int string1Length = string1.length();
        int string2Length = string2.length();
        int string3Length = string3.length();
        int string4Length = string4.length();

        for (int i = 0; i < string1Length; i++)
        {
            array[arrayOffset++] = string1.charAt(i);
        }

        for (int i = 0; i < string2Length; i++)
        {
            array[arrayOffset++] = string2.charAt(i);
        }

        for (int i = 0; i < string3Length; i++)
        {
            array[arrayOffset++] = string3.charAt(i);
        }

        for (int i = 0; i < string4Length; i++)
        {
            array[arrayOffset++] = string4.charAt(i);
        }

        return string1Length + string2Length + string3Length + string4Length;
    }

    public static String newString(char[] chars)
    {
        return new String(chars);
    }

    public static String newString(char[] chars, int offset, int length)
    {
        return new String(chars, offset, length);
    }

    public static char[] trimNumberCharsThousand_x_xxx_xxx_000(int number)
    {
        return numbersChars[number % NUMBER_1_000];
    }

    public static char[] trimNumberCharsThousand_x_xxx_000_xxx(int number)
    {
        return numbersChars[(number / NUMBER_1_000) % NUMBER_1_000];
    }

    public static char[] trimNumberCharsThousand_x_000_xxx_xxx(int number)
    {
        return numbersChars[(number / NUMBER_1_000_000) % NUMBER_1_000];
    }

    public static char[] trimNumberCharsThousand_0_xxx_xxx_xxx(int number)
    {
        return numbersChars[number / NUMBER_1_000_000_000];
    }

    public static char[] zeroNumberCharsThousand_x_xxx_xxx_000(int number)
    {
        return zeroPaddedCharsValues_3[number % NUMBER_1_000];
    }

    public static char[] zeroNumberCharsThousand_x_xxx_000_xxx(int number)
    {
        return zeroPaddedCharsValues_3[(number / NUMBER_1_000) % NUMBER_1_000];
    }

    public static char[] zeroNumberCharsThousand_x_000_xxx_xxx(int number)
    {
        return zeroPaddedCharsValues_3[(number / NUMBER_1_000_000) % NUMBER_1_000];
    }

    public static void main (String[] args){
        String comparisonStr = "123.45";
        byte b2 = 2; byte b3 = 3; byte b4 = 4; byte b5 = 5; byte b6 = 6; byte b7 = 7;
        FastCharacterWriter writer = new FastCharacterWriter();

        writer.clear();
        if (StringHelper.appendPriceWithScale(writer, 12345, b2).toString().equalsIgnoreCase(comparisonStr))
            System.out.println("Method passed for price scale 2");
        else
            System.out.println("Method failed for price scale 2");

        writer.clear();
        if (StringHelper.appendPriceWithScale(writer, 123456, b3).toString().equalsIgnoreCase(comparisonStr))
            System.out.println("Method passed for price scale 3");
        else
            System.out.println("Method failed for price scale 3");

        writer.clear();
        if (StringHelper.appendPriceWithScale(writer, 1234567, b4).toString().equalsIgnoreCase(comparisonStr))
            System.out.println("Method passed for price scale 4");
        else
            System.out.println("Method failed for price scale 4");

        writer.clear();
        if (StringHelper.appendPriceWithScale(writer, 12345678, b5).toString().equalsIgnoreCase(comparisonStr))
            System.out.println("Method passed for price scale 5");
        else
            System.out.println("Method failed for price scale 5");

        writer.clear();
        if (StringHelper.appendPriceWithScale(writer, 123456789, b6).toString().equalsIgnoreCase(comparisonStr))
            System.out.println("Method passed for price scale 6");
        else
            System.out.println("Method failed for price scale 6");

        writer.clear();
        if (StringHelper.appendPriceWithScale(writer, 1234567899, b7).toString().equalsIgnoreCase(comparisonStr))
            System.out.println("Method passed for price scale 7");
        else
            System.out.println("Method failed for price scale 7");

    }
}
