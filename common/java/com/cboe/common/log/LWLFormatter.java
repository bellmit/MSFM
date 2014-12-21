package com.cboe.common.log;

/**
 * LWLFormatter.java
 *
 *
 * Created: Mon May 12 10:47:58 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.logging.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.MessageFormat;

public class LWLFormatter
extends java.util.logging.Formatter
{
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("'<' EEE yyyy/MM/dd HH:mm:ss:SS '>' ");

	public LWLFormatter() {
	} // LWLFormatter constructor

	public String format( LogRecord logRec ) {
		String source = "";
		Object[] params = null;
		if ( logRec.getParameters() != null && logRec.getParameters().length > 0 ) {
			params = logRec.getParameters();
		}
		if ( logRec.getSourceClassName() != null && logRec.getSourceClassName().length() > 0 ) {
			try {
				if ( params != null ) {
					source = "<" + MessageFormat.format( logRec.getSourceClassName(), params );
				} else {
					source = "<" + logRec.getSourceClassName();
				}
			} catch( Throwable t ) {
				// Can't get anything via formatter.
				source = "<" + logRec.getSourceClassName();
			}
			if ( logRec.getSourceMethodName() != null && logRec.getSourceMethodName().length() > 0 ) {
				try {
					if ( params != null ) {
						source += "." + MessageFormat.format( logRec.getSourceMethodName(), params );
					} else {
						source += "." + logRec.getSourceMethodName();
					}
				} catch( Throwable t ) {
					source += "." + logRec.getSourceMethodName();
				}
			}
			source += "> ";
		}
		String threadName = "<" + Thread.currentThread() + "> ";

		String message = null;
		if ( params == null ) {
			// If there are no parameters, just use logRec.getMessage.
			message = logRec.getMessage();
		} else {
			// Attempt to do MessageFormat, but if it fails for some reason,
			// note the problem, but use logRec.getMessage.
			try {
				message = MessageFormat.format( logRec.getMessage(), params );
			} catch( Throwable t ) {
				message = "MessageFormat problem.  Using Message only, no params. " + logRec.getMessage();
			}
		}

		StringBuffer msg = new StringBuffer( "<" + logRec.getLevel().toString() +
									  "> <" + logRec.getLoggerName() + "> " +
									  getTimeStamp( logRec.getMillis() ) +
									  source + threadName +
									  message );
		if ( logRec.getThrown() != null ) {
			msg.append( "\n<" + logRec.getLevel().toString() + "> Stack Trace follows:\n" );
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			logRec.getThrown().printStackTrace( pw );
			msg.append( sw.getBuffer() );
		}
		return msg.toString() + "\n";
	}
	
	private String getTimeStamp( long millis ) {
		String timeStamp = null;
		Date currentDate = new Date(millis);
		timeStamp = dateFormatter.format(currentDate);
		return timeStamp;
	}

} // LWLFormatter
