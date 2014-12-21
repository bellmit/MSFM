package com.cboe.common.utils;

import com.cboe.common.log.Logger;

/**
 * MatchUtil.java
 *
 *
 * Created: Fri Sep 12 14:33:22 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class MatchUtil {

	private JDK14MatchUtil jdk14MatchUtil = null;
	private ApacheMatchUtil apacheMatchUtil = null;

	public MatchUtil( String[] matchList ) throws InvalidMatchList {
		try {
			jdk14MatchUtil = new JDK14MatchUtil( matchList );
			Logger.debug( "MatchUtil: using JDK1.4 regexp." );
		} catch( InvalidMatchList e ) {
			throw e;
		} catch( Throwable t ) {
			apacheMatchUtil = new ApacheMatchUtil( matchList );
			Logger.sysNotify( "MatchUtil: JDK1.4 not available, using apache regexp." );
		}
	}

	public boolean matches( String s ) {
		if ( jdk14MatchUtil != null ) {
			return jdk14MatchUtil.matches( s );
		} 
        
        return apacheMatchUtil.matches( s );
    }

} // MatchUtil

