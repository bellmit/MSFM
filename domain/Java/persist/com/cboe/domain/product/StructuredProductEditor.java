package com.cboe.domain.product;

// Source file: com/cboe/domain/product/StructuredProductImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.domain.util.ProductStructBuilder;
import java.lang.reflect.*;

/**
 * A persistent implementation of <code>StructuredProduct</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public abstract class StructuredProductEditor extends DerivativeEditor implements StructuredProduct
{
/**
 * Creates instance of structured product with default values.
 */
public StructuredProductEditor(ProductImpl editedProduct)
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
	setSymbol(newProduct.productName.productSymbol);
	setIssuingCompany(newProduct.companyName);
	setDescription(newProduct.description);
}
/**
 * Fills in the product struct with values defined at this level.
 *
 * @param aStruct CORBA struct being updated
 */
protected void fillInStruct(ProductStruct aStruct)
{
	super.fillInStruct(aStruct);
	aStruct.companyName = getIssuingCompany();
	aStruct.description = getDescription();
}
/**
 * Gets description of this product.
 *
 * @see StructuredProduct#getDescription
 */
public String getDescription()
{
	return getProduct().getDescription();
}
/**
 * Get issuing company of this product.
 *
 * @see StructuredProduct#getIssuingCompany
 */
public String getIssuingCompany()
{
	return getProduct().getCompanyName();
}
/**
 * Gets product name.  For structured products, name is equal to symbol.
 *
 * @return product name
 */
public ProductNameStruct getProductName()
{
	ProductNameStruct nameStruct = ProductStructBuilder.buildProductNameStruct();
	nameStruct.productSymbol = getSymbol();
	nameStruct.reportingClass = getReportingClass().getSymbol();
	return nameStruct;
}
/**
 * Gets symbol of this product.
 *
 * @see StructuredProduct#getSymbol
 */
public String getSymbol()
{
	return getProduct().getSymbol();
}
/**
 * Sets description of this product.
 *
 * @see StructuredProduct#setDescription
 */
public void setDescription(String newDescription)
{
	getProduct().setDescription(newDescription);
}
/**
 * Sets issuing company of this product.
 *
 * @see StructuredProduct#setIssuingCompany
 */
public void setIssuingCompany(String newCompany)
{
	getProduct().setCompanyName(newCompany);
}
/**
 * Sets symbol of this product.
 *
 * @see StructuredProduct#setSymbol
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
	setIssuingCompany(updatedProduct.companyName);
	setDescription(updatedProduct.description);
}
/**
 * Updates name of this product.
 *
 * @see Product#updateName
 */
public void updateName(ProductNameStruct newName)
{
	super.updateName(newName);
	setSymbol(newName.productSymbol);
}
}
