package com.cboe.interfaces.internalCallback;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 *  @author Steven Sinclair
 */

public interface CacheUpdateConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "CacheUpdateConsumerHome";
  /**
   * Returns a reference to the CacheUpdateConsumer service.
   *
   * @return reference to CacheUpdateConsumer service
   *
   */
  public CacheUpdateConsumer find();
  /**
   * Creates an instance of the CacheUpdateConsumer service.
   *
   * @return reference to CacheUpdateConsumer service
   *
   */
  public CacheUpdateConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(CacheUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(CacheUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(CacheUpdateConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}

