package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Current Market Home
 * @author Connie Liang
 */
public interface QuoteNotificationConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "QuoteNotificationConsumerHome";
  /**
   * Returns a reference to the QuoteNotificationConsumer service.
   *
   * @return reference to QuoteNotificationConsumer service
   *
   * @author Connie Liang
   */
  public QuoteNotificationConsumer find();
  /**
   * Creates an instance of the QuoteNotificationConsumer service.
   *
   * @return reference to QuoteNotificationConsumer service
   *
   * @author Connie Liang
   */
  public QuoteNotificationConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(QuoteNotificationConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(QuoteNotificationConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(QuoteNotificationConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

