package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Forced Logout message delivery service
 * @author Mike Pyatetsky
 */
public interface UserSessionConsumerHome
{
    /**
    * Name that will be used for this home.
    */
    public final static String HOME_NAME = "UserSessionConsumerHome";

    /**
    * Returns a reference to the Forced Logout message delivery service
    *
    * @return reference to UserSessionConsumer object
    *
    * @author Mike Pyatetsky
    */
    public UserSessionConsumer find();

    /**
    * Creates an instance of the  Forced Logout message delivery service
    *
    * @return reference to UserSessionConsumer object
    *
    * @author Mike Pyatetsky
    */
    public UserSessionConsumer create();

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(UserSessionConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(UserSessionConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(UserSessionConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
