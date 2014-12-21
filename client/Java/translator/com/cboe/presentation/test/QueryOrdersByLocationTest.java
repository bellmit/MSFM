//
// -----------------------------------------------------------------------------------
// Source file: QueryOrdersByLocationTest.java
//
// PACKAGE: com.cboe.presentation.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.test;

import java.util.*;

import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.constants.OHSRoutingReasons;
import com.cboe.idl.order.OrderLocationServerResponseStruct;
import com.cboe.idl.order.OrderQueryResultStruct;
import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.util.ServerResponseStructV2;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.api.OrderQueryThrottleException;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.order.OrderFactory;

import com.cboe.domain.util.DateWrapper;

/**
 * This class is used to test Query Orders By Location functionality.
 *
 * @author Martha Fourt
 * @since Aug 23, 2007
 */
public class QueryOrdersByLocationTest extends AbstractQueryTest<OrderLocationServerResponseStruct>
{
    private DateTimeStruct dateTimeStruct;

    // Various class keys that may be used to retrieve orders by class key
    private static final int ABB = 227691291;
    private static final int ACAD = 237109252;
    private static final int ADPT = 69206372;
    private static final int ANX = 233832451;
    private static final int ATMI = 236847113;
    private static final int ATML = 69207316;
    private static final int AUY = 233832458;
    private static final int AVII = 69207653;
    private static final int CRI = 227488699;
    private static final int NUM_ORDERS = 4;

    public ServerResponseStructV2[] getOrdersByLocation(String location, String transactionId)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, OrderQueryThrottleException
    {
        setTransactionId(transactionId);
        startPublishingToEventChannel();
        return getServerResponseStructs();
    }

    protected List<OrderLocationServerResponseStruct> getStructsToPublish()
    {
        List<OrderLocationServerResponseStruct> myList = new ArrayList<OrderLocationServerResponseStruct>(1);
        myList.add(getOrderLocationServerResponseStruct(BC_ONE_SERVER, getTransactionId(), USER_ID, 0, 1, 2));
        myList.add(getOrderLocationServerResponseStruct(BC_TWO_SERVER, getTransactionId(), USER_ID, 0, 1, 2));
        myList.add(getOrderLocationServerResponseStruct(BC_THREE_SERVER, getTransactionId(), USER_ID, 0, 1, 1));
        myList.add(getOrderLocationServerResponseStruct(BC_FIVE_SERVER, getTransactionId(), USER_ID, 0, 1, 1));
        myList.add(getOrderLocationServerResponseStruct(BC_ONE_SERVER, getTransactionId(), USER_ID, 0, 2, 2));
        myList.add(getOrderLocationServerResponseStruct(BC_TWO_SERVER, getTransactionId(), USER_ID, 0, 2, 2));
        return myList;
    }

    public void publishChannelEvent(OrderLocationServerResponseStruct publishStruct)
    {
        orderRoutingConsumer.acceptOrderLocationServerResponse(publishStruct);
    }

    public OrderLocationServerResponseStruct getOrderLocationServerResponseStruct(String aServer,
        String aTransactionId, String aUserId, int classKey, int pageNum, int totalPageNum)
    {
        OrderStruct[] orderStructs = getOrderStructs(classKey);
        OrderRoutingStruct[] routingStructs = new OrderRoutingStruct[orderStructs.length];
        for(int i = 0; i < orderStructs.length; i++)
        {
            OrderStruct orderStruct = orderStructs[i];
            RouteReasonStruct routeReason =
                    new RouteReasonStruct(OHSRoutingReasons.DIRECT_ROUTE,
                                          "Test Route", i + 1,
                                          DateWrapper.convertToDateTime(System.currentTimeMillis()));
            OrderRoutingStruct routingStruct = new OrderRoutingStruct(orderStruct, routeReason);
            routingStructs[i] = routingStruct;
        }
        return new OrderLocationServerResponseStruct(getServerTransactionIdStruct(aServer, aTransactionId, aUserId),
                                                     routingStructs, routingStructs.length * 2, pageNum, totalPageNum);
    }

    private OrderStruct[] getOrderStructs(int classKey)
    {
        if (classKey > 0)
        {
            //  get orders by class and time
            try
            {
                OrderQueryResultStruct orders = APIHome.findOrderManagementTerminalAPI().
                                getOrdersByClassAndTime(classKey, getDateTimeStruct(), (short)1);
                return orders.orderStructSequence;
            }
            catch(SystemException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
            catch(CommunicationException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
            catch(DataValidationException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
            catch(AuthorizationException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
            catch(OrderQueryThrottleException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
        else
        {
            // create default orders
            OrderStruct[] orderSeq = new OrderStruct[NUM_ORDERS];
            for(int i = 0; i < NUM_ORDERS; i++)
            {
                orderSeq[i] = OrderFactory.createDefaultOrderStruct();
            }
            return orderSeq;
        }
        return null;
    }

    private DateTimeStruct getDateTimeStruct()
    {
        if (dateTimeStruct == null)
        {
            TimeStruct time = new TimeStruct();
            DateStruct date = new DateStruct((byte)8, (byte)1, (short)2007);
            dateTimeStruct = new DateTimeStruct(date, time);
        }
        return dateTimeStruct;
    }

}
