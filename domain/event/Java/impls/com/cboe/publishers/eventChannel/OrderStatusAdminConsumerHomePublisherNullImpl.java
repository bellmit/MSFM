package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.idl.internalEvents.OrderStatusAdminEventConsumer;
import com.cboe.idl.internalEvents.OrderStatusAdminEventConsumerHelper;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.interfaces.events.OrderStatusAdminConsumer;
import com.cboe.interfaces.events.OrderStatusAdminConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.consumers.eventChannel.EventChannelFilterHelper;

/**
 * An implementation of the <code>OrderStatusAdminConsumerHome</code> for
 * creating OrderStatusAdmin publisher
 *  
 * @author Emily Huang
 */
public class OrderStatusAdminConsumerHomePublisherNullImpl extends BOHome
     implements OrderStatusAdminConsumerHome
{
    private OrderStatusAdminEventConsumer orderStatatusAdminEvent;
    private OrderStatusAdminConsumerPublisherImpl orderStatusAdminConsumer;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "OrderStatusAdmin";


  public OrderStatusAdminConsumer create()
  {
      return find();
  }

  /**
   * Return the OrderStatusAdmin Listener (If first time, create it and bind it to the orb).
   * @return OrderStatusAdminConsumer
   */
  public OrderStatusAdminConsumer find()
  {
      return orderStatusAdminConsumer;
  }

  public void start()
  {
      try
      {
          orderStatusAdminConsumer = new OrderStatusAdminConsumerPublisherImpl(null);
          orderStatusAdminConsumer.create(String.valueOf(orderStatusAdminConsumer.hashCode()));
          //Every bo object must be added to the container.
          addToContainer(orderStatusAdminConsumer);
      }
      catch (Exception e)
      {
          Log.exception(e);
      }
  }

  public void initialize()
  {
      FoundationFramework ff = FoundationFramework.getInstance();
      eventService = ff.getEventService();
      eventChannelFilterHelper = new EventChannelFilterHelper();
  }

  /**
   * Adds a  Filter to the internal event channel. Constraints based on the
   * ChannelKey will be added as well. Do not make call to addConstraints when this method has
   * already being called.
   *
   * @param channelKey the event channel key
   *
   * @author Connie Feng
   * @author Jeff Illian
   * @version 12/1/00
   */
  public void addFilter ( ChannelKey channelKey )
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      // NO IMPL NEEDED FOR THE PUBLISHER
  }

  /**
   * Removes the event channel Filter from the CBOE event channel.
   *
   * @param channelKey the event channel key
   *
   * @author Connie Feng
   * @author Jeff Illian
   * @version 12/1/00
   */
  public void removeFilter ( ChannelKey channelKey )
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
  {
      // NO IMPL NEEDED FOR THE PUBLISHER
  }

  public void addConsumer(OrderStatusAdminConsumer consumer, ChannelKey key) {}
  public void removeConsumer(OrderStatusAdminConsumer consumer, ChannelKey key) {}
  public void removeConsumer(OrderStatusAdminConsumer consumer) {}
  }// EOF
