/*
 * Created on Sep 26, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.stateMachine;


/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Event
{
    protected StateMachine stateMachine = null; 

    public void setStateMachine( StateMachine p_stateMachine )
    {
        stateMachine = p_stateMachine;
    }
    
    public boolean equals( Object p_otherEvent )
    {
        if ( ! ( p_otherEvent instanceof Event ) )
        {
            assert ( p_otherEvent != null ) : "Comparing Event to null";
            assert ( false ) : "Comparing Event to type : " + p_otherEvent.getClass().getName();
            
            return false;
        }
        
        Event other = (Event)p_otherEvent;
        return getName().equals( other.getName() );
    }
        
    public abstract String getName();
        
    public abstract void execute();  // command pattern
}
