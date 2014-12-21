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
public class TimeoutEvent extends Event 
{
    int transitionCountAtRequest;
    
    public TimeoutEvent( int p_transitionCountAtRequest )
    {
        transitionCountAtRequest = p_transitionCountAtRequest;
    }
    
    public void execute()
    {
        if ( transitionCountAtRequest != stateMachine.getTransitionCount() ) 
         {
             // then other transitions invalidated the timeout
             return;
         }

         stateMachine.getCurrentState().timeout_EVENT();   
    }

    public String getName()
    {
        return "Timeout";
    }
    
    public boolean equals( Object p_otherEvent )
    {
        boolean isEqual = super.equals( p_otherEvent );
        if ( isEqual )  // if it's another timeout event
        {
            // then make sure it came from the same context as this one
            TimeoutEvent otherEvent = (TimeoutEvent)p_otherEvent;
            isEqual = ( transitionCountAtRequest == otherEvent.transitionCountAtRequest );
        }
        
        return isEqual;
    }
    
}
