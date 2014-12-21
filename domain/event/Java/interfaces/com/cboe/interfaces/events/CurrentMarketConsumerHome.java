package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Current Market Home
 * @author Jeff Illian
 */
public interface CurrentMarketConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "CurrentMarketConsumerHome";
  /**
   * Returns a reference to the CurrentMarketConsumer service.
   *
   * @return reference to CurrentMarketConsumer service
   *
   * @author Jeff Illian
   */
  public CurrentMarketConsumer find();
  /**
   * Creates an instance of the CurrentMarketConsumer service.
   *
   * @return reference to CurrentMarketConsumer service
   *
   * @author Jeff Illian
   */
  public CurrentMarketConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(CurrentMarketConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(CurrentMarketConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(CurrentMarketConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

