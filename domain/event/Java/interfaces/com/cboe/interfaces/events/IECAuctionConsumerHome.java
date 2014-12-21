package com.cboe.interfaces.events;

/** Home for Auction Internal Event Channels.
 * Instantiated using Java reflection, based on configuration data in
 * XML files.
 */
public interface IECAuctionConsumerHome
        extends AuctionConsumerHome, EventChannelConsumerManager
{ }
