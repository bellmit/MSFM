package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the OrderStatusAdminConsumerHome.
 *
 * @author Eric Fredericks
 * @date 08/31/2000
 */
public interface OrderStatusAdminConsumerHome
{
  /**
   * Name that will be used for this home.
   */
  public final static String HOME_NAME = "OrderStatusAdminConsumerHome";
  public final static String PUBLISHER_HOME_NAME = "OrderStatusAdminPublisherHome";

  /**
   * Returns a reference to the OrderStatusAdminConsumer service.
   *
   * @return reference to OrderStatusAdminConsumer service
   */
  public OrderStatusAdminConsumer find();

  /**
   * Creates an instance of the OrderStatusAdminConsumer service.
   *
   * @return reference to OrderStatusAdminConsumer service
   */
  public OrderStatusAdminConsumer create();

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderStatusAdminConsumer consumer, ChannelKey key)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderStatusAdminConsumer consumer, ChannelKey key)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderStatusAdminConsumer consumer)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
