package com.cboe.domain.product;

// Source file: com/cboe/domain/product/DerivativeImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.domain.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiUtil.DateStruct;

/**
 * A persistent implementation of <code>Derivative</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public abstract class DerivativeEditor extends ProductEditor implements Derivative
{
/**
 * Creates derivative with default values.
 */
public DerivativeEditor(ProductImpl editedProduct)
{
	super(editedProduct);
}
/**
 * Extends create method to initialize values defined at this level.
 *
 * @see BaseProductImpl#create
 */
public void create(ProductStruct newProduct) throws DataValidationException
{
	super.create(newProduct);
    setExpirationDate(newProduct.productName.expirationDate);
}
/**
 * Fills in the product struct with values defined at this level.
 *
 * @param aStruct CORBA struct being updated
 */
protected void fillInStruct(ProductStruct aStruct)
{
	super.fillInStruct(aStruct);
	aStruct.productName.expirationDate = getExpirationDate().toStruct();
}
/**
 * Gets expiration date of this product.
 *
 * @see Derivative#getExpirationDate
 */
public ExpirationDate getExpirationDate()
{
	return getProduct().getExpirationDate();
}
/**
 * Converts expiration date to standard.
 */
private void setExpirationDate(DateStruct newDate)
{
	ExpirationDate exDate = ExpirationDateFactory.createStandardDate(newDate, ExpirationDateFactory.ANY_DAY_EXPIRATION);
	setExpirationDate(exDate);
}
/**
 * Sets expiration date of this product.
 *
 * @see Derivative#setExpirationDate
 */
public void setExpirationDate(ExpirationDate newDate)
{
	getProduct().setExpirationDate(newDate);
}
/**
 * Updates this product.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
	super.update(updatedProduct);
	setExpirationDate(updatedProduct.productName.expirationDate);
}
/**
 * Updates name of this product.
 *
 * @see Product#updateName
 */
public void updateName(ProductNameStruct newName)
{
	super.updateName(newName);
	setExpirationDate(newName.expirationDate);
}
}
