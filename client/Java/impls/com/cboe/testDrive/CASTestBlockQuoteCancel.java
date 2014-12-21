package com.cboe.testDrive;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiConstants.QuoteUpdateControlValues;
import com.cboe.delegates.application.*;
import com.cboe.delegates.callback.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.exceptions.*;
/**
 * This class will send block quote cancel
 *  1. BQC1: will send block of quotes that contains valid quotes and cancel quote in the same block. 
 *  Number of cancel quotes will be vary on the variable "blockQuoteCancelQuantity".
 *      -BQC1_V1: will use method acceptQuotesForClass()
 *      -BQC1_V3: will use method acceptQuotesForClassV3()
 *      
 *  2. BQC2: will send block of valid quotes and follow by number of single cancel quotes which specify by the variable "CancelRatio" in ini file.  
 *  e.g. Blocksize = 40 CancelRatio 4 means the driver will send 4 block of valid quote and send number of cancel quotes equal to "numOfQuoteCancelEntries". each block will contain 40 cancels 
 *      -BQC2_V1: will use method acceptQuotesForClass()
 *      -BQC2_V3: will use method acceptQuotesForClassV3()
 *      
 * @author krueyay
 *
 */

public class CASTestBlockQuoteCancel extends CASTestQuote
{
    private UserSessionManager sessionManager;
    protected String UserName;
    ArrayList myClasses = null;
    protected int myloginNumber = 0;
    protected boolean multiThreaded = false;
    int uniqThreadNum = 0;
    int askQuantity = 0;
    int bidQuantity = 0;
    int numOfClass = 0;
    ArrayList currentProducts = null;
    int currentClass = 0;
    Hashtable prodKeyWalkers = new Hashtable();
    // maintain a count of quote drivers for each session manager, so when the count reaches zero the session can be logged out
    private static HashMap quotersPerSessionManager = new HashMap();
    Hashtable cancelClassKeysCache = new Hashtable();
    ArrayList cancelProductKeysCache = new ArrayList();

    public CASTestBlockQuoteCancel(TestParameter parm, SessionManagerStructV2 sessionManagerStruct, CASMeter casMeter, int tid)
        throws Exception
    {
        super(parm, sessionManagerStruct, casMeter);

        uniqThreadNum = tid;

        if (parm.UseSeperateProdKeys)
        {
            this.myClasses = buildSessionClasses(parm.getProductKeysByUserThread(tid));
        }
        else
        {
            this.myClasses = buildSessionClasses(parm.productKeys);
        }

        numOfClass = myClasses.size();
        try
        {
            this.UserName = sessionManagerStruct.sessionManager.getValidUser().userId;
            setName("CASTestBlockQuote-" + UserName + "-" + uniqThreadNum);
        }
        catch(Exception e)
        {
            setName("CASTestBlockQuote-" + tid);
            System.out.println("Exception trying to get userID from SessionManagerStructV2");
            e.printStackTrace();
        }

        System.out.println("Number of classes from thread # " + tid + " = " + numOfClass);

        sessionManager = sessionManagerStruct.sessionManager;
        incrementNumQuoteDrivers(sessionManager);
    }

    public CASTestBlockQuoteCancel(TestParameter parm, UserSessionManagerV3 sessionManagerV3, CASMeter casMeter, int tid)
        throws Exception
    {
        super(parm, sessionManagerV3, casMeter);

        uniqThreadNum = tid;

        if (parm.UseSeperateProdKeys)
        {
            this.myClasses = buildSessionClasses(parm.getProductKeysByUserThread(tid));
        }
        else
        {
            this.myClasses = buildSessionClasses(parm.productKeys);
        }

        numOfClass = myClasses.size();
        try
        {
            this.UserName = sessionManagerV3.getValidUser().userId;
            setName("CASTestBlockQuote-" + UserName + "-" + uniqThreadNum);
        }
        catch(Exception e)
        {
            setName("CASTestBlockQuote-" + tid);
            System.out.println("Exception trying to get userID from UserSessionManagerV3");
            e.printStackTrace();
        }

        System.out.println("Number of classes from thread # " + tid + " = " + numOfClass);

        sessionManager = sessionManagerV3;
        incrementNumQuoteDrivers(sessionManager);
    }

