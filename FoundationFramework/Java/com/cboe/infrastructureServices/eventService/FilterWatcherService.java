package com.cboe.infrastructureServices.eventService;

import com.cboe.exceptions.SystemException;

/**
 * Service for filte registration notifications
 *
 * @see com.cboe.filterWatcher.FilterListenManager
 * @author Steven Sinclair
 */
public interface FilterWatcherService
{
    /**
       Adds a callback object which will be invoked when a filter constraint
       is added for the first time or removed the last time.
       If the same callback object is added for the same Channel/Interface
       combination, then callback object is not added, but the methods
       returns successfully with the desired values.
       @param channel The EventChannel name for which to watch for filters.
       @param interfaceIDs The interfaceIDs on this channel which are of 
       interest for this callback object. This needs to have atleast one
       interface. If the interface id is a null string, the callback is
       invoked for all filters on the channel.
       @param callback The callback object on which methods will be invoked
       when filters are set for the specified channel and interfaceIDs
       @param contextData This piece of information is stored and passed back
       to the callback object every time it is invoked.
       @return Array of key values of filters that have already been set for
       the specified channel and interfaces. This return happens even if the
       callback object was a duplicate and was not added.
    */
    public String[] subscribeForFilters( 
                        String channel,
                        String[] interfaceIDs,
                        FilterWatcherCallback callback,
                        Object contextData) throws SystemException;
    /**
       Removes a callback object that was added using the subscribe method
       If a callback object for the same Channel/Interface was not 
       subscribed, then this method does nothing.
       @param channel The EventChannel name for which to watch for filters.
       @param interfaceIDs The interfaceIDs on this channel which are of 
       interest for this callback object. This needs to have atleast one
       interface. If the interface id is a null string, the callback is
       invoked for all filters on the channel.
       @param callback The callback object on which methods will be invoked
       when filters are set for the specified channel and interfaceIDs
    */
    public void unsubscribeForFilters( String channel,
                            String[] interfaceIDs,
                            FilterWatcherCallback callback) throws SystemException;
}
