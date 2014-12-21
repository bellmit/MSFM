/**
 * Factory to create and provide access to a singleton instance of an event
 * channel.
 * @author Jing Chen
 * @author Gijo Joseph
 */
package com.cboe.domain.instrumentedChannel.event;



public class InstrumentedEventChannelAdapterFactory
{
    private static class IECAdapterHolder
    {
    	private static InstrumentedEventChannelAdapter eventChannel = new InstrumentedEventChannelAdapter();
    }

    /**
     * InstrumentedEventChannelAdapterFactory constructor.
     */
    public InstrumentedEventChannelAdapterFactory()
    {
        super();
    }

    /**
     * Creates a new instance of an InstrumentedEventChannelAdapter if one doesn't already
     * exist otherwise returns the existing instance.
     */
    public static InstrumentedEventChannelAdapter create()
    {
    	return IECAdapterHolder.eventChannel;
    }

    /**
     * Returns the singleton instance of an InstrumentedEventChannelAdapter.
     */
    public static InstrumentedEventChannelAdapter find()
    {
        return create();
    }
}
