package com.cboe.interfaces.events;

import com.cboe.util.ChannelKey;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.order.OrderAcknowledgeStruct;

/**
 * @author Brian Mahoney
 * @version 11/07/2006
 */
public interface OrderStatusConsumerV2Home
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "OrderStatusConsumerHome";

  /**
   * Returns a reference to the market query service.
   *
   * @return reference to market query service
   */
  public OrderStatusConsumerV2 find();

  /**
   * Returns a user specific OrderStatusConsumer. This has been added for the client side
   * to create separate callback consumers per each user.
   *
   * @param userId
   * @return OrderStatusConsumer for the user specified
   */
  public OrderStatusConsumerV2 find(String userId);

  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market query service
   */
  public OrderStatusConsumerV2 create();

  /**
   * Returns a user specific OrderStatusConsumer. This has been added for the client side
   * to create separate callback consumers per each user.
   *
   * @param userId
   * @return OrderStatusConsumer for the user specified
   */
  public OrderStatusConsumerV2 create(String userId);

  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OrderStatusConsumerV2 consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OrderStatusConsumerV2 consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OrderStatusConsumerV2 consumer)
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
