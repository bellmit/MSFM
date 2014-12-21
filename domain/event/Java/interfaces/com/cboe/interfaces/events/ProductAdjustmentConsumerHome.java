package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Product Adjustment Event channel home
 * @author John Wickberg
 */
public interface ProductAdjustmentConsumerHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ProductAdjustmentConsumerHome";
  /**
   * Returns a reference to the product adjustment event channel publisher.
   *
   * @return reference to product adjustment event channel publisher
   */
  public ProductAdjustmentConsumer find();
  /**
   * Creates an instance of the product adjustment event channel publisher.
   *
   * @return reference to product adjustment event channel publisher
   */
  public ProductAdjustmentConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(ProductAdjustmentConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(ProductAdjustmentConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(ProductAdjustmentConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


}

