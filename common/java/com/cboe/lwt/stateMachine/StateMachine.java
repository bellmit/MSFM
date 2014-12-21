/**
 * StateMachine.java
 *
 * Created on January 30, 2002, 3:10 PM
 */

package com.cboe.lwt.stateMachine;

import com.cboe.lwt.queue.CircularQueue;
import com.cboe.lwt.thread.ThreadTask;
import com.cboe.lwt.eventLog.Logger;


/**
 * Abstract class with core state machine functionality.  Basically the ability
 * to request and receive timeout events, and register a state as the new current state
 *
 * @author  dotyl
 */
public class StateMachine
{
    ////////////////////////////////////////////////////////////////////////////
    // Inner Class

    private class TimeoutWorker extends ThreadTask
    {
        private long timeout_MS;
        private int transitionCountAtRequest;

        TimeoutWorker( String p_name, long p_timeout_MS )
        {
            super( "Timeout Thread for " + p_name );
            timeout_MS = p_timeout_MS;
            transitionCountAtRequest = getTransitionCount();
            go();
        }

        public void doTask() throws InterruptedException
        {
            try
            {
                Thread.sleep( timeout_MS );
                event( new TimeoutEvent( transitionCountAtRequest ) );
            }
            finally
            {
                setComplete();
            }
        }
    };


    // END Inner Class
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    // Inner Class

    protected static class EventServicer extends ThreadTask
    {
        CircularQueue eventSource;
        StateMachine  stateMachine;

        EventServicer( String        p_name,
                       StateMachine  p_stateMachine,
                       CircularQueue p_eventSource )
        {
            super( p_name + ".StateMachine" );

            stateMachine = p_stateMachine;
            eventSource  = p_eventSource;
        }

        public void doTask()
            throws InterruptedException
        {
            Event event = null;

            synchronized( eventSource )
            {
                while( eventSource.isEmpty() )
                {
                    eventSource.wait();
                }

                eventSource.notify();

                event = (Event)eventSource.dequeue(); 
            }

            Logger.trace( "=[Event]= Processing event [" + event.getName() + "] in state " + stateMachine.getCurrentState().getName() );

            try
            {
                event.execute();
            }
            catch ( StateMachineError ex )
            {
                Logger.error( "Received State Machine Error", ex );
            }
        }
    };

    // END Inner Class
    ////////////////////////////////////////////////////////////////////////////

    protected String name;
    protected EventServicer eventServicer;


    protected State  currentState    = null;
    protected int    transitionCount = 0;

    protected CircularQueue eventQueue;

    protected Controller controller;


    public StateMachine( String     p_name,
                         Controller p_controller )
    {
        name = p_name;
        controller = p_controller;
        eventQueue = new CircularQueue( 256 );
        eventServicer = new EventServicer( p_name, this, eventQueue );
    }


    public void requestTimeout( long p_wait_MS )
    {
        new TimeoutWorker( name, p_wait_MS );
    }


    public String getName()
    {
        return name;
    }


    public int getTransitionCount()
    {
        synchronized ( this )
        {
            return transitionCount;
        }
    }


    /* transitions from the current state to the new state at the same level of nesting
     */
    public void transition( State p_fromState, State p_toState )
    {
        assert ( p_fromState != null ) : name + " : transition from state is null";
        assert ( p_toState != null ) : name + " : transition to state is null";

        logTransition( p_toState.getName() );

        doTransition( p_fromState,
                      p_toState,
                      p_fromState.superState );
    }


    public void internalTransition( State p_superState, State p_newSubState )
    {
        logTransition( p_newSubState.getName() + " (INTERNAL)" );

        assert ( p_superState.subState != null ) : "Can't perform an internal transition without an internally active state";

        doTransition( p_superState.subState,
                      p_newSubState,
                      p_superState );
    }


    /* starts the new state as a substate of the current state
     */
    public void nest( State p_nestedState )
    {
        State newParent;
        synchronized ( this )
        {
            newParent = currentState;

            logTransition( p_nestedState.getName() + " (NEST)" );

            // lind currentState to its new substate  (nestedState)

            newParent.subState = p_nestedState;

            currentState = p_nestedState;
        }

        // transition to the new subState
        p_nestedState.entry( this, newParent );
    }


