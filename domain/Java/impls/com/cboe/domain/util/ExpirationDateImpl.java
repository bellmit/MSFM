package com.cboe.domain.util;

// Source file: com/cboe/domain/util/ExpirationDate.java

import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.util.FormatNotFoundException;
import com.cboe.idl.cmiUtil.DateStruct;
import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;
import java.util.Calendar;

/**
 * A wrapper around <code>Date</code>.  This wrapper provides an convenience method
 * for creating standard expiration dates and formats the date in a standard way.
 *
 * @author John Wickberg
 */
public class ExpirationDateImpl implements ExpirationDate, SqlScalarType
{
	/**
	 * Name of the expiration date format.
	 */
	public static final String EXPIRATION_FORMAT_NAME = "ExpirationFormat";
	/**
	 * Date format for expiration dates
	 */
	public static final String EXPIRATION_DATE_FORMAT = "yyyyMMdd";
	/**
	 * Expiration date
	 */
	private DateWrapper expireDate;
	/**
	 * Formatatted expiration date
	 */
	private String dateString;
	private int hashcode;

	static
	{
		// add format for expiration dates.
		DateWrapper.addDateFormatter(EXPIRATION_FORMAT_NAME, EXPIRATION_DATE_FORMAT);
		/*
		 * Register anonymous inner class as generator for type
		 */
		SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF()
		{
			/*
			 * Creates instance of date from string
			 */
			public SqlScalarType createInstance(String value)
			{
				return new ExpirationDateImpl(value);
			}
			/*
			 * Returns this class as generated type.
			 */
			public Class typeGenerated()
			{
				return ExpirationDate.class;
			}
		});
	}
/**
 * Creates a expiration date from a date wrapper.
 *
 * @param aDate expiration date
 *
 * @author John Wickberg
 */
protected ExpirationDateImpl(DateWrapper aDate)
{
	expireDate = aDate;
	hashcode = Math.abs((int) getDateInMillis());
}
/**
 * Creates a expiration date from a string.
 *
 * @param value expiration date in the standard format
 *
 * @author John Wickberg
 */
protected ExpirationDateImpl(String value)
{
	
	try
	{
		expireDate = DateWrapper.parse(EXPIRATION_FORMAT_NAME, value);
		dateString = value;
		hashcode = Math.abs((int) getDateInMillis());
	}
	catch (java.text.ParseException e)
	{
		throw new IllegalArgumentException("Invalid expiration date value: " + value);
	}
	catch (FormatNotFoundException e)
	{
		throw new IllegalArgumentException("Could not find expiration date format");
	}
}
/**
 * Compares two expiration dates.
 */
public boolean equals(Object otherDate) {
    if (otherDate instanceof ExpirationDate) {
        return getDateInMillis() == ((ExpirationDate) otherDate).getDateInMillis();
    }
    return false;
}
/**
 * Gets expiration date as time in millis.
 *
 * @return expiration date as time in millis
 *
 * @author John Wickberg
 */
public long getDateInMillis()
{
	return expireDate.getTimeInMillis();
}
/**
 * Computes hash code for expiration date.
 */
public int hashCode() {
    return hashcode;
}
/**
 * Changes the passed date to be the Saturday after the third Friday, the standard
 * expiration date.
 *
 * @param standardDate date to be standardized
 *
 * @author John Wickberg
 */
private static void standardizeDate(DateWrapper standardDate)
{
	Calendar calendar = standardDate.getCalendar();
	calendar.set(Calendar.DAY_OF_MONTH, 1);
	int expirationSaturday = 0;
	switch (calendar.get(Calendar.DAY_OF_WEEK))
	{
		case Calendar.FRIDAY :
			expirationSaturday = 16;
			break;
		case Calendar.THURSDAY :
			expirationSaturday = 17;
			break;
		case Calendar.WEDNESDAY :
			expirationSaturday = 18;
			break;
		case Calendar.TUESDAY :
			expirationSaturday = 19;
			break;
		case Calendar.MONDAY :
			expirationSaturday = 20;
			break;
		case Calendar.SUNDAY :
			expirationSaturday = 21;
			break;
		case Calendar.SATURDAY :
			expirationSaturday = 22;
			break;
	}
	// Reset time so that it is for correct day of month and has no time of day
	int expirationYear = calendar.get(Calendar.YEAR);
	int expirationMonth = calendar.get(Calendar.MONTH);
	calendar.clear();
	calendar.set(expirationYear, expirationMonth, expirationSaturday);
	standardDate.setDate(calendar);
}
/**
 * Returns string value for storing date in database.
 *
 * @return date as a standard string
 *
 * @author John Wickberg
 */
public String toDatabaseString()
{
	return toString();
}
/**
 * Creates formatted date string for this expiration date.
 *
 * @return date formatted as yyyyMMdd
 *
 * @author John Wickberg
 * @roseuid 362F45340267
 */
public String toString()
{
	if (dateString == null)
	{
		try
		{
			dateString = expireDate.format(EXPIRATION_FORMAT_NAME);
		}
		catch (FormatNotFoundException e)
		{
			// should not happen, but..
			throw new NullPointerException("Could not find expiration date format");
		}
	}
	return dateString;
}
/**
 * Converts this expiration date to a CORBA date struct.
 *
 * @return struct representing date
 *
 * @author John Wickberg
 */
public DateStruct toStruct()
{
	return expireDate.toDateStruct();
}
}
