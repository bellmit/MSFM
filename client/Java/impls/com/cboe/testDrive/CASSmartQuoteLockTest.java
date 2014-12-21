package com.cboe.testDrive;

/**
 * CASSmartQuoteLockTest
 *
 * Will try to pound the CAS with N threads all randomly accepting/cancelling quotes by product/class or session
 *
 * The input file should only contain products/classes for a single session and user
 *
 * The ini file should specify number of tests to run, but these are not used verbatim: for AcceptClass/AcceptProduct,
 * there will be a random number between 10 and numOfTests+10, for CancelClass/CancelProduct, there will be a random
 * number between 1 and numOfTests+1, and for CancelSession, there will be a random number between 1 and numOfTests/10
 *
 * @author: Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiV2.*;
import com.cboe.domain.util.PriceFactory;

public class CASSmartQuoteLockTest extends Thread
{
    protected SessionManagerStructV2                            sessionManagerStruct;
    protected CASMeter                                          casMeter;
    protected TestParameter                                     parm;
    protected com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer   clientListenerV1;
    protected com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListenerV2;
    protected com.cboe.idl.cmi.Quote                            quoteV1;
    protected com.cboe.idl.cmiV2.Quote                          quoteV2;
    protected double                                            lastQuoteBidPrice;
    protected double                                            lastOrderBidPrice;
    protected double                                            lastOrderAskPrice;
    protected List                                              threads = new ArrayList();

    protected int                                               bidDirection = 1;
    protected int                                               askDirection = 1;
    protected boolean                                           isV2;

    protected int[]                                             distribution = new int[5];
    protected int[]                                             invoked      = new int[5];
    protected int[]                                             expected     = new int[5];

    protected static QuoteCallbackD                             callbackConsumer;
    protected static QuoteCallbackDV2                           callbackV2Consumer;

    public static final int THREAD_TYPE_AcceptProductThread = 0; public static final int Percent_AcceptProductThread = 0                           + 32;
    public static final int THREAD_TYPE_AcceptClassThread   = 1; public static final int Percent_AcceptClassThread   = Percent_AcceptProductThread + 43;
    public static final int THREAD_TYPE_CancelProductThread = 2; public static final int Percent_CancelProductThread = Percent_AcceptClassThread   + 10;
    public static final int THREAD_TYPE_CancelClassThread   = 3; public static final int Percent_CancelClassThread   = Percent_CancelProductThread + 10;
    public static final int THREAD_TYPE_CancelSessionThread = 4; public static final int Percent_CancelSessionThread = Percent_CancelClassThread   +  5;

    protected void getBidPrice(TestParameter parm, boolean quote, PriceStruct priceStruct)
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

        priceStruct = PriceFactory.create(newPrice).toStruct();
    }

    protected void getAskPrice(TestParameter parm, boolean quote, PriceStruct priceStruct)
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

        priceStruct = PriceFactory.create(newPrice).toStruct();
    }

    public CASSmartQuoteLockTest(TestParameter parm, SessionManagerStructV2 sessionManagerStruct, CASMeter casMeter) throws Exception
    {
        this.sessionManagerStruct = sessionManagerStruct;
        this.casMeter             = casMeter;
        this.parm                 = parm;

        isV2 = "V2".equals(parm.callbackVersion);

        if (isV2)
        {
            callbackV2Consumer = new QuoteCallbackDV2(casMeter);
            try
            {
                clientListenerV2 = TestCallbackFactory.getV2QuoteStatusConsumer(callbackV2Consumer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(0);
            }

            quoteV2 = sessionManagerStruct.sessionManagerV2.getQuoteV2();
            quoteV2.subscribeQuoteStatusV2(clientListenerV2, parm.subscribeWithPublish, parm.includeUserInitiatedStatus, parm.gmdQuote);
        }
        else
        {
            callbackConsumer = new QuoteCallbackD(casMeter);
            try
            {
                clientListenerV1 = TestCallbackFactory.getV1QuoteStatusConsumer(callbackConsumer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(0);
            }

            quoteV1 = sessionManagerStruct.sessionManager.getQuote();
            quoteV1.subscribeQuoteStatus(clientListenerV1, parm.gmdQuote);
        }
    }

    public class SQThread extends Thread
    {
        public long    enterTime;
        public long    exitTime;
        public int     threadType;
        public boolean succeeded = false;

        public SQThread(int threadType) {this.threadType = threadType; distribution[threadType]++;}
        public int      getThreadType() {return threadType;}
        public boolean  succeeded()     {return succeeded;}
        public long     enterTime()     {return enterTime;}
        public long     exitTime()      {return exitTime;}
        public int      elapsed()       {return (int) (exitTime - enterTime);}
    }

    public class AcceptProductThread extends SQThread
    {
        public AcceptProductThread()
        {
            super(THREAD_TYPE_AcceptProductThread);
        }

        public void run()
        {
            int                 currentProduct;
            ProdClass           tmpProdClass;
            Random              random       = new Random();
            int                 numOfTests   = 10 + random.nextInt(parm.numOfTests);
            List                productKeys  = parm.productKeys;
            QuoteEntryStruct    quote        = new QuoteEntryStruct(1234, "",
                                                                    PriceHelper.createPriceStructFromDollarsAndCents(10,  0), 10,
                                                                    PriceHelper.createPriceStructFromDollarsAndCents(10, 50), 10,
                                                                    "");

            try
            {
                synchronized(expected) {expected[threadType] += numOfTests;}

                for (int i = 0; i < numOfTests && !parm.threadDone; i++)
                {
                    currentProduct = random.nextInt(productKeys.size());

                    tmpProdClass = (ProdClass) productKeys.get(currentProduct);

                    quote.productKey     = tmpProdClass.itsProductKey;
                    quote.sessionName    = tmpProdClass.itsSessionName;
                    quote.bidQuantity    = parm.quoteQuantity;
                    quote.askQuantity    = parm.quoteQuantity;
                    quote.userAssignedId = StringHelper.intToString(i);
                    getBidPrice(parm, true, quote.bidPrice);
                    getAskPrice(parm, true, quote.askPrice);

                    try
                    {
                        synchronized(invoked) {invoked[threadType]++;}

                        enterTime = System.currentTimeMillis();

                        if (isV2)
                        {
                            quoteV2.acceptQuote(quote);
                        }
                        else
                        {
                            quoteV1.acceptQuote(quote);
                        }

                        succeeded = true;
                    }
                    catch (NotAcceptedException e)
                    {

                    }
                    catch (DataValidationException e)
                    {
                        System.out.println("Exception from CAS " + e.details.message);
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Exception from CAS");
                        e.printStackTrace();
                    }
                    finally
                    {
                        exitTime = System.currentTimeMillis();
                    }

                    if ( parm.reportEvery != 0 && (i+1) % parm.reportEvery == 0 )
                    {
                        System.out.println( "AcceptSingleQuote: " + (i+1) );
                    }
                    ThreadHelper.sleep(random.nextInt(7000));
                }
            }
            catch (Exception e)
            {
                System.out.println("error in acceptQuote");
                e.printStackTrace();
            }
        }
    }

    public class AcceptClassThread extends SQThread
    {
        public AcceptClassThread()
        {
            super(THREAD_TYPE_AcceptClassThread);
        }

        public void run()
        {
            int                 q;
            int                 currentProduct;
            ProdClass           tmpProdClass;
            List                productKeys  = parm.productKeys;
            Random              random       = new Random();
            int                 numOfTests   = 10 + random.nextInt(parm.numOfTests);
            QuoteEntryStruct[]  quotes       = new QuoteEntryStruct[5];
            QuoteEntryStruct    quote;
            int                 classKey = 0;

            for (q = 0; q < quotes.length; q++)
            {
                quotes[q]    = new QuoteEntryStruct(1234, "",
                                                    PriceHelper.createPriceStructFromDollarsAndCents(10,  0), 10,
                                                    PriceHelper.createPriceStructFromDollarsAndCents(10, 50), 10,
                                                    "");
            }

            try
            {
                synchronized(expected) {expected[threadType] += numOfTests;}

                for (int i = 0; i < numOfTests && !parm.threadDone; i++)
                {
                    for (q = 0; q < quotes.length; q++)
                    {
                        currentProduct = random.nextInt(productKeys.size());

                        tmpProdClass   = (ProdClass) productKeys.get(currentProduct);

                        classKey       = tmpProdClass.itsClassKey;

                        quote          = quotes[q];

                        quote.productKey     = tmpProdClass.itsProductKey;
                        quote.sessionName    = tmpProdClass.itsSessionName;
                        quote.bidQuantity    = parm.quoteQuantity;
                        quote.askQuantity    = parm.quoteQuantity;
                        quote.userAssignedId = StringHelper.intToString(i);
                        getBidPrice(parm, true, quote.bidPrice);
                        getAskPrice(parm, true, quote.askPrice);
                    }

                    try
                    {
                        synchronized(invoked) {invoked[threadType]++;}

                        enterTime = System.currentTimeMillis();

                        if (isV2)
                        {
                            quoteV2.acceptQuotesForClass(classKey, quotes);
                        }
                        else
                        {
                            quoteV1.acceptQuotesForClass(classKey, quotes);
                        }

                        succeeded = true;
                    }
                    catch (NotAcceptedException e)
                    {

                    }
                    catch (DataValidationException e)
                    {
                        System.out.println("Exception from CAS " + e.details.message);
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Exception from CAS");
                        e.printStackTrace();
                    }
                    finally
                    {
                        exitTime = System.currentTimeMillis();
                    }

                    if ( parm.reportEvery != 0 && (i+1) % parm.reportEvery == 0 )
                    {
                        System.out.println( "AcceptQuotesByClass: " + (i+1) );
                    }

                    ThreadHelper.sleep(random.nextInt(7000));
                }
            }
            catch (Exception e)
            {
                System.out.println("error in acceptQuotesForClass");
                e.printStackTrace();
            }
        }
    }

    public class CancelProductThread extends SQThread
    {
        public CancelProductThread()
        {
            super(THREAD_TYPE_CancelProductThread);
        }

        public void run()
        {
            int                 currentProduct;
            int                 productKey;
            String              sessionName;
            ProdClass           tmpProdClass;
            Random              random       = new Random();
            int                 numOfTests   = 5 + random.nextInt(parm.numOfTests);
            List                productKeys  = parm.productKeys;

            try
            {
                synchronized(expected) {expected[threadType] += numOfTests;}

                for (int i = 0; i < numOfTests && !parm.threadDone; i++)
                {
                    currentProduct = random.nextInt(productKeys.size());

                    tmpProdClass   = (ProdClass) productKeys.get(currentProduct);

                    productKey     = tmpProdClass.itsProductKey;
                    sessionName    = tmpProdClass.itsSessionName;

                    try
                    {
                        synchronized(invoked) {invoked[threadType]++;}

                        enterTime = System.currentTimeMillis();

                        if (isV2)
                        {
                            quoteV2.cancelQuote(sessionName, productKey);
                        }
                        else
                        {
                            quoteV1.cancelQuote(sessionName, productKey);
                        }

                        succeeded = true;
                    }
                    catch (NotAcceptedException e)
                    {

                    }
                    catch (DataValidationException e)
                    {
                        System.out.println("Exception from CAS " + e.details.message);
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Exception from CAS");
                        e.printStackTrace();
                    }
                    finally
                    {
                        exitTime = System.currentTimeMillis();
                    }

                    if ( parm.reportEvery != 0 && (i+1) % parm.reportEvery == 0 )
                    {
                        System.out.println( "AcceptQuoteCancel: " + (i+1) );
                    }
                    ThreadHelper.sleep(random.nextInt(1000));
                }
            }
            catch (Exception e)
            {
                System.out.println("error in cancelQuote");
                e.printStackTrace();
            }
        }
    }

    public class CancelClassThread extends SQThread
    {
        public CancelClassThread()
        {
            super(THREAD_TYPE_CancelClassThread);
        }

        public void run()
        {
            int                 currentProduct;
            int                 classKey;
            String              sessionName;
            ProdClass           tmpProdClass;
            Random              random       = new Random();
            int                 numOfTests   = 5 + random.nextInt(parm.numOfTests);
            List                productKeys  = parm.productKeys;

            try
            {
                synchronized(expected) {expected[threadType] += numOfTests;}

                for (int i = 0; i < numOfTests && !parm.threadDone; i++)
                {
                    currentProduct = random.nextInt(productKeys.size());

                    tmpProdClass   = (ProdClass) productKeys.get(currentProduct);

                    classKey       = tmpProdClass.itsClassKey;
                    sessionName    = tmpProdClass.itsSessionName;

                    try
                    {
                        synchronized(invoked) {invoked[threadType]++;}

                        enterTime = System.currentTimeMillis();

                        if (isV2)
                        {
                            quoteV2.cancelQuotesByClass(sessionName, classKey);
                        }
                        else
                        {
                            quoteV1.cancelQuotesByClass(sessionName, classKey);
                        }

                        succeeded = true;
                    }
                    catch (NotAcceptedException e)
                    {

                    }
                    catch (DataValidationException e)
                    {
                        System.out.println("Exception from CAS " + e.details.message);
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Exception from CAS");
                        e.printStackTrace();
                    }
                    finally
                    {
                        exitTime = System.currentTimeMillis();
                    }
                    if ( parm.reportEvery != 0 && (i+1) % parm.reportEvery == 0 )
                    {
                        System.out.println( "AcceptQuoteCancelByClass: " + (i+1) );
                    }
                    ThreadHelper.sleep(random.nextInt(1000));
                }
            }
            catch (Exception e)
            {
                System.out.println("error in cancelQuotesByClass");
                e.printStackTrace();
            }
        }
    }

    public class CancelSessionThread extends SQThread
    {
        public CancelSessionThread()
        {
            super(THREAD_TYPE_CancelSessionThread);
        }

        public void run()
        {
            int                 currentProduct;
            String              sessionName;
            ProdClass           tmpProdClass;
            Random              random       = new Random();
            int                 numOfTests   = 1 + random.nextInt((parm.numOfTests / 10) <= 0 ? 1 : (parm.numOfTests / 10));
            List                productKeys  = parm.productKeys;

            try
            {
                synchronized(expected) {expected[threadType] += numOfTests;}

                for (int i = 0; i < numOfTests && !parm.threadDone; i++)
                {
                    currentProduct = random.nextInt(productKeys.size());

                    tmpProdClass   = (ProdClass) productKeys.get(currentProduct);

                    sessionName    = tmpProdClass.itsSessionName;

                    try
                    {
                        synchronized(invoked) {invoked[threadType]++;}

                        enterTime = System.currentTimeMillis();

                        if (isV2)
                        {
                            quoteV2.cancelAllQuotes(sessionName);
                        }
                        else
                        {
                            quoteV1.cancelAllQuotes(sessionName);
                        }

                        succeeded = true;
                    }
                    catch (NotAcceptedException e)
                    {

                    }
                    catch (Exception e)
                    {
                        System.out.println("Exception from CAS");
                        e.printStackTrace();
                    }
                    finally
                    {
                        exitTime = System.currentTimeMillis();
                    }
                    if ( parm.reportEvery != 0 && (i+1) % parm.reportEvery == 0 )
                    {
                        System.out.println( "AcceptQuoteCancelBySession: " + (i+1) );
                    }
                    ThreadHelper.sleep(random.nextInt(3000));
                }
            }
            catch (Exception e)
            {
                System.out.println("error in cancelAllQuotes");
                e.printStackTrace();
            }
        }
    }

    public void run()
    {
        ThreadHelper.sleepSeconds(10);

        Random random = new Random();

        Thread thread;
        int r;

        thread = new Thread()
        {
            public void run()
            {
                while (true)
                {
                    ThreadHelper.sleepSeconds(5);

                    System.err.println("[" + new Date() + "] XXX " +
                           "AP[dis=" + distribution[THREAD_TYPE_AcceptProductThread]  + "/inv=" + invoked[THREAD_TYPE_AcceptProductThread] + "/exp=" + expected[THREAD_TYPE_AcceptProductThread] + "] " +
                           "AC[dis=" + distribution[THREAD_TYPE_AcceptClassThread]    + "/inv=" + invoked[THREAD_TYPE_AcceptClassThread]   + "/exp=" + expected[THREAD_TYPE_AcceptClassThread]   + "] " +
                           "CP[dis=" + distribution[THREAD_TYPE_CancelProductThread]  + "/inv=" + invoked[THREAD_TYPE_CancelProductThread] + "/exp=" + expected[THREAD_TYPE_CancelProductThread] + "] " +
                           "CC[dis=" + distribution[THREAD_TYPE_CancelClassThread]    + "/inv=" + invoked[THREAD_TYPE_CancelClassThread]   + "/exp=" + expected[THREAD_TYPE_CancelClassThread]   + "] " +
                           "CS[dis=" + distribution[THREAD_TYPE_CancelSessionThread]  + "/inv=" + invoked[THREAD_TYPE_CancelSessionThread] + "/exp=" + expected[THREAD_TYPE_CancelSessionThread] + "] " +
                           "TH["     +(distribution[THREAD_TYPE_AcceptProductThread]  +
                                       distribution[THREAD_TYPE_AcceptClassThread]    +
                                       distribution[THREAD_TYPE_CancelProductThread]  +
                                       distribution[THREAD_TYPE_CancelClassThread]    +
                                       distribution[THREAD_TYPE_CancelSessionThread]) + "]"
                    );
                }
            }
        };

        thread.setDaemon(true);
        thread.start();

        for (int i = 0; i < 500; i++)
        {
            r = random.nextInt(100);

            if      (r <= Percent_AcceptProductThread) {thread = new AcceptProductThread();}
            else if (r <= Percent_AcceptClassThread)   {thread = new AcceptClassThread();}
            else if (r <= Percent_CancelProductThread) {thread = new CancelProductThread();}
            else if (r <= Percent_CancelClassThread)   {thread = new CancelClassThread();}
            else                                       {thread = new CancelSessionThread();}

            threads.add(thread);
            thread.start();
        }

        // wait for all threads to finish
        for (int i = 0; i < threads.size(); i++)
        {
            thread = (Thread) threads.get(i);
            try
            {
                thread.join();
            }
            catch (Exception ex)
            {

            }
        }

        try
        {
            if (isV2)
            {
                sessionManagerStruct.sessionManagerV2.getQuoteV2().unsubscribeQuoteStatusV2(clientListenerV2);
            }
            else
            {
                sessionManagerStruct.sessionManager.getQuote().unsubscribeQuoteStatus(clientListenerV1);
            }

            sessionManagerStruct.sessionManager.logout();
        }
        catch (Throwable e)
        {

        }

        // now, try to validate
        //for (int i = 0; i < threads.size(); i++)
        //{
        //    thread = (Thread) threads.get(i);
        //    try
        //    {
        //        //TBD
        //    }
        //    catch (Exception ex)
        //    {
        //
        //    }
        //}
   }
}
