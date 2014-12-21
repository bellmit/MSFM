package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the AcceptTextMessageConsumer Home
 * @author Keith A. Korecky
 */
public interface TextMessageConsumerHome
{
    /**
	 * Name that will be used for this home.
	 */
    public final static String HOME_NAME = "TextMessageConsumerHome";

  /**
   * Returns a reference to the AcceptTextMessageConsumer service.
   *
   * @return reference to AcceptTextMessageConsumer service
   *
   * @author Keith A. Korecky
   */
  public TextMessageConsumer find();

  /**
   * Creates an instance of the AcceptTextMessageConsumer service.
   *
   * @return reference to AcceptTextMessageConsumer service
   *
   * @author Keith A. Korecky
   */
  public TextMessageConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(TextMessageConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(TextMessageConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(TextMessageConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

