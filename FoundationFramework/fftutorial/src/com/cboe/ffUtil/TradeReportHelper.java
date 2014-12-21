package com.cboe.ffUtil;

import com.cboe.ffidl.ffTrade.TradeReportStruct;
import java.io.*;
import java.text.*;

/**
 *  Read/write struct to/from reader/writer.
 */
public class TradeReportHelper
{
    // Message format:
    // 0..3 4.......13 14......23 24......33 34...40 41...47 48....55
    // 4    10         10         10         7       7       8
    // (--) (-symbol-) (--buyer-) (-seller-) (price) (-qu--) (-time-)
    // TRPT xxxxxxxxxx xxxxxxxxxx xxxxxxxxxx 0000.00 0000000 00:00:00
    //

    public static final String MSG_PREFIX = "TRPT";
    public static final int MSG_LEN = 4+10+10+10+7+7+8;

    protected static final DecimalFormat PRICE_FORMAT    = new DecimalFormat("0000.00");
    protected static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat("0000000");

    public static void writeReport(Writer out, TradeReportStruct report)
        throws IOException
    {
        String price    = PRICE_FORMAT.format(report.price);
        String quantity = QUANTITY_FORMAT.format(report.quantity);

        out.write(MSG_PREFIX);
        Util.writePaddedString(out, report.symbol, 10, true);
        Util.writePaddedString(out, report.buyer,  10, true);
        Util.writePaddedString(out, report.seller, 10, true);
        out.write(price);
        out.write(quantity);
        out.write(TimeHelper.toString(report.sentTime));
    }

    public static TradeReportStruct readReport(Reader in)
        throws IOException, ParseException
    {
        char[] chars = new char[MSG_LEN];
        int numRead = in.read(chars);
        if (numRead < MSG_LEN)
        {
            throw new IOException("Failed to read full message: read " + numRead + " byte(s).");
        }

        String str = new String(chars);

        if (!str.substring(0,4).equals(MSG_PREFIX))
        {
            throw new ParseException("Invalid message prefix: got '" + str.substring(0,4) + "', expected '" + MSG_PREFIX + "'", 0);
        }

        TradeReportStruct report = new TradeReportStruct();
        report.symbol   = str.substring(4, 14).trim();
        report.buyer    = str.substring(14,24).trim();
        report.seller   = str.substring(24,34).trim();
        report.price    = PRICE_FORMAT.parse(str.substring(34,41)).floatValue();
        report.quantity = QUANTITY_FORMAT.parse(str.substring(41,48)).intValue();
        report.sentTime = TimeHelper.fromString(str.substring(48,56));
        return report;
    }
}
