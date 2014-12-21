package com.cboe.common.log;

/**
 * LWLFileHandler.java
 *
 *
 * Created: Mon Jun 16 15:13:57 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.logging.*;
import java.io.*;

public class LWLFileHandler extends FileHandler {

	private static final String PATTERN_PROPERTY = "com.cboe.common.log.LWLFileHandler.pattern";
	private static final String FILTER_PROPERTY = "com.cboe.common.log.LWLFileHandler.filter";
	private static String pattern = null;
	private static String filter = null;
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

	public LWLFileHandler() throws IOException {
		this( pattern, filter );
	} // LWLFileHandler constructor

	public LWLFileHandler( String overridePattern, String overrideFilter ) throws IOException {
		super( overridePattern );

		if ( overrideFilter != null && overrideFilter.length() > 0 ) {
			try {
				Class filterClass = Class.forName( overrideFilter );
				Filter filterObj = (Filter)filterClass.newInstance();
				setFilter( filterObj );
			} catch( Exception e ) {
				System.out.println("LWLFileHandler.init: unable to create filter object.  ClassName(" + overrideFilter + ").  " + e );
			}
		}
	}
	
} // LWLFileHandler
