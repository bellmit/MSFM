package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for NBBOAgentAdminConsumerHome
 * @author Ravi Nagayach
 */
public interface NBBOAgentAdminConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "NBBOAgentAdminConsumerHome";
  /**
   * Returns a reference to the NBBOAgentAdminConsumer
   *
   * @return reference to NBBOAgentAdminConsumer
   *
   * @author Ravi Nagayach
   */
  public NBBOAgentAdminConsumer find();
  /**
   * Creates an instance of the NBBOAgentAdminConsumer
   *
   * @return reference to NBBOAgentAdminConsumer
   *
   * @author Ravi Nagayach
   */
  public NBBOAgentAdminConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(NBBOAgentAdminConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(NBBOAgentAdminConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(NBBOAgentAdminConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
