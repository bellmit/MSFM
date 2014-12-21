package com.cboe.domain.product;

// Source file: com/cboe/domain/product/OptionType.java

import com.objectwave.persist.*;
import com.cboe.interfaces.domain.product.*;
import com.cboe.idl.cmiConstants.*;

/**
 * This class is used to specify the type of an option, currently just call or put.
 * 
 * @author John Wickberg
 */
public abstract class OptionTypeImpl implements OptionType, SqlScalarType
{
	public static final String CALL_STRING = "CALL";
	public static final String PUT_STRING = "PUT";

	
	static
	{
		/*
		 * Register anonymous inner class as generator for option types.
		 */
		SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF()
		{
			/*
			 * Creates an option type instance matching the input value.
			 */
			public SqlScalarType createInstance(String value)
			{
				return getOptionType(value);
			}
			/*
			 * Returns the OptionType class since it is the parent class of the
			 * generated instance types.
			 */
			public Class typeGenerated()
			{
				return OptionType.class;
			}
		});
	}
/**
 * This method returns the call option type singleton.
 * @return com.cboe.utils.OptionType
 */
public static OptionTypeImpl getCallType()
{
	return new CallOptionType();
}
/**
 * This method returns either the call or put option type depending on the first character of the type string.
 * @return OptionType
 * @param aType java.lang.String
 */
public static OptionTypeImpl getOptionType(String aType)
{
	char firstChar = Character.toUpperCase(aType.charAt(0));
	if (firstChar == 'C')
		return getCallType();
	else if (firstChar == 'P')
		return getPutType();
	else
		throw new IllegalArgumentException("The given string (" + aType + ") is not a valid option type");
}
/**
 * Gets instance of <code>OptionType</code> corresponding to type code.
 * 
 * @param type option type code
 * @return corresponding <code>OptionType</code> instance
 */
public static OptionTypeImpl getOptionType(char type) {
	if (type == OptionTypes.CALL)
		return getCallType();
	else if (type == OptionTypes.PUT)
		return getPutType();
	else
		throw new IllegalArgumentException("The given enumeration value (" + type + ") is not a valid option type");
}
/**
 * This method returns the put option type singleton.
 * @return com.cboe.utils.OptionType
 */
public static OptionTypeImpl getPutType()
{
	return new PutOptionType();
}
	/**
	 * This method is used to test the type of an option.
	 * @return boolean
	 */
	public boolean isCall() {
	return false;
	}
	/**
	 * This method is used to test the type of an option.
	 * @return boolean
	 */
	public boolean isPut() {
	return false;
	}
/**
 * Returns a string to represent an OptionType in the datbase.
 * 
 * @return database string for type
 */
public String toDatabaseString() {
	return toString();
}
}
