/**
 * Factory to create and provide access to a singleton instance of an event
 * channel.
 *
 * @author Jeff Illian
 * @author Derek T. Chambers-Boucher
 * @version 03/17/1999
 */
package com.cboe.util.event;

public class EventChannelAdapterFactory
{
    private static class IECAdapterHolder
    {
    	private static EventChannelAdapter eventChannel = new EventChannelAdapter();
    }

    /**
     * EventChannelAdapterFactory constructor.
     */
    public EventChannelAdapterFactory()
    {
        super();
    }

    /**
     * Creates a new instance of an EventChannelAdapter if one doesn't already
     * exist otherwise returns the existing instance.
     *
     * @author Jeff Illian
     * @author Derek T. Chambers-Boucher
     *
     * @return the singleton EventChannelAdapter instance.
     */
    public static EventChannelAdapter create()
    {
    	return IECAdapterHolder.eventChannel;
    }

    /**
     * Returns the singleton instance of an EventChannelAdapter.
     *
     * @author Jeff Illian
     * @author Derek T. Chambers-Boucher
     *
     * @return the singleton EventChannelAdapter instance.
     */
    public static EventChannelAdapter find()
    {
        return create();
    }
}
