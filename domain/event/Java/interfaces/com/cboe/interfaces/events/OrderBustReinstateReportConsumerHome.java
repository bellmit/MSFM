package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the OrderBustReinstateReportConsumer
 * @author Connie Feng
 */
public interface OrderBustReinstateReportConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OrderBustReinstateReportConsumerHome";
  /**
   * Returns a reference to theOrderBustReinstateReportConsumer
   *
   * @return reference to OrderBustReinstateReportConsumer
   *
   *    */
  public OrderBustReinstateReportConsumer find();
  /**
   * Creates an instance of the OrderBustReinstateReportConsumer.
   *
   * @return reference to OrderBustReinstateReportConsumer
   */
  public OrderBustReinstateReportConsumer create();

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderBustReinstateReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderBustReinstateReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderBustReinstateReportConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

