package com.cboe.client.util;

/**
 * PriceHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;

import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.*;

public final class PriceHelper
{
    public static final PriceStruct NO_PRICE_STRUCT          = new PriceStruct(PriceTypes.NO_PRICE, 0, 0);
    public static final PriceStruct VALUED_ZERO_PRICE_STRUCT = new PriceStruct(PriceTypes.VALUED,   0, 0);
    public static final PriceStruct MARKET_PRICE_STRUCT      = new PriceStruct(PriceTypes.MARKET,   0, 0);

    public static PriceStruct createNoPriceStruct()
    {
        return new PriceStruct(PriceTypes.NO_PRICE, 0, 0);
    }

    public static PriceStruct createMarketPriceStruct()
    {
        return new PriceStruct(PriceTypes.MARKET, 0, 0);
    }

    public static PriceStruct createPriceStruct(int whole)
    {
        return new PriceStruct(PriceTypes.VALUED, whole, 0);
    }

    public static PriceStruct createPriceStruct(int whole, int fraction)
    {
        return new PriceStruct(PriceTypes.VALUED, whole, fraction);
    }

    public static PriceStruct createPriceStructFromDollarsAndCents(int dollars, int cents)
    {
        return new PriceStruct(PriceTypes.VALUED, dollars, cents * (PriceScale.DEFAULT_SCALE / 100));
    }

    public static PriceStruct createPriceStruct(double d)
    {
        int whole    = (int) d;
        int fraction = (int) ((long) (d * PriceScale.DEFAULT_SCALE) - (long) (whole * PriceScale.DEFAULT_SCALE));

        return new PriceStruct(PriceTypes.VALUED, whole, fraction);
    }

    public static PriceStruct createPriceStruct(String str)
    {
        int periodIndex = str.indexOf('.');
        if (periodIndex < 0) // no fraction
        {
            return new PriceStruct(PriceTypes.VALUED, IntegerHelper.parseInt(str), 0);
        }

        return createPriceStructFromDollarsAndCents(
                IntegerHelper.parseInt(str, 0, periodIndex), IntegerHelper.parseInt(str, periodIndex + 1));
    }

    public static PriceStruct createPriceStruct(byte[] array, int offset, int length)
    {
        if (length == 0)
        {
            return createMarketPriceStruct();
        }

        PriceStruct priceStruct = new PriceStruct(PriceTypes.VALUED, 0, 0);
        boolean doingWhole = true;
        int  magnitude = 0;

        length += offset;

        for (int i = offset; i < length; i++)
        {
            if (array[i] >= '0' && array[i] <= '9')
            {
                if (doingWhole)
                {
                    priceStruct.whole = priceStruct.whole * 10 + (array[i] - (byte) '0');
                }
                else
                {
                    priceStruct.fraction = priceStruct.fraction * 10 + (array[i] - (byte) '0');
                    magnitude++;
                }

                continue;
            }

            if (array[i] == '.')
            {
                if (doingWhole)
                {
                    doingWhole = false;
                    continue;
                }
            }

            return null;
        }

        priceStruct.fraction *= Math.pow(10, 9 - magnitude);

        return priceStruct;
    }

    public static PriceStruct createPriceStruct(char[] array, int offset, int length)
    {
        if (length == 0)
        {
            return createMarketPriceStruct();
        }

        PriceStruct priceStruct = new PriceStruct(PriceTypes.VALUED, 0, 0);
        boolean doingWhole = true;
        int  magnitude = 0;

        length += offset;

        for (int i = offset; i < length; i++)
        {
            if (array[i] >= '0' && array[i] <= '9')
            {
                if (doingWhole)
                {
                    priceStruct.whole = priceStruct.whole * 10 + (array[i] - '0');
                }
                else
                {
                    priceStruct.fraction = priceStruct.fraction * 10 + (array[i] - '0');
                    magnitude++;
                }

                continue;
            }

            if (array[i] == '.')
            {
                if (doingWhole)
                {
                    doingWhole = false;
                    continue;
                }
            }

            return null;
        }

        priceStruct.fraction *= Math.pow(10, 9 - magnitude);

        return priceStruct;
    }

    public static double createDouble(PriceStruct priceStruct)
    {
        if (priceStruct.type == PriceTypes.VALUED)
        {
            return (double) priceStruct.whole + ((double) priceStruct.fraction / PriceScale.DEFAULT_SCALE);
        }

        return 0;
    }

    public static boolean equals(PriceStruct a, PriceStruct b)
    {
        if (a.type == b.type)
        {
            if (a.type == PriceTypes.VALUED)
            {
                return a.whole == b.whole && a.fraction == b.fraction;
            }

            return true;
        }

        return false;
    }

    public static String toString(PriceStruct priceStruct)
    {
        if (priceStruct.type == PriceTypes.VALUED)
        {
            return StringHelper.priceFractionToString(priceStruct.whole, priceStruct.fraction);
        }

        return StringHelper.zeroes[1];
    }

    public static Writer appendPriceStruct(Writer writer, PriceStruct priceStruct) throws Exception
    {
        if (priceStruct.type == PriceTypes.VALUED)
        {
            return StringHelper.appendPriceStruct(writer, priceStruct);
        }

        writer.write(StringHelper.zeroes[1]);
        return writer;
    }

    public static FastCharacterWriter appendPriceStruct(FastCharacterWriter writer, PriceStruct priceStruct)
    {
        if (priceStruct.type == PriceTypes.VALUED)
        {
            return StringHelper.appendPriceStruct(writer, priceStruct);
        }

        writer.write(StringHelper.zeroes[1]);
        return writer;
    }
}
