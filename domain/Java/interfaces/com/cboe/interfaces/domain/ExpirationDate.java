package com.cboe.interfaces.domain;

// Source file: com/cboe/domain/util/ExpirationDate.java

import com.cboe.util.FormatNotFoundException;
import com.cboe.idl.cmiUtil.DateStruct;
import java.util.Calendar;
import com.objectwave.persist.*;

/**
 * A wrapper around <code>Date</code>.  This wrapper provides an convenience method
 * for creating standard expiration dates and formats the date in a standard way.
 *
 * @author John Wickberg
 */
public interface ExpirationDate
{
/**
 * Gets expiration date as time in millis.
 *
 * @return expiration date as time in millis
 *
 * @author John Wickberg
 */
public long getDateInMillis();

/**
 * Creates formatted date string for this expiration date.
 *
 * @return date formatted as yyyyMMdd
 *
 * @author John Wickberg
 * @roseuid 362F45340267
 */
public String toString();

/**
 * Converts this expiration date to a CORBA date struct.
 *
 * @return struct representing date
 *
 * @author John Wickberg
 */
public DateStruct toStruct();

}