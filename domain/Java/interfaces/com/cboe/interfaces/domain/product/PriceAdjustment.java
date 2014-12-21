package com.cboe.interfaces.domain.product;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;
import com.cboe.exceptions.*;

/**
 * A change to the value of a security that affects the prices of derivative products related
 * to it.
 * <p>
 * A <code>PriceAdjustment</code> is entered prior to the time that the products are
 * changed.  An adjustment must be applied for the affected products to be updated.
 * UD 06/06/05, Changes added for stock splits from IPD.
 * @author John Wickberg
 * @author Uma Diddi
 */
public interface PriceAdjustment
{
/**
 * Applies this adjustment to products being changed.  No products are changed by a
 * price adjustment until it is applied.
 *
 * @exception TransactionFailedException if adjustment could not be applied
 */
void apply() throws TransactionFailedException;
/**
 * Creates a valid instance.
 *
 * @param newAdjustment a CORBA struct containing the values for the adjustment
 * @exception AlreadyExistsException if an adjustment already exists for the product
 * @exception DataValidationException if validation checks fail
 */
void create(PriceAdjustmentStruct newAdjustment) throws AlreadyExistsException, DataValidationException;

/**
* Creates a valid instance and takes in a result struct to capture successful/failed adjustment creates.
*
* @param newAdjustment a CORBA struct containing the values for the adjustment
* @exception AlreadyExistsException if an adjustment already exists for the product
* @exception DataValidationException if validation checks fail
*/
void create(PriceAdjustmentStruct newAdjustment, PriceAdjustmentReportingClassResultStruct[] reportingClassAdjustmentsResults) throws AlreadyExistsException, DataValidationException;

/**
 * Finds the request class adjustment.
 *
 * @param classSymbol symbol of requested class
 * @return adjustment for class
 * @exception NotFoundException if class adjustment is not found
 */
ReportingClassAdjustment findClassAdjustment(String classSymbol, short productType) throws NotFoundException;
/**
 * Gets all of the classes changed by this <code>PriceAdjustment</code>.
 *
 * @return all changed classes for this adjustment
 */
ReportingClassAdjustment[] getAdjustedClasses();
/**
 * Gets the product that is being adjusted.
 *
 * @return product being adjusted
 */
Product getAdjustedProduct();
/**
 * Gets source of price adjustment.
 *
 * @return code of price adjustment source
 */
short getSource();
/**
 * Checks to see if adjustment should be applied at this time.
 *
 * @return true if adjustment should be applied
 */
boolean isTimeToApply();
/**
 * Converts this adjustment to a sequence of CORBA structs that are used for display
 * purposes rather than for maintenance.
 *
 * @param includeProducts if set, the pending product information for each class
 *                        will be included in the result.
 * @return PendingAdjustmentInfo[]
 */
PendingAdjustmentStruct[] toPendingInfo(boolean includeProducts);
/**
 * Convert this adjustment to a pending name struct.
 *
 * @return struct containing pending name information
 */
PendingNameStruct toPendingName();
/**
 * Converts this <code>PriceAdjustment</code> to a CORBA struct.
 *
 * @param includeDetail if set, information about reporting classes and products
 *                      affected by the adjustment will be included.
 * @return a CORBA struct representing this adjustment
 */
PriceAdjustmentStruct toStruct(boolean includeDetail);
/**
 * Updates this adjustment with values from CORBA struct.
 *
 * @param updatedAdjustment CORBA struct containing updated values
 *
 * @exception DataValidationException if validation checks for update fail
 */
void update(PriceAdjustmentStruct updatedAdjustment) throws DataValidationException;

/**
* Updates this adjustment with values from CORBA struct.
* UD 06/06/05, Updates this adjustments and takes in a result struct to capture successful/failed adjustment updates.
*
* @param updatedAdjustment CORBA struct containing updated values
*
* @exception DataValidationException if validation checks for update fail
*/
void update(PriceAdjustmentStruct updatedAdjustment, PriceAdjustmentReportingClassResultStruct[] reportingClassAdjustmentsResults) throws DataValidationException;

/**
 * Finds the request class adjustments.
 *
 * @param classSymbol symbol of requested class
 * @return adjustment for class
 * @exception NotFoundException if class adjustment is not found
 */
ReportingClassAdjustment[] findClassAdjustments(String classSymbol, short productType) throws NotFoundException;
}
