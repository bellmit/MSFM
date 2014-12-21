package com.cboe.interfaces.events;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.util.ChannelKey;

/**
 * This is the TradeNotification Interface used ETN Processing. 
 * @author Anil Kalra
 * @version 10/31/2007
 */
public interface TradeNotificationAdminConsumerHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "TradeNotificationAdminConsumerHome";

    /**
   * Returns a reference to the TradeNotificationAdmin publisher.
   *
   * @return reference to market query service
   *
   */
  public TradeNotificationAdminConsumer find();
  

  /**
   * Creates an instance of the TradeNoticationAdmin.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public TradeNotificationAdminConsumer create();


  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(TradeNotificationAdminConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
   
   
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(TradeNotificationAdminConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
   
   
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(TradeNotificationAdminConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}


