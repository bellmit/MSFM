package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the PropertyConsumer  Home
 */
public interface PropertyConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "PropertyConsumerHome";
  /**
   * Returns a reference to the PropertyConsumer service.
   *
   * @return reference to PropertyConsumer service
   *
   * @author Emily Huang
   */
  public PropertyConsumer find();
  /**
   * Creates an instance of the PropertyConsumer service.
   *
   * @return reference to PropertyConsumer service
   *
   * @author Emily Huang
   */
  public PropertyConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(PropertyConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(PropertyConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(PropertyConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}


