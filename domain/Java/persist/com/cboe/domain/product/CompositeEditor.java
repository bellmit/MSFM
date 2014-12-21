package com.cboe.domain.product;

// Source file: com/cboe/domain/product/CompositeEditor.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.util.ProductStructBuilder;

/**
 * A persistent implementation of <code>Composite</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public abstract class CompositeEditor extends ProductEditor implements Composite
{
	/**
	 * Cached reference to the product component home.
	 */
	private static ProductComponentHome productComponentHome;
/**
 * Creates an instance with default values.
 */
public CompositeEditor(ProductImpl editedProduct)
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
 * Gets the components of this composite.
 *
 * @see Composite#getComponents
 */
public ProductComponent[] getComponents() {
	return getProductComponentHome().findByProduct(getProduct());
}
/**
 * Gets the product component home.  Rather than having a collection of the
 * components in the product, the components are obtained from the home.
 */
private ProductComponentHome getProductComponentHome() {
	if (productComponentHome == null)
	{
	    try
	    {
		    productComponentHome = (ProductComponentHome) HomeFactory.getInstance().findHome(ProductComponentHome.HOME_NAME);
		}
		catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
		{
		    throw new NullPointerException("Cannot not find ProductComponentHome");
		}
	}
	return productComponentHome;
}
/**
 * Gets product name.  For composites, the product name is the symbol.
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
 * @see Composite#getSymbol
 */
public String getSymbol()
{
	return getProduct().getSymbol();
}
/**
 * Sets symbol of this product.
 *
 * @see Composite#setSymbol
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
