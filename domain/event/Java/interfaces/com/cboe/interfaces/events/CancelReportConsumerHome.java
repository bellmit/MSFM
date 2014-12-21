package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Market Query Home
 * @author Jeff Illian
 */
public interface CancelReportConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "CancelReportConsumerHome";
  /**
   * Returns a reference to the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public CancelReportConsumer find();
  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public CancelReportConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(CancelReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(CancelReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(CancelReportConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

