package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.util.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.delegates.callback.*;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.application.cas.TestCallback;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.domain.util.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiV2.Quote;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.exceptions.*;

public class CASTestQuote extends Thread
{
    /*
    private int numOfQuotes = 0;
    private boolean randomKeys = false;
    private int msgRate = 0;
    private int reportEvery = 0;
    */
    protected SessionManagerStructV2 sessionManagerStruct = null;
    protected UserSessionManagerV3 sessionManagerV3 = null;
    protected String version = "0";
    protected CASMeter casMeter = null;
    protected TestParameter parm = null;
    //  private ArrayList productKeys = null;
    protected com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListenerV1 = null;
    protected com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListenerV2 = null;
    protected com.cboe.idl.cmi.Quote quoteV1 = null;
    protected com.cboe.idl.cmiV2.Quote quoteV2 = null;
    protected com.cboe.idl.cmiV3.Quote quoteV3 = null;

    protected double lastQuoteBidPrice;
    protected double lastOrderBidPrice;
    protected double lastOrderAskPrice;
    protected int bidDirection = 1;
    protected int askDirection = 1;
    protected static QuoteCallbackD callbackConsumer;
    protected static QuoteCallbackDV2 callbackV2Consumer;

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

        if (parm.fixPriceSide == 1 && quote)
        {
            newPrice = minPrice;
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

        if (parm.fixPriceSide == 2 && quote)
        {
            newPrice = parm.maxQuoteBidPrice;
        }

        return PriceFactory.create(newPrice).toStruct();
    }

    public CASTestQuote(TestParameter parm, SessionManagerStructV2 sessionManagerStruct, CASMeter casMeter)
        throws Exception
    {
        this.sessionManagerStruct = sessionManagerStruct;
        this.casMeter = casMeter;
        //this.casMeter = casMeter;
        this.parm = parm;
        /*    numOfQuotes = parm.numOfQuotes;
        randomKeys = parm.randomKeys;
        msgRate = parm.msgRate;
        reportEvery = parm.reportEvery;
        casMeter = parm.casMeter;
        productKeys = parm.productKeys;
        */

        setUpCallbacks();
    }

    public CASTestQuote(TestParameter parm, UserSessionManagerV3 sessionManagerV3, CASMeter casMeter)
        throws Exception
    {
        this.casMeter = casMeter;
        //this.casMeter = casMeter;
        this.parm = parm;
        this.sessionManagerV3 = sessionManagerV3;
        /*    numOfQuotes = parm.numOfQuotes;
        randomKeys = parm.randomKeys;
        msgRate = parm.msgRate;
        reportEvery = parm.reportEvery;
        casMeter = parm.casMeter;
        productKeys = parm.productKeys;
        */
        this.version = "V3";
        setUpCallbacks();
    }

    public void setUpCallbacks() throws Exception
    {
        System.out.println("Setting up callback objects..............................");
        if (this.version.equals("V3"))
        {
             setUpCallbacksV3();
        }
        else if (this.parm.callbackVersion.equals("V2"))
        {
            setUpCallbacksV2();
        }
        else
        {
            setUpCallbacksV1();
        }
    }

