package com.cboe.domain.product;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.interfaces.domain.product.*;
import com.cboe.idl.product.*;
import com.cboe.exceptions.*;
import com.cboe.util.*;
import java.util.*;

/**
 * An implementation of <code>PriceAdjustmentHome</code> that manages price adjustments
 * using JavaGrinder persistence mapping.
 *
 * @author John Wickberg
 */
public class PriceAdjustmentHomeImpl extends BOHome implements PriceAdjustmentHome
{
/**
 * PriceAdjustmentHomeImpl constructor comment.
 */
public PriceAdjustmentHomeImpl() {
	super();
}
/**
 * Creates valid price adjustment.
 *
 * @see PriceAdjustment#create
 */
public PriceAdjustment create(PriceAdjustmentStruct newAdjustment) throws AlreadyExistsException, DataValidationException
{
    PriceAdjustmentImpl adjustment = new PriceAdjustmentImpl();
	addToContainer(adjustment);
    adjustment.create(newAdjustment);
	return adjustment;
}

/**
* Creates valid price adjustment.
* UD 06/06/05, takes in a result structure to capture a successful/failed adjustment.
* @see PriceAdjustment#create
*/
public PriceAdjustment create(PriceAdjustmentStruct newAdjustment, PriceAdjustmentReportingClassResultStruct[] reportingClassAdjustmentsResults) throws AlreadyExistsException, DataValidationException
{
	PriceAdjustmentImpl adjustment = new PriceAdjustmentImpl();
	addToContainer(adjustment);
    adjustment.create(newAdjustment, reportingClassAdjustmentsResults);
	return adjustment;
}

/**
 * Deletes the given adjustment.
 *
 * @see PriceAdjustmentHome#delete
 */
public void delete(PriceAdjustment adjustment)
{
	try
	{
		((PriceAdjustmentImpl) adjustment).markForDelete();
	}
	catch (PersistenceException e)
	{
		Log.exception(this, "Cannot delete price adjustment for product = " + adjustment.getAdjustedProduct().getProductKey(), e);
	}
}
/**
 * findByKey method comment.
 */
public PriceAdjustment[] findAll()
{
	PriceAdjustmentImpl example = new PriceAdjustmentImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		Vector tempResult = query.find();
		PriceAdjustment[] result = new PriceAdjustment[tempResult.size()];
		tempResult.copyInto(result);
		return result;
	}
	catch (PersistenceException e)
	{
		Log.exception(this, "Query for price adjustments failed", e);
		throw new NullPointerException("Query for price adjustments failed: " + e);
	}
}
/**
 * Finds price adjustment affecting product related to class.
 *
 * @see PriceAdjustmentHome#findByClass
 */
public PriceAdjustment findByClass(ProductClass adjustedClass) throws NotFoundException
{
	PriceAdjustment result;
	if (adjustedClass.getUnderlyingProduct() != null)
	{
		result = findByProduct(adjustedClass.getUnderlyingProduct());
	}
	else
	{
		Product[] products = adjustedClass.getProducts(true);
		if (products.length == 0)
		{
			throw ExceptionBuilder.notFoundException("No active products are related to class = " + adjustedClass.getClassKey(), 0);
		}
		else if (products.length > 1)
		{
			Log.alarm(this, "More than one product related to class (" + adjustedClass.getClassKey() + ") using first");
		}
		result = findByProduct(products[0]);
	}
	return result;
}
/**
 * findByKey method comment.
 */
public PriceAdjustment findByKey(int adjustmentKey) throws NotFoundException
{
	PriceAdjustment result = null;
	PriceAdjustmentImpl example = new PriceAdjustmentImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		example.setObjectIdentifierFromInt(adjustmentKey);
		result = (PriceAdjustment) query.findUnique();
		return result;
	}
	catch (PersistenceException e)
	{
		throw ExceptionBuilder.notFoundException("Unable to locate price adjustment for key = " + adjustmentKey, 0);
	}
}
/**
 * Finds price adjustment for product.
 *
 * @see PriceAdjustmentHome#findByProduct
 */
public PriceAdjustment findByProduct(Product adjustedProduct) throws NotFoundException
{
	PriceAdjustment result = null;
	PriceAdjustmentImpl example = new PriceAdjustmentImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		example.adjustedProduct = adjustedProduct;
		result = (PriceAdjustment) query.findUnique();
		return result;
	}
	catch (PersistenceException e)
	{
		throw ExceptionBuilder.notFoundException("Unable to locate price adjustment for product = " + adjustedProduct.getProductKey(), 0);
	}
}
}
