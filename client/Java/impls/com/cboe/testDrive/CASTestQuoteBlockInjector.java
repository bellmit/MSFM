package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.util.*;
import java.io.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.delegates.application.*;
import com.cboe.delegates.callback.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.exceptions.*;


public class CASTestQuoteBlockInjector extends Thread
{
    QuoteQueue qq;
    TestParameter parm;
    CASMeter meter;
    protected double lastQuoteBidPrice;
    protected double lastOrderBidPrice;
    protected double lastOrderAskPrice;
    protected int bidDirection = 1;
    protected int askDirection = 1;
    ArrayList myClasses = null;
    ArrayList myProducts = null;
    boolean DONE = false;

    public CASTestQuoteBlockInjector(TestParameter parm, CASMeter casMeter, QuoteQueue qq)
        throws Exception
    {
        this.qq = qq;
        this.parm = parm;
        this.meter = casMeter;
        this.myClasses = buildSessionClasses(parm.productKeys);
    }


    protected PriceStruct getBidPrice(TestParameter parm, boolean quote)
    {
        double maxPrice;
        double minPrice;
        double lastPrice;
        if (quote)
        {
            maxPrice = parm.maxQuoteBidPrice;
            minPrice = parm.minQuoteBidPrice;
            lastPrice = lastQuoteBidPrice;
        }
        else
        {
            maxPrice = parm.maxOrderBidPrice;
            minPrice = parm.minOrderBidPrice;
            lastPrice = lastOrderBidPrice;
        }

        double newPrice = lastPrice + (bidDirection * parm.priceIncrement);
        if (newPrice >= maxPrice)
        {
            bidDirection = bidDirection * -1;
            newPrice = maxPrice;
        }
        if (newPrice <= minPrice)
        {
            bidDirection = bidDirection * -1;
            newPrice = minPrice;
        }
        if (quote)
        {
            lastQuoteBidPrice = newPrice;
        }
        else
        {
            lastOrderBidPrice = newPrice;
        }

        return PriceFactory.create(newPrice).toStruct();
    }

    protected PriceStruct getAskPrice(TestParameter parm, boolean quote)
    {
        double maxPrice;
        double minPrice;
        double lastPrice;
        double newPrice;
        if (!quote)
        {
            maxPrice = parm.maxOrderAskPrice;
            minPrice = parm.minOrderAskPrice;
            lastPrice = lastOrderAskPrice;
            newPrice = lastPrice + (askDirection * parm.priceIncrement);
            if (newPrice >= maxPrice)
            {
                askDirection = askDirection * -1;
                newPrice = maxPrice;
            }
            if (newPrice <= minPrice)
            {
                askDirection = askDirection * -1;
                newPrice = minPrice;
            }
            lastOrderAskPrice = newPrice;
        }
        else
        {
            newPrice = lastQuoteBidPrice + parm.quoteWidth;
        }

        return PriceFactory.create(newPrice).toStruct();
    }

    private static ArrayList buildSessionClasses(ArrayList productKeys)
    {
        ArrayList classes = new ArrayList();
        ArrayList products = new ArrayList();;
        int lastClass = 0;
        for ( int i = 0; i < productKeys.size(); i++)
        {
            ProdClass product = (ProdClass)productKeys.get(i);
            if (lastClass != product.itsClassKey)
            {
                if (lastClass != 0)
                {
                    classes.add(products);
                    products = new ArrayList();
                }
                lastClass = product.itsClassKey;
            }
            products.add(product);
        }
        classes.add(products);
        return classes;
    }

    public void setDone()
    {
        DONE = true;
    }

