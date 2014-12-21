package com.cboe.domain.product;

// Source file: com/cboe/domain/product/StrategyEditor.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.util.*;

/**
 * A persistent implementation of <code>Strategy</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public class StrategyEditor extends CompositeEditor implements Strategy
{
/**
 * Creates an instance with default values.
 */
public StrategyEditor(ProductImpl editedProduct)
{
	super(editedProduct);
}
/**
 * Calls strategy handling method of handler.
 *
 * @see Product#dispatch
 */
public Object dispatch(ProductDispatch handler, Object context)
{
	return handler.handleStrategy(this, context);
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return productType == ProductTypes.STRATEGY;
}
/**
 * Converts this product to a strategy struct.  This method overrides
 * the default implementation and adds legs for the strategy.
 *
 * @see Product#toStrategyStruct
 */
public StrategyStruct toStrategyStruct() {
	// create a strategy struct with no legs
	StrategyStruct result = new StrategyStruct();
	result.product = toStruct();
	ProductComponent[] components = getComponents();
	result.strategyLegs = new StrategyLegStruct[components.length];
	for (int i = 0; i < components.length; i++) {
		result.strategyLegs[i] = components[i].toLegStruct();
	}
        result.strategyType = getProduct().getProductSubType();
	return result;
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
