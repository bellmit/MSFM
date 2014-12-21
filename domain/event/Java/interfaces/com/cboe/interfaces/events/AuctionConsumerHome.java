package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * Home interface for creating and managing AuctionConsumer objects.
 */
public interface AuctionConsumerHome {
    /** Name that will be used for this home.
     */
    public final static String HOME_NAME = "AuctionConsumerHome";

    /** Returns a reference to the AuctionConsumer.
     * @return reference to AuctionConsumer.
     */
    public AuctionConsumer find();

    /** Create an instance of the AuctionConsumer.
     * @return reference to AuctionConsumer.
     */
    public AuctionConsumer create();

    /** Register consumer as a listener to this channel for events matching key.
     *
     * @param consumer implementation to receive events
     * @param key filtering key
     */
    public void addConsumer(AuctionConsumer consumer, ChannelKey key)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /** Unregister consumer as a listener to this channel for events matching key.
     * @param consumer implementation to receive events
     * @param key filtering key
     */
    public void removeConsumer(AuctionConsumer consumer, ChannelKey key)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /** Unregister consumer as a listener to this channel for all events.
     * @param consumer implementation to receive events
     */
    public void removeConsumer(AuctionConsumer consumer)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
