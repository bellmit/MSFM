package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 *  @author Steven Sinclair
 */

public interface TradingSessionEventStateConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "TradingSessionEventStateConsumerHome";
  /**
   * Returns a reference to the TradingSessionEventStateConsumer service.
   *
   * @return reference to TradingSessionEventStateConsumer service
   *
   */
  public TradingSessionEventStateConsumer find();
  /**
   * Creates an instance of the TradingSessionEventStateConsumer service.
   *
   * @return reference to TradingSessionEventStateConsumer service
   *
   */
  public TradingSessionEventStateConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(TradingSessionEventStateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(TradingSessionEventStateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(TradingSessionEventStateConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}

