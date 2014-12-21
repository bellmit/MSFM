package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/ReportingClass.java

import com.cboe.interfaces.domain.DomainBase;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;

/**
 * A collection of products in the same product class that are
 * grouped for reporting purposes.
 *
 * @author John Wickberg
 */
public interface ReportingClass extends DomainBase
{
/**
 * Adds a <code>Product</code> to the reporting class.  The given product must
 * be of the same type as the reporting class.
 *
 * @param newProduct product to be added
 */
public void addProduct(Product newProduct);
/**
 * Changes this instance to be an instance representing the passed struct.
 *
 * @param newClass CORBA struct for reporting class
 * @exception DataValidationException if validation checks fail
 */
public void create(ReportingClassStruct newClass) throws DataValidationException;
/**
 * Gets date that this reporting class was activated.
 *
 * @return activation date
 */
public long getActivationDate();
/**
 * Gets key of this reporting class.
 *
 * @return reporting class key
 */
public int getClassKey();
/**
 * Gets size of contracts for this reporting class.  The size of contracts
 * is the number of underlying products that each derivative product represents.
 *
 * @return size of contracts
 */
public int getContractSize();
/**
 * Gets date that this reporting class was inactivated.
 *
 * @return inactivation date
 */
public long getInactivationDate();
/**
 * Gets the listing state of this class.
 *
 * @return listing state
 */
short getListingState();
/**
 * Gets the <code>ProductClass</code> that is the owner of this reporting class.
 *
 * @return owning product class
 */
public ProductClass getProductClass();
/**
 * Gets the products that belong to this reporting class.
 *
 * @param activeOnly if <code>true</code> restricts the result to be only
 * products that are active
 * @return array of products
 */
public Product[] getProducts(boolean activeOnly);
/**
 * Gets product type of this reporting class.
 *
 * @return product type
 */
public short getProductType();
/**
 * Gets the symbol of this reporting class.
 *
 * @return reporting class symbol
 */
String getSymbol();
/**
 * Checks to see if this reporting class is active for trading.
 *
 * @return <code>true</code> if this class is active
 */
boolean isActive();
/**
 * Removes a <code>Product</code> from the reporting class.
 *
 * @param removedProduct product to be removed
 */
public void removeProduct(Product removedProduct);
/**
 * Sets date that this reporting class was activated.
 *
 * @param newDate new activation date
 */
public void setActivationDate(long newDate);
/**
 * Sets size of contracts for this reporting class.  The size of contracts
 * is the number of underlying products that each derivative product represents.
 *
 * @param newSize new size of contracts
 */
public void setContractSize(int newSize);
/**
 * Sets date that this reporting class was inactivated.
 *
 * @param newDate new inactivation date
 */
public void setInactivationDate(long newDate);
/**
 * Sets the listing state of this class.
 *
 * @param newState new listing state
 */
void setListingState(short newState);
/**
 * Sets the <code>ProductClass</code> that is the owner of this reporting class.
 *
 * @param newClass new owning product class
 */
public void setProductClass(ProductClass newClass);
/**
 * Sets the symbol of this reporting class.
 *
 * @param newSymbol reporting class symbol
 */
void setSymbol(String newSymbol);
/**
 * Converts this reporting class to a CORBA struct.
 *
 * @return CORBA struct representing this class
 */
public ReportingClassStruct toStruct();
/**
 * Updates this reporting class with values from passed CORBA struct.
 *
 * @param updatedClass CORBA struct containing updated values
 * @exception DataValidationException if validation checks fail on updated values
 */
public void update(ReportingClassStruct updatedClass) throws DataValidationException;

/*
*   Get the extensions field.
*/
public String getExtensions();

/*
*   Set the extensions field.
*/
public void setExtensions( String extensions );
}
