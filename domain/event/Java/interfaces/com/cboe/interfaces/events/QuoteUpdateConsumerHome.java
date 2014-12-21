package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * @author Rajeev Jain
 */
public interface QuoteUpdateConsumerHome {
	public final static String HOME_NAME = "QuoteUpdateConsumerHome";
	
	public QuoteUpdateConsumer find();
	
	public QuoteUpdateConsumer create();
	
	public void addConsumer(QuoteUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
	
	public void removeConsumer(QuoteUpdateConsumer consumer, ChannelKey key)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
	
	public void removeConsumer(QuoteUpdateConsumer consumer)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}