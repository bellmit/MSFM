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
import com.cboe.exceptions.*;


public class CASTestMBQuote extends CASTestQuote
{
    ArrayList myClasses = null;
    ArrayList myProducts = null;
    QuoteBlock bq;
    protected String UserName;
//    static int myTid =  0;
    protected int myloginNumber = 0;
    protected boolean multiThreaded = false;

    public CASTestMBQuote(TestParameter parm, SessionManagerStructV2 sessionManagerStruct, CASMeter casMeter, int tid)
        throws Exception
    {
        super(parm, sessionManagerStruct, casMeter);
//        myTid = tid;

        if (parm.UseSeperateProdKeys)
        {
            this.myClasses = buildSessionClasses(parm.getProductKeysByUserThread(tid));
        }
        else
        {
            this.myClasses = buildSessionClasses(parm.productKeys);
        }

        System.out.println("Number of classes from thread # " + tid + " = " + myClasses.size());
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
    }

    public  void run()
    {
        try {
            Thread.currentThread().sleep(10000);
        } catch (Exception e)
        {
        }

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

        ///////////////////////////////////////////

        try
        {
            System.out.println("Number of Quote Blocks (" + parm.numOfTests + ")");

            startTime = System.currentTimeMillis( );
            stepTime = startTime; // Step up every 2 minutes
            int numFailed = 0;
            System.out.println( " Starting test..." );
            for(int X = 0; X < parm.numOfTests && !parm.threadDone; X++)
            {
                bq = this.getNextQuoteBlock(X);
                try
                {
                    casMeter.setStartTime(X);
                    casMeter.setMethodCalled(X, 'Q');
                    this.enterQuoteBlock(bq.itsClassKey, bq.qs);
                    currentCount++;
                    casMeter.setFinishTime(X);

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