    public CASTestBlockQuoteCancel(TestParameter parm, SessionManagerStructV2 sessionManagerStruct, CASMeter casMeter, int tid, int loginNumber)
        throws Exception
    {
        super(parm, sessionManagerStruct, casMeter);

        multiThreaded = true;
        myloginNumber = loginNumber;
        this.UserName = (String) parm.userNames.get(tid);
        quoteV2 = this.sessionManagerStruct.sessionManagerV2.getQuoteV2();
        quoteV1 = this.sessionManagerStruct.sessionManager.getQuote();
        uniqThreadNum = tid + tid * (parm.loginsPerUser - 1) + loginNumber;
        setName("CASTestBlockQuote-" + UserName + "-" + uniqThreadNum);

        if (parm.UseSeperateProdKeys)
        {
            this.myClasses = buildSessionClasses(parm.getProductKeysByUserThread(tid + tid * (parm.loginsPerUser - 1) + loginNumber));
        }
        else
        {
            this.myClasses = buildSessionClasses(parm.productKeys);
        }

        numOfClass = myClasses.size();

        System.out.println("Created Quote Thread for user = " + UserName + " login #" + loginNumber);
        System.out.println("Number of classes from thread # " + tid + " = " + myClasses.size());

        sessionManager = sessionManagerStruct.sessionManager;
        incrementNumQuoteDrivers(sessionManager);
    }

    public class ProductKeysWalker
    {
        private int count = 0;
        private int numOfProducts = 0;

        public ProductKeysWalker(int numOfProds)
        {
            numOfProducts = numOfProds;
        }
        public int getNextProductPos()
        {
            count = count + 1;
            if (count >= numOfProducts)
            {
                count = 0;
            }
            return count;
        }
    }

    public void setUpCallbacks() throws Exception
    {
        if ( parm.loginsPerUser == 1)
        {
            super.setUpCallbacks();
        }
    }

