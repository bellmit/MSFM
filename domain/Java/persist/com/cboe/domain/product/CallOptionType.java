package com.cboe.domain.product;

// Source file: com/cboe/domain/product/CallOptionType.java

import com.cboe.idl.cmiConstants.*;

/**
 * This class is used to represent the call option type.
 * 
 * @author John Wickberg
 */
public class CallOptionType extends OptionTypeImpl {
	
/**
 * Creates an instance.
 */
protected CallOptionType()
{
}
/**
 * @see #OptionType#isCall
 *
 * @return always <code>true</code> for this type
 */
public boolean isCall()
{
	return true;
}
/**
 * Returns the static string for call as the string value.
 *
 * @return defined "call" string
 */
public String toString()
{
	return OptionTypeImpl.CALL_STRING;
}
/**
 * Return static type code for this option type.
 * 
 * @return defined type code for this option type
 */
public char toValue()
{
	return OptionTypes.CALL;
}
}
