package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the IntermarketAdminMessageConsumer Home
 * @author Emily Huang
 */
public interface IntermarketAdminMessageConsumerHome
{
    /**
	 * Name that will be used for this home.
	 */
    public final static String HOME_NAME = "IntermarketAdminMessageConsumerHome";

  /**
   * Returns a reference to the AcceptIntermarketAdminMessageConsumer service.
   *
   * @return reference to AcceptIntermarketAdminMessageConsumer service
   *
   * @author Emily Huang
   */
  public IntermarketAdminMessageConsumer find();

  /**
   * Creates an instance of the AcceptIntermarketAdminMessageConsumer service.
   *
   * @return reference to AcceptIntermarketAdminMessageConsumer service
   *
   * @author Emily Huang
   */
  public IntermarketAdminMessageConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(IntermarketAdminMessageConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(IntermarketAdminMessageConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(IntermarketAdminMessageConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

