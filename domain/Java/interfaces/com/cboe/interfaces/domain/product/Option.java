package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/Option.java

import com.cboe.interfaces.domain.Price;
import com.cboe.util.*;
import com.cboe.exceptions.DataValidationException;

/**
 * A product that gives the holder the right to buy or sell a fixed amount of a product at
 * a fixed exercise price by exercising the option prior to its expiration date.
 *
 * @author John Wickberg
 */
public interface Option extends Derivative
{
/**
 * Gets price at which buys (calls) or sales (puts) of this product can be
 * exercised.
 *
 * @return exercise price
 *
 * @author John Wickberg
 * @roseuid 362E08200018
 */
public Price getExercisePrice();
/**
 * Gets the code used by OPRA to identify the month and type of this
 * option.
 *
 * @return OPRA month code.
 *
 * @author John Wickberg
 * @roseuid 362E08A90322
 */
public char getOpraMonthCode();
/**
 * Gets the code used by OPRA to identify the price of this option.
 *
 * @return OPRA price code.
 *
 * @author John Wickberg
 */
char getOpraPriceCode();
/**
 * Gets type of this option.
 *
 * @return option type
 *
 * @author John Wickberg
 * @roseuid 362E07C20302
 */
public OptionType getOptionType();
/**
 * Sets price at which buys (calls) or sales (puts) of this product can be
 * exercised.
 *
 * @param newPrice new exercise price
 *
 * @author John Wickberg
 * @roseuid 362E0867016F
 */
public void setExercisePrice(Price newPrice);
/**
 * Sets the code used by OPRA to identify the and type of this
 * option.
 *
 * @param newCode new OPRA month code
 *
 * @author John Wickberg
 * @roseuid 362E089E00B0
 */
public void setOpraMonthCode(char newCode) throws DataValidationException;
/**
 * Sets the code used by OPRA to identify the price of this option.
 *
 * @param newCode new OPRA price code.
 *
 * @author John Wickberg
 */
void setOpraPriceCode(char newCode) throws DataValidationException;
}
