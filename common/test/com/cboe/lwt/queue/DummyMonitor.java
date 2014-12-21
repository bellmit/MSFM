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
public class DummyMonitor implements LineObserver   
{
    boolean      enabled;
    
    /** Creates a new instance of RouterDestinationMonitor */
    public DummyMonitor()
    {
        enabled           = false;
    }
    
    public boolean isEnabled()
    {
        return enabled;
    }    
    
    public void setDisabled()
    {
        enabled = false;
    }    
    
    public void setEnabled()
    {
        enabled = true;
    }
    
}
