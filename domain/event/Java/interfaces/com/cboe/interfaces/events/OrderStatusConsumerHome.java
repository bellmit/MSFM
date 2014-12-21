package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.order.OrderAcknowledgeStruct;

/**
 * This is the common interface for the Market Query Home
 * @author Steven Sinclair
 * @author Gijo Joseph
 * @version 4/18/2006
 */
public interface OrderStatusConsumerHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OrderStatusConsumerHome";
    public final static String EXTERNAL_HOME_NAME = "ExternalOrderStatusConsumerHome";
    public final static String LINKAGE_HOME_NAME = "LinkageOrderStatusConsumerHome";
    public final static String CLIENT_CHANNEL_HOME_NAME = "OrderStatusConsumerClientChannelHome";

  /**
   * Returns a reference to the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public OrderStatusConsumer find();
  
  /**
   * Returns a user specific OrderStatusConsumer. This has been added for the client side 
   * to create separate callback consumers per each user.
   *
   * @param userId
   * @return OrderStatusConsumer for the user specified
   *
   * @author Gijo Joseph
   */
  public OrderStatusConsumer find(String userId);

  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public OrderStatusConsumer create();

  /**
   * Returns a user specific OrderStatusConsumer. This has been added for the client side 
   * to create separate callback consumers per each user.
   *
   * @param userId
   * @return OrderStatusConsumer for the user specified
   *
   * @author Gijo Joseph
   */
  public OrderStatusConsumer create(String userId);

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderStatusConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Have OSSS/OSS Acknowledge events.
   *
   * @param orderAcknowledge struct of ack info.
   */
    public void ackOrderStatus(OrderAcknowledgeStruct orderAcknowledge)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Have OSSS/OSS send any unAcknowledged events.
   *
   * @param userId of logged in user
   */
   public void resubscribeOrderStatus(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void publishUnackedOrderStatusByClass(String userId, int classKey)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}


