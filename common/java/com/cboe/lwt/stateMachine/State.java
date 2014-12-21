/*
 * State.java
 *
 * Created on January 30, 2002, 3:29 PM
 */

package com.cboe.lwt.stateMachine;




/**
 *
 * @author  dotyl
 *
 * Represents a state in the state machine.  
 *
 * The methods which end in "_EVENT" represent events that may occur on a 
 * given state, and return the new state (or current state if the event 
 * didn't cause a state transition).  All event methods' default 
 * implementation is to throw an UnexpectedEventError error, since this 
 * indicates that the state machine received an event while in a state where 
 * that event is illegal
 */
public abstract class State 
{
    protected StateMachine stateMachine;
    protected String       name;
    protected State        superState;  
    protected State        subState;
    
    
    /**
     * Constructor for nested states, meaning that this state is nested
     * within a Super-State.  All events not handled by this state are propagated
     * to the superstate for handling.
     *
     * @param p_stateMachine The state machine in which theis state exists
     * @param p_lineController The interface this state uses to effect the OpraLine
     * @param p_name The name of this state 
     * @param p_superState The state that encloses this one
     */
    public State( String p_name )
    { 
        assert ( p_name != null ) : "name is null";
        
        stateMachine   = null;  // will be set by state machine as part of the transition or nest
        superState     = null;  // will be set by state machine as part of the transition or nest
        subState       = null;  // will be set by state machine as part of nest
        name           = p_name;
    }
    
    
    /**
     * Represents this state's default response to the SEQUENCE RESPONSE TIMEOUT 
     * event (which is to not handle the event)
     *
     * if this method has not been overridden in the derived class, then 
     * this state's superstate is allowed to process the event.  If there is no
     * superstate, then an unexpected event error is thrown to inform the programmer
     * that there has been a coding error.  (all possible events must be handled)
     */
    public void timeout_EVENT()
    { 
        // allow any potential super state to handle the event
        if ( superState != null )
        {
            superState.timeout_EVENT();
        }
        else
        {
            // then this is the top-level state, and event is not handled
            throwStateError( "Unhandled Timeout Event" );
        }
    }
    
    
    /**
     * Represents this state's default response to the exit event (this event indicates
     * that the state machine should be shut down
     *
     * if this method has not been overridden in the derived class, then 
     * this state's superstate is allowed to process the event.  If there is no
     * superstate, then an unexpected event error is thrown to inform the programmer
     * that there has been a coding error.  (all possible events must be handled)
     */
    public void exit_EVENT()
    {
        // allow any potential super state to handle the event
        if ( superState != null )
        {
            superState.exit_EVENT();
        }
        else
        {
            // then this is the top-level state, and the entire state machine needs to exit
            stateMachine.exit();
        }
    }

    
    /**
     * Default implementation of the state's entry code, which is to do nothing
     */
    public void entry( StateMachine p_stateMachine, State p_superState )
    {
        stateMachine = p_stateMachine;
        superState   = p_superState;
    }
    
    
    /**
     * Default implementation of the state's exit code, which is to do nothing
     * in this state, but to clean up any substates gracefully, by calling their
     * exit code
     *
     */
    public void exit() 
    {
    }
    
    
    /** @return the name of the state
     */
    public String getName()
    {
        return name;
    }
    
    
    protected void throwStateError( String p_msg )
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( "=[STATE]= State Machine Error ----\n" )
          .append( p_msg )
          .append( "\n-------------------------------\n" )
          .append( "        |  Current State Stack" );
        
        State cur = findTopLevelState();
        while ( cur != null )
        {
            sb.append( "\n        |  " )
              .append( cur.name );
              
            cur = cur.subState;
        }
        
        sb.append( "\n-------------------------------\n" );
        
        throw new StateMachineError( sb.toString() );
    }
    
    
    protected State findMostNestedState()
    {
        State cur = this;
        State next = this;
        
        while ( next != null )
        {
            cur  = next;
            next = cur.subState; 
        }
        
        return cur;
    }
    
    
    protected State findTopLevelState()
    {
        State cur = this;
        State next = this;
        while ( next != null )
        {
            cur  = next;
            next = cur.superState; 
        }
        return cur;
    }
    
};

