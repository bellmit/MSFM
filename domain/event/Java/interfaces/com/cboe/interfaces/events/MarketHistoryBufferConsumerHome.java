package com.cboe.interfaces.events;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
//import com.cboe.externalIntegrationServices.msgCodec.DataBufferBlock;

/**
 */
public interface MarketHistoryBufferConsumerHome  extends BufferConsumerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "MarketHistoryBufferConsumerHome";
    
   
    /**
     * Returns a reference to the MarketHistoryBufferConsumer service.
     * 
     * @return reference to MarketHistoryBufferConsumer service
     * 
     */
    public MarketHistoryBufferConsumer find(int channelIndex);

    /**
     * Registers consumer as a listener to all channels (0 .. getNumChannels()-1) for events matching key.
     * 
     * @param consumer implementation to receive events
     * @param key filtering key
     * @throws DataValidationException
     * @throws SystemException
     */
    public void addUnfilteredConsumer(MarketHistoryBufferConsumer consumer) throws SystemException, DataValidationException;

    /**
     * Registers consumer as a listener to a specific channel for events matching key.
     * 
     * @param channelIdx The 0-based index of the channel. Ex, channel xxxMarketHistoryBuffer1 is index 0.
     */
    public void addUnfilteredConsumer(int channelIdx, MarketHistoryBufferConsumer consumer) throws SystemException,
            DataValidationException;

    
}

