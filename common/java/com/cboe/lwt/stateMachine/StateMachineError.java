/*
 * Created on Jul 25, 2003
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
public class StateMachineError extends Error
{
    public StateMachineError( String p_msg )
    {
        super( p_msg );
    }
    
    
    public StateMachineError( String p_msg, Throwable p_cause )
    {
        super( p_msg, p_cause );
    }

}
