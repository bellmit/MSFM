package com.cboe.testDrive;


import java.io.*;
import java.util.*;
import java.lang.*;

public class CASMeter
{
    private static final int DEFAULT_MAP_CAPACITY = 300000;
    private HashMap aLaps;
    private int length = 0;
    private PrintWriter writer = null;
    private boolean bPrinted = false;
    private boolean dataPrinted = false;

    public static CASMeter create(String File, int len) throws IOException
    {
        CASMeter cm = new CASMeter();
        cm.init(File, len);
        return cm;
    }

    public void init(String FileName, int len) throws IOException
    {
        writer =  new PrintWriter(new FileWriter(FileName));
        length = len;
        if(len > 0)
        {
            aLaps = new HashMap(len);
            for (int i = 0; i < len; i++)
            {
                aLaps.put(new Integer(i), new Laps());
            }
        }
        else
        {
            aLaps = new HashMap(DEFAULT_MAP_CAPACITY);
        }
    }

    public synchronized void setStartTime(int Operation)
    {
        Laps laps = getLaps(Operation);
        laps.lStartTime = System.currentTimeMillis();
        laps.lCalltime = System.currentTimeMillis();
    }

    public synchronized void setCallTime(int Operation)
    {
        getLaps(Operation).lCalltime = System.currentTimeMillis();
    }

    public synchronized void setFinishTime(int Operation)
    {
        Laps laps = getLaps(Operation);
        if (laps.lFinishTime == 0)
            laps.lFinishTime = System.currentTimeMillis();
    }

    public synchronized void setEndTime(int Operation)
    {
        Laps laps = getLaps(Operation);
        if (laps.lEndTime == 0)
            laps.lEndTime = System.currentTimeMillis();
        if (bPrinted && Operation+1 == length)
        {
            bPrinted = true;
            printData();
        }
    }

    public synchronized void setMethodCalled(int Operation, char methodCalled)
    {
        Laps laps = getLaps(Operation);
        laps.methodCalled = methodCalled;
    }

    public synchronized void incrementFillCount(int Operation)
    {
        Laps laps = getLaps(Operation);
        laps.lFillCount++;
        if (bPrinted && Operation+1 == length)
        {
            bPrinted = true;
            printData();
        }
    }

    public synchronized void incrementFillCount(int Operation, int messageCount)
    {
        Laps laps = getLaps(Operation);
        laps.lFillCount=laps.lFillCount+messageCount;
        if (bPrinted && Operation+1 == length)
        {
            bPrinted = true;
            printData();
        }
    }

    public synchronized void incrementFailedCount(int Operation)
    {
        Laps laps = getLaps(Operation);
        laps.lfailedCount++;
        if (bPrinted && Operation+1 == length)
        {
            bPrinted = true;
            printData();
        }
    }

    public synchronized void incrementCancelCount(int Operation)
    {
        Laps laps = getLaps(Operation);
        laps.lCancelCount++;
        if (bPrinted && Operation+1 == length)
        {
            bPrinted = true;
            printData();
        }
    }

