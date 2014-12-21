//
// -----------------------------------------------------------------------------------
// Source file: QueryOrdersByLocationTypeTest.java
//
// PACKAGE: com.cboe.presentation.orderQuery.locationType
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.test;

import java.util.*;

import static com.cboe.idl.constants.OrderLocations.BOOTH_OMT;
import static com.cboe.idl.constants.OrderLocations.CMI;
import static com.cboe.idl.constants.OrderLocations.CROWD_OMT;
import static com.cboe.idl.constants.OrderLocations.HELP_DESK_OMT;
import static com.cboe.idl.constants.OrderLocations.LINKAGE;
import static com.cboe.idl.constants.OrderLocations.OHS;
import static com.cboe.idl.constants.OrderLocations.PAR;
import static com.cboe.idl.constants.OrderLocations.TE;
import static com.cboe.idl.constants.OrderLocations.TPF;
import com.cboe.idl.order.OrderLocationSummaryServerResponseStruct;
import com.cboe.idl.order.OrderLocationSummaryStruct;
import com.cboe.idl.util.LocationStruct;
import com.cboe.idl.util.ServerResponseStructV2;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.api.OrderQueryThrottleException;

/**
 * This class is used to test Query Orders By Location Type functionality.
 *
 * @author Thomas Morrow
 * @since Aug 23, 2007
 */
public class QueryOrdersByLocationTypeTest extends AbstractQueryTest<OrderLocationSummaryServerResponseStruct>
{

    public ServerResponseStructV2[] getOrdersByLocationType(short[] locationTypes, String transactionId)
            throws SystemException, CommunicationException, DataValidationException,
            AuthorizationException, OrderQueryThrottleException
    {
        setTransactionId(transactionId);
        startPublishingToEventChannel();
        return getServerResponseStructs();
    }

    protected List<OrderLocationSummaryServerResponseStruct> getStructsToPublish()
    {
        List<OrderLocationSummaryServerResponseStruct> myList = new ArrayList<OrderLocationSummaryServerResponseStruct>(1);
        myList.add(getOrderLocationSummaryServerResponseStruct(BC_ONE_SERVER, getTransactionId(), USER_ID));
        myList.add(getOrderLocationSummaryServerResponseStruct(BC_TWO_SERVER, getTransactionId(), USER_ID));
        myList.add(getOrderLocationSummaryServerResponseStruct(BC_THREE_SERVER, getTransactionId(), USER_ID));
        myList.add(getOrderLocationSummaryServerResponseStruct(BC_FIVE_SERVER, getTransactionId(), USER_ID));
        return myList;
    }

    public void publishChannelEvent(OrderLocationSummaryServerResponseStruct publishStruct)
    {
        orderRoutingConsumer.acceptOrderLocationSummaryServerResponse(publishStruct);
    }

    public OrderLocationSummaryServerResponseStruct getOrderLocationSummaryServerResponseStruct(String aServer, String aTransactionId, String aUserId)
    {
        OrderLocationSummaryStruct[] locationSummaryStructs = {
                                                    getOrderLocationSummaryStruct(CMI          ,  ABC_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(TPF          ,  ABC_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(TE           ,  ABC_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(PAR          ,  ABC_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(BOOTH_OMT    ,  ABC_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(CROWD_OMT    ,  ABC_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(HELP_DESK_OMT,  ABC_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(OHS          ,  ABC_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(LINKAGE      ,  ABC_USER,  true, 10),

                                                    getOrderLocationSummaryStruct(CMI          ,  ABD_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(TPF          ,  ABD_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(TE           ,  ABD_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(PAR          ,  ABD_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(BOOTH_OMT    ,  ABD_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(CROWD_OMT    ,  ABD_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(HELP_DESK_OMT,  ABD_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(OHS          ,  ABD_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(LINKAGE      ,  ABD_USER,  true, 10),

                                                    getOrderLocationSummaryStruct(CMI          , B001_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(TPF          , B001_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(TE           , B001_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(PAR          , B001_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(BOOTH_OMT    , B001_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(CROWD_OMT    , B001_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(HELP_DESK_OMT, B001_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(OHS          , B001_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(LINKAGE      , B001_USER,  true, 10),

                                                    getOrderLocationSummaryStruct(CMI          , B002_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(TPF          , B002_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(TE           , B002_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(PAR          , B002_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(BOOTH_OMT    , B002_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(CROWD_OMT    , B002_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(HELP_DESK_OMT, B002_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(OHS          , B002_USER,  true, 10),
                                                    getOrderLocationSummaryStruct(LINKAGE      , B002_USER,  true, 10),
                                            };
        return new OrderLocationSummaryServerResponseStruct(getServerTransactionIdStruct(aServer, aTransactionId, aUserId), locationSummaryStructs);
    }

    public OrderLocationSummaryStruct getOrderLocationSummaryStruct(short aLocationType, String aLocation, boolean isLoggedIn, int orderCount)
    {
        return new OrderLocationSummaryStruct(getLocationStruct(aLocationType, aLocation), isLoggedIn, orderCount);
    }

    public LocationStruct getLocationStruct(short aLocationType, String aLocation)
    {
        return new LocationStruct(aLocationType, aLocation);
    }

}
