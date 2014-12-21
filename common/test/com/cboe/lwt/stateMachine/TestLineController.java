/*
 * DummyLineController.java
 *
 * Created on May 3, 2002, 11:42 AM
 */

package com.cboe.lwt.stateMachine;



/**
 *
 * @author  dotyl
 */
public class TestLineController implements Controller
{
    StateMachine stateMachine;
               
    
    /** Creates a new instance of DummyLineController */
    public TestLineController()
    {
        stateMachine = null;
    }

    
    public void setEventSink( StateMachine p_stateMachine )
    {
        stateMachine = p_stateMachine;
    }

}
