package com.cboe.interfaces.events;

import com.cboe.externalIntegrationServices.msgCodec.DataBufferBlock;

public interface BufferConsumerHome 
{
    public int getNumChannels();

    /**
     * Return the 0-based channel index to use for the given integer.
     * 
     * @param p_hashValue (for instance, class key)
     * @return
     */
    public int getChannelIndexForHash(int p_hashValue);

    /**
     * Publish an event directly, first updating the sequence number in the block.
     * @param p_block
     * @param p_channelIdx
     */
    public void publishDataToChannel(DataBufferBlock p_block, int p_subIdentifier, int p_channelIdx);
}
