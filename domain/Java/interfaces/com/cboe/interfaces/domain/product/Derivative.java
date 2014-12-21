package com.cboe.interfaces.domain.product;

import com.cboe.interfaces.domain.ExpirationDate;
// Source file: com/cboe/interfaces/domain/product/Derivative.java

/**
 * A product whose values are dependent on a single underlying product.
 *
 * @author John Wickberg
 */
public interface Derivative extends Product
{
/**
 * Gets the date on which this product expires.
 *
 * @return expiration date
 *
 * @author John Wickberg
 * @roseuid 362DF8360327
 */
public ExpirationDate getExpirationDate();
/**
 * Sets the date on which this product expires.
 *
 * @param newDate new expiration date
 *
 * @author John Wickberg
 * @roseuid 362DF842032E
 */
public void setExpirationDate(ExpirationDate newDate);
}
