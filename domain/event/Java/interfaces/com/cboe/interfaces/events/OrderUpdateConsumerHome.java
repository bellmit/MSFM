package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Market Query Home
 * @author Jeff Illian
 */
public interface OrderUpdateConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OrderUpdateConsumerHome";
  /**
   * Returns a reference to the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public OrderUpdateConsumer find();
  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public OrderUpdateConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderUpdateConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

