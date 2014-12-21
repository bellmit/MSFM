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
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.cmiConstants.UserRoles;
import com.cboe.idl.cmiConstants.OrderOrigins;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.exceptions.*;

public class CASTestOrderSeed extends CASTestQuote
{
    protected String UserName;
    protected int tid;
    protected int loginNumber;
    protected String prefix;
    protected CMIOrderStatusConsumer clientListener;
    protected String optionalData;
    protected boolean strategyProduct = false;
    protected boolean specifyOrderOriginType = false;
    protected char orderOriginType = 'C';
    protected ArrayList productKeys;
    protected String branch = "ABC";

    public CASTestOrderSeed(TestParameter parm, SessionManagerStructV2 sessionManagerStructV2,
                        CASMeter casMeter, int tid, int loginNumber, CMIOrderStatusConsumer clientListener) throws Exception
    {
        super(parm, sessionManagerStructV2, casMeter);
        this.UserName = (String) parm.userNames.get(tid);
        this.tid = tid;
        this.loginNumber = loginNumber;
        this.prefix = parm.branch;
        this.clientListener = clientListener;
        this.optionalData = parm.optionalData;
        this.strategyProduct = parm.strategyProduct;
        this.specifyOrderOriginType = parm.specifyOrderOriginType;
        this.orderOriginType = parm.orderOriginType;

        if (parm.threadPerClass)
        {
            productKeys = parm.getClassAndProductKeys(tid + tid * (parm.loginsPerUser - 1) + loginNumber);
            System.out.println("Doing threadPerClass for classKey: " + ((ProdClass)productKeys.get(0)).itsClassKey);
        }
        else
        {
            productKeys = parm.productKeys;
        }

        System.out.println("Created Order Thread for user = " + UserName + " login #" + loginNumber);
    }

    public void setUpCallbacks() throws Exception
    {
        System.out.println("Setting up callback objects from CASTestOrder..............................");
    }

