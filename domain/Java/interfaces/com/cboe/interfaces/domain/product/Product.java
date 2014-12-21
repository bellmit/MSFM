package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/Product.java

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.DomainBase;
import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.exceptions.*;

/**
 * Any financial product that is tradeable or underlies another product that is tradeable.
 *
 * @author John Wickberg
 */
public interface Product extends DomainBase
{
/**
 * Changes this instance so that it is an instance of the product represented
 * by the passed struct.
 *
 * @param newProduct CORBA representation of new product
 * @exception DataValidationException If validation checks fail
 */
public void create(ProductStruct newProduct) throws DataValidationException;
/**
 * Calls method on handler corresponding to type of this product.  This method
 * is used to implement a generic double dispatching on product type.
 *
 * @param handler object that will be called
 * @param context an object that can server as context info to the handling method
 * @return result of handler method
 */
public Object dispatch(ProductDispatch handler, Object context);
/**
 * Gets date when this product was activated.
 *
 * @return activation date
 */
public long getActivationDate();
/**
 * Gets date when this product was inactivated.
 *
 * @return inactivation date
 */
public long getInactivationDate();
/**
 * Gets the listing state for this product.
 *
 * @return product's listing state
 */
short getListingState();

/**
 * Gets description of this product.
 */
String getDescription();

/**
 * Gets product class of this product.
 *
 * @return product class
 */
ProductClass getProductClass();
/**
 * Get the editor associated with this product.  The editor is used to validate
 * updates to this product.
 *
 * @return the editor for this product
 */
Product getProductEditor();
/**
 * Gets the key of this product.
 *
 * @return product key
 */
public int getProductKey();
/**
 * Gets product name.  Product name is used to identify a product, but it is calculated from
 * other fields depending on product type.
 *
 * @return product name
 */
public ProductNameStruct getProductName();
/**
 * Gets reporting class of this product.
 *
 * @return reporting class
 */
public ReportingClass getReportingClass();
/**
 * Checks to see if the product is listed for trading.
 *
 * @return true if the product is listed
 */
boolean isActive();
    /**
     * Checks to see if the product is of the requested type.
     *
     * @param productType desired product type
     * @return true if the product is of this type
     */
    boolean isProductType(short productType);
/**
 * Sets date when this product was activated.
 *
 * @param newDate new activation date
 */
public void setActivationDate(long newDate);
/**
 * Sets date when this product was inactivated.
 *
 * @param newDate new inactivation date
 */
public void setInactivationDate(long newDate);
/**
 * Sets the listing state of this product.
 *
 * @param newState listing state value
 */
void setListingState(short newState);

/**
 * Sets description of this product.
 *
 * @see Commodity#setDescription
 */
void setDescription(String newDescription);

/**
 * Sets product class of this product.
 *
 * @param newClass new product class
 */
 void setProductClass(ProductClass newClass);
/**
 * Sets reporting class of this product.
 *
 * @param newClass new reporting class
 */
public void setReportingClass(ReportingClass newClass);
/**
 * Creates a CORBA struct containing the keys of this product.
 *
 * @return struct containing product keys
 */
public ProductKeysStruct toKeysStruct();
/**
 * Creates a CORBA struct representing this product.
 *
 * @return struct representing product
 */
public ProductStruct toStruct();
/**
 * Creates a CORBA struct representing a strategy struct.
 *
 * @return struct representing product
 */
public StrategyStruct toStrategyStruct();
/**
 * Updates this product using values from the passed struct.
 *
 * @param updatedProduct struct containing updated product values
 * @exception DataValidationException if validation checks fail
 */
public void update(ProductStruct updatedProduct) throws DataValidationException;
/**
 * Updates the name of this product.  The name of a product is normally only changed
 * by a price adjustment.
 *
 * @param newName ProductNameStruct
 */
void updateName(ProductNameStruct newName);

public Price getSettlementPrice();
public int getOpenInterest();
/**
 * Get the security CUSIP identifier
 */
public String getCusip( );

public void setSettlementPrice(Price newPrice);
public void setOpenInterest( int newInterest );
/**
 * Set the security CUSIP identifier.
 */
public void setCusip( String cusip );

/*
*   Get the extensions field.
*/
public String getExtensions();

/*
*   Set the extensions field.
*/
public void setExtensions( String extensions);

/**
 * Sets expiration date of this product.
 */
public void setExpirationDate(ExpirationDate newDate);

/*
 * get restricted indicator
 */
public boolean getRestrictedIndicator();

/*
 * Sets restricted indicator
 */
public void setRestrictedIndicator(boolean restrictedIndicator);
/**
 * Get UpdatedTime of openInterest
 */
public long getOpenInterestUpdateTime();

/**
 * Sets UpdatedTime of openInterest
 */
public void setOpenInterestUpdateTime( long openInterestUpdateTime );

/**
 *   Set the settlement price suffix.
 */
public void setSettlementPriceSuffix(String settlementPriceSuffix);

/**
 *   Set the yesterday's settlement price.
 */
public void setYesterdaysClosePrice(Price yesterdaysClosePrice);

/**
 *   Set the yesterday's settlement price suffix.
 */
public void setYesterdaysClosePriceSuffix(String yesterdaysClosePriceSuffix);

/**
 *   Get the settlement price suffix.
 */
public String getSettlementPriceSuffix();

/**
 *   Get the yesterday's settlement price.
 */
public Price getYesterdaysClosePrice();

/**
 *   Get the yesterday's settlement price suffix.
 */
public String getYesterdaysClosePriceSuffix();
/*
 * Reset New ReprotingClassSymbol on price adjustment roll back. 
 */
public void setNewReportingClassName(String newRptClassSym);
}
