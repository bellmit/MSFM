/*
 * Created on Mar 23, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cboe.lwt.transaction;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TransactException extends Exception
{
    public TransactException( String p_msg )
    {
        super( p_msg );
    }


    public TransactException( String p_msg, Throwable p_cause )
    {
        super( p_msg );
        initCause( p_cause );
    }
}
