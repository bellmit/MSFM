package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/ProductClassHome.java

import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;

/**
 * A home class used to create or find product classes.
 *
 * @author John Wickberg
 */

public interface ProductClassHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "ProductClassHome";
/**
 * Creates a product class corresponding to the passed values.
 *
 * @param newClass CORBA struct defining new product class.
 * @exception AlreadyExistsException if class already exists
 * @exception DataValidationException if other validation checks fail
 */
public ProductClass create(ClassDefinitionStruct newClass) throws AlreadyExistsException, DataValidationException, SystemException;
/**
 * Searches for all <code>ProductClass</code>'s.
 *
 * @param activeOnly if <code>true</code>, result will only contain active classes
 * @return array of found <code>ProductClass</code>'s.  Array will contain
 *         zero elements if search fails.
 */
public ProductClass[] findAll(boolean activeOnly);
	/**
	 * Searches for all classes that use the requested description.
	 *
	 * @param description reference to target description
     * @param activeOnly if <code>true</code>, result will only contain active classes
	 * @return array of found <code>ProductClass</code>'s.  Array will contain
	 *         zero elements if search fails.
	 */
	public ProductClass[] findByDescription(ProductDescription description, boolean activeOnly);
/**
 * Searches for a <code>ProductClass</code> having the given key.
 *
 * @param key the key of the desired <code>ProductClass</code>
 * @exception NotFoundException If search fails
 */
public ProductClass findByKey(int key) throws NotFoundException;
/**
 * Searches for a <code>ProductClass</code> having the given symbol and type.
 *
 * @param classSymbol symbol of desired <code>ProductClass</code>
 * @param type type must be specified since symbols are only unique by type
 * @exception NotFoundException If search fails
 */
public ProductClass findBySymbol(String classSymbol, short type) throws NotFoundException;
/**
 * Searches for all <code>ProductClass</code>'s of a given type.
 *
 * @param type desired type
 * @param activeOnly if <code>true</code>, result will only contain active classes
 * @return array of found <code>ProductClass</code>'s.  Array will contain
 *         zero elements if search fails.
 */
public ProductClass[] findByType(short type, boolean activeOnly);
/**
 * Searches for product classes that have the given product as their underlying.
 *
 * @param underlyingProduct requested underlying product
 * @param activeOnly if set, only active product classes will be returned
 * @return classes that are derived from the underlying.  An empty array will be
 *         returned if nothing is found
 */
ProductClass[] findByUnderlying(Product underlyingProduct, boolean activeOnly);
/**
 * Purges old product classes and marks old inactive classes obsolete.
 *
 * @param retentionCutoff the time in millis to be used as the end of the retention period
 */
void purge(long retentionCutoff);
}
