package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for IntermarketOrderStatusConsumerHome
 * @author Ravi Nagayach
 */
public interface IntermarketOrderStatusConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "IntermarketOrderStatusConsumerHome";
  /**
   * Returns a reference to the IntermarketOrderStatusConsumer
   *
   * @return reference to IntermarketOrderStatusConsumer
   *
   * @author Ravi Nagayach
   */
  public IntermarketOrderStatusConsumer find();
  /**
   * Creates an instance of the IntermarketOrderStatusConsumer
   *
   * @return reference to IntermarketOrderStatusConsumer
   *
   * @author Ravi Nagayach
   */
  public IntermarketOrderStatusConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(IntermarketOrderStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(IntermarketOrderStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(IntermarketOrderStatusConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
