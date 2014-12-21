//
// -----------------------------------------------------------------------------------
// Source file: AbstractQueryTest.java
//
// PACKAGE: com.cboe.presentation.orderQuery
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.test;

import java.text.DateFormat;
import java.util.*;

import com.cboe.idl.constants.ServerResponseCodes;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;
import com.cboe.idl.util.ServerResponseStructV2;
import com.cboe.idl.util.ServerTransactionIdStruct;

import com.cboe.exceptions.ExceptionDetails;

import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.consumers.callback.OrderRoutingConsumerFactory;

/**
 * @author Thomas Morrow
 * @since Aug 23, 2007
 */
public abstract class AbstractQueryTest<E>
{
    protected OrderRoutingConsumer orderRoutingConsumer;
    protected Timer timer;
    protected List<E> publishStructs;
    protected int publishIndex;
    protected long timerDelay;
    protected long timerPeriod;

    public static final String ABC_USER  = "ABC";
    public static final String ABD_USER  = "ABD";
    public static final String B001_USER = "B001";
    public static final String B002_USER = "B002";

    public static final String BC_ONE_SERVER    = "BC1";
    public static final String BC_TWO_SERVER    = "BC2";
    public static final String BC_THREE_SERVER  = "BC3";
    public static final String BC_FOUR_SERVER   = "BC4";
    public static final String BC_FIVE_SERVER   = "BC5";

    public static final String USER_ID = "HD1";

    private String transactionId;

    protected AbstractQueryTest()
    {
        orderRoutingConsumer = OrderRoutingConsumerFactory.create(EventChannelAdapterFactory.find());
        timerDelay = 2000;
        timerPeriod = 2000;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public ServerResponseStructV2[] getServerResponseStructs()
    {
        return new ServerResponseStructV2[]{
                                            getServerResponseStruct(BC_ONE_SERVER  ,
                                                                    ServerResponseCodes.SUCCESS, ""                   ),
                                            getServerResponseStruct(BC_TWO_SERVER  ,
                                                                    ServerResponseCodes.SUCCESS, ""                   ),
                                            getServerResponseStruct(BC_THREE_SERVER,
                                                                    ServerResponseCodes.SUCCESS, ""                   ),
                                            getServerResponseStruct(BC_FOUR_SERVER ,
                                                                    ServerResponseCodes.SYSTEM_EXCEPTION,
                                                                    "Server Unavailable" ),
                                            getServerResponseStruct(BC_FIVE_SERVER ,
                                                                    ServerResponseCodes.SUCCESS, "" ),
                                          };
    }

    private ServerResponseStructV2 getServerResponseStruct(String aServer, short aErrorCode,
                                                         String aDescription)
    {
        ExceptionDetails errorMsg = new ExceptionDetails();
        errorMsg.error = aErrorCode;
        errorMsg.message = aDescription;
        errorMsg.dateTime = DateFormat.getDateInstance().format(new Date());

        return new ServerResponseStructV2(aServer, aErrorCode, errorMsg);
    }

    public ServerTransactionIdStruct getServerTransactionIdStruct(String aServer, String aTransactionId, String aUserId)
    {
        return new ServerTransactionIdStruct(aServer, aTransactionId, aUserId);
    }

    protected void startPublishingToEventChannel()
    {
        publishStructs = getStructsToPublish();
        publishIndex = 0;
        if(publishStructs != null && publishStructs.size() >0)
        {
            startEventTimer();
        }
    }

    public void startEventTimer()
    {
        stopEventTimer();
        timer = new Timer();
        TimerTask task = new TimerTask()
        {
            public void run()
            {
                publishChannelEvent(publishStructs.get(publishIndex));
                if (++publishIndex == publishStructs.size()) {
                    cancel();
                }
            }
        };
        timer.schedule(task, timerDelay, timerPeriod);
    }

    public void stopEventTimer()
    {
        if (timer != null)
        {
            timer.cancel();
        }
    }

    protected abstract List<E> getStructsToPublish();

    //call the appropriate callback in the implementaion of this
    protected abstract void publishChannelEvent(E publishStruct);

}
