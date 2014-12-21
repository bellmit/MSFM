package com.cboe.interfaces.domain.product;

/**
 * The contract type of an option, call or put.
 *
 * @author John Wickberg
 */
public interface OptionType
{
/**
 * Returns true is this option type is a call.
 *
 */
boolean isCall();
/**
 * Returns true if this option type is a put.
 */
boolean isPut();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String toString();
/**
 * This method was created in VisualAge.
 * @return short
 */
char toValue();
}
