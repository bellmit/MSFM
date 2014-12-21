package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for HeldOrderCancelReportConsumerHome
 * @author Ravi Nagayach
 */
public interface HeldOrderCancelReportConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "HeldOrderCancelReportConsumerHome";
  /**
   * Returns a reference to the HeldOrderCancelReportConsumer
   *
   * @return reference to HeldOrderCancelReportConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderCancelReportConsumer find();
  /**
   * Creates an instance of the HeldOrderCancelReportConsumer
   *
   * @return reference to HeldOrderCancelReportConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderCancelReportConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(HeldOrderCancelReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(HeldOrderCancelReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(HeldOrderCancelReportConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
