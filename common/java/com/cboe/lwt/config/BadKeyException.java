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
public class BadKeyException extends ConfigException
{
    public BadKeyException()
    {
        super( "Bad key in Configuration access" );
    }
    

    public BadKeyException( String p_msg )
    {
        super( p_msg );
    }

}
