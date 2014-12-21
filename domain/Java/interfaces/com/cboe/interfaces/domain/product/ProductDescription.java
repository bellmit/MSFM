 package com.cboe.interfaces.domain.product;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.exceptions.*;

/**
 * A product description contains information about formatting of
 * prices.
 *
 * @author John Wickberg
 */
public interface ProductDescription {
    /**
     * Creates a new product description.
     *
     * @param description CORBA struct containing definition
     * @exception DataValidationException if validation checks fail
     */
    public void create(ProductDescriptionStruct description) throws DataValidationException;
    /**
     * Converts this description to CORBA struct.
     *
     * @return CORBA struct containing definition
     */
    public ProductDescriptionStruct toStruct();
    /**
     * Updates this description with values from CORBA struct.
     *
     * @param description struct containing new definition
     * @exception DataValidationException if validation checks fail
     */
    public void update(ProductDescriptionStruct description) throws DataValidationException;
    
    /**
     * Gets the key of this product description.
     *
     * @return product descripion key
     */
    public int getProductDescriptionKey();
}
