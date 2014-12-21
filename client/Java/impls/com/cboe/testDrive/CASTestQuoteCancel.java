package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.util.*;
import java.io.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.delegates.application.*;
import com.cboe.delegates.callback.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.exceptions.*;


public class CASTestQuoteCancel extends CASTestQuote
{
    protected Quote  qt;
    ArrayList myClasses = null;
    ArrayList myProducts = null;
    ArrayList cachedQuoteBlocks = null;
    Hashtable quoteCacheByProduct = new Hashtable();
    Hashtable quoteCacheByClass = new Hashtable();
    long cancelTime = 0;
    QuoteBlock bq;
    int myTid;

    public CASTestQuoteCancel(TestParameter parm, SessionManagerStructV2 sessionManagerStructV2, CASMeter casMeter, int tid)
        throws Exception
    {
        super(parm, sessionManagerStructV2, casMeter);
        qt = sessionManagerStructV2.sessionManager.getQuote();
        this.myClasses = buildSessionClasses(parm.productKeys);
        this.myTid = tid;
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

    public synchronized QuoteBlock getNextQuoteBlock(int uid)
    {
        int currentClass = 0;
        int numOfClass = myClasses.size();
        Random numgen = new Random();
        QuoteBlock qb = new QuoteBlock();
        int quantity = 0;

        if (uid == parm.numOfTests)
        {
            parm.threadDone = true;
            return null;
        }

        if ( parm.randomKeys && numOfClass > 1 )
        {
            currentClass = (Math.abs(numgen.nextInt())) % (numOfClass);
        }
        else
        {
            if ( currentClass >= numOfClass )
            {
                currentClass = 0;
            }
        }

        myProducts = (ArrayList)myClasses.get(currentClass);
        int currentProduct = 0;

        QuoteEntryStruct[] quoteEntryStructs = new QuoteEntryStruct[parm.numPerSeq];

        for (int i = 0; i < parm.numPerSeq; i++)
        {
            quantity = quantity + 1;

            if (quantity >= 200)
            {
                quantity = 1;
            }

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
            if (myTid%2 == 0)
            {
                quoteEntryStructs[i].askPrice = PriceFactory.create(1.2).toStruct();
                quoteEntryStructs[i].bidPrice = PriceFactory.create(1.0).toStruct();
            }
            else
            {
                quoteEntryStructs[i].askPrice = PriceFactory.create(1.0).toStruct();
                quoteEntryStructs[i].bidPrice = PriceFactory.create(0.9).toStruct();
            }
//            quoteEntryStructs[i].bidPrice = getBidPrice(parm, true);
            quoteEntryStructs[i].bidQuantity = quantity;
            quoteEntryStructs[i].askQuantity = quantity;
//            quoteEntryStructs[i].askPrice = getAskPrice(parm, true);
            quoteEntryStructs[i].userAssignedId = Integer.toString(uid);
            currentProduct++;
        }

        qb.qs = quoteEntryStructs;

        return qb;
    }

    private void cancelQuotesByClass()
    {
        if (!quoteCacheByClass.isEmpty())
        {
            System.out.println("Canceling by class for " + quoteCacheByClass.size() + " classes of quotes.......");
            try
            {
                for (Enumeration qe = quoteCacheByClass.elements(); qe.hasMoreElements() ;)
                {
                    int quoteClass = ((Integer)qe.nextElement()).intValue();
                    qt.cancelQuotesByClass(parm.quoteCancelSessionName, quoteClass);
                }
            } catch (Exception e)
            {
            }
            quoteCacheByClass.clear();
            quoteCacheByProduct.clear();
            this.cancelTime = System.currentTimeMillis();
        }
    }

    private void cancelSingleQuotes()
    {
        if (!quoteCacheByProduct.isEmpty())
        {
            try
            {
                for (Enumeration qe = quoteCacheByProduct.elements(); qe.hasMoreElements() ;)
                {
                    int prodKey = ((Integer)qe.nextElement()).intValue();
                    qt.cancelQuote(parm.quoteCancelSessionName, prodKey);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            quoteCacheByProduct.clear();
            quoteCacheByClass.clear();
            this.cancelTime = System.currentTimeMillis();
        }
    }

    private void enterQuoteBlock(int classKey, QuoteEntryStruct[] quotes)
        throws DataValidationException, TransactionFailedException, CommunicationException, AuthorizationException, NotAcceptedException, SystemException
    {
        if (parm.callbackVersion.equals("V2"))
        {
            quoteV2.acceptQuotesForClassV2(classKey, quotes);
        } else
        {
            quoteV1.acceptQuotesForClass(classKey, quotes);
        }

        quoteCacheByClass.put(new Integer(classKey), new Integer(classKey));

        int numOfQuotes = quotes.length;
        Integer prodKey = new Integer(0);
        for (int i = 0; i < numOfQuotes; i++)
        {
            prodKey = new Integer(quotes[i].productKey);
            quoteCacheByProduct.put(prodKey, prodKey);
        }
    }

    public  void run()
    {
        try {
            Thread.currentThread().sleep(10000);
        } catch (Exception e)
        {
        }
        //
        long startTime;
        long curTime;
        long remainTime;
        int waitCnt = 0;
        long cumWaitTime = 0;
        int msgRate = parm.msgRate;
        int msgInterval = parm.msgInterval;
        int reportEvery = parm.reportEvery;
        int stepUp = parm.stepUp;
        int currentCount = 0;
        long currentInterval = 0;
        double currentRate = 0;
        double newRate = 0;
        long stepTime;
//        long lastCancel = 0;

        ///////////////////////////////////////////

        try
        {
            System.out.println("Number of Quote Blocks (" + parm.numOfTests + ")");

            startTime = System.currentTimeMillis( );
            stepTime = startTime; // Step up every 2 minutes
            int qtyInc = 1;
            int numFailed = 0;
            long cancelCallCount = 0;
            char methodCalled = 'Q';

            System.out.println( " Starting test..." );
            for(int X = 0; X < parm.numOfTests && !parm.threadDone; X++)
            {
                try
                {
                    casMeter.setStartTime(X);

                    if ( reportEvery != 0 && (X+1) % reportEvery == 0 )
                    {
                        System.out.println( "MsgsSent: " + (X+1) + ", Failed: " + numFailed + " total quotes = " + ((X+1)*parm.numPerSeq) );
                        System.out.println("Total Cancel Call Count = " + cancelCallCount);
                    }

                    curTime = System.currentTimeMillis();
                    if (! parm.cancelByClass)
                    {
                        if (!quoteCacheByProduct.isEmpty())
                        {
                            if (curTime >= (cancelTime + parm.cancelInterval))
                            {
                                    methodCalled = 'C';
                                    cancelSingleQuotes();
                                    cancelCallCount = cancelCallCount + 1;
                            }
                            else
                            {
                                methodCalled = 'Q';
                                bq = this.getNextQuoteBlock(X);
                                this.enterQuoteBlock(bq.itsClassKey, bq.qs);
                                currentCount++;
                            }

                        }
                        else
                        {
                            methodCalled = 'Q';
                            bq = this.getNextQuoteBlock(X);
                            this.enterQuoteBlock(bq.itsClassKey, bq.qs);
                            currentCount++;
                        }
                    }
                    else
                    {
                        if (! quoteCacheByClass.isEmpty() )
                        {
                            if (curTime >= (cancelTime + parm.cancelInterval))
                            {
                                    methodCalled = 'C';
                                    cancelQuotesByClass();
    //                                this.cancelSingleQuotes();
                                    cancelCallCount = cancelCallCount + 1;
                            }
                            else
                            {
                                methodCalled = 'Q';
                                bq = this.getNextQuoteBlock(X);
                                this.enterQuoteBlock(bq.itsClassKey, bq.qs);
                                currentCount++;
                            }
                        }
                        else
                        {
                            methodCalled = 'Q';
                            bq = this.getNextQuoteBlock(X);
                            this.enterQuoteBlock(bq.itsClassKey, bq.qs);
                            currentCount++;
                        }
                    }


                    casMeter.setFinishTime(X);
                    casMeter.setMethodCalled(X, methodCalled);

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
                catch ( Exception e )
                {
                    System.out.println("Exception from CAS");
                    e.printStackTrace();
                    casMeter.setFinishTime(X);
                    casMeter.setEndTime(X);
                    numFailed++;
                }
            }

            if (waitCnt == 0)
            waitCnt = 1;
            System.out.println("End of test: waitCnt(" + waitCnt + ") avgWaitTime(" +
            cumWaitTime / waitCnt + ").");

            System.out.println("done testing cas services ...\n");
            System.out.println("sleeping...");
            try {
                Thread.currentThread().sleep(120000);
            } catch (Exception e)
            {
                ;
            }
            casMeter.printData();
            this.logoutUser();
            //com.cboe.instrumentationService.commPath.CommPathFactory.getCommPathFactory().cleanup();
        }
        catch (Exception e)
        {
            System.out.println("error in block quote thread");
            e.printStackTrace();
            return;
        }
    }
}
