package com.cboe.interfaces.events;

import com.cboe.util.ChannelKey;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

public interface OptionTickerConsumerHome {

/**
 * This is the common interface for the TickerConsumer Home
 * @author baranski
 */

	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "OptionTickerConsumerHome";
  /**
   * Returns a reference to the OptionTickerConsumer service.
   *
   * @return reference to OptionTickerConsumer service
   *
   * @author baranski
   */
  public OptionTickerConsumer find();
  /**
   * Creates an instance of the OptionTickerConsumer service.
   *
   * @return reference to OptionTickerConsumer service
   *
   * @author baranski
   */
  public OptionTickerConsumer create();
  /**
   * Registers consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void addConsumer(OptionTickerConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for events matching key.
   *
   * @param consumer implementation to receive events
   * @param key filtering key
   */
   public void removeConsumer(OptionTickerConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
  /**
   * Unregisters consumer as a listener to this channel for all events.
   *
   * @param consumer implementation to receive events
   */
   public void removeConsumer(OptionTickerConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
