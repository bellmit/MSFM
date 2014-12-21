package com.cboe.domain.product;

// Source file: com/cboe/domain/product/DebtImpl.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.StructBuilder;

/**
 * A persistent implementation of <code>Debt</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */

public class DebtEditor extends BaseProductEditor implements Debt
{
/**
 * Creates debt with default values.
 */
public DebtEditor(ProductImpl editedProduct)
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
	setMaturityDate(DateWrapper.convertToMillis(newProduct.maturityDate));
}
/**
 * Calls debt handling method of handler.
 *
 * @see Product#dispatch
 */
public Object dispatch(ProductDispatch handler, Object context)
{
	return handler.handleDebt(this, context);
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
	aStruct.companyName = StructBuilder.nullToEmpty(getCompanyName());
	aStruct.maturityDate = DateWrapper.convertToDate(getMaturityDate());
}
/**
 * Gets company name of this product.
 *
 * @see Debt#getCompanyName
 */
public String getCompanyName()
{
	return getProduct().getCompanyName();
}
/**
 * Gets maturity date of this product.
 *
 * @see Debt#getMaturityDate
 */
public long getMaturityDate()
{
	return getProduct().getMaturityDate();
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return productType == ProductTypes.DEBT;
}
/**
 * Sets company name of this product.
 *
 * @see Debt#setCompanyName
 */
public void setCompanyName(String newName)
{
	getProduct().setCompanyName(newName);
}
/**
 * Sets maturity date of this product.
 *
 * @see Debt#setMaturityDate
 */
public void setMaturityDate(long newDate)
{
	getProduct().setMaturityDate(newDate);
}
/**
 * Updates this product.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
	super.update(updatedProduct);
	setCompanyName(updatedProduct.companyName);
	DateWrapper matDate = new DateWrapper(updatedProduct.maturityDate);
	setMaturityDate(matDate.getTimeInMillis());
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
