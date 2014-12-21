package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for HeldOrderStatusConsumerHome
 * @author Ravi Nagayach
 */
public interface HeldOrderStatusConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "HeldOrderStatusConsumerHome";
  /**
   * Returns a reference to the HeldOrderStatusConsumer
   *
   * @return reference to HeldOrderStatusConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderStatusConsumer find();
  /**
   * Creates an instance of the HeldOrderStatusConsumer
   *
   * @return reference to HeldOrderStatusConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderStatusConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(HeldOrderStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(HeldOrderStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(HeldOrderStatusConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
