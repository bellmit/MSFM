package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.util.*;
import java.io.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.delegates.application.*;
import com.cboe.delegates.callback.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.ReflectiveStructBuilder;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.exceptions.*;

public class CASTestInvalidOrderID extends CASTestQuote
{
    protected String UserName;
    protected int tid;
    protected int loginNumber;
    protected String prefix;
    protected OrderQuery orderQueryService;

    public CASTestInvalidOrderID(TestParameter parm, SessionManagerStructV2 sessionManagerStructV2,
                        CASMeter casMeter, int tid, int loginNumber, CMIOrderStatusConsumer clientListener) throws Exception
    {
        super(parm, sessionManagerStructV2, casMeter);
        this.UserName = (String) parm.userNames.get(tid);
        this.tid = tid;
        this.loginNumber = loginNumber;
        this.prefix = parm.branch;
        this.orderQueryService = sessionManagerStructV2.sessionManager.getOrderQuery();
        System.out.println("Created Order Thread for user = " + UserName + " login #" + loginNumber);
    }

    public void setUpCallbacks() throws Exception
    {
    }

    public OrderIdStruct buildInvalidOrderIdStruct(TestParameter parm)
    {
        OrderIdStruct orderId = (OrderIdStruct)ReflectiveStructBuilder.newStruct(com.cboe.idl.cmiOrder.OrderIdStruct.class);
        java.util.Calendar javaDate = java.util.Calendar.getInstance();
        java.util.Date aDate = javaDate.getTime();

        //The place to make sure an OrderIdStruct ivalid is here when we add one year to make it ONE year AFTER the current year.
        int year = aDate.getYear() + 1900 + 1;
        int month = aDate.getMonth() + 1;
        int Day = aDate.getDate();
        String dateString;
        String monthStr;
        String dayStr;
        monthStr = String.valueOf(month);
        if (month < 10)
        {
            monthStr = "0" + String.valueOf(month);
        }
        dayStr = String.valueOf(Day);
        if (Day < 10)
        {
            dayStr = "0" + String.valueOf(Day);
        }

        dateString = String.valueOf(year) + monthStr + dayStr;

        orderId.orderDate = dateString;
        orderId.branch = (UserName + "ZZZ").substring(0,3);
        return orderId;
    }

