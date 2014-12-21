package com.cboe.common.log;

/**
 * LWLLevel.java
 *
 *
 * Created: Mon May 12 09:14:05 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.logging.*;

class LWLLevel extends Level {
		public static LWLLevel STATS = new LWLLevel( "STATS", Level.INFO.intValue()+1 );

		private LWLLevel( String name, int value ) {
			super( name, value );
		}
	
} // LWLLevel
