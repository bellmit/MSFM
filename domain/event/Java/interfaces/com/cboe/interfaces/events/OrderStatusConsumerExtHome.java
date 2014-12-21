package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for OrderStatusConsumerExtHome
 * @author Ravi Nagayach
 */
public interface OrderStatusConsumerExtHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OrderStatusConsumerExtHome";
  /**
   * Returns a reference to the OrderStatusConsumerExt
   *
   * @return reference to OrderStatusConsumerExt
   *
   * @author Ravi Nagayach
   */
  public OrderStatusConsumerExt find();
  /**
   * Creates an instance of the OrderStatusConsumerExt
   *
   * @return reference to OrderStatusConsumerExt
   *
   * @author Ravi Nagayach
   */
  public OrderStatusConsumerExt create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderStatusConsumerExt consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderStatusConsumerExt consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderStatusConsumerExt consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
