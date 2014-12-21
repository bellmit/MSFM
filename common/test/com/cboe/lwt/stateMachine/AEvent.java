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
public class AEvent extends Event 
{
    public AEvent()
    {
    }
    
    public void setStateMachine( StateMachine p_stateMachine )
    {
        stateMachine = p_stateMachine;
    }
    
    public void execute()
    {
        TestState current = (TestState)stateMachine.getCurrentState();
        
        current.a_EVENT();   
    }

    public String getName()
    {
        return "AEvent";
    }
}
