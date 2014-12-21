/*
 * OpraLineStateMachineTest.java
 * JUnit based test
 *
 * Created on February 19, 2002, 11:27 AM
 */

package com.cboe.lwt.stateMachine;



import org.junit.Assert;

import com.cboe.lwt.eventLog.ConsoleLogger;
import com.cboe.lwt.testUtils.SystemUtils;


/**
 *
 * @author dotyl
 */
public class StateMachineTest
{
    StateMachine stateMachine = null;
    
    
    @org.junit.Test
    public void testSimpleStateMachine()
    {
        System.out.println("StateMachine: test a simple nested state machine");

        stateMachine = new StateMachine( "DummyStateMachine", new TestLineController() );
        stateMachine.start( new TestState1() );

        assertCurrentState( TestState1.typeName );

        stateMachine.event( new AEvent() );
        assertCurrentState( TestState2a.typeName );

        stateMachine.event( new AEvent() );
        assertCurrentState( TestState2b.typeName );

        stateMachine.event( new AEvent() );
        assertCurrentState( TestState2a.typeName );

        stateMachine.event( new BEvent() );
        assertCurrentState( TestState2a.typeName );

        stateMachine.event( new AEvent() );
        assertCurrentState( TestState2b.typeName );

        stateMachine.event( new BEvent() );
        assertCurrentState( TestState1.typeName );

        stateMachine.event( new BEvent() );
            
//        stateMachine.exit_EVENT();
    }
    
    
    
    private void assertCurrentState( String p_nameShouldBe )
    {
        for ( int i = 0; i < 5; ++i )
        {
            SystemUtils.pauseForOtherThreads();
            if ( p_nameShouldBe.equals( stateMachine.getCurrentState().getName() ) )   
            {
                break;
            }
        }
        Assert.assertEquals( p_nameShouldBe, stateMachine.getCurrentState().getName() );
    }
    
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
}
