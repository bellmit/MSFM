package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ProductClassHomeImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;
import com.cboe.idl.cmiErrorCodes.AlreadyExistCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.util.*;
import com.cboe.util.ExceptionBuilder;
import java.util.*;

/**
 * An implementation of <code>ProductClassHome</code> that manages product classes
 * using JavaGrinder persistence mapping.
 *
 * @author John Wickberg
 */
public class ProductClassHomeImpl extends BOHome implements ProductClassHome
{
	/**
	 * Holds reference to most recently created class.  This is required in case
	 * a query is done for the class within the transaction when it was created.
	 */
	private ProductClass mostRecentAdd;
/**
 * Creates an instance.
 */
public ProductClassHomeImpl()
{
	super();
}
/**
 * Checks most recently added class against request.  Since a class added in the current transaction
 * is not in the database, it cannot be found with a query.
 *
 * @param symbol symbol of requested class
 * @param type product type of class
 *
 * @return <code>true</code> if requested class matches most recent class
 */
private boolean checkMostRecentAdd(String symbol, short type)
{
	ProductClass mostRecent = getMostRecentAdd();
	if (mostRecent == null)
	{
		return false;
	}
	return mostRecent.getProductType() == type && mostRecent.getSymbol().equals(symbol);
}
/**
 * @see ProductClassHome#create
 */
public ProductClass create(ClassDefinitionStruct newClass) throws AlreadyExistsException, DataValidationException,
    SystemException
{
	if (queryBySymbol(newClass.classSymbol, newClass.productType) == null)
 	{
		ProductClassImpl newInstance = new ProductClassImpl();
		addToContainer(newInstance);
		newInstance.create(newClass);
        newInstance.initializeObjectIdentifier();
		setMostRecentAdd(newInstance);
		return newInstance;
  	}
   	else
    {
        throw ExceptionBuilder.alreadyExistsException("Product class already exists", AlreadyExistCodes.PRODUCT_CLASS_ALREADY_EXISTS);
    }
}
/**
 * @see ProductClassHome#findAll
 */
public ProductClass[] findAll(boolean activeOnly)
{
	ProductClass[] result = null;
	ProductClassImpl example = new ProductClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		if (activeOnly)
		{
			example.setListingState(ListingStates.ACTIVE);
		}
		Vector queryResult = query.find();
		result = new ProductClass[queryResult.size()];
		queryResult.copyInto(result);
		return result;
	}
	catch (PersistenceException e)
	{
		return new ProductClassImpl[0];
	}
}
/**
 * Find by product description.
 *
 * @see ProductClassHome#findByDescription
 */
public ProductClass[] findByDescription(ProductDescription description, boolean activeOnly) {
	ProductClass[] result = null;
	ProductClassImpl example = new ProductClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try	{
		example.setProductDescription(description);
		if (activeOnly)
		{
			example.setListingState(ListingStates.ACTIVE);
		}
		Vector queryResult = query.find();
		result = new ProductClass[queryResult.size()];
		queryResult.copyInto(result);
	}
	catch (PersistenceException e) {
		Log.exception(this, "Unable to query product classes by description", e);
		result = new ProductClass[0];
	}
	return result;
}

/**
 * @see ProductClassHome#findByKey
 */
public ProductClass findByKey(int key) throws NotFoundException
{
	ProductClass result = null;
	ProductClassImpl example = new ProductClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		example.setObjectIdentifierFromInt(key);
		result = (ProductClass) query.findUnique();
		return result;
	}
	catch (PersistenceException e)
	{
		throw ExceptionBuilder.notFoundException("Unable to locate product class for key = " + key, NotFoundCodes.RESOURCE_DOESNT_EXIST);
	}
}
/**
 * @see ProductClassHome#findBySymbol
 */
public ProductClass findBySymbol(String classSymbol, short type) throws NotFoundException
{
	if (checkMostRecentAdd(classSymbol, type))
	{
		return getMostRecentAdd();
	}
	ProductClass result = queryBySymbol(classSymbol, type);
 	if (result == null)
	{
		throw ExceptionBuilder.notFoundException("No product class found with symbol = " + classSymbol + " and type = " + type, AlreadyExistCodes.PRODUCT_CLASS_ALREADY_EXISTS);
	}
 	return result;
}
/**
 * @see ProductClassHome#findByType
 */
