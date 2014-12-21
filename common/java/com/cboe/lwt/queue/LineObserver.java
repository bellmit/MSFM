/*
 * LineObserver.java
 *
 * Created on February 24, 2003, 10:00 AM
 */

package com.cboe.lwt.queue;

/**
 *
 * @author  dotyl
 */
public interface LineObserver
{
    void setEnabled();
    void setDisabled();
    
    boolean isEnabled();
}
