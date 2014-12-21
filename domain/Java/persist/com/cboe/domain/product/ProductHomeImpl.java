package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ProductHomeImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiErrorCodes.AlreadyExistCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.domain.util.ExpirationDateFactory;
import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.util.ExceptionBuilder;
import com.objectwave.persist.constraints.Constraint;
import com.objectwave.persist.constraints.ConstraintCompare;
import com.objectwave.persist.constraints.ConstraintIsNull;

import java.util.*;

/**
 * An implementation of <code>ProductHome</code> that manages persistent products
 * using JavaGrinder O-R mapping.
 *
 * @author John Wickberg
 */
public class ProductHomeImpl extends BOHome implements ProductHome
{
    private static final String ENABLE_OSI_PRODUCT_LOOKUP = "enableOSIProductLookup";
    private ProductComponentHome productComponentHome;
    private ProductClassHome productClassHome;
    private static boolean enableOSILookup = true;
    
    static
    {
        String osiProductLook = System.getProperty(ENABLE_OSI_PRODUCT_LOOKUP);
        if(osiProductLook !=null && osiProductLook.equalsIgnoreCase("true"))
        {
            enableOSILookup = true;
        }
        else
        {
            enableOSILookup = false;
        }
        Log.information("ENABLE OSI Product Lookup Configured: " + enableOSILookup);
    }

/**
 * Creates an instance of the product home.
 */
public ProductHomeImpl()
{
	super();
}
/**
 * @see Product#create
 */
public Product create(ProductStruct newProduct) throws AlreadyExistsException, DataValidationException
{
	if (queryByName(newProduct.productName) == null)
 	{
		ProductImpl result = new ProductImpl();
		result.create(newProduct);
		addToContainer(result);
		return result;
  	}
   	else
    {
    	throw ExceptionBuilder.alreadyExistsException("Product already exists with same name", AlreadyExistCodes.PRODUCT_ALREADY_EXISTS);
    }
}
/**
 * see ProductHome#findByKey
 */
public Product findByKey(int productKey) throws NotFoundException
{
	// query for product
	ProductImpl example = new ProductImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	example.setObjectIdentifierFromInt(productKey);
	try
	{
		Product result = (Product) query.findUnique();
		return result;
	}
	catch (PersistenceException e)
	{
        Log.debug( this, "Failed to find product by key '" + productKey + '\'' );
		throw ExceptionBuilder.notFoundException("No product found with key = " + productKey, NotFoundCodes.RESOURCE_DOESNT_EXIST);
	}
}
/**
 * @see ProductHome#findByName
 *
 * Note: the expiration date may not be the standardized expiration date, so the method will try
 *
 * 1. find product by whatever passed in, if not found then
 * 2. find product by standizing the expiration date on Saturday expiration style, if not found then
 * 3. find product by standizing the expiration date on Friday expiration style
 */
public Product findByName(ProductNameStruct productName) throws NotFoundException
{
	Product result = queryByName(productName);
 	if (result != null){
		return result;
	}
 	
 	// After OSI, Product lookup should be exact, no expiration style should be used to query a product.
    if (enableOSILookup && productName.expirationDate.day > 1) {
       throw ExceptionBuilder.notFoundException("No product name found with exact match = " + ProductStructBuilder.toString(productName), NotFoundCodes.RESOURCE_DOESNT_EXIST);
    }
    
    // Lookup based on expiration style only if option types is neither a CALL or a PUT
    if(productName.optionType != OptionTypes.CALL && productName.optionType != OptionTypes.PUT)
    {
	    DateStruct datePassedIn = productName.expirationDate;
	    ExpirationDate standardDate = ExpirationDateFactory.createStandardDate(datePassedIn, ExpirationDateFactory.SATURDAY_EXPIRATION);
	    productName.expirationDate = standardDate.toStruct();
	    result = queryByName(productName);
	    if (result != null) {
	        return result;
	    }
	    standardDate = ExpirationDateFactory.createStandardDate(datePassedIn, ExpirationDateFactory.FRIDAY_EXPIRATION);
	    productName.expirationDate = standardDate.toStruct();
	    result = queryByName(productName);
    }
    if (result != null){
        return result;
    }
	else{
		throw ExceptionBuilder.notFoundException("No product found with name = " + ProductStructBuilder.toString(productName), NotFoundCodes.RESOURCE_DOESNT_EXIST);
	}
}

/**
 * Initializes class.
 */
public void goMaster(boolean failover)
{
}
/**
 * Queries database for product data and creates cached indices.
 */
private void loadFromDatabase()
{
	Log.information(this, "Pre-loading products from database");
	ProductImpl example = new ProductImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		java.util.Vector result = query.find();
	}
	catch (PersistenceException e)
	{
		Exception orig = e.getOriginalException();
		Log.alarm(this, "Query failed for product: " + orig);
		Log.exception(this, orig);
	}
	Log.information(this, "Completed pre-loading of products");
}
/**
 * Purges old obsolete products and marks old inactive products obsolete.
 *
 * @see ProductHome#purge
 */
