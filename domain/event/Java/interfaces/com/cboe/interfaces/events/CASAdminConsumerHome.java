package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the CASAdminConsumer Home
 * @author Keith A. Korecky
 */
public interface CASAdminConsumerHome
{
    /**
	 * Name that will be used for this home.
	 */
    public final static String HOME_NAME = "CASAdminConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "CASAdminPublisherHome";

  /**
   * Returns a reference to the CASAdminConsumer service.
   *
   * @return reference to CASAdminConsumer service
   *
   * @author Keith A. Korecky
   */
  public CASAdminConsumer find();

  /**
   * Creates an instance of the CASAdminConsumer service.
   *
   * @return reference to CASAdminConsumer service
   *
   * @author Keith A. Korecky
   */
  public CASAdminConsumer create();

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(CASAdminConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(CASAdminConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(CASAdminConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

