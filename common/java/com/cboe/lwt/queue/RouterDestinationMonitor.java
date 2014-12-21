/*
 * RouterDestinationMonitor.java
 *
 * Created on February 24, 2003, 10:04 AM
 */

package com.cboe.lwt.queue;



/**
 *
 * @author  dotyl
 */
public class RouterDestinationMonitor implements LineObserver   
{
    HashRouter     router;
    InterThreadQueue monitoredResource;
    boolean          enabled;
    
    /** Creates a new instance of RouterDestinationMonitor */
    public RouterDestinationMonitor( HashRouter p_router, 
                                     InterThreadQueue p_monitoredResource )
    {
        router            = p_router;
        monitoredResource = p_monitoredResource;
        enabled           = false;
    }
    
    public boolean isEnabled()
    {
        return enabled;
    }    
    
    public void setDisabled()
    {
        enabled = false;
        router.disableDest( monitoredResource );
    }    
    
    public void setEnabled()
    {
        enabled = true;
        router.enableDest( monitoredResource );
    }
    
}