public ProductClass[] findByType(short type, boolean activeOnly)
{
	ProductClass[] result = null;
	ProductClassImpl example = new ProductClassImpl();
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
		result = new ProductClass[queryResult.size()];
		queryResult.copyInto(result);
		return result;
	}
	catch (PersistenceException e)
	{
		return new ProductClassImpl[0];
	}
}
/**
 * Searches for product classes with the requested underlying.
 *
 * @see ProductClassHome#findByUnderlying
 */
public ProductClass[] findByUnderlying(Product underlyingProduct, boolean activeOnly)
{
	ProductClass[] result = null;
	ProductClassImpl example = new ProductClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		example.setUnderlyingProduct(underlyingProduct);
		if (activeOnly)
		{
			example.setListingState(ListingStates.ACTIVE);
		}
		Vector queryResult = query.find();
		result = new ProductClassImpl[queryResult.size()];
		queryResult.copyInto(result);
		return result;
	}
	catch (PersistenceException e)
	{
		return new ProductClassImpl[0];
	}
}
/**
 * Gets most recently added product class.
 *
 * @return most recently added class
 */
private ProductClass getMostRecentAdd()
{
	return mostRecentAdd;
}
/**
 * Initializes home.
 */
public void goMaster(boolean failover)
{
}
/**
 * Completes initialization of the home.  Purpose of query is to populate <code>ObjectPool</code>
 * of persistence layer.
 */
private void loadFromDatabase()
{
	Log.information(this, "Pre-loading product classes from database");
	ProductClassImpl example = new ProductClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		java.util.Vector result = query.find();
	}
	catch (PersistenceException e)
	{
		Exception orig = e.getOriginalException();
		Log.alarm(this, "Unable to query for product classes: " + orig);
		Log.exception(this, orig);
	}
	Log.information(this, "Completed pre-loading of product classes");
}
/**
 * Purges old obsolete product classes and marks old inactive classes obsolete.
 *
 * @see ProductClassHome#purge
 */
public void purge(long retentionCutoff)
{
	ProductClassImpl example = new ProductClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		query.setFieldConstraint(example, "lastModifiedTime", "" + retentionCutoff, "<=");
		// Unlisted records will also be returned, but will be skipped in processing
		query.setFieldConstraint(example, "listingState", "" + ListingStates.ACTIVE, "!=");
		Vector queryResult = query.find();
        Log.information(this, "Found " + queryResult.size() + " product classes for purge processing");
		ProductClassImpl productClass;
		Enumeration resultsEnum = queryResult.elements();
		while (resultsEnum.hasMoreElements())
		{
			productClass = (ProductClassImpl) resultsEnum.nextElement();
			if (productClass.getListingState() == ListingStates.OBSOLETE)
			{
				try
				{
					productClass.markForDelete();
				}
				catch (PersistenceException e)
				{
					Log.alarm(this, "Unable to delete product class = " + productClass.getClassKey());
				}
			}
			else if (productClass.getListingState() == ListingStates.INACTIVE)
			{
				productClass.setListingState(ListingStates.OBSOLETE);
			}
		}
	}
	catch (PersistenceException e)
	{
		Log.exception(this, "Query for purging of product classes failed", e);
	}

}
/**
 * Searches for class by symbol.
 *
 * @param classSymbol symbol of class
 * @param type product type of class
 */
private ProductClass queryBySymbol(String classSymbol, short type)
{
	if (checkMostRecentAdd(classSymbol, type))
	{
		return getMostRecentAdd();
	}
	ProductClass result = null;
	ProductClassImpl example = new ProductClassImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		example.setSymbol(classSymbol);
		example.setProductType(type);
		result = (ProductClass) query.findUnique();
	}
	catch (PersistenceException e)
	{
		// ignore error - return null result
	}
	return result;
}
/**
 * Sets most recently added product class.
 *
 * @param addedClass most recently added class
 */
private void setMostRecentAdd(ProductClass addedClass)
{
	mostRecentAdd = addedClass;
}
}