    public OrderEntryStruct buildOrderEntryStruct(TestParameter parm)
    {
        //Change to set to current date
        DateStruct date;
        TimeStruct timeStruct = new TimeStruct((byte)0,(byte)0,(byte)0,(byte)0);
        java.util.Calendar javaDate = java.util.Calendar.getInstance();
        java.util.Date aDate = javaDate.getTime();
        int year = aDate.getYear() + 1900;
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

        date = new DateStruct((byte)javaDate.get(javaDate.DAY_OF_MONTH),(byte)javaDate.get(javaDate.MONTH),(short)javaDate.get(javaDate.YEAR));

        TimeStruct expTimeStruct = new TimeStruct((byte)22,(byte)0,(byte)0,(byte)0);
        DateTimeStruct expireTime = new DateTimeStruct(date, expTimeStruct);
        // create an OrderStruct, thing we'll pass to OrderHandlingService
        OrderEntryStruct order = (OrderEntryStruct)ReflectiveStructBuilder.newStruct(com.cboe.idl.cmiOrder.OrderEntryStruct.class);
        order.expireTime = expireTime;
        order.orderDate = dateString;

        order.originalQuantity = parm.orderQuantity;
        order.orderOriginType = parm.orderOriginType;
        order.timeInForce = parm.orderTimeInForce;
        order.positionEffect = 'O';
        order.coverage = 'B';
        //order.productType = 7;
        //order.state = 6;
        PriceStruct price = new PriceStruct((short) 2, 10, 0); //Price.create(10.0).toStruct();
        order.price = price;
        order.side = 'S';
        OrderContingencyStruct contingency = new com.cboe.idl.cmiOrder.OrderContingencyStruct(parm.orderContingencyType,price,0);
        order.contingency = contingency; //.type = 1;
        order.productKey = 0;
        //order.orderId.branch = "NFS";
        order.branch = (UserName + "ZZZ").substring(0,3);
        order.account = UserName;
        order.subaccount = UserName;
        order.optionalData = optionalData;
        String[] tmpSessions = new String[1];
        tmpSessions[0] = "";
        order.sessionNames = tmpSessions;
        return order;
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

        int numOfOrders = productKeys.size();

        System.out.println("Start of " + numOfOrders + " order seeding test.");
        System.out.println("Sending orders at a message rate of (" + msgRate + ")");

        int currentProduct = 0;
        int currentProductOffset = 0;

        boolean limitProdRange = parm.limitProdRange;

        Random numgen = new Random();
//        ArrayList productKeys = parm.productKeys;

        //String memberKey = parm.memberKey;
        //String clearingFirmKey = "";
        // create an OrderStruct, thing we'll pass to OrderHandlingService

        OrderEntryStruct order = buildOrderEntryStruct(parm);
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
        int prodKeySize = productKeys.size();
        int randomIndex;
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

            OrderEntry  oe = sessionManagerStruct.sessionManager.getOrderEntry();
            UserStruct userStruct = sessionManagerStruct.sessionManager.getValidUser();

            if ( specifyOrderOriginType )
            {
                order.orderOriginType = orderOriginType;
            }
            else if ( userStruct.role == UserRoles.MARKET_MAKER || userStruct.role == UserRoles.DPM_ROLE )
            {
                order.orderOriginType = OrderOrigins.MARKET_MAKER;
            }
            else
            {
                order.orderOriginType = OrderOrigins.BROKER_DEALER;
            }

            order.correspondentFirm = (prefix + newLogin + "ZZZZ").substring(0,4);

            // for(int x = 0; x < prodKeySize; x++)
            for(int x = 0; x < numOfOrders && !parm.threadDone; x++)
            {
                if (x % reportEvery == 0)
                {
                    System.out.println("Done with " + x + " orders.");
                }

                order.branchSequenceNumber = branchSeqValue++;

                if (branchSeqValue > 9998)
                {
                    branchSeqValue = 1;
                    char newPrefix = (prefix.toCharArray())[0];
                    newPrefix++;
                    prefix = String.valueOf(newPrefix);
                    System.out.println("Sequence number Using new prefix of " + prefix);
                    order.correspondentFirm = (prefix + newLogin + "ZZZZ").substring(0,4);
                }

                ProdClass tmpProdClass = (ProdClass)productKeys.get(x);
                order.executingOrGiveUpFirm = userStruct.defaultProfile.executingGiveupFirm;
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

//                order.correspondentFirm = "";

                order.productKey = tmpProdClass.itsProductKey;
                order.userAssignedId = Integer.toString(x + loginNumber * numOfOrders);
                order.sessionNames[0] = tmpProdClass.itsSessionName;

                if (parm.side == 'R')
                {
                    if (numgen.nextBoolean())
                    {
                        if (this.strategyProduct)
                        {
                            order.price = this.getAskPrice(parm,false);
                            order.side = Sides.OPPOSITE;
                        } else
                        {
                            order.price = this.getAskPrice(parm,false);
                            order.side = Sides.SELL;
                        }
                    }
                    else
                    {
                        if (this.strategyProduct)
                        {
                            // price should be negative
                            order.price = this.getBidPrice(parm, false);
                            order.side = Sides.AS_DEFINED;
                        } else
                        {
                            order.price = this.getBidPrice(parm, false);
                            order.side = Sides.BUY;
                        }
                    }
                }
                else if (parm.side == 'B')
                {
                    if (this.strategyProduct)
                    {
                        // price should be negative
                        order.price = this.getBidPrice(parm, false);
                        order.side = Sides.AS_DEFINED;
                    } else
                    {
                        order.price = this.getBidPrice(parm, false);
                        order.side = Sides.BUY;
                    }
                }
                else
                {
                    if (this.strategyProduct)
                    {
                        order.price = this.getAskPrice(parm,false);
                        order.side = Sides.OPPOSITE;
                    } else
                    {
                        order.price = this.getAskPrice(parm,false);
                        order.side = Sides.SELL;
                    }
                }

                //        System.out.println("product key = " + String.valueOf(order.productKey));
                //order.classKey = tmpProdClass.itsClassKey;
                try
                {
//                    System.out.println("Entering Order: " + order.branch + " : " + order.correspondentFirm + ":" + order.branchSequenceNumber);
                    casMeter.setStartTime(x + loginNumber * numOfOrders);
                    casMeter.setMethodCalled(x + loginNumber * numOfOrders, 'N');
                    oe.acceptOrder(order);
                    currentCount++;
                    casMeter.setFinishTime(x + loginNumber * numOfOrders);
                    //      System.out.println(Integer.toString(x + 1)+" Calls returned");
                    if ( msgRate != 0 && currentCount % msgRate == 0 )
                    {
                        curTime = System.currentTimeMillis();

                        if (curTime >= (stepTime + parm.stepUpInterval))
                        {
                            currentInterval = curTime - stepTime;
                            currentRate = (double)currentCount/(double)currentInterval * 1000;
                            msgRate = msgRate + stepUp;
                            newRate = (double)msgRate/(double)msgInterval;
                            System.out.println("Orders: " + currentCount + " : Time: " + currentInterval + " : Rate: " + currentRate
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
            System.out.println(" End of Order Loop ");
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
