package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ReportingClassHomeImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiErrorCodes.AlreadyExistCodes;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.*;
import java.util.*;

/**
 * A implementation of <code>ReportingClassHome</code> that manages persistent reporting
 * classes using the JavaGrinder O-R mapping.
 *
 * @author John Wickberg
 */
public class ReportingClassHomeImpl extends BOHome implements ReportingClassHome
{
	/**
	 * Holds reference to most recently created class.  This is required in case
	 * a query is done for the class within the transaction when it was created.
	 */
	private ReportingClass mostRecentAdd;
/**
 * Creates an instance.
 */
public ReportingClassHomeImpl()
{
	super();
}
/**
 * Checks most recently added class against request.  Since a class added in the current transaction
 * is not in the database, it cannot be found with a query.
 *
 * @param symbol symbol of requested class
 * @param type product type of class
 * @return <code>true</code> if requested class matches most recent class
 */
private boolean checkMostRecentAdd(String symbol, short type)
{
	ReportingClass mostRecent = getMostRecentAdd();
	if (mostRecent == null)
	{
		return false;
	}
	return mostRecent.getProductType() == type && mostRecent.getSymbol().equals(symbol);
}
/**
 * Creates new reporting class.
 *
 * @see ReportingClassHome#create
 */
public ReportingClass create(ReportingClassStruct newClass) throws AlreadyExistsException, DataValidationException, SystemException
{
	if (queryBySymbol(newClass.reportingClassSymbol, newClass.productType) == null)
 	{
        ReportingClassImpl newInstance = new ReportingClassImpl();
        newInstance.create(newClass);
        addToContainer(newInstance);
        newInstance.initializeObjectIdentifier();
        setMostRecentAdd(newInstance);
        return newInstance;
    }
   	else
    {
        throw ExceptionBuilder.alreadyExistsException("Reporting class already exists: " + newClass.reportingClassSymbol, AlreadyExistCodes.REPORTING_CLASS_ALREADY_EXISTS);
    }
}
/**
 * @see ReportingClassHome#findByKey
 */
public ReportingClass findByKey(int key) throws NotFoundException
{
	ReportingClass result = null;
	ReportingClassImpl example = new ReportingClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		example.setObjectIdentifierFromInt(key);
		result = (ReportingClass) query.findUnique();
		return result;
	}
	catch (PersistenceException e)
	{
		throw ExceptionBuilder.notFoundException("Unable to locate reporting class for key = " + key, NotFoundCodes.RESOURCE_DOESNT_EXIST);
	}
}
/**
 * @see ReportingClassHome#findBySymbol
 */
public ReportingClass findBySymbol(String classSymbol, short type) throws NotFoundException
{
	ReportingClass result = queryBySymbol(classSymbol, type);
 	if (result == null)
	{
		throw ExceptionBuilder.notFoundException("No reporting class found with symbol = " + classSymbol + " and type = " + type, NotFoundCodes.RESOURCE_DOESNT_EXIST);
	}
 	return result;
}
/**
 * @see ReportingClassHome#findByType
 */
public ReportingClass[] findByType(short type, boolean activeOnly)
{
	ReportingClass[] result = null;
	ReportingClassImpl example = new ReportingClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		example.setProductType(type);
		if (activeOnly)
		{
			example.setListingState(ListingStates.ACTIVE);
		}
		Vector queryResult = query.find();
		result = new ReportingClass[queryResult.size()];
		queryResult.copyInto(result);
		return result;
	}
	catch (PersistenceException e)
	{
		return new ReportingClassImpl[0];
	}
}
/**
 * Gets most recently added reporting class.
 *
 * @return most recently added class
 */
private ReportingClass getMostRecentAdd()
{
	return mostRecentAdd;
}
/**
 * Initializes home values.
 */
public void goMaster(boolean failover)
{
}
/**
 * Completes initialization of the home.  Initialization is completed by querying the\
 * database for all product classes and then adding the results to the transient cache.
 */
private void loadFromDatabase()
{
	Log.information(this, "Starting to pre-load reporting classes from database");
	ReportingClassImpl example = new ReportingClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		java.util.Vector result = query.find();
	}
	catch (PersistenceException e)
	{
		Exception orig = e.getOriginalException();
		Log.alarm(this, "Unable to query for reporting classes: " + orig);
		Log.exception(this, orig);
	}
	Log.information(this, "Completed pre-loading of reporting classes");
}
/**
 * Purges old obsolete reporting classes and marks old inactive classes obsolete.
 *
 * @see ReportingClassHome#purge
 */
public void purge(long retentionCutoff)
{
	ReportingClassImpl example = new ReportingClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		query.setFieldConstraint(example, "lastModifiedTime", "" + retentionCutoff, "<=");
		// Unlisted records will also be returned, but will be skipped in processing
		query.setFieldConstraint(example, "listingState", "" + ListingStates.ACTIVE, "!=");
		Vector queryResult = query.find();
        Log.information(this, "Found " + queryResult.size() + " reporting classes for purge processing");
		ReportingClassImpl reportingClass;
		Enumeration resultsEnum = queryResult.elements();
		while (resultsEnum.hasMoreElements())
		{
			reportingClass = (ReportingClassImpl) resultsEnum.nextElement();
			if (reportingClass.getListingState() == ListingStates.OBSOLETE)
			{
				try
				{
					reportingClass.markForDelete();
				}
				catch (PersistenceException e)
				{
					Log.alarm(this, "Unable to delete reporting class = " + reportingClass.getClassKey());
				}
			}
			else if (reportingClass.getListingState() == ListingStates.INACTIVE)
			{
				reportingClass.setListingState(ListingStates.OBSOLETE);
			}
		}
	}
	catch (PersistenceException e)
	{
		Log.exception(this, "Query for purging of reporting classes failed", e);
	}

}
/**
 * Searches for reporting class by symbol.
 *
 * @param classSymbol reporting class symbol
 * @param type product type of class
 * @return found class or null
 */
public ReportingClass queryBySymbol(String classSymbol, short type)
{
	if (checkMostRecentAdd(classSymbol, type))
	{
		return getMostRecentAdd();
	}
	ReportingClass result = null;
	ReportingClassImpl example = new ReportingClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		example.setSymbol(classSymbol);
		example.setProductType(type);
		result = (ReportingClass) query.findUnique();
	}
	catch (PersistenceException e)
	{
		// ignore - null result will be returned
	}
	return result;
}
/**
 * Sets most recently added reporting class.
 *
 * @param addedClass most recently added class
 */
private void setMostRecentAdd(ReportingClass addedClass)
{
	mostRecentAdd = addedClass;
}
}
