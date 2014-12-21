package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for QuoteStatusConsumerExtHome
 * @author Ravi Nagayach
 */
public interface QuoteStatusConsumerExtHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "QuoteStatusConsumerExtHome";
  /**
   * Returns a reference to the QuoteStatusConsumerExt
   *
   * @return reference to QuoteStatusConsumerExt
   *
   * @author Ravi Nagayach
   */
  public QuoteStatusConsumerExt find();
  /**
   * Creates an instance of the QuoteStatusConsumerExt
   *
   * @return reference to QuoteStatusConsumerExt
   *
   * @author Ravi Nagayach
   */
  public QuoteStatusConsumerExt create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(QuoteStatusConsumerExt consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(QuoteStatusConsumerExt consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(QuoteStatusConsumerExt consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
