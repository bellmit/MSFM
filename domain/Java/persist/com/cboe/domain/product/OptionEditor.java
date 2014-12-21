package com.cboe.domain.product;

// Source file: com/cboe/domain/product/OptionImpl.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.product.*;
import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.domain.util.*;
import com.cboe.util.*;


/**
 * A persistent implementation of <code>Option</code>.
 *
 * For more information on persistence for products:
 * @see ProductImpl
 *
 * @author John Wickberg
 */
public class OptionEditor extends DerivativeEditor implements Option
{
    private static final char OPRA_CALL_START_MONTH_CODE = 'A';
    private static final char OPRA_PUT_START_MONTH_CODE = 'M';

    private static final char OPRA_MIN_PRICE_CODE = 'A';
    private static final char OPRA_MAX_PRICE_CODE = 'Z';

/**
 * Creates option with default values.
 */
public OptionEditor(ProductImpl editedProduct)
{
    super(editedProduct);
}
/**
 * Extends create method to initialize values defined at this level.
 *
 * @see DerivativeEditor #create
 */
public void create(ProductStruct newProduct) throws DataValidationException
{
    super.create(newProduct);
    PriceSqlType exPrice = new PriceSqlType(newProduct.productName.exercisePrice);
    setExercisePrice(exPrice);
    setOptionType(OptionTypeImpl.getOptionType(newProduct.productName.optionType));
    setOpraMonthCode(newProduct.opraMonthCode);
    setOpraPriceCode(newProduct.opraPriceCode);
}
/**
 * Calls option handling method of handler.
 *
 * @see Product#dispatch
 */
public Object dispatch(ProductDispatch handler, Object context)
{
    return handler.handleOption(this, context);
}
/**
 * Fills in the product struct with values defined at this level.
 *
 * @param aStruct CORBA struct being updated
 */
protected void fillInStruct(ProductStruct aStruct)
{
    super.fillInStruct(aStruct);
    aStruct.productName.exercisePrice = getExercisePrice().toStruct();
    aStruct.productName.optionType = getOptionType().toValue();
    aStruct.opraMonthCode = getOpraMonthCode();
    aStruct.opraPriceCode = getOpraPriceCode();
}
/**
 * Gets exercise price of this product.
 *
 * @see Option#getExercisePrice
 */
public Price getExercisePrice()
{
    return getProduct().getExercisePrice();
}
/**
 * Gets OPRA month code of this product.
 *
 * @see Option#getOpraMonthCode
 */
public char getOpraMonthCode()
{
    return getProduct().getOpraMonthCode();
}
/**
 * Gets OPRA price code of this product.
 *
 * @see Option#getOpraPriceCode
 */
public char getOpraPriceCode()
{
    return getProduct().getOpraPriceCode();
}
/**
 * Gets option type.
 *
 * @see Option#getOptionType
 */
public OptionType getOptionType()
{
    return getProduct().getOptionType();
}
/**
 * Gets product name.  For options, the product name is the reporting class symbol +
 * expiration date + exercise price + option type.
 *
 * @see Product#getProductName
 *
 * @author John Wickberg
 * @roseuid 3623A11B0346
 */
public ProductNameStruct getProductName()
{
    ProductNameStruct productName = ProductStructBuilder.buildProductNameStruct();
    productName.reportingClass = getReportingClass().getSymbol();
    productName.expirationDate = getExpirationDate().toStruct();
    productName.exercisePrice = getExercisePrice().toStruct();
    productName.optionType = getOptionType().toValue();
    return productName;
}
/**
 * Checks if product is of requested type.
 *
 * @see Product#isProductType
 */
public boolean isProductType(short productType)
{
    return productType == ProductTypes.OPTION;
}
/**
 * Sets exercise price of this product.
 *
 * @see Option#setExercisePrice
 */
public void setExercisePrice(Price newPrice)
{
    getProduct().setExercisePrice(newPrice);
}
/**
 * Sets OPRA month code of this product.
 *
 * @see Option#setOpraMonthCode
 */
public void setOpraMonthCode(char newCode) throws DataValidationException
{
    ExpirationDate expirationDate = getExpirationDate();
    DateStruct dateStruct = expirationDate.toStruct();
    byte month = dateStruct.month;
    if (getOptionType().isCall()) {
        if (newCode != OPRA_CALL_START_MONTH_CODE + month - 1) {
            // use 0 for now, need to be fixed later.
            throw ExceptionBuilder.dataValidationException("Invalid OPRA MONTH CODE: " +
                newCode + ".  "  + "It does not match with month number: " + month + ".", DataValidationCodes.INVALID_OPRA_MONTH_CODE);
        }
    }
    else
    {
        if (newCode != OPRA_PUT_START_MONTH_CODE + month - 1) {
            // use 0 for now, need to be fixed later.
            throw ExceptionBuilder.dataValidationException("Invalid OPRA MONTH CODE: " +
                newCode + ".  "  + "It does not match with month number: " + month + ".",DataValidationCodes.INVALID_OPRA_MONTH_CODE);
        }
    }
    getProduct().setOpraMonthCode(newCode);
}
/**
 * Sets OPRA price code of this product.
 *
 * @see Option#setOpraPriceCode
 */
public void setOpraPriceCode(char newCode) throws DataValidationException
{
    if (newCode < OPRA_MIN_PRICE_CODE || newCode > OPRA_MAX_PRICE_CODE)
    {
        // use 0 for now, need to be fixed later.
        throw ExceptionBuilder.dataValidationException("Invalid OPRA PRICE CODE: " +
            newCode + ".  OPRA Price Code should be " + OPRA_MIN_PRICE_CODE +
            "-" + OPRA_MAX_PRICE_CODE, 0);
    }

    getProduct().setOpraPriceCode(newCode);
}
/**
 * Sets option type code.
 *
 * @param newType new type code
 */
private void setOptionType(OptionType newType)
{
    getProduct().setOptionType(newType);
}
/**
 * Updates this product.
 *
 * @see Product#update
 */
public void update(ProductStruct updatedProduct) throws DataValidationException
{
    super.update(updatedProduct);
    updateName(updatedProduct.productName);
    setOpraMonthCode(updatedProduct.opraMonthCode);
    setOpraPriceCode(updatedProduct.opraPriceCode);
}
/**
 * Updates name of this product.
 *
 * @see Product#updateName
 */
public void updateName(ProductNameStruct newName)
{
    super.updateName(newName);
    setExercisePrice(new PriceSqlType(newName.exercisePrice));
    setOptionType(OptionTypeImpl.getOptionType(newName.optionType));
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

