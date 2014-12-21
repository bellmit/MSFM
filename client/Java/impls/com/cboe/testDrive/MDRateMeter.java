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

public class MDRateMeter
{
    public static String CURRENTMARKET="CM";
    public static String BESTMARKET="BM";
    public static String PUBLICMARKET="PM";
    public static String RECAP="RECAP";
    public static String TICKER="TICKET";
    public static String BESTBOOK="BESTBOOK";
    public static String UNDERLYING_RECAP="UNDERLYING_RECAP";
    public static String UNDERLYING_TICKER="UNDERLYING_TICKER";
    public static String NBBO="NBBO";
    public static String EOP="EOP"; // ExpectedOpeningPrice;

    private static Hashtable recapMetersById = new Hashtable();
    private static Hashtable currentMarketMeterById = new Hashtable();
    private static Hashtable bestBookMeterById = new Hashtable();
    private static Hashtable tickerMeterById = new Hashtable();
    private static Hashtable underlyingRecapMeterById = new Hashtable();
    private static Hashtable underlyingTickerMeterById = new Hashtable();
    private static Hashtable NBBOMeterById = new Hashtable();
    private static Hashtable EOPMeterById = new Hashtable();
    private static Hashtable PulbicMarketMeterById = new Hashtable();
    private static Hashtable bestMarketMeterById = new Hashtable();

    private static Hashtable ids = new Hashtable();

    private static PrintWriter writer = null;
    private static int minuteCounter = 0;
    private static int length = 0;

