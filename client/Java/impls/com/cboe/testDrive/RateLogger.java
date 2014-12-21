package com.cboe.testDrive;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.application.test.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

public class RateLogger
{
    private static PrintWriter quoteRateLogger = null;

    private static PrintWriter createQuoteRateLogger()
    {
        if (quoteRateLogger == null)
        {
            try
            {
                 quoteRateLogger =  new PrintWriter(new FileWriter("quoteRate.out", true));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return quoteRateLogger;
    }

    public static void logQuoteRate(String rate)
    {
        createQuoteRateLogger();
        System.out.println(rate);
        quoteRateLogger.println(rate);
        quoteRateLogger.flush();
    }

    public static void closeRateLogger()
    {
        if (quoteRateLogger != null)
        {
            quoteRateLogger.flush();
            quoteRateLogger.close();
        }
        System.out.println("RateLogger CLOSED!!!!!!!!!!!!");
    }
}
