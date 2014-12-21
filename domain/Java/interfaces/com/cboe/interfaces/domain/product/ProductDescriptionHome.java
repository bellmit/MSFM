package com.cboe.interfaces.domain.product;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.exceptions.*;

/**
 * A home for product descriptions.
 *
 * @author John Wickberg
 */
public interface ProductDescriptionHome {
	/**
	 * Name of home used for HomeFactory.
	 */
	public static final String HOME_NAME = "ProductDescriptionHome";
	/**
	 * Creates a new description.
	 */
	public ProductDescription create(ProductDescriptionStruct description) throws DataValidationException, AlreadyExistsException;
	/**
	 * Finds product description given it's key.
	 *
	 * @param descriptionKey product description key
	 * @return product description having requested key
	 * @exception NotFoundException if description isn't found
	 */
	public ProductDescription findByKey(int descriptionKey) throws NotFoundException;
	/**
	 * Finds product description given it's name.
	 *
	 * @param descriptionName name of description
	 * @return product description having requested name
	 * @exception NotFoundException if description isn't found
	 */
	public ProductDescription findByName(String descriptionName) throws NotFoundException;
	/**
	 * Finds all defined product descriptions.
	 */
	public ProductDescription[] findAll();
}
