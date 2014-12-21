package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the OrderBustReportConsumer
 * @author Connie Feng
 */
public interface OrderBustReportConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OrderBustReportConsumerHome";
  /**
   * Returns a reference to the OrderBustReportConsumer.
   *
   * @return reference to market query service
   */
  public OrderBustReportConsumer find();
  /**
   * Creates an instance of the OrderBustReportConsumer.
   *
   * @return reference to OrderBustReportConsumer
   */
  public OrderBustReportConsumer create();

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderBustReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderBustReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderBustReportConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


}

