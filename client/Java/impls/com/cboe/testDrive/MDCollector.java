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
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

public class MDCollector
{
    public static String RECAP="Recap";
    public static String CURRENTMARKET="CurrentMarket";
    public static String NBBO="NBBO";
    public static String EOP="EOP";
    public static String PUBLIC="Public";
    public static String BestMarket="BestMarket";
    public static String TICKER="Ticker";
    public static String UNDERLYING_RECAP="UnderlyingRecap";
    public static String UNDERLYING_TICKER="UnderlyingTicker";
    public static String BESTBOOK="Bestbook";

    public static final String RECAP_V4 = "RecapV4";
    public static final String CURRENTMARKET_V4 = "CurrentMarketV4";
    public static final String PUBLIC_V4 = "PublicV4";
    public static final String BEST_MARKET_V4 = "BestMarketV4";
    public static final String TICKER_V4 = "TickerV4";

    private static Hashtable allCounters = new Hashtable();
    private static PrintWriter writer = null;
    private static int minuteCounter = 0;
    private static boolean COUNTBYTYPE = false;
    private static long lastTime = 0;

    private static PrintWriter getWriter()
    {
        if (writer == null)
        {
            try
            {
                 writer =  new PrintWriter(new FileWriter("msgCount.out"));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return writer;
    }

    public static MarketDataCounter getMarketDataCounterByTypeByThreadID(String type, int tid)
    {
        COUNTBYTYPE = true;
        lastTime = System.currentTimeMillis()/1000;
        Hashtable typeCounters = getCountersByType(type);
        if (typeCounters.containsKey(new Integer(tid)))
        {
            return (MarketDataCounter)typeCounters.get(new Integer(tid));
        }
        else
        {
            MarketDataCounter mdc = new MarketDataCounter(tid);
            typeCounters.put(new Integer(tid), mdc);
            return mdc;
        }
    }

    public static MarketDataCounter getMarketDataCounter(String theType, int classkey)
    {
        Hashtable typeCounters = getCountersByType(theType);
        if (typeCounters.containsKey(new Integer(classkey)))
        {
            return (MarketDataCounter)typeCounters.get(new Integer(classkey));
        }
        else
        {
            MarketDataCounter mdc = new MarketDataCounter(classkey);
            typeCounters.put(new Integer(classkey), mdc);
            return mdc;
        }
    }


    private static Hashtable getCountersByType(String theType)
    {
        if (allCounters.containsKey(theType))
        {
            return (Hashtable)allCounters.get(theType);
        }
        else
        {
            Hashtable tmp = new Hashtable();
            allCounters.put(theType, tmp);
            return tmp;
        }
    }

    public static void dumpCount()
    {
        PrintWriter myWriter = getWriter();
        SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm:ss");
        String timestamp =  formatter.format(new Date());
        StringBuffer sb = new StringBuffer();
        long currentTime = System.currentTimeMillis()/1000;
        long msgNumForType =0;
        long msgCount = 0;
        long totalMsg = 0;
        float rate = 0;
        NumberFormat  nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);

        minuteCounter++;
        myWriter.println(timestamp);
        myWriter.println();
        myWriter.flush();

        Enumeration typeEn = allCounters.keys();

        while (typeEn.hasMoreElements())
        {
            String theType = (String)typeEn.nextElement();
            Hashtable classCounters = (Hashtable)allCounters.get(theType);
            int numOfThreads = classCounters.size();
            msgNumForType = 0;
            msgCount = 0;

            if (COUNTBYTYPE)
            {
                sb.append("[" + timestamp + "] ");
                sb.append (theType);
                sb.append(" ");

                Enumeration countersEn = classCounters.elements();
                while (countersEn.hasMoreElements())
                {
                    MarketDataCounter mdc = (MarketDataCounter)countersEn.nextElement();
                    msgCount = mdc.getCount();
                    sb.append(msgCount + " ");
                    msgNumForType = msgNumForType + msgCount;
                    mdc.resetCounter();
/*                    if (msgCount != 0)
                    {
                        rate = (float) msgNumForType / (float)(currentTime - lastTime);
                        System.out.println("[" + timestamp + "] " + theType + " " + mdc.getClassKey() + " " + msgCount + rate);
                        myWriter.println("[" + timestamp + "] " + theType + " " + mdc.getClassKey() + " " + msgCount + rate);
                        mdc.resetCounter();
                    }
*/
                }
                if (msgNumForType == 0)
                {
                    rate = 0;
                }
                else
                {
//                    System.out.println(msgNumForType + ":" + numOfThreads + ":" + currentTime + ":" + lastTime);
                    rate = (float) msgNumForType / (float) (numOfThreads * (currentTime - lastTime));
                }

                sb.append(" " + nf.format(rate));
                System.out.println(sb.toString());
//                System.out.println("---------------------------------------------------");
                myWriter.println(sb.toString());
//                myWriter.println("---------------------------------------------------");
                sb.setLength(0);
            }
            else
            {
                Enumeration countersEn = classCounters.elements();
                while (countersEn.hasMoreElements())
                {
                    MarketDataCounter mdc = (MarketDataCounter)countersEn.nextElement();
                    msgCount = mdc.getCount();
                    msgNumForType = msgNumForType + msgCount;
                    if (msgCount != 0)
                    {
                        myWriter.println("[" + minuteCounter + "] " + theType + " " + mdc.getClassKey() + " " + msgCount);
                        mdc.resetCounter();
                    }
                }
            }
            totalMsg = totalMsg + msgNumForType;
            myWriter.println("[" + minuteCounter + "] " + timestamp + " Total " + theType + ": " + msgNumForType);
            myWriter.println();
            myWriter.flush();
        }
        myWriter.println("[" + minuteCounter + "] " + timestamp + " Total Market Data this minute: " + totalMsg);
        myWriter.println();
        myWriter.println();
        myWriter.flush();
        lastTime = currentTime;
    }
}
