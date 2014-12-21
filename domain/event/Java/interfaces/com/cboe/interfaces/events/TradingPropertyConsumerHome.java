package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the TradingPropertyConsumer Home
 * @author John Wickberg
 */
public interface TradingPropertyConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "TradingPropertyConsumerHome";
  /**
   * Returns a reference to the TradingPropertyConsumer service.
   *
   * @return reference to TradingPropertyConsumer service
   */
  public TradingPropertyConsumerExt find();
  /**
   * Creates an instance of the TickerConsumer service.
   *
   * @return reference to TickerConsumer service
   */
  public TradingPropertyConsumerExt create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events, still keep the type more general
   * @param key filtering key
   */
   public void addConsumer(TradingPropertyConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events, still keep the type more general
   * @param key filtering key
   */
   public void removeConsumer(TradingPropertyConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events, still keep the type more general
   */
   public void removeConsumer(TradingPropertyConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}