    public synchronized QuoteBlock getNextQuoteBlock(int uid)
    {
        int currentClass = 0;
        Random numgen = new Random();
        QuoteBlock qb = new QuoteBlock();

        if (uid == parm.numOfTests)
        {
            parm.threadDone = true;
            return null;
        }

        if ( parm.randomKeys && myClasses.size() > 1 )
        {
            currentClass = (Math.abs(numgen.nextInt())) % (myClasses.size());
        }
        else
        {
            if ( currentClass >= myClasses.size() )
            {
                currentClass = 0;
            }
        }

        myProducts = (ArrayList)myClasses.get(currentClass);
        int currentProduct = 0;

        QuoteEntryStruct[] quoteEntryStructs = new QuoteEntryStruct[parm.numPerSeq];

        for (int i = 0; i < parm.numPerSeq; i++)
        {
            if ( parm.randomBlockKeys && myProducts.size() > 0 )
            {
                currentProduct = (Math.abs(numgen.nextInt())) % (myProducts.size());
            }
            else
            {
                if ( currentProduct >= myProducts.size() )
                {
                    currentProduct = 0;
                }
            }

            quoteEntryStructs[i] = (QuoteEntryStruct )ReflectiveStructBuilder.newStruct(QuoteEntryStruct.class);
            ProdClass product = (ProdClass)myProducts.get(currentProduct);
            qb.itsClassKey = product.itsClassKey ;
            quoteEntryStructs[i].productKey = product.itsProductKey;
            quoteEntryStructs[i].sessionName = product.itsSessionName;
            quoteEntryStructs[i].bidPrice = getBidPrice(parm, true);
            quoteEntryStructs[i].bidQuantity = parm.quoteQuantity;
            quoteEntryStructs[i].askQuantity = parm.quoteQuantity;
            quoteEntryStructs[i].askPrice = getAskPrice(parm, true);
            quoteEntryStructs[i].userAssignedId = Integer.toString(uid);
            currentProduct++;
        }

        qb.qs = quoteEntryStructs;

        return qb;
    }

    public  void run()
    {
        long startTime;
        long curTime;
        long remainTime;
        int waitCnt = 0;
        long cumWaitTime = 0;
        int msgRate = parm.msgRate;
        int msgInterval = parm.msgInterval;
        int reportEvery = parm.reportEvery;
        boolean incQty = true;
        int stepUp = parm.stepUp;
        int currentCount = 0;
        long currentInterval = 0;
        double currentRate = 0;
        double newRate = 0;
        long stepTime;

        ///////////////////////////////////////////

        try
        {

            System.out.println("Number of Quote Blocks (" + parm.numOfTests + ")");

            startTime = System.currentTimeMillis( );
            stepTime = startTime; // Step up every 2 minutes
            int numFailed = 0;
            System.out.println( " Starting test..." );
            for(int X = 0; X < parm.numOfTests && !DONE; X++)
            {
                synchronized (qq)
                {
                    meter.setStartTime(X);
                    qq.insertQuoteBlock(this.getNextQuoteBlock(X));
                }

                currentCount++;

                if ( reportEvery != 0 && (X+1) % reportEvery == 0 )
                {
                    System.out.println( "MsgsSent: " + (X+1) + ", Failed: " + numFailed + " total quotes = " + ((X+1)*parm.numPerSeq) );
                }

                if ( msgRate != 0 && currentCount % msgRate == 0 )
                {
                    curTime = System.currentTimeMillis();

                    if (curTime >= (stepTime + parm.stepUpInterval))
                    {
                        currentInterval = curTime - stepTime;
                        currentRate = (double)currentCount/(double)currentInterval * 1000;
                        msgRate = msgRate + stepUp;
                        newRate = (double)msgRate/(double)msgInterval;
                        System.out.println("Block quotes: " + currentCount + " : Time: " + currentInterval + " : Rate: " + currentRate
                            + " : NewRate: " + newRate + " : M: " + qq.getMaxQueueSize() + " Q Depth: " + qq.getQueueSize());
                        stepTime = curTime;
                        currentCount = 0;
                    }

                    remainTime = (1000 * msgInterval) - (curTime - startTime) - 20; // 20 = Fudge factor for sleep...
                    if ( remainTime > 20 )
                    {
                        waitCnt++;
                        cumWaitTime += remainTime;
                        try
                        {
                            Thread.sleep( remainTime );
                        }
                        catch ( Exception e )
                        {
                        }
                    }
                    startTime = System.currentTimeMillis( );
                }
            }
            System.out.println("Thread DONE, Stop generating quotes....");
        }
        catch (Exception e)
        {
            System.out.println("error in block quote thread");
            e.printStackTrace();
            return;
        }

        try
        {
            Thread.currentThread().sleep(12000);
        }  catch (Exception e){}
    }
}
