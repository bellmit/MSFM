package com.cboe.interfaces.domain.product;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;
import com.cboe.exceptions.*;

/**
 * An update for a <code>Product</code> that is being changed as part of a
 * <code>PriceAdjustment</code>.
 *
 * @author John Wickberg
 */
public interface ProductAdjustment {
/**
 * Applies this adjustment to the product being changed.
 *
 * @exception TransactionFailedException if product adjustment cannot be applied
 */
void apply() throws TransactionFailedException;
/**
 * Creates a valid instance.
 *
 * @param parent the parent class adjustment of this item adjustment
 * @param newAdjustmentItem CORBA struct containing values for this item
 * @exception DataValidationException if validation checks fail
 */
void create(ReportingClassAdjustment parent, PriceAdjustmentItemStruct newAdjustmentItem) throws DataValidationException;
/**
 * Gets the product being adjusted by this entry.  If the action == CREATE, this value
 * will be null.
 *
 * @return reference to adjusted product
 */
Product getAdjustedProduct();
/**
 * Gets the new product name of the product being adjusted.  This will be the name
 * of the product after this adjustment is applied.
 *
 * @return CORBA struct containing new product name
 */
ProductNameStruct getNewProductName();
/**
 * Gets the product name of the product being adjusted.
 *
 * @return CORBA struct containing product name
 */
ProductNameStruct getProductName();
/**
 * Convert this adjustment to a pending name struct.
 *
 * @return struct containing pending name information
 */
PendingNameStruct toPendingName();
/**
 * Converts this item to a CORBA struct.
 *
 * @return CORBA struct containing values of this item
 */
PriceAdjustmentItemStruct toStruct();
/**
 * Updates this item with values from a CORBA struct.
 *
 * @param updatedItem CORBA struct containing new values
 */
void update(PriceAdjustmentItemStruct updatedItem);
}