    public  void run()
    {
        try {
            Thread.currentThread().sleep(10000);
        } catch (Exception e)
        {
        }
        int msgRate = parm.msgRate;
        int msgInterval = parm.msgInterval;
        int reportEvery = parm.reportEvery;
        int sequenceSeed = parm.sequenceSeed;

        int numOfOrders = parm.numOfTests;

        System.out.println("Start of " + numOfOrders + " order performance test.");
        System.out.println("Sending orders at a message rate of (" + msgRate + ")");

        //String memberKey = parm.memberKey;
        //String clearingFirmKey = "";
        // create an OrderStruct, thing we'll pass to OrderHandlingService

        OrderIdStruct orderId = buildInvalidOrderIdStruct(parm);
        System.out.println("testing cas services ...\n");
        ///
        int branchSeqValue = parm.sequenceSeed;
        branchSeqValue = branchSeqValue + 1;

        long startTime = System.currentTimeMillis( );
        long curTime = 0;
        long remainTime = 0;
        long cumWaitTime = 0;
        int waitCnt = 0;
        System.out.println("Starting Order Loop");
        int stepUp = parm.stepUp;
        int currentCount = 0;
        long currentInterval = 0;
        double currentRate = 0;
        double newRate = 0;
        long stepTime;

        char newLogin = 'A';
        for (int i = 0; i < loginNumber; i++)
        {
            newLogin++;
        }

        try {
            stepTime = startTime; // Step up every 2 minutes

            UserStruct userStruct = sessionManagerStruct.sessionManager.getValidUser();
            orderId.correspondentFirm = (prefix + newLogin + "ZZZZ").substring(0,4);

            // for(int x = 0; x < prodKeySize; x++)
            for(int x = 0; x < numOfOrders && !parm.threadDone; x++)
            {
                if (x % reportEvery == 0)
                {
                    System.out.println("Done with " + x + " invalid order query.");
                }

                orderId.branchSequenceNumber = branchSeqValue++;
                if (branchSeqValue > 9998)
                {
                    branchSeqValue = 1;
                    char newPrefix = (prefix.toCharArray())[0];
                    newPrefix++;
                    prefix = String.valueOf(newPrefix);
                    System.out.println("Sequence number Using new prefix of " + prefix);
                    orderId.correspondentFirm = (prefix + newLogin + "ZZZZ").substring(0,4);
                }

//                orderId.executingOrGiveUpFirm = userStruct.defaultProfile.executingGiveupFirm;
                // Setting executing giveup firm to "" to force a table scan to slow down the query
                orderId.executingOrGiveUpFirm.exchange = "";
                orderId.executingOrGiveUpFirm.firmNumber = "";
/*
                if (userStruct.role == UserRoles.BROKER_DEALER)
                {
                    if (userStruct.executingGiveupFirms.length > 0)
                    {
                        order.executingOrGiveUpFirm = userStruct.executingGiveupFirms[0];
                    }
                }
                else if (order.executingOrGiveUpFirm.equals(""))
                {
                    if (userStruct.executingGiveupFirms.length > 0)
                    {
                        order.executingOrGiveUpFirm = userStruct.executingGiveupFirms[0];
                    }
                    else
                    {
                        order.executingOrGiveUpFirm = userStruct.firm;
                    }
                }
*/
                try
                {
                    casMeter.setStartTime(x + loginNumber * numOfOrders);
                    casMeter.setMethodCalled(x + loginNumber * numOfOrders, 'G');
                    orderQueryService.getOrderById(orderId);

                    currentCount++;
                    casMeter.setFinishTime(x + loginNumber * numOfOrders);
                    //  	System.out.println(Integer.toString(x + 1)+" Calls returned");
                    if ( msgRate != 0 && currentCount % msgRate == 0 )
                    {
                        curTime = System.currentTimeMillis();

                        if (curTime >= (stepTime + parm.stepUpInterval))
                        {
                            currentInterval = curTime - stepTime;
                            currentRate = (double)currentCount/(double)currentInterval * 1000;
                            msgRate = msgRate + stepUp;
                            newRate = (double)msgRate/(double)msgInterval;
                            System.out.println("Order Qurey: " + currentCount + " : Time: " + currentInterval + " : Rate: " + currentRate
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
                    casMeter.setMethodCalled(x + loginNumber * numOfOrders,'F');
                    casMeter.incrementFailedCount(x + loginNumber * numOfOrders);
                    casMeter.setFinishTime(x + loginNumber * numOfOrders);
                    System.out.println("Exception from CAS " + e.details.message );
                    e.printStackTrace();
                }
                catch (NotFoundException e)
                {
                    casMeter.setMethodCalled(x + loginNumber * numOfOrders,'F');
                    casMeter.incrementFailedCount(x + loginNumber * numOfOrders);
                    casMeter.setFinishTime(x + loginNumber * numOfOrders);
                    System.out.println("Exception from CAS " + e.details.message );
                }
                catch (Exception e)
                {
                    casMeter.setMethodCalled(x + loginNumber * numOfOrders,'F');
                    casMeter.setFinishTime(x + loginNumber * numOfOrders);
                    casMeter.incrementFailedCount(x + loginNumber * numOfOrders);
                    System.out.println("Exception from CAS " + e.toString());
                    e.printStackTrace();
                    //numFailed++;
                }
            }
            System.out.println(" End of Order Query Loop ");
            System.out.println("done testing cas services ...\n");
            System.out.println("sleeping...");
            try {
                Thread.currentThread().sleep(30000);
            } catch (Exception e)
            {
            }
            casMeter.printData();
            //com.cboe.instrumentationService.commPath.CommPathFactory.getCommPathFactory().cleanup();
        }
        catch (Exception e)
        {
            System.out.println("order thread failed ");
            e.printStackTrace();
            return;
        }
    }
}
