package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the SystemControlConsumerHome
 */
public interface SystemControlConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "SystemControlConsumerHome";
  /**
   * Returns a reference to the SystemControlConsumer service.
   *
   * @return reference to SystemControlConsumer service
   *
   */
  public SystemControlConsumer find();
  /**
   * Creates an instance of the SystemControlConsumer service.
   *
   * @return reference to SystemControlConsumer service
   *
   */
  public SystemControlConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(SystemControlConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(SystemControlConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(SystemControlConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}


