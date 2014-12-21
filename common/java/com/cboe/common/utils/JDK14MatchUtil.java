package com.cboe.common.utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * JDK14MatchUtil.java
 *
 *
 * Created: Fri Sep 12 14:33:22 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class JDK14MatchUtil {

	private ArrayList reList = new ArrayList();

	public JDK14MatchUtil( String[] matchList ) throws InvalidMatchList {
		if ( matchList != null && matchList.length > 0 ) {
			for( int i = 0; i < matchList.length; i++ ) {
				try {
					reList.add( java.util.regex.Pattern.compile( matchList[i] ) );
				} catch( java.util.regex.PatternSyntaxException e ) {
					throw new InvalidMatchList( "MatchUtil: pattern(" + matchList[i] + ") invalid.",
										   e );
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
			java.util.regex.Pattern p = (java.util.regex.Pattern)iter.next();
			java.util.regex.Matcher m = p.matcher( s );
			if ( m.matches() ) {
				return true;
			}
		}
		return false;
	}
}
