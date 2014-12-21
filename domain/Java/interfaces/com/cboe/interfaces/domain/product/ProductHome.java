package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/ProductHome.java

import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;

/**
 * A home to create and find products.
 *
 * @author John Wickberg
 */
public interface ProductHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "ProductHome";
/**
 * Creates new product from passed values.
 *
 * @param newProduct CORBA struct containing values for new product
 * @return created product
 * @exception AlreadyExistsException if product already exists
 * @exception DataValidationException if validation checks fail
 */
public Product create(ProductStruct newProduct) throws AlreadyExistsException, DataValidationException;
/**
 * Searches for product by key.
 *
 * @param productKey key of desired product
 * @return found product
 * @exception NotFoundException if search fails
 */
public Product findByKey(int productKey) throws NotFoundException;
/**
 * Searches for product by name.  The name of a product is a calculated value that must be unique.
 *
 * @param productName standard name of desired product
 * @return found product
 * @exception NotFoundException if search fails
 */
public Product findByName(ProductNameStruct productName) throws NotFoundException;

/**
 *  Returns the product component home
 */ 
public ProductComponentHome getProductComponentHome();

/**
 * Returns all active expired products of the given product type
 * @param productType short (from cmiConstants.ProductTypes)
 * @return Product[]
 */
public Product[] findActiveExpiredProducts(short productType);

/**
 * Purges old obsolete products and marks old inactive products obsolete.
 *
 * @param retentionCutoff the time in millis to be used as the end of the retention period
 */
void purge(long retentionCutoff);

/**
 * Returns all the products that are not updated.
 * @param openInterestUpdateTime
 * @param classKey
 * @return Product[]
 */
public Product[] findProductsNotUpdatedForOpenInterestUpdateTime( int classKey, long openInterestUpdateTime ) throws NotFoundException; 
}
