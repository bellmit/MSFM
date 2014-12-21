package com.cboe.infrastructureServices.eventService;

/**
 * Callback interface
 *
 * @see com.cboe.filterWatcher.FilterListener
 */
public interface FilterWatcherCallback
{
    /**
       Called to signal the setting of a filter
       @param contextData Place holder for correlation data.
       usually passed to the manager during callback registration
       @param keyValues all keyValues that have been set during this event
     */
    public void receiveSubscribe( Object contextData, String[] keyValues);

    /**
       Called to signal the removal of a filter
       @param contextData Place holder for correlation data.
       usually passed to the manager during callback registration
       @param keyValues all keyValues that have been set during this event
    */
    public void receiveUnsubscribe( Object contextData, String[] keyValues);

    /**
       Called to signal the setting of a filter
       @param contextData Place holder for correlation data.
       usually passed to the manager during callback registration
       @param constraintLHS the left hand side part of the filter constraint
       expression
       @param keyValues all keyValues that have been set during this event
     */
    public void receiveSubscribe( Object contextData, 
				  String constraintLHS,
				  String[] keyValues);

    /**
       Called to signal the removal of a filter
       @param contextData Place holder for correlation data.
       usually passed to the manager during callback registration
       @param constraintLHS the left hand side part of the filter constraint
       expression
       @param keyValues all keyValues that have been set during this event
    */
    public void receiveUnsubscribe( Object contextData, 
				    String constraintLHS,
				    String[] keyValues);
}
