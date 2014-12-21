package com.cboe.domain.product;

// Source file: com/cboe/domain/product/PutOptionType.java

import com.cboe.idl.cmiConstants.*;

/**
 * This class represents a put option type
 * 
 * @author Mark Novak
 * @author John Wickberg
 */
public class PutOptionType extends OptionTypeImpl {
	
/**
 * Creates an instance.
 */
protected PutOptionType()
{
}
/**
 * @see OptionType#isPut
 *
 * @return always returns <code>true</code> for this type.
 */
public boolean isPut()
{
	return true;
}
/**
 * Returns the static string for put as the string value.
 *
 * @return defined string for this type
 */
public String toString()
{
	return OptionTypeImpl.PUT_STRING;
}
/**
 * Returns the static type code for this option type.
 *
 * @return defined code for this type
 */
public char toValue()
{
	return OptionTypes.PUT;
}
}
