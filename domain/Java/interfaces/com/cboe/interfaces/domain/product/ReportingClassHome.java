package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/ReportingClassHome.java

import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;

/**
 * A home for creating and finding reporting classes.
 *
 * @author John Wickberg
 */
public interface ReportingClassHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "ReportingClassHome";
/**
 * Creates new instance of a <code>ReportingClass</code> from the passed values.
 *
 * @param newClass CORBA struct containing new class values
 * @exception AlreadyExistsException if reporting class already exists
 * @exception DataValidationException if validation checks fail
 */
public ReportingClass create(ReportingClassStruct newClass) throws AlreadyExistsException, DataValidationException, SystemException;
/**
 * Searches for reporting class by key.
 *
 * @param key key of desired class
 * @return found reporting class
 * @exception NotFoundException if search fails
 */
public ReportingClass findByKey(int key) throws NotFoundException;
/**
 * Searches for reporting class by symbol and product type.
 *
 * @param classSymbol symbol of desired class
 * @param type product type of class
 * @return found reporting class
 * @exception NotFoundException if search fails
 */
public ReportingClass findBySymbol(String classSymbol, short type) throws NotFoundException;
/**
 * Searches for all reporting classes of the given product type.
 *
 * @param type product type for search
 * @param activeOnly if <code>true</code>, the result set will only contain active classes
 * @return array of found reporting classes
 */
public ReportingClass[] findByType(short type, boolean activeOnly);
/**
 * Purges old obsolete reporting classes and marks old inactive classes obsolete.
 *
 * @param retentionCutoff the time in millis to be used as the end of the retention period
 */
void purge(long retentionCutoff);
}
