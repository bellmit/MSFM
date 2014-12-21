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
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.exceptions.*;

public class CASTestQuoteSeed extends Thread
{
    /*
    private int numOfQuotes = 0;
    private boolean randomKeys = false;
    private int msgRate = 0;
    private int reportEvery = 0;
    */
    protected SessionManagerStructV2 sessionManagerStructV2 = null;
    protected CASMeter casMeter = null;
    protected TestParameter parm = null;
    //  private ArrayList productKeys = null;
    protected com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListenerV1 = null;
    protected com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListenerV2 = null;

    protected double lastQuoteBidPrice;
    protected double lastOrderBidPrice;
    protected double lastOrderAskPrice;
    protected int bidDirection = 1;
    protected int askDirection = 1;
    protected static QuoteCallbackD callbackConsumerV1;
    protected static QuoteCallbackDV2 callbackConsumerV2;


    protected PriceStruct getBidPrice(TestParameter parm, boolean quote)
    {
        double minPrice;

        lastQuoteBidPrice = parm.minQuoteBidPrice;
        return PriceFactory.create(lastQuoteBidPrice).toStruct();
    }

    protected PriceStruct getAskPrice(TestParameter parm, boolean quote)
    {
        double newPrice;

        newPrice = lastQuoteBidPrice + parm.quoteWidth;
        return PriceFactory.create(newPrice).toStruct();
    }

    public CASTestQuoteSeed(TestParameter parm, SessionManagerStructV2 sessionManagerStructV2, CASMeter casMeter)
        throws Exception
    {
        this.sessionManagerStructV2 = sessionManagerStructV2;
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

    public void setUpCallbacks() throws Exception
    {
        if (parm.callbackVersion.equals("V2"))
        {
            callbackConsumerV2 = new QuoteCallbackDV2(casMeter);
            this.clientListenerV2 = TestCallbackFactory.getV2QuoteStatusConsumer(callbackConsumerV2);
            sessionManagerStructV2.sessionManagerV2.getQuoteV2().subscribeQuoteStatusV2(
                    clientListenerV2, parm.subscribeWithPublish, parm.includeUserInitiatedStatus,parm.gmdQuote);
        }
        else
        {
            //casMeter =   CASMeter.create(outFile, numOfQuotes);
            callbackConsumerV1 = new QuoteCallbackD(casMeter);
            clientListenerV1 = TestCallbackFactory.getV1QuoteStatusConsumer(callbackConsumerV1);
            sessionManagerStructV2.sessionManager.getQuote().subscribeQuoteStatus(clientListenerV1, parm.gmdQuote);
        }
    }

    private void enterQuote(QuoteEntryStruct quote)
        throws SystemException, AuthorizationException, CommunicationException, NotAcceptedException, TransactionFailedException, DataValidationException
    {
        if (parm.callbackVersion.equals("V2"))
        {
            sessionManagerStructV2.sessionManagerV2.getQuoteV2().acceptQuote(quote);
        }
        else
        {
            sessionManagerStructV2.sessionManager.getQuote().acceptQuote(quote);
        }
    }

    private void logoutUser() throws SystemException, AuthorizationException, CommunicationException, DataValidationException
    {
        if (parm.callbackVersion.equals("V2"))
        {
            sessionManagerStructV2.sessionManagerV2.getQuoteV2().unsubscribeQuoteStatusV2(clientListenerV2);
        }
        else
        {
            sessionManagerStructV2.sessionManager.getQuote().unsubscribeQuoteStatus(clientListenerV1);
        }
        sessionManagerStructV2.sessionManager.logout();
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


        int msgRate = parm.msgRate;
        int msgInterval = parm.msgInterval;
        int stepUp = parm.stepUp;
        int currentCount = 0;
        long currentInterval = 0;
        double currentRate = 0;
        double newRate = 0;
        long stepTime;

        ArrayList productKeys = parm.productKeys;
        int numOfQuotes = productKeys.size();
        int reportEvery = parm.reportEvery;

        int currentProduct = 0;

        try {

            startTime = System.currentTimeMillis( );
            stepTime = startTime;

            for(int X = 0; X < numOfQuotes; X++)
            {
                quote.userAssignedId = Integer.toString(X);
                ProdClass tmpProdClass = (ProdClass)productKeys.get(currentProduct);

                quote.productKey = tmpProdClass.itsProductKey;
                quote.sessionName = tmpProdClass.itsSessionName;
                currentProduct++;  // Will have no effect if using random keys.
                quote.bidPrice = this.getBidPrice(parm, true);
                quote.askPrice = this.getAskPrice(parm, true);
                quote.askQuantity = parm.quoteQuantity;
                quote.bidQuantity = parm.quoteQuantity;

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

        while (!parm.threadDone)
        {
            ;
        }

        try
        {
            logoutUser();
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
