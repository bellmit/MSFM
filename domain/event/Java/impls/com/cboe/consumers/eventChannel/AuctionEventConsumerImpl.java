package com.cboe.consumers.eventChannel;

/* Copyright 2004 by the Chicago Board Options Exchange ("CBOE"), as an
 * unpublished work. The information contained in this software program
 * constitutes confidential and/or trade secret information belonging to CBOE.
 * This software program is made available to CBOE members and member firms to
 * enable them to develop software applications using the CBOE Market Interface
 * (CMi), and its use is subject to the terms and conditions of a Software
 * License Agreement that governs its use. This document is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 */

import com.cboe.idl.cmiOrder.*;
import com.cboe.interfaces.events.*;
import com.cboe.idl.util.RoutingParameterStruct;

/** Receive auction messages from the server and forward them to an object
 * that will distribute them to appropriate Internal Event Channels.
 * @see AuctionConsumerIECImpl
 */

public class AuctionEventConsumerImpl
        extends com.cboe.idl.events.POA_AuctionEventConsumer
        implements AuctionConsumer
{
    private AuctionConsumer delegate;

    /** Build CORBA-specific object, save worker object.
     * @param auctionConsumer Object that performs the actual work.
     */
    public AuctionEventConsumerImpl(AuctionConsumer auctionConsumer)
    {
        super();
        delegate = auctionConsumer;
    }

    /** Receive event (announcing an auction) from the CBOE event channel and
     * delegate it to the FoundationFramework-based consumer.
     * @param routingParameters Information for distributing this message.
     * @param activeUserKeys Users interested in this auction.
     * @param auctionStruct Details about this auction.
     */
    public void acceptAuction(
            RoutingParameterStruct routingParameters,
            int[] activeUserKeys,
            AuctionStruct auctionStruct)
    {
        // Send message to object that will dispatch to appropriate IECs.
        delegate.acceptAuction(routingParameters, activeUserKeys, auctionStruct);
    }

    public void acceptDirectedAIMAuction(
            RoutingParameterStruct routingParameters,
            int[] activeUserKeys,
            AuctionStruct auctionStruct)
    {
        // Send message to object that will dispatch to appropriate IECs.
        delegate.acceptDirectedAIMAuction(routingParameters, activeUserKeys, auctionStruct);
    }
    // Functions required by interface, but that we will not need.

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data) throws org.omg.CosEventComm.Disconnected
    { }

    public void disconnect_push_consumer()
    { }
}