    private void setUpCallbacksV1() throws Exception
    {
        //casMeter =   CASMeter.create(outFile, numOfQuotes);
        callbackConsumer = new QuoteCallbackD(casMeter);
        try {
            clientListenerV1 = TestCallbackFactory.getV1QuoteStatusConsumer(callbackConsumer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        quoteV1 = this.sessionManagerStruct.sessionManager.getQuote();
        quoteV1.subscribeQuoteStatus( clientListenerV1, parm.gmdQuote);
    }

    private void setUpCallbacksV2() throws Exception
    {
        //casMeter =   CASMeter.create(outFile, numOfQuotes);
        callbackV2Consumer = new QuoteCallbackDV2(casMeter);
        try {
            clientListenerV2 = TestCallbackFactory.getV2QuoteStatusConsumer(callbackV2Consumer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        quoteV2 = this.sessionManagerStruct.sessionManagerV2.getQuoteV2();
        quoteV2.subscribeQuoteStatusV2( clientListenerV2, parm.subscribeWithPublish, parm.includeUserInitiatedStatus, parm.gmdQuote);
    }

    private void setUpCallbacksV3() throws Exception
    {
        //casMeter =   CASMeter.create(outFile, numOfQuotes);
        callbackV2Consumer = new QuoteCallbackDV2(casMeter);
        try {
            clientListenerV2 = TestCallbackFactory.getV2QuoteStatusConsumer(callbackV2Consumer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        quoteV3 = this.sessionManagerV3.getQuoteV3();
        quoteV3.subscribeQuoteStatusV2( clientListenerV2, parm.subscribeWithPublish, parm.includeUserInitiatedStatus, parm.gmdQuote);
        System.out.println("Subscribed V2 quote status through V3 API ....");
    }

    protected void unsubscribeQuoteStatus()
            throws Exception
    {
        if(version.equals("V3"))
        {
            sessionManagerV3.getQuoteV3().unsubscribeQuoteStatusV2(this.clientListenerV2);
        }
        else if(parm.callbackVersion.equals("V2"))
        {
            sessionManagerStruct.sessionManagerV2.getQuoteV2().unsubscribeQuoteStatusV2(this.clientListenerV2);
        }
        else
        {
            sessionManagerStruct.sessionManager.getQuote().unsubscribeQuoteStatus(this.clientListenerV1);
        }
    }

    protected void logoutUser() throws Exception
    {
        unsubscribeQuoteStatus();
        if (version.equals("V3"))
        {
            this.sessionManagerV3.logout();
        }
        else if (parm.callbackVersion.equals("V2"))
        {
            this.sessionManagerStruct.sessionManager.logout();
        } else
        {
            this.sessionManagerStruct.sessionManager.logout();
        }
    }

    public QuoteEntryStruct buildQuoteEntryStruct()
    {
        QuoteEntryStruct quote = (QuoteEntryStruct )ReflectiveStructBuilder.newStruct(QuoteEntryStruct.class);
        quote.productKey = 2196; //6; //2196;
        quote.bidPrice = new com.cboe.idl.cmiUtil.PriceStruct((short) 2, 10, 0);
        quote.bidQuantity = 10;
        quote.askQuantity = 10;
        quote.askPrice = new com.cboe.idl.cmiUtil.PriceStruct((short) 2, 10, 500000000);
        quote.userAssignedId = new String();
        quote.sessionName = "";
        return quote;
    }

    private void enterQuote(QuoteEntryStruct quote)
        throws DataValidationException, TransactionFailedException, CommunicationException, AuthorizationException, NotAcceptedException, SystemException
    {
        if (parm.callbackVersion.equals("V2"))
        {
            quoteV2.acceptQuote(quote);
        } else
        {
            quoteV1.acceptQuote(quote);
        }
    }


    public void run()
    {
        try {
            Thread.currentThread().sleep(10000);
        } catch (Exception e)
        {
        }
        QuoteEntryStruct quote = buildQuoteEntryStruct();
        long startTime;
        long curTime;
        long remainTime;
        int waitCnt = 0;
        long cumWaitTime = 0;
        int productOffset = 0;
        int qtyInc = 1;
        int numFailed = 0;
        int quoteKey = 1;
        int quoteAdjustment = 5;
        int quoteQuantity = 11;

        int numOfQuotes = parm.numOfTests;

        int msgRate = parm.msgRate;
        int msgInterval = parm.msgInterval;
        int stepUp = parm.stepUp;
        int currentCount = 0;
        long currentInterval = 0;
        double currentRate = 0;
        double newRate = 0;
        long stepTime;

        boolean randomKeys = parm.randomKeys;
        boolean incQty = parm.incQty;
        ArrayList productKeys = parm.productKeys;
        int reportEvery = parm.reportEvery;

        int currentProduct = 0;
        try {
            Random numgen = new Random();

            startTime = System.currentTimeMillis( );
            stepTime = startTime;

            for(int X = 0; X < numOfQuotes && !parm.threadDone; X++)
            {
                quote.userAssignedId = Integer.toString(X);

                if ( randomKeys )
                {
                    currentProduct = (Math.abs(numgen.nextInt())) % (productKeys.size());
                }
                else
                {
                    if ( currentProduct >= productKeys.size() )
                    {
                        currentProduct = 0;
                    }
                }

                ProdClass tmpProdClass = (ProdClass)productKeys.get(currentProduct);

                quote.productKey = tmpProdClass.itsProductKey;
                quote.sessionName = tmpProdClass.itsSessionName;
                currentProduct++;  // Will have no effect if using random keys.
                quote.bidPrice = this.getBidPrice(parm, true);
                quote.askPrice = this.getAskPrice(parm, true);
                quote.askQuantity = parm.quoteQuantity;
                quote.bidQuantity = parm.quoteQuantity;

                //Get around a know bug by updating both sides of the quote
                if ( incQty )
                {
                    qtyInc = 1;
                    quote.askQuantity += qtyInc;
                    quote.bidQuantity += qtyInc;
                }

                // Uncomment for HomeFactoryObjectGetter
                try
                {
                    casMeter.setStartTime(X);
                    enterQuote(quote);
                    currentCount++;
                    casMeter.setFinishTime(X);

                    if ( reportEvery != 0 && (X+1) % reportEvery == 0 )
                    {
                        System.out.println( "MsgsSent: " + (X+1) + ", Failed: " + numFailed );
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
                            System.out.println("Quotes: " + currentCount + " : Time: " + currentInterval + " : Rate: " + currentRate
                                + " : NewRate: " + newRate);
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
                catch (DataValidationException e)
                {
                    System.out.println("Exception from CAS " + e.details.message );
                    e.printStackTrace();
                    casMeter.setEndTime(X);
                    numFailed++;
                }
                catch ( Exception e )
                {
                    System.out.println("Exception from CAS");
                    e.printStackTrace();
                    casMeter.setEndTime(X);
                    numFailed++;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("error in acceptquote ");
            e.printStackTrace();
            return;
        }

        if (waitCnt == 0)
        {
            waitCnt = 1;
        }
        System.out.println("End of test: waitCnt(" + waitCnt + ") avgWaitTime(" +
                        cumWaitTime / waitCnt + ").");
        try
        {
            this.logoutUser();
            System.out.println("done testing cas services ...\n");
            System.out.println("sleeping...");
            Thread.currentThread().sleep(120000);
        } catch (Exception e)
        {
            ;
        }
        casMeter.printData();
    }
}
