package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;


public interface AlertConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "AlertConsumerHome";

  public AlertConsumer find();
  /**
   * Creates an instance of the AlertConsumer service.
   *
   * @return reference to AlertConsumer service
   *
   */
  public AlertConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(AlertConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(AlertConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(AlertConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

