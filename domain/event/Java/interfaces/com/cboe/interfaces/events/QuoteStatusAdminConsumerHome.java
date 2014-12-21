package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the QuoteStatusAdminConsumerHome.
 *
 * @author Eric Fredericks
 * @date 08/31/2000
 */
public interface QuoteStatusAdminConsumerHome
{
  /**
   * Name that will be used for this home.
   */
  public final static String HOME_NAME = "QuoteStatusAdminConsumerHome";
  public final static String PUBLISEHER_HOME_NAME = "QuoteStatusAdminPublisherHome";

  /**
   * Returns a reference to the QuoteStatusAdminConsumer service.
   *
   * @return reference to QuoteStatusAdminConsumer service
   */
  public QuoteStatusAdminConsumer find();

  /**
   * Creates an instance of the QuoteStatusAdminConsumer service.
   *
   * @return reference to QuoteStatusAdminConsumer service
   */
  public QuoteStatusAdminConsumer create();

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(QuoteStatusAdminConsumer consumer, ChannelKey key)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(QuoteStatusAdminConsumer consumer, ChannelKey key)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(QuoteStatusAdminConsumer consumer)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
