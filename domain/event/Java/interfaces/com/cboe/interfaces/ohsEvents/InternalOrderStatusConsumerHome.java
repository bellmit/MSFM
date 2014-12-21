package com.cboe.interfaces.ohsEvents;

import com.cboe.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.order.OrderAcknowledgeStruct;

/**
 * This is the common interface for IOSC
 */

public interface InternalOrderStatusConsumerHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "InternalOrderStatusConsumerHome";
	public final static String TRANSIENT_CHANNEL_HOME_NAME = "TransientInternalOrderStatusConsumerHome";
    public final static String EXTERNAL_HOME_NAME = "ExternalInternalOrderStatusConsumerHome";
    public final static String LINKAGE_HOME_NAME = "LinkageInternalOrderStatusConsumerHome";

   /**
   * Returns a reference to InternalOrderStatus Consumer.
   */
  public InternalOrderStatusConsumer find();

  /**
   * Creates an instance of InternalOrderStatus Consumer.
   */
  public InternalOrderStatusConsumer create();

   /**
   * Registers consumer as a listener to this channel for events matching key.
   */
   public void addConsumer(InternalOrderStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

   /**
   * Unregisters consumer as a listener to this channel for events matching key.
   */
   public void removeConsumer(InternalOrderStatusConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

   /**
   * Unregisters consumer as a listener to this channel for all events.
   */
   public void removeConsumer(InternalOrderStatusConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}


