package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for OrderFillRejectConsumerHome
 * @author Ravi Nagayach
 */
public interface OrderFillRejectConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OrderFillRejectConsumerHome";
  /**
   * Returns a reference to the OrderFillRejectConsumer
   *
   * @return reference to OrderFillRejectConsumer
   *
   * @author Ravi Nagayach
   */
  public OrderFillRejectConsumer find();
  /**
   * Creates an instance of the OrderFillRejectConsumer
   *
   * @return reference to OrderFillRejectConsumer
   *
   * @author Ravi Nagayach
   */
  public OrderFillRejectConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderFillRejectConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderFillRejectConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderFillRejectConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
