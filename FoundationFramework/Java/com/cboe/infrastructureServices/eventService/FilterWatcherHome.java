package com.cboe.infrastructureServices.eventService;

/**
 * Find a FilterWatcherService
 * 
 * @author Steven Sinclair
 */
public interface FilterWatcherHome
{
    public static final String HOME_NAME = "FilterWatcherHome";

    public FilterWatcherService find();
}
