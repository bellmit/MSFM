package com.cboe.interfaces.internalCallback;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * 
 */

public interface CalendarUpdateConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "CalendarUpdateConsumerHome";
  /**
   * Returns a reference to the CalendarUpdateConsumer service.
   *
   * @return reference to CalendarUpdateConsumer service
   *
   */
  public CalendarUpdateConsumer find();
  /**
   * Creates an instance of the CalendarUpdateConsumer service.
   *
   * @return reference to CalendarUpdateConsumer service
   *
   */
  public CalendarUpdateConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(CalendarUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(CalendarUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(CalendarUpdateConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}

