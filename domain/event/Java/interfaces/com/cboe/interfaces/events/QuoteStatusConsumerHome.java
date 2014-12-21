package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;

/**
 * This is the common interface for the Market Query Home
 * @author Jeff Illian
 * @author Gijo Joseph
 * @version 4/19/2006
 */
public interface QuoteStatusConsumerHome
{
    /**
     * Name that will be used for this home.
     */
	public final static String HOME_NAME = "QuoteStatusConsumerHome";
	public final static String EXTERNAL_HOME_NAME = "ExternalQuoteStatusConsumerHome";
    public final static String CLIENT_CHANNEL_HOME_NAME = "QuoteStatusConsumerClientChannelHome";    

  /**
   * Returns a reference to the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public QuoteStatusConsumer find();
  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public QuoteStatusConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */

  /**
   * Returns a user specific QuoteStatusConsumer. This has been added for the client side 
   * to create separate callback consumers per each user.
   *
   * @param userId
   * @return QuoteStatusConsumer for the user specified
   *
   * @author Gijo Joseph
   */
  public QuoteStatusConsumer find(String userId);

  /**
   * Returns a user specific QuoteStatusConsumer. This has been added for the client side 
   * to create separate callback consumers per each user.
   *
   * @param userId
   * @return QuoteStatusConsumer for the user specified
   *
   * @author Gijo Joseph
   */
  public QuoteStatusConsumer create(String userId);


   public void addConsumer(QuoteStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(QuoteStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(QuoteStatusConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Have QSSS/QSS Acknowledge events.
   *
   * @param quoteAcknowledge struct of ack info.
   */
    public void ackQuoteStatus(QuoteAcknowledgeStruct quoteAcknowledge)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Have QSSS/QSS send any unAcknowledged events.
   *
   * @param userId of logged in user
   */
    public void resubscribeQuoteStatus(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void publishUnackedQuoteStatusByClass(String userId, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}

