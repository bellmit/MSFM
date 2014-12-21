/*
 * Created on Aug 24, 2004
 *
 */
package com.cboe.presentation.fix.quote;

import com.cboe.consumers.callback.LockedQuoteStatusV2ConsumerFactory;
import com.cboe.consumers.callback.QuoteStatusV2ConsumerFactory;
import com.cboe.consumers.callback.RFQConsumerFactory;
import com.cboe.idl.cmiCallback.CMIRFQConsumer;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * Factory to access a consumer for quotes
 * @author Don Mendelson
 *
 */
public class QuotePublisherFactory {

	// Singleton
	private static QuotePublisherFactory theInstance = null;

	/**
	 * Access a singleton QuotePublisherFactory
	 * @return the instance of the factory
	 */
	public static synchronized QuotePublisherFactory instance() {
		if (theInstance == null) {
			theInstance = new QuotePublisherFactory();
		}
		return theInstance;
	}
	
	// Currently only one consumer is returned
	// If design is ever changed to support multiple sessions, then may
	// need one consumer per session.
	
	private CMIQuoteStatusConsumer quoteStatusConsumer;
	private CMIRFQConsumer rfqConsumer;
	private CMILockedQuoteStatusConsumer lockedQuoteStatusConsumer;

	// No public constructor
	protected QuotePublisherFactory() {
		initializeConsumers();
	}
    
	/**
	 * Returns a quote status consumer
	 * @return the quote consumer
	 */
    public CMIQuoteStatusConsumer getQuoteStatusConsumer() {
    	return quoteStatusConsumer;
    }
	
	/**
	 * Returns a quote request consumer
	 * @return the quote request consumer
	 */
    public CMIRFQConsumer getRFQConsumer() {
    	return rfqConsumer;
    }
    
	/**
	 * Returns a quote locked status consumer
	 * @return the quote locked status consumer
	 */
    public CMILockedQuoteStatusConsumer getQuoteLockedStatusConsumer() {
    	return lockedQuoteStatusConsumer;
    }
    
    protected void initializeConsumers(){
    	EventChannelAdapter eventChannel = EventChannelAdapterFactory.find();
        eventChannel.setDynamicChannels(true);
        quoteStatusConsumer = QuoteStatusV2ConsumerFactory.create(eventChannel);
        rfqConsumer = RFQConsumerFactory.create(eventChannel);
        lockedQuoteStatusConsumer = LockedQuoteStatusV2ConsumerFactory.create(eventChannel);
    }
}
