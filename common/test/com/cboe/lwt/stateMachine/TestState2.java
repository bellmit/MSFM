/*
 * Created on Aug 1, 2003
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
public class TestState2 extends TestState
{
    public static final String typeName = "state2";

    public TestState2()
    {
        super( typeName );
    }

    /* (non-Javadoc)
     * @see com.cboe.lwt.stateMachine.TestEventSink#a_EVENT()
     */
    public void a_EVENT()
    {
       stateMachine.internalTransition( this, new TestState2a() );
    }

    /* (non-Javadoc)
     * @see com.cboe.lwt.stateMachine.TestEventSink#b_EVENT()
     */
    public void b_EVENT()
    {
        // external self transition
        stateMachine.transition( this, this );
    }

    /* (non-Javadoc)
     * @see com.cboe.lwt.stateMachine.State#exit_EVENT()
     */
    public void exit_EVENT()
    {
        stateMachine.transition( this, new TestState1() );
    }

    /* (non-Javadoc)
     * @see com.cboe.lwt.stateMachine.State#entry(com.cboe.lwt.stateMachine.StateMachine, com.cboe.lwt.stateMachine.State)
     */
    public void entry(StateMachine p_stateMachine, State p_superState)
    {
        super.entry(p_stateMachine, p_superState);
        
        stateMachine.nest( new TestState2a() );
    }

}
