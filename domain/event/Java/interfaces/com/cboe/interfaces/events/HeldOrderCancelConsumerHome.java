package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for HeldOrderCancelConsumerHome
 * @author Ravi Nagayach
 */
public interface HeldOrderCancelConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "HeldOrderCancelConsumerHome";
  /**
   * Returns a reference to the HeldOrderCancelConsumer
   *
   * @return reference to HeldOrderCancelConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderCancelConsumer find();
  /**
   * Creates an instance of the HeldOrderCancelConsumer
   *
   * @return reference to HeldOrderCancelConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderCancelConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(HeldOrderCancelConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(HeldOrderCancelConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(HeldOrderCancelConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
