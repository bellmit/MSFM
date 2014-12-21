package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the UserTimeoutWarningConsumer Home
 * @author Connie Feng
 */
public interface UserTimeoutWarningConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "UserTimeoutWarningConsumerHome";
  /**
   * Returns a reference to the UserTimeoutWarningConsumer service.
   *
   * @return reference to UserTimeoutWarningConsumer service
   *
   * @author Connie Feng
   */
  public UserTimeoutWarningConsumer find();
  /**
   * Creates an instance of the UserTimeoutWarningConsumer service.
   *
   * @return reference to UserTimeoutWarningConsumer service
   *
   * @author Connie Feng
   */
  public UserTimeoutWarningConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(UserTimeoutWarningConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(UserTimeoutWarningConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(UserTimeoutWarningConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

