package com.cboe.domain.product;

// Source file: com/cboe/domain/product/IndexEditor.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.domain.util.StructBuilder;

/**
 * A persistent implementation of <code>Index</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public class IndexEditor extends CompositeEditor implements Index
{
/**
 * Creates an instance with default values.
 */
public IndexEditor(ProductImpl editedProduct)
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
	setFullName(newProduct.description);
}
/**
 * Calls index handling method of handler.
 *
 * @see Product#dispatch
 */
public Object dispatch(ProductDispatch handler, Object context)
{
	return handler.handleIndex(this, context);
}
/**
 * Fills in the product struct with values defined at this level.
 *
 * @param aStruct CORBA struct being updated
 */
protected void fillInStruct(ProductStruct aStruct)
{
	super.fillInStruct(aStruct);
	aStruct.description = StructBuilder.nullToEmpty(getFullName());
}
/**
 * Gets full name of this product.  Full name is stored in description.
 *
 * @see Index#getFullName
 */
public String getFullName()
{
	return getProduct().getDescription();
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return productType == ProductTypes.INDEX;
}
/**
 * Sets full name of this product.  Full name is stored in description.
 *
 * @see Index#setFullName
 */
public void setFullName(String newName)
{
	getProduct().setDescription(newName);
}
/**
 * Updates this product.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
	super.update(updatedProduct);
	setFullName(updatedProduct.description);
}
public String getSettlementPriceSuffix() {
	// TODO Auto-generated method stub
	return null;
}
public Price getYesterdaysClosePrice() {
	// TODO Auto-generated method stub
	return null;
}
public String getYesterdaysClosePriceSuffix() {
	// TODO Auto-generated method stub
	return null;
}
public void setSettlementPriceSuffix(String settlementPriceSuffix) {
	// TODO Auto-generated method stub
	
}
public void setYesterdaysClosePrice(Price yesterdaysClosePrice) {
	// TODO Auto-generated method stub
	
}
public void setYesterdaysClosePriceSuffix(String yesterdaysClosePriceSuffix) {
	// TODO Auto-generated method stub
	
}
}
