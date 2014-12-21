package com.cboe.domain.product;

// Source file: com/cboe/domain/product/BaseProductImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
//import com.cboe.util.*;
import com.cboe.domain.util.ProductStructBuilder;

/**
 * An editor for <code>BaseProducts</code>.
 *
 * For more information on product editors:
 * @see ProductEditor
 *
 * @author John Wickberg
 */
public abstract class BaseProductEditor extends ProductEditor implements BaseProduct
{
/**
 * Creates instance with default values.
 */
public BaseProductEditor(ProductImpl editedProduct)
{
	super(editedProduct);
}
/**
 * Extends create method to initialize values defined at this level.
 *
 * @see ProductImpl#create
 */
public void create(ProductStruct newProduct) throws DataValidationException
{
	super.create(newProduct);
	setSymbol(newProduct.productName.productSymbol);
}
/**
 * Fills in the product struct with values defined at this level.
 *
 * @param aStruct CORBA struct being updated
 *
 * @see ProductImpl#toStruct
 */
protected void fillInStruct(ProductStruct aStruct)
{
	super.fillInStruct(aStruct);
	// No values needed at this level since symbol will be
	// stored in struct as productName
}
/**
 * Gets product name.  The product name for a <code>BaseProduct</code> is it's symbol.
 *
 * @see Product#getProductName
 */
public ProductNameStruct getProductName()
{
	ProductNameStruct nameStruct = ProductStructBuilder.buildProductNameStruct();
	nameStruct.productSymbol = getSymbol();
	nameStruct.reportingClass = getReportingClass().getSymbol();
	return nameStruct;
}
/**
 * Gets symbol for this product.
 *
 * @see BaseProduct#getSymbol
 */
public String getSymbol()
{
	return getProduct().getSymbol();
}
/**
 * Sets the symbol of this product
 *
 * @see BaseProduct#setSymbol
 */
public void setSymbol(String newSymbol)
{
	getProduct().setSymbol(newSymbol);
}
/**
 * Updates this product.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
	super.update(updatedProduct);
	setSymbol(updatedProduct.productName.productSymbol);
}
/**
 * Updates this product.
 *
 * @see Product#updateName
 */
public void updateName(ProductNameStruct newName)
{
	super.updateName(newName);
	setSymbol(newName.productSymbol);
}
}
