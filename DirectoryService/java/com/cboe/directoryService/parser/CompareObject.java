package com.cboe.directoryService.parser;

import com.cboe.directoryService.persist.TraderOffer;
//import com.cboe.loggingService.Log;
import org.omg.CORBA.TCKind;

import com.cboe.common.log.InfraLoggingRb;
import com.cboe.common.log.Logger;

import java.util.*;

public class CompareObject
implements OpHandler, DirectoryServiceParserConstants
{
	public String name;
	public String value;
	public int oper;

	private static boolean isRegistered = false;
	private static ResourceBundle rb = null;

	public CompareObject() { 
		initializeLogging();  // chanaka
	}

	public CompareObject(String n, String v, int o)
	{
		name = n;
		value = v;
		oper = o;
		
		initializeLogging();  // chanaka
	}

	// new chanaka
	private  void initializeLogging() {
		try {
			rb = ResourceBundle.getBundle( InfraLoggingRb.class.getName() );
			
		} catch( Exception e ) {
			Logger.sysAlarm( Logger.createLogMessageId( Logger.getDefaultLoggerName(),
											    "Unable to set Logging ResourceBundle({0}).",
											    "TraderServer", "" ),
						  new Object[] {InfraLoggingRb.class.getName()} );
		}
	}

	public boolean performOp(TraderOffer anOffer, java.util.Stack theStack)
	{
		String[] propNames = anOffer.getPropertyNames();
		String[] propValues = anOffer.getPropertyValues();
		int[] propType = anOffer.getPropertyTypes();

		java.lang.Object[] params = { "CompareObject", "performOp",
								"TraderOffer", anOffer };		

		// find the matching property & test
		boolean retVal = false;
		for (int i=0; i<propNames.length; i++)
		{
			if ( propNames[i].equalsIgnoreCase(this.name) ) {
				if ( propValues != null ) {
					retVal = makeTest(propValues[i], this.value, this.oper, propType[i]);
				}
				//Log.trace(this, "performOp: " + toString() + " = " + retVal);  // chanaka

				if ( retVal ) break; // quit only if test is true
			}
		}

		return retVal;
	}

	/**
	* Perform the value comparison.
	* Comparison test is in the format "value1 oper value2"
	* @param value1 first value
	* @param value2 second value
	* @param oper operation
	* @param type typecode
	* @return true if test succeeded, false otherwise.
	*/
	public boolean makeTest(String value1, String value2, int oper, int type)
	{
		//Log.traceEntry(this, "makeTest");
		//Log.debug(this, "value1", value1);
		//Log.debug(this, "value2", value2);
		//Log.debug(this, "operation", oper);
		//Log.debug(this, "typecode", type);

		java.lang.Object[] params = { "CompareObject", "makeTest",
						      "value1", value1,
						      "value2", value2,
		                                         "operation", new Integer( oper ),
		                                         "typecode", new Integer( type )};	

		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_4, Logger.TRACE ) ) {
			Logger.traceEntry( rb,
						    InfraLoggingRb.METHOD_ENTRY_4,
						    params);
		}

		boolean retVal = false;
		// special case for the "EXIST" operator
		if ( oper == DirectoryServiceParserConstants.EXIST ) {
			retVal = true;
		}

		// special case for the "IN" operator
		// format: "propertyVal" "opVal1,opVal2,opVal3"
		// becomes: check if value1 is a substring of value2
		else if ( oper == DirectoryServiceParserConstants.IN ) {
			String tmp1 = value1.toLowerCase();
			String tmp2 = value2.toLowerCase();
			retVal = ( tmp2.indexOf(tmp1) > 0 );
		}

		// special case for the "TWIDDLE" operator
		// format: "property" ~ "substring"
		// becomes: check if value2 is a substring of value1
		else if ( oper == DirectoryServiceParserConstants.TWIDDLE ) {
			String tmp1 = value1.toLowerCase();
			String tmp2 = value2.toLowerCase();
			retVal = ( tmp1.indexOf(tmp2) > 0 );
		}

		// regular operators
		else {
			Comparable comp1 = makeComparable(value1, type);
			Comparable comp2 = makeComparable(value2, type);
			int answer = value1.compareTo(value2);
			switch (oper) {
				case DirectoryServiceParserConstants.NE:
					retVal = (answer != 0);
					break;
				case DirectoryServiceParserConstants.EQ:
					retVal = (answer == 0);
					break;
				case DirectoryServiceParserConstants.LT:
					retVal = (answer < 0);
					break;
				case DirectoryServiceParserConstants.LE:
					retVal = (answer <= 0);
					break;
				case DirectoryServiceParserConstants.GT:
					retVal = (answer > 0);
					break;
				case DirectoryServiceParserConstants.GE:
					retVal = (answer >= 0);
					break;
			}
		}

		//Log.traceExit(this, "makeTest");
		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			Logger.traceExit( rb, 
						   InfraLoggingRb.METHOD_EXIT,
						   params );
		}

		return retVal;
	}

	/**
	* Convert a String value into its corresponding Comparable Object type
	* based on the typecode.
	* @param value String value
	* @param type CORBA typecode
	* @return a Comparable Object
	*/
	public java.lang.Comparable makeComparable(String value, int type)
	{
		//Log.traceEntry(this, "makeComparable");
		
		java.lang.Object[] params = { "CompareObject", "makeComparable",
								"value", value,
								"typecode", new Integer( type ) };

		if (Logger.isLoggable( rb, InfraLoggingRb.METHOD_ENTRY_2, Logger.TRACE ) ) {
			Logger.traceEntry( rb,
						    InfraLoggingRb.METHOD_ENTRY_2,
						    params);
		}
		
		java.lang.Comparable retVal = null;
		try {
			switch (type) {
				case TCKind._tk_string:
				case TCKind._tk_char:
					retVal = value.toLowerCase();
					break;
				case TCKind._tk_long:
				case TCKind._tk_ulong:
				case TCKind._tk_short:
				case TCKind._tk_ushort:
					retVal = Integer.valueOf(value);
					break;
				case TCKind._tk_float:
					retVal = Float.valueOf(value);
					break;
				case TCKind._tk_double:
					retVal = Double.valueOf(value);
					break;
				case TCKind._tk_boolean:
					retVal = new ComparableBoolean(value);
					break;
				default:
					//Log.trace(this, "makeComparable: TCKind + " + type + " not Supported");
					Logger.sysWarn( rb,
								 InfraLoggingRb.DS_TS_PARSER_TYPE_NOT_SUPPORTED,
								 params );

					throw new RuntimeException("makeComparable: TCKind + " + type + " not Supported");
			}
		}
		catch(NumberFormatException nfe) {
			//Log.debugException(this, nfe);
			Logger.sysWarn( rb,
						 InfraLoggingRb.DS_TS_PARSER_NUMBER_FORMAT_EXCEPTION,
						 params,
						 nfe );

			throw new RuntimeException("makeComparable: NumberFormatException for " + value);
		}

		//Log.traceExit(this, "makeComparable");

		if (Logger.isLoggable ( rb, InfraLoggingRb.METHOD_EXIT, Logger.TRACE ) ) {
			Logger.traceExit( rb, 
						   InfraLoggingRb.METHOD_EXIT,
						   params );
		}
		
		return retVal;
	}

	public String toString()
	{
		return "Name=" + name + ", Value=" + value + ", oper=" + tokenImage[oper];
	}

	/**
	* java.lang.Boolean is final, so we can't subclass
	*/
	class ComparableBoolean
	implements Comparable
	{
		private boolean value;

		public ComparableBoolean()
		{ }

		public ComparableBoolean(boolean val)
		{
			value = val;
		}

		public ComparableBoolean(String val)
		{
			value = toBoolean(val);
		}

		public boolean equals(Object obj)
		{
			if (obj instanceof Boolean) {
				return value == ((Boolean)obj).booleanValue();
			} 
			return false;
		}

		public int compareTo(java.lang.Object obj)
		{
			return ( equals(obj) ? 1 : 0 );
		}

		public boolean toBoolean(String name)
		{ 
			return ((name != null) && name.equalsIgnoreCase("true"));
		}
	}
}
