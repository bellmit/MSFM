package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for HeldOrderConsumerHome
 * @author Ravi Nagayach
 */
public interface HeldOrderConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "HeldOrderConsumerHome";
  /**
   * Returns a reference to the HeldOrderConsumer
   *
   * @return reference to HeldOrderConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderConsumer find();
  /**
   * Creates an instance of the HeldOrderConsumer
   *
   * @return reference to HeldOrderConsumer
   *
   * @author Ravi Nagayach
   */
  public HeldOrderConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(HeldOrderConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(HeldOrderConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(HeldOrderConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
