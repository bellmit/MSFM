/*
 * TestEventSink.java
 *
 * Created on March 7, 2002, 4:16 PM
 */

package com.cboe.lwt.stateMachine;



/**
 *
 * @author  dotyl
 */
public class TestStateMachine extends StateMachine
{
    ////////////////////////////////////////////////////////////////////////////
    // nested class
    
    public class SimulatedStartState extends State
    {
        public SimulatedStartState()
        {
            super( "Simulated Start State" );
        }
    };
    
    // end nested class
    ////////////////////////////////////////////////////////////////////////////

    
    public TestStateMachine()
    {
        super( "Test", null );
        
        start( new SimulatedStartState() );
    }

    
    public synchronized void start( State p_startState )
    {
        logTransition( p_startState.getName() + " (START STATE)" );

        currentState = p_startState;

        currentState.entry( this, null );
        
        // eventServicer.go(); Commented out to keep the state machine from processing events
    }
    
    
    public Event popLastEvent() 
    {
        synchronized( eventQueue )
        {
            if( eventQueue.isEmpty() )
            {
                return null;   
            }

            if ( eventQueue.getCapacity() - eventQueue.available() < 2 )
            {
                eventQueue.notify();
            }
            
            return (Event)eventQueue.dequeue();
        }
    }
    
}
