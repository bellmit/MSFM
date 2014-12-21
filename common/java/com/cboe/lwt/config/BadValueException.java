/*
 * Created on Jan 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.config;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BadValueException extends ConfigException
{
    public BadValueException()
    {
        super( "Bad value in Configuration access" );
    }
    

    public BadValueException( String p_msg )
    {
        super( p_msg );
    }
    
    
    public BadValueException( String p_msg, Throwable p_cause )
    {
        super( p_msg );
        initCause( p_cause );
    }

}