public void purge(long retentionCutoff)
{
	ProductImpl example = new ProductImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		query.setFieldConstraint(example, "lastModifiedTime", "" + retentionCutoff, "<=");
		// Unlisted records will also be returned, but will be skipped in processing
		query.setFieldConstraint(example, "listingState", "" + ListingStates.ACTIVE, "!=");
		Vector queryResult = query.find();
        Log.information(this, "Found " + queryResult.size() + " products for purge processing");
		ProductImpl product;
		Enumeration resultsEnum = queryResult.elements();
		while (resultsEnum.hasMoreElements())
		{
			product = (ProductImpl) resultsEnum.nextElement();
			if (product.getListingState() == ListingStates.OBSOLETE)
			{
				try
				{
					product.markForDelete();
				}
				catch (PersistenceException e)
				{
					Log.alarm(this, "Unable to delete product = " + product.getProductKey());
				}
			}
			else if (product.getListingState() == ListingStates.INACTIVE)
			{
				product.setListingState(ListingStates.OBSOLETE);
			}
		}
	}
	catch (PersistenceException e)
	{
		Log.exception(this, "Query for purging of product classes failed", e);
	}
 }
/**
 * Searches database for product by product name.
 *
 * @param productName name struct for product
 * @return found product or null if product isn't found
 */
private Product queryByName(ProductNameStruct productName)
{
	// query for product
	ProductImpl example = new ProductImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	example.productName = ProductStructBuilder.toString(productName);
	Product result = null;
	try
	{
		result = (Product) query.findUnique();
	}
	catch (PersistenceException e)
	{
        Log.debug( this, "Failed to queryByName '" + example.productName + '\'' );
		// calling routing will check for null result
	}
 	return result;
}

/**
 * Returns all active expired products of the given product type
 * @param productType short (from cmiConstants.ProductTypes)
 * @return Product[]
 */
public Product[] findActiveExpiredProducts(short productType)
{
    ProductImpl example = new ProductImpl();
    addToContainer(example);
    ObjectQuery query = new ObjectQuery(example);
    example.setListingState(ListingStates.ACTIVE);
    example.setProductType(productType);

    String nextExpirationDate = ExpirationDateFactory.createNearTermDate(getExpirationStyle(productType)).toString();

    query.setFieldConstraint(example, "expireDate", nextExpirationDate, "<");

    Collection result = null;
    try
    {
        result = (Collection) query.find();
    }
    catch(PersistenceException e)
    {
        Log.exception(this, "Could not query for active expired products for product type " + productType + ".", e);
    }

    Product[] products;
    if(result == null)
    {
        products = new ProductImpl[0];
    }
    else
    {
        Iterator productIterator = result.iterator();
        products = new Product[result.size()];
        for(int i = 0; productIterator.hasNext(); i++)
        {
            ProductImpl product = (ProductImpl) productIterator.next();
            addToContainer(product);

            products[i] = product;
        }
    }

    return products;
}

private int getExpirationStyle(int productType){
    if (productType == ProductTypes.FUTURE){
        return ExpirationDateFactory.FRIDAY_EXPIRATION;
    }
    return ExpirationDateFactory.SATURDAY_EXPIRATION;
}
/**
 * Returns all the products that are not updated.
 * @param openInterestUpdateTime
 * @param classKey
 * @return Product[]
 */
public Product[] findProductsNotUpdatedForOpenInterestUpdateTime(  int classKey, long openInterestUpdateTime ) throws NotFoundException
{
    ProductImpl example = new ProductImpl();
    addToContainer(example);
    ProductClassImpl productClass = new ProductClassImpl();
    productClass.setObjectIdentifierFromInt(classKey);
    ObjectQuery query = new ObjectQuery(example);
    example.setOpenInterestUpdateTime(openInterestUpdateTime);
    example.setProductClass(productClass);
    
    ConstraintCompare notEqualConstraint = new ConstraintCompare();
    notEqualConstraint.setPersistence(example);
    notEqualConstraint.setField("openInterestUpdateTime");
    notEqualConstraint.setComparison("!=");
    notEqualConstraint.setCompValue(""+openInterestUpdateTime) ;
	
    ConstraintIsNull nullConstraint = new ConstraintIsNull();
    nullConstraint.setPersistence(example);
    nullConstraint.setField("openInterestUpdateTime");
    query.addConstraint(notEqualConstraint);
    query.addConstraint(nullConstraint);
    Collection result = null;
    try
    {
        result = (Collection) query.find();
    }
    catch(PersistenceException e)
    {
        Log.exception(this, "Could not query for Products Not updated for OpenInterestUpdateTime", e);
        throw ExceptionBuilder.notFoundException("No product found with class key = " + classKey, NotFoundCodes.RESOURCE_DOESNT_EXIST);
    }
    Product[] products;
    if(result == null)
    {
        products = new ProductImpl[0];
    }
    else
    {
        Iterator productIterator = result.iterator();
        products = new Product[result.size()];
        for(int i = 0; productIterator.hasNext(); i++)
        {
            ProductImpl product = (ProductImpl) productIterator.next();
            products[i] = product;
        }
    }
    return products;
}
/**
 *  Returns the product component home
 */
public ProductComponentHome getProductComponentHome()
{
	if (productComponentHome == null)
	{
	    try
	    {
		    productComponentHome = (ProductComponentHome) HomeFactory.getInstance().findHome(ProductComponentHome.HOME_NAME);
		}
		catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
		{
		    throw new NullPointerException("Cannot find ProductComponentHome");
		}
	}
	return productComponentHome;
}

/**
 *  Returns the product class home
 */
public ProductClassHome getProductClassHome()
{
	if (productClassHome == null)
	{
	    try
	    {
		    productClassHome = (ProductClassHome) HomeFactory.getInstance().findHome(ProductClassHome.HOME_NAME);
		}
		catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
		{
		    throw new NullPointerException("Cannot find ProductClassHome");
		}
	}
	return productClassHome;
}

}
