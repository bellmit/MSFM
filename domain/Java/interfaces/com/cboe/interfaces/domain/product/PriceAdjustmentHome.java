package com.cboe.interfaces.domain.product;

import com.cboe.idl.product.*;
import com.cboe.exceptions.*;

/**
 * A home to create and find price adjustments.
 * UD 06/06/05, Changes added for stock splits from IPD.
 * @author John Wickberg
 */
public interface PriceAdjustmentHome
{
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "PriceAdjustmentHome";
/**
 * Creates a new price adjustment.
 *
 * @param newAdjustment CORBA struct containing price adjustment values.
 * @return new price adjustment
 * @exception AlreadyExistsException if price adjustment already exists for product
 * @exception DataValidationException if validation checks fail
 */
PriceAdjustment create(PriceAdjustmentStruct newAdjustment) throws AlreadyExistsException, DataValidationException;

/**
* Creates a new price adjustment.
* UD 06/06/05, and takes in a result struct to capture successful/failed adjustment creates. 
* @param newAdjustment CORBA struct containing price adjustment values.
* @return new price adjustment
* @exception AlreadyExistsException if price adjustment already exists for product
* @exception DataValidationException if validation checks fail
*/
PriceAdjustment create(PriceAdjustmentStruct newAdjustment, PriceAdjustmentReportingClassResultStruct[] reportingClassAdjustmentsResults) throws AlreadyExistsException, DataValidationException;
/**
 * Deletes the given price adjustment.  Delete will cascade to reporting class and
 * product entries for the adjustment.
 *
 * @param adjustment adjustment to be deleted.
 */
void delete(PriceAdjustment adjustment);
/**
 * Finds all price adjustments.
 *
 * @return found adjustments
 */
PriceAdjustment[] findAll();
/**
 * Finds price adjustment affecting product related to class.  The related product is
 * either the underlying product of the class or the first active product of the
 * class.
 *
 * @param adjustedClass product class being adjusted
 * @return price adjustment of related product
 * @exception NotFoundException if the class, related product or price adjustment isn't found
 */
PriceAdjustment findByClass(ProductClass adjustedClass) throws NotFoundException;
/**
 * Finds a price adjustment given its key.
 *
 * @param adjustmentKey key of price adjustment
 * @return found adjustment
 * @exception NotFoundException if price adjustment for key doesn't exist
 */
PriceAdjustment findByKey(int adjustmentKey) throws NotFoundException;
/**
 * Finds a price adjustment given its product.
 *
 * @param adjustedProduct product being adjusted
 * @return found adjustment
 * @exception NotFoundException if price adjustment for product
 */
PriceAdjustment findByProduct(Product adjustedProduct) throws NotFoundException;
}
