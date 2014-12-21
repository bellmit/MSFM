package com.cboe.common.utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * ApacheMatchUtil.java
 *
 *
 * Created: Fri Sep 12 14:33:22 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class ApacheMatchUtil {

	private ArrayList reList = new ArrayList();

	public ApacheMatchUtil( String[] matchList ) throws InvalidMatchList {
		if ( matchList != null && matchList.length > 0 ) {
			for( int i = 0; i < matchList.length; i++ ) {
				try {
					reList.add( new org.apache.regexp.RE( matchList[i] ) );
				} catch( Exception e ) {
					throw new InvalidMatchList( "MatchUtil: pattern(" + matchList[i] + ") invalid. " + e );
				}
			}
		}
	}

	public boolean matches( String s ) {
		if ( reList.size() == 0 ) {
			return true; // Empty match list, everything matches.
		}

		Iterator iter = reList.iterator();
		while( iter.hasNext() ) {
			org.apache.regexp.RE re = (org.apache.regexp.RE)iter.next();
			if ( re.match( s ) ) {
				return true;
			}
		}
		return false;
	}
}
