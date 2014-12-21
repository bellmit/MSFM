/*
 * Created on Mar 23, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cboe.lwt.transaction;

/**
 * persistence-related exception
 */
public class PersistException extends TransactException
{
    public PersistException( String p_msg )
    {
        super( p_msg );
    }
    
    
    public PersistException( String p_msg, Throwable p_cause )
    {
        super( p_msg, p_cause );
    }
}
