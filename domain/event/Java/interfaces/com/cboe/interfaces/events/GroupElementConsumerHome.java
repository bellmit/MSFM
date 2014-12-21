package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the GroupElement Home
 * 
 */
public interface GroupElementConsumerHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "GroupElementConsumerHome";
  /**
   * Returns a reference to the GroupElementConsumer service.
   *
   * @return reference to GroupElementConsumer service
   * 
   */
  public GroupElementConsumer find();
  /**
   * Creates an instance of the GroupElementonsumer service.
   *
   * @return reference to GroupElementConsumer service
   *
   */
  public GroupElementConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(GroupElementConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(GroupElementConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(GroupElementConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}


