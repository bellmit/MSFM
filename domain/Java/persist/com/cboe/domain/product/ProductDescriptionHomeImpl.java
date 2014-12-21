package com.cboe.domain.product;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiErrorCodes.AlreadyExistCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.product.*;
import com.cboe.util.*;
import java.util.*;

/**
 * A JavaGrinder based implementation of <code>ProductDescriptionHome</code>.
 *
 * @author John Wickberg
 */
public class ProductDescriptionHomeImpl extends BOHome implements ProductDescriptionHome {

	/**
	 * Creates a new description.
	 *
	 * @see ProductDescriptionHome#create
	 */
	public ProductDescription create(ProductDescriptionStruct description)
		throws DataValidationException, AlreadyExistsException {
		if (queryByName(description.name) == null) {
			ProductDescriptionImpl result = new ProductDescriptionImpl();
			addToContainer(result);
			result.create(description);
			return result;
		}
		else {
			throw ExceptionBuilder.alreadyExistsException("A product description already exists with name = " + description.name, AlreadyExistCodes.PRODUCT_DESCRIPTION_ALREADY_EXISTS);
		}
	}

	/**
	 * Finds all descriptions.
	 *
	 * @see ProductDescriptionHome#findAll
	 */
	public ProductDescription[] findAll() {
		ProductDescription[] result = null;
		ProductDescriptionImpl example = new ProductDescriptionImpl();
		addToContainer(example);	// need to get database settings
		ObjectQuery query = new ObjectQuery(example);
		try	{
			Vector queryResult = query.find();
			result = new ProductDescription[queryResult.size()];
			queryResult.copyInto(result);
		}
		catch (PersistenceException e) {
			result = new ProductDescription[0];
		}
		return result;
	}


	/**
	 * Finds for product description by key.
	 *
	 * @see ProductDescriptionHome#findByKey
	 */
	public ProductDescription findByKey(int descriptionKey) throws NotFoundException {
		ProductDescription result = null;
		ProductDescriptionImpl example = new ProductDescriptionImpl();
		addToContainer(example);	// need to get database settings
		ObjectQuery query = new ObjectQuery(example);
		try	{
			example.setObjectIdentifierFromInt(descriptionKey);
			result = (ProductDescription) query.findUnique();
        	addToContainer((BObject)result);
			return result;
		}
		catch (PersistenceException e) {
			throw ExceptionBuilder.notFoundException("No product description found with key = " + descriptionKey, NotFoundCodes.RESOURCE_DOESNT_EXIST);
		}
	}

	/**
	 * Finds product description by name.
	 *
	 * @see ProductDescriptionHome#findByName
	 */
	public ProductDescription findByName(String descriptionName) throws NotFoundException {
		ProductDescription result = queryByName(descriptionName);
		if (result == null) {
			throw ExceptionBuilder.notFoundException("No product description found with name = " + descriptionName, NotFoundCodes.RESOURCE_DOESNT_EXIST);
		}
        addToContainer((BObject)result);
			return result;
	}

	/**
	 * Queries for product description by name.
	 *
	 * @param descriptionName name of product description
	 * @return found description or null
	 */
	private ProductDescription queryByName(String descriptionName) {
		ProductDescription result = null;
		ProductDescriptionImpl example = new ProductDescriptionImpl();
		addToContainer(example);	// need to get database settings
		ObjectQuery query = new ObjectQuery(example);
		try	{
			example.setDescriptionName(descriptionName);
			result = (ProductDescription) query.findUnique();
		}
		catch (PersistenceException e) {
			// ignore error - return null result
		}
		return result;
	}
}

