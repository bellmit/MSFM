package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Product Update Event channel home
 * @author John Wickberg
 */
public interface ProductUpdateConsumerHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ProductUpdateConsumerHome";
  /**
   * Returns a reference to the publisher for the product update channel.
   *
   * @return reference to publisher for the product update channel
   */
  public ProductUpdateConsumer find();
  /**
   * Creates an instance of the publisher for the product update channel.
   *
   * @return reference to publisher for the product update channel
   */
  public ProductUpdateConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(ProductUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(ProductUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(ProductUpdateConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


}

