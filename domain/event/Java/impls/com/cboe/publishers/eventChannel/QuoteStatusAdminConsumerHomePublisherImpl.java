package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.idl.internalEvents.QuoteStatusAdminEventConsumer;
import com.cboe.idl.internalEvents.QuoteStatusAdminEventConsumerHelper;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.interfaces.events.QuoteStatusAdminConsumer;
import com.cboe.interfaces.events.QuoteStatusAdminConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.consumers.eventChannel.EventChannelFilterHelper;

/**
 * An implementation of the <code>QuoteStatusAdminConsumerHome</code> for
 * creating QuoteStatusAdmin publisher
 *  
 * @author Emily Huang
 */
public class QuoteStatusAdminConsumerHomePublisherImpl extends BOHome
     implements QuoteStatusAdminConsumerHome
{
    private QuoteStatusAdminEventConsumer quoteStatatusAdminEvent;
    private QuoteStatusAdminConsumerPublisherImpl quoteStatusAdminConsumer;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "QuoteStatusAdmin";


  public QuoteStatusAdminConsumer create()
  {
      return find();
  }

  /**
   * Return the QuoteStatusAdmin Listener (If first time, create it and bind it to the orb).
   * @return QuoteStatusAdminConsumer
   */
  public QuoteStatusAdminConsumer find()
  {
      return quoteStatusAdminConsumer;
  }

  public void start()
  {
      try
      {
          String interfaceRepId = com.cboe.idl.internalEvents.QuoteStatusAdminEventConsumerHelper.id();

          org.omg.CORBA.Object obj;
          String eventChannelName = eventChannelFilterHelper.getChannelName(CHANNEL_NAME);
          String repID = QuoteStatusAdminEventConsumerHelper.id();
          obj = eventService.getEventChannelSupplierStub( eventChannelName, repID );
          quoteStatatusAdminEvent = QuoteStatusAdminEventConsumerHelper.narrow( obj );

          quoteStatusAdminConsumer = new QuoteStatusAdminConsumerPublisherImpl(quoteStatatusAdminEvent);
          quoteStatusAdminConsumer.create(String.valueOf(quoteStatusAdminConsumer.hashCode()));
          //Every bo object must be added to the container.
          addToContainer(quoteStatusAdminConsumer);
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

  public void addConsumer(QuoteStatusAdminConsumer consumer, ChannelKey key) {}
  public void removeConsumer(QuoteStatusAdminConsumer consumer, ChannelKey key) {}
  public void removeConsumer(QuoteStatusAdminConsumer consumer) {}
  }// EOF
