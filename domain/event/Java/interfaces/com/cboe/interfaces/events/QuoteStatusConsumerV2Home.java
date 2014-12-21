/**
 * 
 */
package com.cboe.interfaces.events;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.util.ChannelKey;

/**
 * @author Gijo Joseph
 *
 */
public interface QuoteStatusConsumerV2Home {
    /**
     * Name that will be used for this home.
     */
	public final static String HOME_NAME = "QuoteStatusConsumerHome";
	public final static String EXTERNAL_HOME_NAME = "ExternalQuoteStatusConsumerHome";
  /**
   * Returns a reference to the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public QuoteStatusConsumerV2 find();
  /**
   * Creates an instance of the market query service.
   *
   * @return reference to market query service
   *
   * @author Jeff Illian
   */
  public QuoteStatusConsumerV2 create();
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
  public QuoteStatusConsumerV2 find(String userId);

  /**
   * Returns a user specific QuoteStatusConsumer. This has been added for the client side 
   * to create separate callback consumers per each user.
   *
   * @param userId
   * @return QuoteStatusConsumer for the user specified
   *
   * @author Gijo Joseph
   */
  public QuoteStatusConsumerV2 create(String userId);


   public void addConsumer(QuoteStatusConsumerV2 consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(QuoteStatusConsumerV2 consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(QuoteStatusConsumerV2 consumer)
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
