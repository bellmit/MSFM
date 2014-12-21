package com.cboe.domain.product;

// Source file: com/cboe/domain/product/FutureImpl.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.domain.util.ProductStructBuilder;

/**
 * A persistent implementation of <code>Future</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public class FutureEditor extends DerivativeEditor implements Future
{
/**
 * Creates an instance with default values.
 */
public FutureEditor(ProductImpl editedProduct)
{
	super(editedProduct);
}
/**
 * Calls future handling method of handler.
 *
 * @see Product#dispatch
 */
public Object dispatch(ProductDispatch handler, Object context)
{
	return handler.handleFuture(this, context);
}
/**
 * Gets product name.  The product name for a <code>Future</code> is the concatenation
 * of reporting class symbol and expiration date.
 *
 * @see Product#getProductName
 *
 * @return reporting class symbol + expiration date
 */
public ProductNameStruct getProductName()
{
	ProductNameStruct productName = ProductStructBuilder.buildProductNameStruct();
	productName.reportingClass = getReportingClass().getSymbol();
	productName.expirationDate = getExpirationDate().toStruct();
	return productName;
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return productType == ProductTypes.FUTURE;
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
