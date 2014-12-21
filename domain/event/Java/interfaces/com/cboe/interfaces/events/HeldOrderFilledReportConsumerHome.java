package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for HeldOrderFilledReportConsumerHome
 * @author Ravi Nagayach
 */
public interface HeldOrderFilledReportConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "HeldOrderFilledReportConsumerHome";
  /**
   * Returns a reference to the HeldOrderFilledReportConsumer
   *
   * @return reference to HeldOrderFilledReportConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderFilledReportConsumer find();
  /**
   * Creates an instance of the HeldOrderFilledReportConsumer
   *
   * @return reference to HeldOrderFilledReportConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderFilledReportConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(HeldOrderFilledReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(HeldOrderFilledReportConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(HeldOrderFilledReportConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
