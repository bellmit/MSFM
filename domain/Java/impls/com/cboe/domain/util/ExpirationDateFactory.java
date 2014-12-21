package com.cboe.domain.util;

// Source file: com/cboe/domain/util/ExpirationDate.java

import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.util.FormatNotFoundException;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;
import java.util.Calendar;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * A wrapper around <code>Date</code>.  This wrapper provides an convenience method
 * for creating standard expiration dates and formats the date in a standard way.
 *
 * @author John Wickberg
 */
public class ExpirationDateFactory
{
private static String DISABLE_FRIDAY_EXPIRATION_PROPERTY = "disableFridayExpiration";

public static int SATURDAY_EXPIRATION = 1;
public static int FRIDAY_EXPIRATION = 2;
public static int ANY_DAY_EXPIRATION = 3;
public static boolean disableFridayExpiration = false;

static {
    disableFridayExpiration = Boolean.valueOf(System.getProperty(DISABLE_FRIDAY_EXPIRATION_PROPERTY)).booleanValue();
    Log.information("ExpirationDateFactory >>> disableFridayExpiration : " + disableFridayExpiration);
}

/**
 * Creates an <code>ExpirationDate</code> for the next standard expiration.
 * If current date is the expiration date or is before expiration date, return
 * the current standard expiration.  Otherwise, return the next standard expiration date.
 *
 * @author John Wickberg
 * @roseuid 362F451A0274
 */
public static ExpirationDate createNearTermDate(int expirationStyle)
{
	DateWrapper currentDate = new DateWrapper();
    Calendar calendar = currentDate.getCalendar();
	// Reset time so that it is for correct day of month and has no time of day
	int currentYear = calendar.get(Calendar.YEAR);
	int currentMonth = calendar.get(Calendar.MONTH);
    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);    
	calendar.clear();
	calendar.set(currentYear, currentMonth, currentDay);
    currentDate.setDate(calendar);
    
	DateWrapper standardDate = new DateWrapper();
	standardizeDate(standardDate, expirationStyle);
    
    // only return the next month date if is before current date.
    if(standardDate.compareTo(currentDate) < 0 )
    {
		Calendar cal = standardDate.getCalendar();
		cal.add(Calendar.MONTH, 1);
		standardDate.setDate(cal);
		standardizeDate(standardDate, expirationStyle);
	}
	return new ExpirationDateImpl(standardDate);
}
/**
 * Creates an <code>ExpirationDate</code> for standard expirations.  The standard
 * expiration is the Saturday after the third Friday of the month.
 *
 * @param date a time within the expiration month
 *
 * @author John Wickberg
 * @roseuid 362F451A0274
 */
public static ExpirationDate createStandardDate(long date, int expirationStyle)
{
	DateWrapper standardDate = new DateWrapper(date);
    standardizeDate(standardDate, expirationStyle);
	return new ExpirationDateImpl(standardDate);
}
/**
 * Creates an <code>ExpirationDate</code> for standard expirations.  The standard
 * expiration is the Saturday after the third Friday of the month.
 *
 * @param date a date within the expiration month
 *
 * @author John Wickberg
 * @roseuid 362F451A0274
 */
public static ExpirationDate createStandardDate(DateStruct date, int expirationStyle)
{
	DateWrapper standardDate = new DateWrapper(date);
	standardizeDate(standardDate, expirationStyle);
	return new ExpirationDateImpl(standardDate);
}
/**
 * standardize the date based on the expiration style specified.
 */
private static void standardizeDate(DateWrapper aDate, int expirationStyle){
    if (expirationStyle == ANY_DAY_EXPIRATION ) {
        // don't standardize, use the same passed in date
        // no conversion applied based on expiration style.
    }
    else if ( !disableFridayExpiration && expirationStyle == FRIDAY_EXPIRATION ) {
        standardizeOnFriday(aDate);
    }
    else if (expirationStyle == SATURDAY_EXPIRATION ) {
        standardizeOnSaturday(aDate);
    }
    else {
        standardizeOnSaturday(aDate);
    }
}
/**
 * Changes the passed date to be the Saturday after the third Friday, the default standard
 * expiration date.
 *
 * @param standardDate date to be standardized
 *
 * @author John Wickberg
 */
private static void standardizeOnSaturday(DateWrapper standardDate)
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
 * Changes the passed date to be the third Friday.
 *
 * @param standardDate date to be standardized
 *
 */
private static void standardizeOnFriday(DateWrapper standardDate)
{
	Calendar calendar = standardDate.getCalendar();
	calendar.set(Calendar.DAY_OF_MONTH, 1);
	int expirationFriday = 0;
	switch (calendar.get(Calendar.DAY_OF_WEEK))
	{
		case Calendar.FRIDAY :
			expirationFriday = 15;
			break;
		case Calendar.THURSDAY :
			expirationFriday = 16;
			break;
		case Calendar.WEDNESDAY :
			expirationFriday = 17;
			break;
		case Calendar.TUESDAY :
			expirationFriday = 18;
			break;
		case Calendar.MONDAY :
			expirationFriday = 19;
			break;
		case Calendar.SUNDAY :
			expirationFriday = 20;
			break;
		case Calendar.SATURDAY :
			expirationFriday = 21;
			break;
	}
	// Reset time so that it is for correct day of month and has no time of day
	int expirationYear = calendar.get(Calendar.YEAR);
	int expirationMonth = calendar.get(Calendar.MONTH);
	calendar.clear();
	calendar.set(expirationYear, expirationMonth, expirationFriday);
	standardDate.setDate(calendar);
}
}
