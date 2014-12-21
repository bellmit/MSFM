package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the OrderQueryConsumer Home
 * @author Connie Feng
 */
public interface OrderQueryConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OrderQueryConsumerHome";
  /**
   * Returns a reference to the OrderQueryConsumer service.
   *
   * @return reference to OrderQueryConsumer service
   *
   * @author Connie Feng
   */
  public OrderQueryConsumer find();
  /**
   * Creates an instance of the OrderQueryConsumer service.
   *
   * @return reference to OrderQueryConsumer service
   *
   * @author Connie Feng
   */
  public OrderQueryConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderQueryConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderQueryConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderQueryConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

