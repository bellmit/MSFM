package com.cboe.interfaces.events;

import java.util.ArrayList;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the TickerConsumer Home
 * @author Jeff Illian
 */
public interface TickerConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "TickerConsumerHome";
  /**
   * Returns a reference to the TickerConsumer service.
   *
   * @return reference to TickerConsumer service
   *
   * @author Jeff Illian
   */
  public TickerConsumer find();
  /**
   * Creates an instance of the TickerConsumer service.
   *
   * @return reference to TickerConsumer service
   *
   * @author Jeff Illian
   */
  public TickerConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(TickerConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(TickerConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(TickerConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

   public ArrayList getInvalidSalePrefixesForLargeTrade();
   public ArrayList getValidSessionsForLargeTrade();
}

