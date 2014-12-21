package com.cboe.domain.product;

// Source file: com/cboe/domain/product/EquityImpl.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.domain.util.StructBuilder;

/**
 * A persistent implementation of <code>Equity</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */

public class EquityEditor extends BaseProductEditor implements Equity
{
/**
 * Creates debt with default values.
 */
public EquityEditor(ProductImpl editedProduct)
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
	setCompanyName(newProduct.companyName);
}
/**
 * Calls equity handling method of handler.
 *
 * @see Product#dispatch
 */
public Object dispatch(ProductDispatch handler, Object context)
{
	return handler.handleEquity(this, context);
}
/**
 * Fills in the product struct with values defined at this level.
 *
 * @param aStruct CORBA struct being updated
 */
protected void fillInStruct(ProductStruct aStruct)
{
	super.fillInStruct(aStruct);
	aStruct.companyName = StructBuilder.nullToEmpty(getCompanyName());
}
/**
 * Gets company name.
 *
 * @see Equity#getCompanyName
 */
public String getCompanyName()
{
	return getProduct().getCompanyName();
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return productType == ProductTypes.EQUITY;
}
/**
 * Sets company name.
 *
 * @see Debt#getCompanyName
 */
public void setCompanyName(String newName)
{
	getProduct().setCompanyName(newName);
}
/**
 * Updates product from CORBA struct.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
	super.update(updatedProduct);
	setCompanyName(updatedProduct.companyName);
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
