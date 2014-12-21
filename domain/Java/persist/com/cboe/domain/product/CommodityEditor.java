package com.cboe.domain.product;

// Source file: com/cboe/domain/product/CommodityImpl.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.domain.util.StructBuilder;
import com.cboe.util.*;

/**
 * A persistent implementation of <code>Commodity</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public class CommodityEditor extends BaseProductEditor implements Commodity
{
/**
 * Creates commodity with default values.
 */
public CommodityEditor(ProductImpl editedProduct)
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
	setDescription(newProduct.description);
	setUnitMeasure(newProduct.unitMeasure);
	setStandardQuantity(newProduct.standardQuantity);
    setCompanyName(newProduct.companyName);
}
/**
 * Calls commodity handling method of handler.
 *
 * @see Product#dispatch
 */
public Object dispatch(ProductDispatch handler, Object context)
{
	return handler.handleCommodity(this, context);
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
	aStruct.description = StructBuilder.nullToEmpty(getDescription());
	aStruct.unitMeasure = StructBuilder.nullToEmpty(getUnitMeasure());
	aStruct.standardQuantity = getStandardQuantity();
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
 * Gets description of this product.
 *
 * @see Commodity#getDescription
 */
public String getDescription()
{
	return getProduct().getDescription();
}
/**
 * Gets standard contract quantity of this product.
 *
 * @see Commodity#getStandardQuantity
 */
public double getStandardQuantity()
{
	return getProduct().getStandardQuantity();
}
/**
 * Gets unit of measure of this product.
 *
 * @see Commodity#getUnitMeasure
 */
public String getUnitMeasure()
{
	return getProduct().getUnitMeasure();
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return productType == ProductTypes.COMMODITY;
}
/**
 * Sets description of this product.
 *
 * @see Commodity#setDescription
 */
public void setDescription(String newDescription)
{
	getProduct().setDescription(newDescription);
}
/**
 * Sets standard contract quantity of this product.
 *
 * @see Commodity#setStandardQuantity
 */
public void setStandardQuantity(double newQuantity)
{
	getProduct().setStandardQuantity(newQuantity);
}
/**
 * Sets unit of measure for this product.
 *
 * @see Commodity#setUnitMeasure
 */
public void setUnitMeasure(String newMeasure)
{
	getProduct().setUnitMeasure(newMeasure);
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
 * Updates this product from CORBA struct.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
	super.update(updatedProduct);
	setDescription(updatedProduct.description);
	setUnitMeasure(updatedProduct.unitMeasure);
	setStandardQuantity(updatedProduct.standardQuantity);
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
