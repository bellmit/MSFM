package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for NewHeldOrderConsumerHome
 * @author Ravi Nagayach
 */
public interface NewHeldOrderConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "NewHeldOrderConsumerHome";
  /**
   * Returns a reference to the NewHeldOrderConsumer
   *
   * @return reference to NewHeldOrderConsumer
   *
   * @author Ravi Nagayach
   */
  public NewHeldOrderConsumer find();
  /**
   * Creates an instance of the NewHeldOrderConsumer
   *
   * @return reference to NewHeldOrderConsumer
   *
   * @author Ravi Nagayach
   */
  public NewHeldOrderConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(NewHeldOrderConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(NewHeldOrderConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(NewHeldOrderConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
