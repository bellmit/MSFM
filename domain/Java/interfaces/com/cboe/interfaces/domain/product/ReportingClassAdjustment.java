package com.cboe.interfaces.domain.product;

import com.cboe.idl.product.*;
import com.cboe.exceptions.*;

/**
 * An update for a <code>ReportingClass</code> that is being changed by a
 * <code>PriceAdjustment</code>.
 * Changes added for stock splits from IPD.
 *
 * @author John Wickberg
 * @author Uma Diddi
 */
public interface ReportingClassAdjustment {
/**
 * Applies this adjustment to the class being changed.
 *
 * @exception TransactionFailedException if class adjustment could not be applied
 */
void apply() throws TransactionFailedException;
/**
 * Creates a valid instance.
 *
 * @param parent the parent adjustment of this class adjustment
 * @param newClassAdjustment CORBA struct containing values for adjusted class
 * @exception DataValidationException if validation checks fail
 */
void create(PriceAdjustment parent, PriceAdjustmentClassStruct newClassAdjustment) throws DataValidationException;

/**
* Creates a valid instance.
* UD 06/06/05, takes in a result struct to capture successful/failed adjustment creates
* @param parent the parent adjustment of this class adjustment
* @param newClassAdjustment CORBA struct containing values for adjusted class
* @exception DataValidationException if validation checks fail
*/
void create(PriceAdjustment parent, PriceAdjustmentClassStruct newClassAdjustment, PriceAdjustmentReportingClassResultStruct reportingClassAdjustment) throws DataValidationException;

/**
 * Finds price adjustment for the requested product.
 *
 * @param productKey key of requested product
 * @return price adjustment entry for product
 * @exception NotFoundException if no entry from for product
 */
ProductAdjustment findProductAdjustment(int productKey) throws NotFoundException;
/**
 * Searches adjustment entries for this class for requested product.
 *
 * @param adjustedProduct adjustment item for requested product
 * @return adjustment for the requested product
 * @exception NotFoundException if item does match any product adjustment for this class
 */
ProductAdjustment findProductAdjustment(PriceAdjustmentItemStruct adjustedProduct) throws NotFoundException;
/**
 * Gets all adjustments for products of this adjusted class.
 *
 * @return all adjustments for products of this adjusted class
 */
ProductAdjustment[] getAdjustedProducts();
/**
 * Gets the symbol of the reporting class being adjusted.
 *
 * @return symbol of adjusted reporting class
 */
String getClassSymbol();
/**
 * Checks to see if this adjustment will create a new class.
 *
 * @return true if apply will create a new class
 */
boolean isCreateAdjustment();
/**
 * Converts this class adjustment to a CORBA struct.
 *
 * @return a CORBA struct representing this class adjustment
 */
PriceAdjustmentClassStruct toStruct();
/**
 * Updates this adjusted class with values from CORBA struct.
 *
 * @param updatedClass CORBA struct containing updated values
 * @exception DataValidationException if validation checks fail during update
 */
void update(PriceAdjustmentClassStruct updatedClass) throws DataValidationException;

/**
* Updates this adjusted class with values from CORBA struct.
* UD 06/06/05, takes in a result struct to capture successful/failed adjustment
* 
* @param updatedClass CORBA struct containing updated values
* @exception DataValidationException if validation checks fail during update
*/
void update(PriceAdjustmentClassStruct updatedClass, PriceAdjustmentReportingClassResultStruct reportingClassAdjustment) throws DataValidationException;

/*
*   UD 01/10/05, for range rollover need to inactivate the reporting class
*   if all the series under the reporting class have changed.
*/
public void inactivateIfEmpty(); 
/**
 * Gets contract size after adjustment.
 */
public int getAfterContractSize();
}