    public synchronized void printData()
    {
        try
        {
            if (!dataPrinted)
            {
                long avgTime = 0;
                long peakTime = 0;
                long totalTime = 0;
                long avgTimeSync = 0;
                long peakTimeSync = 0;
                long totalTimeSync = 0;
                long totalFills = 0;
                long totalCanceled = 0;
                long totalFailed = 0;
                int totalDataPoints = 0;
                TreeMap setCallTime = new TreeMap();
                TreeMap setCallbackTime = new TreeMap();

                int[] aCallTime = new int[aLaps.size()];
                int[] aCallBackTime = new int[aLaps.size()];

                writer.println("index,start,call,finish,end,totalCallTime,respTime,cbTime,filled,canceled,method");
                SortedSet ss = new TreeSet(aLaps.keySet());
                for(Iterator it=ss.iterator(); it.hasNext(); )
                {
                    Integer iObj = (Integer)it.next();
                    int i = iObj.intValue();
                    Laps laps = (Laps)aLaps.get(iObj);
                    if (laps.lStartTime > 0)
                    {
                        totalDataPoints++;
                        laps.lLap  = laps.lEndTime - laps.lStartTime;
                        laps.lLapSync  = laps.lFinishTime - laps.lStartTime;
                        laps.lQCallRespTime = laps.lFinishTime - laps.lCalltime;
                        totalTime += laps.lLap;
                        peakTime = peakTime > laps.lLap ? peakTime : laps.lLap;
                        totalTimeSync += laps.lLapSync;
                        peakTimeSync = peakTimeSync > laps.lLapSync ? peakTimeSync : laps.lLapSync;
                        //        System.out.println("lap time = "  + Long.toString( aLaps[i].lLap));
                        writer.println(Integer.toString(i) + ","  + Long.toString( laps.lStartTime)+ ","
                            + Long.toString(laps.lCalltime) + "," + Long.toString( laps.lFinishTime)+ "," + Long.toString( laps.lEndTime)+ ","
                            + Long.toString( laps.lLapSync)+ "," + Long.toString(laps.lQCallRespTime) + "," + Long.toString( laps.lLap) + ","
                            + Long.toString( laps.lFillCount ) + "," + Long.toString(laps.lCancelCount) + ","
                            + String.valueOf(laps.methodCalled));
                        //        writer.println("start time = "  + Long.toString( aLaps[i].lLap));
                        setCallTime.put(new Long(i),new Long(laps.lLapSync)) ;
                        setCallbackTime.put(new Long(i),new Long(laps.lLap)) ;

                        insert(aCallBackTime, i, (int)laps.lLap);
                        insert(aCallTime,     i, (int)laps.lLapSync);
                        totalFills += laps.lFillCount;
                        totalCanceled += laps.lCancelCount;
                        totalFailed += laps.lfailedCount;
                    }
                }

                long lCallMinTime =  aCallTime[0];
                long lCallbackMinTime =  aCallBackTime[0];

                long lCallMedTime =  aCallTime[aLaps.size()/2];

                long lCallbackMedTime =  aCallBackTime[aLaps.size()/2];


                avgTime = (long) (((float)totalTime)/(float) totalDataPoints);
                avgTimeSync = (long) (((float)totalTimeSync)/(float) totalDataPoints);

                System.out.println("Total Fills = " + Long.toString(totalFills));
                System.out.println("Total Canceled = " + Long.toString(totalCanceled));
                System.out.println("Total Failed = " + Long.toString(totalFailed));
                System.out.println("Number of round trips = "  + Integer.toString(totalDataPoints));
                System.out.println("Min   round trip time = " + Long.toString(lCallbackMinTime));
                System.out.println("Average round trip time = " + Long.toString( avgTime));
                System.out.println("Peak  round trip time = " + Long.toString( peakTime));
                System.out.println("Median  round trip time = " + Long.toString( lCallbackMedTime));
                writer.println("Total Fills = " + Long.toString(totalFills));
                writer.println("Total Canceled = " + Long.toString(totalCanceled));
                writer.println("Total Failed = " + Long.toString(totalFailed));
                writer.println("Number of round trips = "  + Integer.toString(totalDataPoints));
                writer.println("Min Callback  round trip time = " + Long.toString(lCallbackMinTime));
                writer.println("Average Callback time = " + Long.toString( avgTime));
                writer.println("Peak  Callback time = " + Long.toString( peakTime));
                writer.println("Median  Callback time = " + Long.toString( lCallbackMedTime));
                writer.println("Min Call Response time = " + Long.toString(lCallMinTime));
                writer.println("Average Call Response time = " + Long.toString( avgTimeSync));
                writer.println("Peak  Call Response time = " + Long.toString( peakTimeSync));
                writer.println("Median  Call Response time = " + Long.toString( lCallMedTime));
//                writer.println("Call Message Rate  = " + Float.toString((float) ( ((float) totalDataPoints* 1000.0)/(float) (aLaps[length-1].lStartTime - aLaps[0].lStartTime))));
//                writer.println("Callback Message Rate  = " + Long.toString((long) ( ((float) totalDataPoints* 1000.0)/(float) (aLaps[length-1].lEndTime - aLaps[0].lEndTime))));
//                writer.println("Total Time = " + Long.toString(aLaps[length-1].lEndTime - aLaps[0].lStartTime));
                writer.flush();
                dataPrinted = true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Returns the cached Laps for the test iteration ("Operation")
     * @param Operation
     * @return Laps
     */
    synchronized Laps getLaps(int Operation)
    {
        Integer operationObj = new Integer(Operation);
        Laps laps = (Laps) aLaps.get(operationObj);
        if(laps == null)
        {
            laps = new Laps();
            aLaps.put(operationObj, laps);
        }
        return laps;
    }

    static void insert(int[] A, int itemsInArray, int newItem) {
        // Assume that A contains itemsInArray items in increasing
        // order (A[0] <= A[1] <= ... <= A[itemsInArray-1]).
        // This routine adds newItem to the array in its proper
        // order.
        int loc = itemsInArray - 1;
        while (loc >= 0 && A[loc] > newItem)
        {
            A[loc + 1] = A[loc];  // bump item from A[loc] up to loc + 1
            loc = loc - 1;        // move down to next location
        }
        A[loc + 1] = newItem;  // put new item in last vacated space
    }
}
