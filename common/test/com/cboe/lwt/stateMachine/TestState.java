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
public abstract class TestState extends State 
{
    public TestState( String p_typeName )
    {
        super( p_typeName );
    }

    /* (non-Javadoc)
     * @see com.cboe.lwt.stateMachine.TestEventSink#a_EVENT()
     */
    public void a_EVENT()
    {
        // allow any potential super state to handle the event
        if ( superState != null )
        {
            ( (TestState)superState ).a_EVENT();
        }
        else
        {
            // then this is the top-level state, and event is not handled
            throwStateError( "Unhandled <A> Event" );
        }
    }

    /* (non-Javadoc)
     * @see com.cboe.lwt.stateMachine.TestEventSink#b_EVENT()
     */
    public void b_EVENT()
    {
        // allow any potential super state to handle the event
        if ( superState != null )
        {
            ( (TestState)superState ).b_EVENT();
        }
        else
        {
            // then this is the top-level state, and event is not handled
            throwStateError( "Unhandled <B> Event" );
        }
    }

}
