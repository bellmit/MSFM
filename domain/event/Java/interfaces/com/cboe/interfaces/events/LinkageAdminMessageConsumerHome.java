package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;
/**
 * This is the common interface for this consumer Home
 */
public interface LinkageAdminMessageConsumerHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "LinkageAdminMessageConsumerHome";
/**
 * Registers consumer as a listener to this channel for events matching key.
 *
 * @param consumer implementation to receive events
 * @param key filtering key
 */
public void addConsumer(LinkageAdminMessageConsumer consumer, ChannelKey key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
public LinkageAdminMessageConsumer create();
public LinkageAdminMessageConsumer find();
public void removeConsumer(LinkageAdminMessageConsumer consumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
public void removeConsumer(LinkageAdminMessageConsumer consumer, ChannelKey key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
