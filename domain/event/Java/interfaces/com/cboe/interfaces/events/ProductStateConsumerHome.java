package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Product State Event Channel Home
 * @author John Wickberg
 */
public interface ProductStateConsumerHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ProductStateConsumerHome";
  /**
   * Returns a reference to the publisher for the product state channel
   *
   * @return reference to publisher for the product state channel
   */
  public ProductStateConsumer find();
  /**
   * Creates an instance of the publisher for the product state channel.
   *
   * @return reference to publisher for the product state channel
   */
  public ProductStateConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(ProductStateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(ProductStateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(ProductStateConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


}

