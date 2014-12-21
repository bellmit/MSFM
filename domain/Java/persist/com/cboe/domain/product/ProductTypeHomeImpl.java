package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ProductTypeHomeImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.foundationFramework.transactionManagement.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.util.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * An implementation of <code>ProductTypeHome</code> that manages persistent product
 * types using JavaGrinder O-R mapping.
 *
 * @author John Wickberg
 */
public class ProductTypeHomeImpl extends BOHome implements ProductTypeHome
{
	/**
	 * A list to hold all product types.
	 */
	private Vector types;
public ProductTypeHomeImpl()
{
	super();
	// need to do query for product types eventually
	setTypes(createList());
}
/**
 * Adds missing types to database.  A check is done to make sure that there is an entry
 * for all types defined in the ProductType CORBA enumeration.
 */
private void addMissingTypes()
{
  boolean success = false;
  try
  {
      Class typeEnumClass = ProductTypes.class;
	Field[] attributes = typeEnumClass.getFields();
      ProductTypeStruct newType = new ProductTypeStruct();
	Transaction.startTransaction();
	for (int i = 0; i < attributes.length; i++)
	{
		if (attributes[i].getType() == short.class)
		{
			try
			{
				newType.type = attributes[i].getShort(null);
				if (query(newType.type) == null)
				{
					newType.name = newType.description = attributes[i].getName();
					create(newType);
					Log.information(this, "Added new product type to database for " + newType.name);
				}
			}
			catch (IllegalAccessException e)
			{
				Log.alarm(this, "Unable to access productType field of ProductTypes");
			}
			catch (AlreadyExistsException e)
			{
				Log.alarm(this, "Could not create product type for " + attributes[i].getName());
			}
			catch (DataValidationException e)
			{
				Log.alarm(this, "Could not create product type for " + attributes[i].getName());
			}
		}
	}
    success = Transaction.commit();
  }
  finally
  {
      if(!success)
      {
          Transaction.rollback();
      }
  }    
}
/**
 * Creates new product type.
 *
 * @see ProductTypeHome#create
 */
public ProductType create(ProductTypeStruct newType) throws AlreadyExistsException, DataValidationException
{
	if (query(newType.type) == null)
	{
		ProductTypeImpl newInstance = new ProductTypeImpl();
		newInstance.create(newType);
		addToContainer(newInstance);
		indexType(newInstance);
		return newInstance;
  	}
  	else
	{
 		throw ExceptionBuilder.alreadyExistsException("Product type already exists", 0);
	}
}
/**
 * Creates list for product types.
 *
 * @return created list
 */
private Vector createList()
{
	return new Vector();
}
/**
 * Finds product type by type code.
 *
 * @see ProductTypeHome#find
 */
public ProductType find(int type) throws NotFoundException
{
	ProductType result = query(type);
	if (result == null)
	{
		throw ExceptionBuilder.notFoundException("No product type found with type code = " + type, 0);
	}
	return result;
}
/**
 * Finds all product types.
 *
 * @see ProductTypeHome#findAll
 */
public ProductType[] findAll()
{
	Vector source;
	source = createList();
	Enumeration prodTypesEnum = getTypes().elements();
	ProductType current;
	while (prodTypesEnum.hasMoreElements())
	{
		current = (ProductType) prodTypesEnum.nextElement();
		// check for nulls, just in case type codes are not a contiguous sequence
		if (current != null)
		{
			source.addElement(current);
		}
	}
	ProductType[] result = new ProductType[source.size()];
	source.copyInto(result);
	return result;
}
/**
 * Get list of product types.
 *
 * @return list of product types
 */
private Vector getTypes()
{
	return types;
}
/**
 * Adds type to indices used to access types.
 *
 * @param newType type to be indexed
 */
private void indexType(ProductType newType)
{
	Vector index = getTypes();
	// Using type value as index of type in array.  Need to add objects
	// so that index is defined.
	int needToAdd = newType.getType() + 1 - index.size();
	for (int i = 0; i < needToAdd; i++)
	{
		index.addElement(null);
	}
	index.setElementAt(newType, newType.getType());
}
/**
 * Initializes the home.  Initialization is completed by querying the\
 * database for all product classes and then adding the results to the transient cache.
 */
public void goMaster(boolean failover)
{
	Log.information(this, "Pre-loading product types from database");
	ProductTypeImpl example = new ProductTypeImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		java.util.Vector result = query.find();
		java.util.Enumeration prodTypesEnum = result.elements();
		ProductType next;
		while (prodTypesEnum.hasMoreElements())
		{
			next = (ProductType) prodTypesEnum.nextElement();
			indexType(next);
		}
		addMissingTypes();
	}
	catch (PersistenceException e)
	{
		Exception orig = e.getOriginalException();
		Log.alarm(this, "Unable to query for product types: " + orig);
		Log.exception(this, orig);
	}
	Log.information(this, "Completed pre-loading product types");
}
/**
 * Finds product type by type code.
 *
 * @param type code of product type
 * @return found type or null
 */
private ProductType query(int type)
{
	ProductType result = null;
	try
	{
		result = (ProductType) getTypes().elementAt(type);;
	}
	catch (ArrayIndexOutOfBoundsException e)
	{
		// ignore - result will be null
	}
	return result;
}
/**
 * Set list of product types.
 *
 * @param newTypes list for types
 */
private void setTypes(Vector newTypes)
{
	types = newTypes;
}
}
