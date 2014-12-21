package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/ProductType.java

import com.cboe.interfaces.domain.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;

/**
 * A means to maintain additional information about the available product
 * types.
 *
 * @author John Wickberg
 */
public interface ProductType extends DomainBase
{
/**
 * Changes this instance so that it is an instance represented by the given struct.
 *
 * @param newType CORBA struct containing values for type
 * @exception DataValidationException if validation checks fail
 *
 * @author John Wickberg
 */
void create(ProductTypeStruct newType) throws DataValidationException;
/**
 * Gets description of this product type.
 *
 * @return description for type
 *
 * @author John Wickberg
 * @roseuid 3630C68C00CC
 */
public String getDescription();
/**
 * Gets name of this product type.
 *
 * @return type name
 *
 * @author John Wickberg
 * @roseuid 3630C6790396
 */
public String getName();
/**
 * Gets type key value.  This value will correspond to the value in the
 * <code>ProductType</code> CORBA enumeration.
 *
 * @return product type key value
 *
 * @author John Wickberg
 * @roseuid 3630C6710286
 */
public short getType();
/**
 * Sets description of this product type.
 *
 * @param newDescription new description for type
 *
 * @author John Wickberg
 * @roseuid 3630C6A3038C
 */
public void setDescription(String newDescription);
/**
 * Sets name of this product type.
 *
 * @param newName new type name
 *
 * @author John Wickberg
 * @roseuid 3630C68202C7
 */
public void setName(String newName);
/**
 * Converts this product type to a CORBA struct.
 *
 * @return CORBA struct representing this product type
 *
 * @author John Wickberg
 * @roseuid 3630C96802A6
 */
public ProductTypeStruct toStruct();
/**
 * Updates this product type using values from CORBA struct.
 *
 * @param updatedType CORBA struct containing updated values
 * @exception DataValidationException if validation checks fail
 *
 * @author John Wickberg
 */
public void update(ProductTypeStruct updatedType) throws DataValidationException;
}
