package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the component(FE) up/down Status
 * @author Keval Desai
 */
public interface ComponentConsumerHome
{
    /**
    * Name that will be used for this home.
    */
    public final static String HOME_NAME = "ComponentConsumerHome";

    /**
    * Returns a reference to the the componentConsumer
    *
    * @return reference to ComponentConsumer object
    *
    */
    public ComponentConsumer find();

    public ComponentConsumer findCASStatusListener();

    /**
    * Creates an instance of the componentConsumer
    *
    * @return reference to componentConsumer object
    *
    */
    public ComponentConsumer create();

    public ComponentConsumer createCASStatusListener();

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(ComponentConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(ComponentConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(ComponentConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
