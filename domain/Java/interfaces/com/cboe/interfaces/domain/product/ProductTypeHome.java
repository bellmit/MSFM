package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/ProductTypeHome.java

import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;

/**
 * A home to create and find product type information.
 *
 * @author John Wickberg
 */
public interface ProductTypeHome
{
    /**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "ProductTypeHome";
/**
 * Creates product type from given values.
 *
 * @param newType values for product type
 * @return constructed product type
 * @exception AlreadyExistsException if product type already exists
 * @exception DataValidationException if validation checks fail
 */
public ProductType create(ProductTypeStruct newType) throws AlreadyExistsException, DataValidationException;
/**
 * Searches for requested product type.
 *
 * @param type enumeration value from <code>ProductType</code> CORBA enumeration
 * @return found product type
 * @exception NotFoundException if search fails
 */
public ProductType find(int type) throws NotFoundException;
/**
 * Searches for all product types.
 *
 * @return all defined product types
 */
ProductType[] findAll();
}
