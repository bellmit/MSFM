package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Quote Risk Management Profile Consume Home
 * @author Mike Pyatetsky
 */
public interface QuoteRiskManagementProfileConsumerHome
{
    /**
     * Name that will be used for this home.
     */
	public final static String HOME_NAME = "QuoteRiskManagementProfileConsumerHome";
  /**
   * Returns a reference to the Quote Risk Management Profile Consume.
   *
   * @return reference to QuoteRiskManagementProfileConsumer
   *
   * @author Mike Pyatetsky
   */
  public QuoteRiskManagementProfileConsumer find();
  /**
   * Creates an instance of the Quote Risk Management Profile Consume.
   *
   * @return reference QuoteRiskManagementProfileConsumer 
   *
   * @author Mike Pyatetsky
   */
  public QuoteRiskManagementProfileConsumer create();

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(QuoteRiskManagementProfileConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(QuoteRiskManagementProfileConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(QuoteRiskManagementProfileConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