    private static PrintWriter getWriter()
    {
        if (writer == null)
        {
            try
            {
                 writer =  new PrintWriter(new FileWriter("rateCount.out"));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return writer;
    }

    private static void addId(Integer id)
    {
        if (!ids.containsKey(id))
        {
            Hashtable lastMDCounts = new Hashtable();

            lastMDCounts.put(CURRENTMARKET, new Long(0));
            lastMDCounts.put(RECAP, new Long(0));
            lastMDCounts.put(TICKER, new Long(0));
            lastMDCounts.put(BESTBOOK, new Long(0));
            lastMDCounts.put(UNDERLYING_RECAP, new Long(0));
            lastMDCounts.put(UNDERLYING_TICKER, new Long(0));
            lastMDCounts.put(NBBO, new Long(0));
            lastMDCounts.put(EOP, new Long(0));
            lastMDCounts.put(BESTMARKET, new Long(0));
            lastMDCounts.put(PUBLICMARKET, new Long(0));
            lastMDCounts.put(EOP, new Long(0));
            ids.put(id, lastMDCounts);
        }
    }

    public static CASMeter getMDMeter(String theType, int len, int id)
    {
        Integer idKey = new Integer(id);
        Hashtable meters = null;
        CASMeter userMeter = null;

        if (length == 0)
        {
            length = len;
        }

        if (theType.equals(CURRENTMARKET))
        {
            meters = currentMarketMeterById;
        }
        if (theType.equals(BESTMARKET))
        {
            meters = bestMarketMeterById;
        }
        if (theType.equals(PUBLICMARKET))
        {
            meters = PulbicMarketMeterById;
        }
        else if (theType.equals(RECAP))
        {
            meters = recapMetersById;
        }
        else if (theType.equals(TICKER))
        {
            meters = tickerMeterById;
        }
        else if (theType.equals(BESTBOOK))
        {
            meters = bestBookMeterById;
        }
        else if (theType.equals(UNDERLYING_RECAP))
        {
            meters = underlyingRecapMeterById;
        }
        else if (theType.equals(UNDERLYING_TICKER))
        {
            meters = underlyingTickerMeterById;
        }
        else if (theType.equals(NBBO))
        {
            meters = NBBOMeterById;
        }
        else if (theType.equals(EOP))
        {
            meters = EOPMeterById;
        }
        else
        {
            System.out.println("Incorrect type .........");
        }

        if (meters != null)
        {
            if (meters.containsKey(idKey))
            {
                 userMeter = (CASMeter) meters.get(idKey);
            }
            else
            {
                try
                {
                    userMeter = CASMeter.create("NoUseMD_" + id + ".out", len);
                    meters.put(idKey, userMeter);
                } catch (Exception e){}
            }
            addId(idKey);
        }

        return userMeter;
    }

    public static void dumpCount()
    {
        PrintWriter myWriter = getWriter();
        SimpleDateFormat formatter = new SimpleDateFormat ("EEE MMM dd yyyy HH:mm:ss:SSS");
        String timestamp =  formatter.format(new Date());
        long msgNumForType =0;
        long msgCount = 0;
        long totalMsg = 0;

        minuteCounter++;
        myWriter.println(timestamp);
        myWriter.println();
        myWriter.flush();

        CASMeter recapM = null;
        CASMeter cmM = null;
        CASMeter bbM = null;
        CASMeter tM = null;
        CASMeter uRecapM = null;
        CASMeter uTickerM = null;

        System.out.println(GregorianCalendar.getInstance().getTime());
        myWriter.println(GregorianCalendar.getInstance().getTime());
        myWriter.println("U#   CM   BD     Recap   Ticker   Urecap   UTicker   Total" );
        System.out.println("U#   CM   BD     Recap   Ticker   Urecap   UTicker   Total");
        myWriter.println("-------------------------------------------------------------");
        myWriter.flush();

        Integer currentID;
        String lineOutput = "";

        Enumeration idEn = ids.keys();
        while (idEn.hasMoreElements())
        {
            currentID = (Integer) idEn.nextElement();
            long userRecap = 0;
            long userCM = 0;
            long userBB = 0;
            long userTicker = 0;
            long userURecap = 0;
            long userUTicker = 0;

            recapM = (CASMeter)recapMetersById.get(currentID);
            cmM = (CASMeter)currentMarketMeterById.get(currentID);
            bbM = (CASMeter)bestBookMeterById.get(currentID);
            tM = (CASMeter)tickerMeterById.get(currentID);
            uRecapM = (CASMeter)underlyingRecapMeterById.get(currentID);
            uTickerM = (CASMeter)underlyingTickerMeterById.get(currentID);

            Hashtable lastRates = (Hashtable)ids.get(currentID);

            for (int i = 0; i < length; i++)
            {
                userRecap += recapM.getLaps(i).lFillCount;
                userCM += cmM.getLaps(i).lFillCount;
                userBB += bbM.getLaps(i).lFillCount;
                userTicker += tM.getLaps(i).lFillCount;
                userURecap += uRecapM.getLaps(i).lFillCount;
                userUTicker += uTickerM.getLaps(i).lFillCount;
            }

            long diffCM = (userCM - ((Long)(lastRates.get(CURRENTMARKET))).longValue());
            long diffR = (userRecap - ((Long)(lastRates.get(RECAP))).longValue());
            long diffB = (userBB - ((Long)(lastRates.get(BESTBOOK))).longValue());
            long diffT = (userTicker - ((Long)(lastRates.get(TICKER))).longValue());
            long diffUR = (userURecap - ((Long)(lastRates.get(UNDERLYING_RECAP))).longValue());
            long diffUT = (userUTicker - ((Long)(lastRates.get(UNDERLYING_TICKER))).longValue());

            long totalMSGs = diffCM + diffR + diffB + diffT + diffUR + diffUT;

            float totalRate = (float) ( ((float)(totalMSGs))/(float)60 );
            float rateCM = (float) ( ((float)(diffCM))/(float)60 );
            float rateR = (float) ( ((float)(diffR))/(float)60 );
            float rateB = (float) ( ((float)(diffB))/(float)60 );
            float rateT = (float) ( ((float)(diffT))/(float)60 );
            float rateUR = (float) ( ((float)(diffUR))/(float)60 );
            float rateUT = (float) ( ((float)(diffUT))/(float)60 );

            NumberFormat  nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);

            lineOutput = currentID + "   " + nf.format(rateCM) + "   " + nf.format(rateB) + "    " + nf.format(rateR) +
                    "    " + nf.format(rateT) + "   " + nf.format(rateUR) + "     " + nf.format(rateUT) + "      " +
                    nf.format(totalRate) + "      # of MSGs " + totalMSGs;
            writer.println(lineOutput);
            writer.println("");
            writer.flush();
            System.out.println(lineOutput);

            lastRates.put(CURRENTMARKET, new Long(userCM));
            lastRates.put(RECAP, new Long(userRecap));
            lastRates.put(BESTBOOK, new Long(userBB));
            lastRates.put(TICKER, new Long(userTicker));
            lastRates.put(UNDERLYING_RECAP, new Long(userURecap));
            lastRates.put(UNDERLYING_TICKER, new Long(userUTicker));
        }
    }
}
