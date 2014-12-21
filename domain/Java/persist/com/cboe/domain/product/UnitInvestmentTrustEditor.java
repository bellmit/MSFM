package com.cboe.domain.product;

// Source file: com/cboe/domain/product/UnitIvestmentTrustImpl.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;

/**
 * A persistent implementation of <code>UnitInvestmentTrust</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public class UnitInvestmentTrustEditor extends StructuredProductEditor implements UnitInvestmentTrust
{
/**
 * Creates an instance with default values.
 */
public UnitInvestmentTrustEditor(ProductImpl editedProduct)
{
	super(editedProduct);
}
/**
 * Calls unit investment trust handling method of handler.
 *
 * @see Product#dispatch
 */
public Object dispatch(ProductDispatch handler, Object context)
{
	return handler.handleUnitInvestmentTrust(this, context);
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return productType == ProductTypes.UNIT_INVESTMENT_TRUST;
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
