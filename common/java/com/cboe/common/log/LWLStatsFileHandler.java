package com.cboe.common.log;

/**
 * LWLStatsFileHandler.java
 *
 *
 * Created: Mon Jun 16 15:13:57 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.logging.*;
import java.io.*;

public class LWLStatsFileHandler extends LWLFileHandler {

	private static final String PATTERN_PROPERTY = "com.cboe.common.log.LWLStatsFileHandler.pattern";
	private static final String FILTER_PROPERTY = "com.cboe.common.log.LWLStatsFileHandler.filter";
	private static String pattern;
	private static String filter;
	static {
		pattern = System.getProperty( PATTERN_PROPERTY );
		if ( pattern == null || pattern.length() == 0 ) {
			pattern = LogManager.getLogManager().getProperty( PATTERN_PROPERTY );
			if ( pattern == null ) {
				pattern = "%h/java%u.log";
			}
		}
		filter = System.getProperty( FILTER_PROPERTY );
		if ( filter == null || filter.length() == 0 ) {
			filter = LogManager.getLogManager().getProperty( FILTER_PROPERTY );
		}
	}

	public LWLStatsFileHandler() throws IOException {
		super( pattern, filter );
	} // LWLStatsFileHandler constructor

} // LWLStatsFileHandler
