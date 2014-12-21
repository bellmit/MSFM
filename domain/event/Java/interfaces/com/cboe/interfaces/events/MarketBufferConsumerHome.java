package com.cboe.interfaces.events;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
//import com.cboe.externalIntegrationServices.msgCodec.DataBufferBlock;

/**
 */
public interface MarketBufferConsumerHome extends BufferConsumerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "MarketBufferConsumerHome";
    
    
    /**
     * Returns a reference to the MarketBufferConsumer service.
     * 
     * @return reference to MarketBufferConsumer service
     * 
     */
    public MarketBufferConsumer find(int channelIndex);

    /**
     * Registers consumer as a listener to all channels (0 .. getNumChannels()-1) for events matching key.
     * 
     * @param consumer implementation to receive events
     * @param key filtering key
     * @throws DataValidationException
     * @throws SystemException
     */
    public void addUnfilteredConsumer(MarketBufferConsumer consumer) throws SystemException, DataValidationException;

    /**
     * Registers consumer as a listener to a specific channel for events matching key.
     * 
     * @param channelIdx The 0-based index of the channel. Ex, channel xxxMarketBuffer1 is index 0.
     */
    public void addUnfilteredConsumer(int channelIdx, MarketBufferConsumer consumer) throws SystemException,
            DataValidationException;

}
