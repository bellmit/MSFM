package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Current Market Home
 * @author Connie Liang
 */
public interface MarketAlertConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "MarketAlertConsumerHome";
  /**
   * Returns a reference to the MarketAlertConsumer service.
   *
   * @return reference to MarketAlertConsumer service
   *
   * @author Connie Liang
   */
  public MarketAlertConsumer find();
  /**
   * Creates an instance of the MarketAlertConsumer service.
   *
   * @return reference to MarketAlertConsumer service
   *
   * @author Connie Liang
   */
  public MarketAlertConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(MarketAlertConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(MarketAlertConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(MarketAlertConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