    private static ArrayList buildSessionClasses(ArrayList productKeys)
    {
        ArrayList classes = new ArrayList();
        ArrayList products = new ArrayList();;
        int lastClass = 0;
//        PrintWriter writer = null;
        
        
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

 //           writer.println(product.itsClassKey + ","+ product.itsProductKey + "," + product.itsSessionName);
            products.add(product);
        }
 //       writer.close();
        classes.add(products);
        return classes;
    }

    private ProdClass getNextProduct(int classKey)
    {
        int currentPos = 0;
        Random numgen = new Random();
        Integer tmpInteger = null;
        
        currentProducts = (ArrayList)myClasses.get(classKey);

        if ( parm.randomBlockKeys && currentProducts.size() > 0 )
        {
            currentPos = (Math.abs(numgen.nextInt())) % (currentProducts.size());
        }
        else
        {
            tmpInteger = new Integer(classKey);
            if (prodKeyWalkers.get(tmpInteger) == null)
            {
                prodKeyWalkers.put(tmpInteger, new ProductKeysWalker(currentProducts.size()));
            }

            currentPos = ((ProductKeysWalker)prodKeyWalkers.get(tmpInteger)).getNextProductPos();
        }
        return (ProdClass)currentProducts.get(currentPos);
    }

    public synchronized QuoteBlock getNextQuoteBlock(int uid)
    {
        int tmpProductKey;
        Random numgen = new Random();
        QuoteBlock qb = new QuoteBlock();
        
        if (parm.numOfTests > 0 && uid == parm.numOfTests)
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
            currentClass = currentClass + 1;

            if ( currentClass >= numOfClass )
            {
                currentClass = 0;
            }
        }

        QuoteEntryStruct[] quoteEntryStructs = new QuoteEntryStruct[parm.numPerSeq];
        
        
        if (isQuoteCache()) 
        {
            System.out.println("Kai: send mix block quotes:"+ uid);
            for (int i = 0; i < parm.numPerSeq; i++)
            {
                quoteEntryStructs[i] = (QuoteEntryStruct )ReflectiveStructBuilder.newStruct(QuoteEntryStruct.class);

                ProdClass product = this.getNextProduct(currentClass);  
                if (parm.fixQuantity)
                {
                       
                    bidQuantity = parm.bidQuantity;
                    askQuantity = parm.askQuantity;
                }
                else
                {
                    bidQuantity = bidQuantity + 1;
                    askQuantity = askQuantity + 1;
                    if (bidQuantity >= 200)
                    {
                        bidQuantity = 1;
                        askQuantity = 1;
                    }
                 }
                //format cancel quote part
                if(i<parm.blockQuoteCancelQuantity) 
                {                      
                     // take the productkey from the list in cancelProductKeysCache. 
                    
                     tmpProductKey = getCancelProductKeyByClass(product.itsClassKey);  
                     if(tmpProductKey != 0) 
                     {                            
                        quoteEntryStructs[i].productKey = tmpProductKey;
                        bidQuantity = 0;
                        askQuantity = 0;                           
                        System.out.println("Kai: remove to the cache: classkey:"+ product.itsClassKey +"productkey:"+tmpProductKey); 
                     }else
                     {
                         quoteEntryStructs[i].productKey = product.itsProductKey; 
                         System.out.println("Kai: get from ini to the cache: classkey:"+ product.itsClassKey +"productkey:"+product.itsProductKey); 
                     }
                     
                       
                // format valid quote part   
                }else   
                {
                     
                     System.out.println("Kai: add to the cache: classkey:"+ product.itsClassKey +"productkey:"+product.itsProductKey);      
                    addCancelProductKeyByClass(product.itsClassKey,product.itsProductKey);
                    quoteEntryStructs[i].productKey = product.itsProductKey;
                  }
                  qb.itsClassKey = product.itsClassKey ;
                  quoteEntryStructs[i].sessionName = product.itsSessionName;
                  quoteEntryStructs[i].bidPrice = getBidPrice(parm, true);
                  quoteEntryStructs[i].bidQuantity = bidQuantity;
                  quoteEntryStructs[i].askQuantity = askQuantity;
                  quoteEntryStructs[i].askPrice = getAskPrice(parm, true);
                  quoteEntryStructs[i].userAssignedId = Integer.toString(uid);
             }
                
     
        }
        else 
        {
            System.out.println("Kai: send valid block quotes:"+ uid);
            for (int i = 0; i < parm.numPerSeq; i++)
            {
                quoteEntryStructs[i] = (QuoteEntryStruct )ReflectiveStructBuilder.newStruct(QuoteEntryStruct.class);

                ProdClass product = this.getNextProduct(currentClass);
                if (parm.fixQuantity)
                {
                    
                    bidQuantity = parm.bidQuantity;
                    askQuantity = parm.askQuantity;
                }
                else
                {
                    bidQuantity = bidQuantity + 1;
                    askQuantity = askQuantity + 1;
                    if (bidQuantity >= 200)
                    {
                        bidQuantity = 1;
                        askQuantity = 1;
                    }
                }
                System.out.println("Kai: add to the cache: classkey:"+ product.itsClassKey +"productkey:"+product.itsProductKey); 
                addCancelProductKeyByClass(product.itsClassKey,product.itsProductKey);
                quoteEntryStructs[i].productKey = product.itsProductKey;
                qb.itsClassKey = product.itsClassKey ;
                quoteEntryStructs[i].sessionName = product.itsSessionName;
                quoteEntryStructs[i].bidPrice = getBidPrice(parm, true);
                quoteEntryStructs[i].bidQuantity = bidQuantity;
                quoteEntryStructs[i].askQuantity = askQuantity;
                quoteEntryStructs[i].askPrice = getAskPrice(parm, true);
                quoteEntryStructs[i].userAssignedId = Integer.toString(uid);
            }    
        }
        qb.qs = quoteEntryStructs;
        return qb;
    }
    
    /**
     * store the product key for the block quote cancel
     * @param classKey
     * @param productKey
     * @author krueyay
     */

    private void addCancelProductKeyByClass(int classKey, int productKey) 
    {
        // cancelClassKeysCache is hashtable contain classkey and arraylist of cancelProductKeys
        // cancelProductKey is ArrayLsit of cancel 
        Integer tmpCancelClassKey = new Integer(classKey);
        Integer tmpCancelProdKey = new Integer(productKey);
        
        // new class key to the cancelClassKey hashtable
        if (!cancelClassKeysCache.containsKey(tmpCancelClassKey)) 
        {
            cancelClassKeysCache.put(tmpCancelClassKey, new ArrayList());            
        }
        ((ArrayList)(cancelClassKeysCache.get(tmpCancelClassKey))).add(tmpCancelProdKey);   
        
    }
    
    /**
     * check any class cache and product cache
     * @author krueyay
     */
    private boolean isQuoteCache()
        {        
        if (cancelClassKeysCache.isEmpty()) 
        {
            return false;
        }
        else
            return true;
               
    }
    
    /**
     * get the product key for block quote cancel
     * @author krueyay
     */
    private int getCancelProductKeyByClass(int classKey)
        {
        
        int productKeyIndex = 0;
        Integer tmpCancelClassKey = new Integer(classKey);
        Integer tmp = new Integer(0);
          
        if ((cancelClassKeysCache.containsKey(tmpCancelClassKey)) 
                && !((ArrayList)(cancelClassKeysCache.get(tmpCancelClassKey))).isEmpty())
        {
            tmp = (Integer)((ArrayList)(cancelClassKeysCache.get(tmpCancelClassKey))).remove(productKeyIndex);
            // After the remove, if the ArrayList is empty, remove the class key too from cancelClassKeysCache
            if (((ArrayList)(cancelClassKeysCache.get(tmpCancelClassKey))).isEmpty()) 
            {
                cancelClassKeysCache.remove(tmpCancelClassKey);
            }
        }
        return tmp.intValue();
        
    }
    
    public QuoteBlockV3 getNextQuoteBlockV3(int uid)
    {
        
        if (parm.numOfTests > 0 && uid == parm.numOfTests)
        {
            parm.threadDone = true;
            return null;
        }

        QuoteBlockV3 qbV3 = new QuoteBlockV3();
        QuoteEntryStructV3[] quoteEntryStructsV3 = new QuoteEntryStructV3[parm.numPerSeq];
        QuoteBlock qbV2 =  getNextQuoteBlock(uid);

        if (qbV2 == null)
        {
            return null;
        }

        QuoteEntryStruct[] quoteEntryStructs = qbV2.qs;


        for (int i = 0; i < quoteEntryStructs.length; i++)
        {
            quoteEntryStructsV3[i] = (QuoteEntryStructV3 )ReflectiveStructBuilder.newStruct(QuoteEntryStructV3.class);

            quoteEntryStructsV3[i].quoteEntry = quoteEntryStructs[i];
            quoteEntryStructsV3[i].quoteUpdateControlId = QuoteUpdateControlValues.CONTROL_DISABLED;
        }

        qbV3.itsClassKey = qbV2.itsClassKey;
        qbV3.qs = quoteEntryStructsV3;

        return qbV3;
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

    private void enterQuoteBlockV3(int classKey, QuoteEntryStructV3[] quotes)
        throws DataValidationException, TransactionFailedException, CommunicationException, AuthorizationException, NotAcceptedException, SystemException
    {
        
        quoteV3.acceptQuotesForClassV3(classKey, quotes);
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
        long tmpTime = 0;
        long tmpDiffTime = 0;
        int gap = msgInterval*1000/msgRate;
        SimpleDateFormat formatter = new SimpleDateFormat ("EEE MMM dd yyyy HH:mm");
        

        try
        {
            if(parm.numOfTests <= 0)
            {
                System.out.println("Number of Quote Blocks is Unlimited (NumberToSend=" + parm.numOfTests + ")");
            }
            else
            {
                System.out.println("Number of Quote Blocks (" + parm.numOfTests + ")");
            }
            
            startTime = System.currentTimeMillis( );
            stepTime = startTime; // Step up every 2 minutes
            int numFailed = 0;
            System.out.println( " Starting test..." );
            int X=0;
            while(!parm.threadDone && parm.ctrlcCount == 0)
            {
                QuoteBlock bq = null;
                QuoteBlockV3 bqV3 = null;
                int classKey;
                if (this.version.equals("V3"))
                {
                    bqV3 = this.getNextQuoteBlockV3(X);
                    if(bqV3 == null)
                    {
                        break;
                    }
                    classKey = bqV3.itsClassKey;
                }
                else
                {
                    bq = this.getNextQuoteBlock(X);
                    if(bq == null)
                    {
                        break;
                    }
                    classKey = bq.itsClassKey;
                }

                if (parm.waitOnGap && (gap - tmpDiffTime > 20))
                {
                    long waitTime = gap - tmpDiffTime - 20;
                    Thread.sleep(waitTime);
                }

                try
                {
/*                    if (gap - tmpDiffTime > 20)
                    {
                        Thread.currentThread().sleep(gap - tmpDiffTime - 20);
                    }   */

                    casMeter.setStartTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setMethodCalled(X + myloginNumber * parm.numOfTests, 'Q');

                    tmpTime = System.currentTimeMillis( );

                    if (this.version.equals("V3"))
                    {
                        this.enterQuoteBlockV3(bqV3.itsClassKey, bqV3.qs);
                    }
                    else
                    {
                        this.enterQuoteBlock(bq.itsClassKey, bq.qs);
                    }

                    tmpDiffTime = System.currentTimeMillis( ) - tmpTime;

                    currentCount++;
                    casMeter.setFinishTime(X + myloginNumber * parm.numOfTests);

                    if ( reportEvery != 0 && (X+1) % reportEvery == 0 )
                    {
                        StringBuffer sb = new StringBuffer(60);
                        
                        sb.append(UserName);
                        sb.append(", MsgsSent: ");
                        sb.append(X + 1);
                        sb.append(", Failed: ");
                        sb.append(numFailed);
                        sb.append(" total quotes = ");
                        sb.append(((X + 1) * parm.numPerSeq));
                        System.out.println(sb.toString());
                    }

                    if ( msgRate != 0 && currentCount % msgRate == 0 )
                    {
                        curTime = System.currentTimeMillis();

                        if (curTime >= (stepTime + parm.stepUpInterval))
                        {
                            currentInterval = curTime - stepTime;
                            currentRate = (double)currentCount/(double)currentInterval * 1000;
                            msgRate = msgRate + stepUp;
                            gap = msgInterval*1000/msgRate;
                            newRate = (double)msgRate/(double)msgInterval;
                            StringBuffer sb = new StringBuffer(80);
                            sb.append("Block quotes: ");
                            sb.append(currentCount);
                            sb.append(" : Time: ");
                            sb.append(currentInterval);
                            sb.append(" : Rate: ");
                            sb.append(currentRate);
                            sb.append(" : NewRate: ");
                            sb.append(newRate);
                            System.out.println(sb.toString());

                            sb.delete(0, sb.length());
                            sb.append(UserName);
                            sb.append(":");
                            sb.append(uniqThreadNum);
                            sb.append(" ");
                            sb.append(formatter.format(new Date()));
                            sb.append(" ");
                            sb.append(currentRate * parm.numPerSeq);
                            RateLogger.logQuoteRate(sb.toString());

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
                catch (DataValidationException dve)
                {
                    System.out.println(dve.details.message);
                    System.out.println("Error Code ....... " + dve.details.error);
                    tmpDiffTime = System.currentTimeMillis( ) - tmpTime;
                    casMeter.setFinishTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setEndTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setMethodCalled(X + myloginNumber * parm.numOfTests, 'F');
                }
                catch (NotAcceptedException nae)
                {
                    System.out.println(nae.details.message + "::classkey::" + classKey);
                    tmpDiffTime = System.currentTimeMillis( ) - tmpTime;
                    casMeter.setFinishTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setEndTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setMethodCalled(X + myloginNumber * parm.numOfTests, 'F');
                }
                catch ( UserException e )
                {
                    System.out.println("Exception from CAS");
                    e.printStackTrace();
                    casMeter.setFinishTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setEndTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setMethodCalled(X + myloginNumber * parm.numOfTests, 'F');
                    numFailed++;
                    tmpDiffTime = System.currentTimeMillis( ) - tmpTime;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    casMeter.setFinishTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setEndTime(X + myloginNumber * parm.numOfTests);
                    casMeter.setMethodCalled(X + myloginNumber * parm.numOfTests, 'F');
                    numFailed++;
                    tmpDiffTime = System.currentTimeMillis() - tmpTime;
                }

                X++;
            }

            System.out.println("Stop sending for user "+UserName+"...");
            if (waitCnt == 0)
            waitCnt = 1;
//todo: why wait for 2 minutes on each thread???  Trying to let consumers catch up?
System.out.println(UserName + " (Thread '"+getName()+"') waiting for 2 minutes before printing CASMeter data...");
            try {
                Thread.currentThread().sleep(120000);
            } catch (Exception e)
            {
                ;
            }
            casMeter.printData();

//todo: why even do this???  How does the user know they have to ctrl-c?  (this loop was just pegging the CPU before changing it to sleep)
            while (parm.ctrlcCount <= 1)
            {
                Thread.sleep(100);
            }

            int numRemaining = decrementNumQuoteDrivers(sessionManager);
            // if this is the last quote driver for the sessionManager, unsubscribe AND logout
            if(numRemaining == 0)
            {
                System.out.println("Logging out user " + UserName + " (Thread '"+getName()+"')...");
                logoutUser();
            }
            else
            {
                unsubscribeQuoteStatus();
            }

            System.out.println("End of test for user "+UserName+": waitCnt(" + waitCnt + ") avgWaitTime(" +
            cumWaitTime / waitCnt + ").");

            System.out.println("done testing cas services for user "+UserName+".\n");
            //com.cboe.instrumentationService.commPath.CommPathFactory.getCommPathFactory().cleanup();
        }
        catch(com.cboe.exceptions.AuthorizationException e)
        {
        }
        catch (Exception e)
        {
            System.out.println("error in block quote thread for user "+UserName);
            e.printStackTrace();
            return;
        }
    }

    private synchronized static int incrementNumQuoteDrivers(UserSessionManager session)
    {
        Integer numQuoteDrivers = (Integer) quotersPerSessionManager.get(session);
        if(numQuoteDrivers == null)
        {
            numQuoteDrivers = new Integer(1);
        }
        else
        {
            numQuoteDrivers = new Integer(numQuoteDrivers.intValue() + 1);
        }
        quotersPerSessionManager.put(session, numQuoteDrivers);
        return numQuoteDrivers.intValue();
    }

    private synchronized static int decrementNumQuoteDrivers(UserSessionManager session)
    {
        Integer numQuoteDrivers = (Integer) quotersPerSessionManager.get(session);
        if(numQuoteDrivers == null)
        {
            numQuoteDrivers = new Integer(0);
        }
        else
        {
            numQuoteDrivers = new Integer(numQuoteDrivers.intValue() - 1);
        }
        quotersPerSessionManager.put(session, numQuoteDrivers);
        return numQuoteDrivers.intValue();
    }
}