    /* starts the new state machine with the specified state as its start state
     */
    public synchronized void start( State p_startState )
    {
        logTransition( p_startState.getName() + " (START STATE)" );

        currentState = p_startState;

        currentState.entry( this, null );
        
        eventServicer.go();
    }


    /* package private:
     * used by the new curren't state's constructor to set
     * itself current prior to invoking its entry code
     */
    public synchronized State getCurrentState()
    {
        return currentState;
    }


    /**
     * delegates the event to the current state for processing
     */
    public void event( Event p_event )
    {
        p_event.setStateMachine( this );

        try
        {
            synchronized( eventQueue )
            {
                while ( eventQueue.isFull() )
                {
                    eventQueue.wait();
                }

                CircularQueue inFlightEvents = eventQueue.shallowCopy();

                Event inFlightEvent = null;

                while ( ! inFlightEvents.isEmpty() )
                {
                    inFlightEvent = (Event)inFlightEvents.dequeue();

                    if ( p_event.equals( inFlightEvent ) ) // remove duplicates of last one still in flight
                    {
                        Logger.trace( "=[Event]= Filtered duplicate Event [" + p_event.getName() + "] in state " + currentState.getName() );
                        return;
                    }
                }

                Logger.trace( "=[Event]= deposited [" + p_event.getName() + "] in state " + currentState.getName() );
                eventQueue.enqueue( p_event );
                eventQueue.notify();
            }
        }
        catch ( InterruptedException ex )
        {
            Logger.error( "Event enqueue interrupted: Event dropped", ex );
            Thread.currentThread().interrupt();  // propogate the interruption
        }
    }



    /**
     * delegates the event to the current state for processing, then
     * Shuts down the state machine gracefully, freeing all resources
     *
     * NOTE: this method needs to be called by all subclasses to insure correct
     * cleanup
     */
    public void exit()
    {
        Logger.info( "Shutting down State machine " + name );

        synchronized ( this )
        {
            eventServicer.signalKill();
    
            currentState = null;
        }
        name += " - SHUT DOWN";
    }


    private void doTransition( State p_fromState, State p_toState, State p_superState )
    {
        synchronized ( this )
        {
            if ( currentState != p_toState )
            {
                ++transitionCount;
            }
            
            // exit all substates of the fromState
            State temp = p_fromState.findMostNestedState();
            while ( temp != p_superState && temp != null )
            {
                temp.exit();
                temp = temp.superState;
            }
    
            // set toState as current
            currentState = p_toState;
    
            if ( p_superState != null )  // if superstate exists
            {
                p_superState.subState = p_toState;  // make toState it's new substate
            }
        }
        
        // enter new current state
        p_toState.entry( this, p_superState );
    }


    StringBuffer logEventBuf = new StringBuffer( 100 );

    protected void logEvent( String p_event )
    {
        if ( Logger.getGlobal().getSeverityFilter() <= Logger.SEV_TRACE )
        {
            appendStateInfo( p_event, logEventBuf );

            Logger.trace( logEventBuf.toString() );
        }
    }


    StringBuffer logTransitionBuf = new StringBuffer( 100 );

    protected void logTransition( String p_toState )
    {
        if ( Logger.getGlobal().getSeverityFilter() <= Logger.SEV_INFO )
        {
            appendStateInfo( "TRANSITION to " + p_toState, logTransitionBuf );

            Logger.info( logTransitionBuf.toString() );
        }
    }


    StringBuffer logErrorBuf = new StringBuffer( 100 );

    protected void logError( String p_info )
    {
        appendStateInfo( p_info, logErrorBuf );

        Logger.error( logErrorBuf.toString() );
    }


    private synchronized void appendStateInfo( String p_info, StringBuffer p_buf )
    {
        p_buf.setLength( 0 );

        p_buf.append( "=[STATE]= [" )
             .append( name )
             .append( "] While in State : <" )
             .append( ( currentState == null )
                      ? "START STATE"
                      : currentState.getName() )
             .append( "> : " )
             .append( p_info );
    }

};
