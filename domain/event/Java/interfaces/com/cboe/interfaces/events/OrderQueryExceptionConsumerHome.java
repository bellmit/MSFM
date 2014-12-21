package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the OrderQueryExceptionConsumer Home
 * @author Connie Feng
 */
public interface OrderQueryExceptionConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OrderQueryExceptionConsumerHome";
  /**
   * Returns a reference to the OrderQueryExceptionConsumer service.
   *
   * @return reference to OrderQueryExceptionConsumer service
   *
   * @author Connie Feng
   */
  public OrderQueryExceptionConsumer find();
  /**
   * Creates an instance of the OrderQueryExceptionConsumer service.
   *
   * @return reference to OrderQueryExceptionConsumer service
   *
   * @author Connie Feng
   */
  public OrderQueryExceptionConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderQueryExceptionConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderQueryExceptionConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderQueryExceptionConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

