package com.cboe.domain.bestQuote;

import java.util.StringTokenizer;

import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;

/**
 * A holder for total volume by order contingency type.
 *
 * @author John Wickberg
 */
public class MarketVolume implements SqlScalarType
{
	/**
	 * Volume type
	 */
	private short volumeType;
	/**
	 * Total volume for type
	 */
	private int volume;
	/**
	 * Indicator that is set if volume involves multiple parties.
	 */
	private boolean multipleParties;

	static
	{
		/*
		 * Register anonymous inner class as generator for type
		 */
		SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF()
		{
			/*
			 * Creates instance of date from string
			 */
			public SqlScalarType createInstance(String values)
			{
				return new MarketVolume(values);
			}
			/*
			 * Returns this class as generated type.
			 */
			public Class typeGenerated()
			{
				return MarketVolume.class;
			}
		});
	}
/**
 * MarketBestVolume constructor comment.
 *
 * @param struct market volume values
 */
public MarketVolume(MarketVolumeStruct struct)
{
	super();
	volumeType = struct.volumeType;
	// Volume type must have a value for parsing from the database to
	// work - set to limit type if it hasn't been set.
	//if (volumeType == 0) {
	//	volumeType = VolumeTypes.LIMIT;
	//}
	volume = struct.quantity;
	multipleParties = struct.multipleParties;
}
/**
 * Creates instance from formatted string.
 *
 * @param values formatted string containing volume values
 * @see toString
 */
public MarketVolume(String values)
{
	super();
	StringTokenizer tokens = new StringTokenizer(values, ":");
	if (tokens.countTokens() == 3)
	{
		volumeType = Short.parseShort(tokens.nextToken());
		volume = Integer.parseInt(tokens.nextToken());
		multipleParties = Boolean.valueOf(tokens.nextToken()).booleanValue();
	}
	else
	{
		throw new IllegalArgumentException("Improper string passed: " + values);
	}
}
/**
 * Getter for volume type.
 *
 * @return volume type 
 */
public short getVolumeType()
{
	return volumeType;
}
/**
 * Getter for multiple parties.
 *
 * @return multiple party indicator
 */
public boolean getMultipleParties()
{
	return multipleParties;
}
/**
 * Getter for volume.
 *
 * @return total volume
 */
public int getVolume()
{
	return volume;
}
/**
 * Formats this object for storing in database.
 *
 * @return formatted string for storing in database
 */
public String toDatabaseString()
{
	return toString();
}
/**
 * Formats this object into standard form.
 *
 * @return formatted string
 */
public String toString()
{
	return volumeType + ":" + volume + ":" + multipleParties;
}
/**
 * Converts this object to a CORBA struct.
 *
 * @return struct containing values
 */
public MarketVolumeStruct toStruct()
{
	/*MarketVolumeStruct result = new MarketVolumeStruct();
	result.volumeType = volumeType;
	result.quantity = volume;
	result.multipleParties = multipleParties;*/
	MarketVolumeStruct result = MarketDataStructBuilder.getMarketVolumeStruct(volumeType, volume, multipleParties);
	return result;
}
}
