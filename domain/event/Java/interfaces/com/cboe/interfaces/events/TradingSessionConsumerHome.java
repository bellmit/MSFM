package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the TickerConsumer Home
 * @author Connie Feng
 */

public interface TradingSessionConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "TradingSessionConsumerHome";
  /**
   * Returns a reference to the TradingSessionConsumer service.
   *
   * @return reference to TradingSessionConsumer service
   *
   * @author Connie Feng
   */
  public TradingSessionConsumer find();
  /**
   * Creates an instance of the TradingSessionConsumer service.
   *
   * @return reference to TradingSessionConsumer service
   *
   * @author Connie Feng
   */
  public TradingSessionConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(TradingSessionConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(TradingSessionConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(TradingSessionConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}

