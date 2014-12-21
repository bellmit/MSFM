package com.cboe.interfaces.internalCallback;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 *  @author William Wei
 */

public interface BookDepthConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "BookDepthConsumerHome";
  /**
   * Returns a reference to the BookDepthConsumer service.
   *
   * @return reference to BookDepthConsumer service
   *
   */
  public BookDepthConsumer find();
  /**
   * Creates an instance of the BookDepthConsumer service.
   *
   * @return reference to BookDepthConsumer service
   *
   */
  public BookDepthConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(BookDepthConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(BookDepthConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(BookDepthConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}

