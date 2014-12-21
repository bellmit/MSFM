package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for QuoteUpdateReportConsumerHome
 * @author Ravi Nagayach
 */
public interface QuoteUpdateReportConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "QuoteUpdateReportConsumerHome";
  /**
   * Returns a reference to the QuoteUpdateReportConsumer
   *
   * @return reference to QuoteUpdateReportConsumer
   *
   * @author Ravi Nagayach
   */
  public QuoteUpdateReportConsumer find();
  /**
   * Creates an instance of the QuoteUpdateReportConsumer
   *
   * @return reference to QuoteUpdateReportConsumer
   *
   * @author Ravi Nagayach
   */
  public QuoteUpdateReportConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(QuoteUpdateReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(QuoteUpdateReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(QuoteUpdateReportConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
