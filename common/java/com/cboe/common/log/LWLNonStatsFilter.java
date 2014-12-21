package com.cboe.common.log;

/**
 * LWLNonStatsFilter.java
 *
 *
 * Created: Mon Jun 16 15:35:11 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.logging.*;

public class LWLNonStatsFilter implements Filter {

	public LWLNonStatsFilter() {
	} // LWLNonStatsFilter constructor

	public boolean isLoggable( LogRecord lr ) {
		return lr.getLevel() != LWLLevel.STATS;
	}

} // LWLNonStatsFilter
