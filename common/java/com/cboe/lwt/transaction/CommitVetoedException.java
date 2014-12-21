/*
 * Created on Mar 23, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cboe.lwt.transaction;

/**
 * informs the transaction that an observer has vetoed the transaction, forcing an abort
 */
public class CommitVetoedException extends TransactException
{
    public CommitVetoedException( String p_msg )
    {
        super( p_msg );
    }
    
    
    public CommitVetoedException( String p_msg, Throwable p_cause )
    {
        super( p_msg, p_cause );
    }
}
