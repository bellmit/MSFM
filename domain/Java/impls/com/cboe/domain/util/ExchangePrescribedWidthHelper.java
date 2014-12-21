package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;

/**
 *   Helper class for EPWStruct validation and use.
 *   This class exists to make sure that the EPW behavior is consistent across services.
 *
 *   @author Steven Sinclair
 */
public class ExchangePrescribedWidthHelper
{
	// a convenient constant for exception building
	//
	private static final int INVALID_PRICE = com.cboe.idl.cmiErrorCodes.DataValidationCodes.INVALID_PRICE;

	/**
	 *  Validates that the structArray's values fully describe the spread.
	 *  The min:max pairs should 'chain' to cover the minimum required range: ex,
	 *	<pre>0:0.5, 0.5:1.5, 1.5:3</pre> is a legal sequence of values if minSpreadRequired is leq 3.
	 *  The values are not required to be in ascending order.
	 *
	 *  <ul>
	 *  	<li> all values must be positive
	 *  	<li> for an EPWStruct, min must be less than max
	 *		<li> no [minBid:maxBid) range can overlap with another.
	 *  	<li> all values from 0 to minSpreadAllowable must be covered by the ranges described in the array.
	 *		<li> all maximumAllowableSpread values must be leq maxLegalAllowableSpread
	 *  </ul>
	 *
	 *  @param structArray - the EPWStruct array which should fully described the price range [0:minSpreadRequired)
	 *  @param minSpreadRequired - the <code>structArray</code> must fully cover [0:minSpreadRequired).
	 *  @param maxLegalAllowableSpread - ignored if negative.  All EPWStruct.maximumAllowableSpread values must
	 *		be leq this value.
	 *  @exception DataValidationException - thrown if any validation conditions are violated.
	 */
	static public void validateEPW(EPWStruct[] structArray, double minSpreadRequired, double maxLegalAllowableSpread)
		throws DataValidationException
	{
		int indexOf0MinBid = -1;	// -1 == not found
		int indexOfMaxBid = -1; 	// -1 == not found
		double maxBidValue = 0.0;	// used to find max maxBid value

		// Preliminary validation: check everything except overlap and full range coverage.  Also find the min/max struct elements.
		//
		for (int i=0; i < structArray.length; i++)
		{
			EPWStruct epw = structArray[i];
			if (epw.minimumBidRange < 0 || epw.maximumBidRange < 0 || epw.maximumAllowableSpread < 0)
			{
				throw ExceptionBuilder.dataValidationException("Negative value in EPWStruct["+i+"]", INVALID_PRICE);
			}
			if (epw.minimumBidRange >= epw.maximumBidRange)
			{
				throw ExceptionBuilder.dataValidationException("minBid >= maxBid in EPWStruct["+i+"]", INVALID_PRICE);
			}
			if (maxLegalAllowableSpread >= 0 && epw.maximumAllowableSpread > maxLegalAllowableSpread)
			{
				throw ExceptionBuilder.dataValidationException("max allowable " + epw.maximumAllowableSpread + " > max legal " + maxLegalAllowableSpread + " in EPWStruct["+i+"]", INVALID_PRICE);
			}
			if (epw.minimumBidRange == 0.0)
			{
				indexOf0MinBid = i;
			}
			if (epw.maximumBidRange > maxBidValue)
			{
				indexOfMaxBid = i;
				maxBidValue = epw.maximumBidRange;
			}
		}

		if (indexOf0MinBid < 0)
		{
			throw ExceptionBuilder.dataValidationException("No minBid == 0.0 found in array", INVALID_PRICE);
		}
		if (maxBidValue < minSpreadRequired)
		{
			throw ExceptionBuilder.dataValidationException("struct array does not cover miminum required range of " + minSpreadRequired, INVALID_PRICE);
		}

		// a more complicated procedure: check for overlapping ranges & ensure that there are no intra-range gaps.
		//
		int index = indexOf0MinBid; // start from min == 0.0, and continue until we reach indexOfMaxBid.
		while (index != indexOfMaxBid)
		{
			EPWStruct currEPW = structArray[index];
			int nextIndex = -1;
			for (int i=0; i < structArray.length; i++)
			{
				if (i != index)
				{
					EPWStruct epw = structArray[i];
					if (epw.minimumBidRange == currEPW.maximumBidRange) // we have a candidate for the next index.
					{
						nextIndex = i; // don't break out of the "for" loop since we still want to check for overlapping ranges.
					}
					if (!(currEPW.maximumBidRange <= epw.minimumBidRange || currEPW.minimumBidRange >= epw.maximumBidRange))
					{
						throw ExceptionBuilder.dataValidationException("Ranges (" + currEPW.minimumBidRange + ":" + currEPW.maximumBidRange + ") " +
						                                               "and (" +epw.minimumBidRange + ":" + epw.maximumBidRange + ") overlap.", INVALID_PRICE);
					}
				}
			}
			if (nextIndex < 0)
			{
				throw ExceptionBuilder.dataValidationException("intra range gap starting at " + currEPW.maximumBidRange, INVALID_PRICE);
			}
			index = nextIndex;
		}
	}

	/**
	 *  Return the maximum allowable spread value for the given value in the EPW array provided.
	 *
	 *  @param value - the value to find the max allowable spread for.
	 *  @param defaultMaxAllowable - if the value does not fall into any of the minBid:maxBid ranges
	 *		in the <code>structArray</code>, then this value will be returned.
	 *  @param structArray - the <code>EPWStruct</code> to use to find the max allowable pread value.
	 *
	 *  @return the max allowable spread for the given value, or <code>defaultMaxAllowable</code> if
	 *		<code>value</code> does not fall into the ranges of <code>structArray</code>.
	 */
	static public double getMaximumAllowableSpread(double value, double defaultMaxAllowable, EPWStruct[] structArray)
	{
		int indexOfMaxMaxBid = -1;
		double maxMaxBid = 0.0;
		
		value = Math.abs(value);
		
		for (int i=0; i < structArray.length; i++)
		{
			EPWStruct epw = structArray[i];
			if (epw.minimumBidRange <= value && value < epw.maximumBidRange)
			{
				return epw.maximumAllowableSpread;
			}
			if (epw.maximumBidRange > maxMaxBid)
			{
				indexOfMaxMaxBid = i;
				maxMaxBid = epw.maximumBidRange;
			}
		}
		if (indexOfMaxMaxBid >= 0 && value == maxMaxBid) // allow the exact max value for the largest startArray value.
		{
			return structArray[indexOfMaxMaxBid].maximumAllowableSpread;
		}
		return defaultMaxAllowable;
	}

    static public String toString(EPWStruct struct)
    {
        if(struct == null)
        {
            return " NULL";
        }

        StringBuilder buf = new StringBuilder(150);
        buf.append(" minimumBidRange: ")
        .append(struct.minimumBidRange)
        .append(" maximumBidRange: ")
        .append(struct.maximumBidRange)
        .append(" maximumAllowableSpread: ")
        .append(struct.maximumAllowableSpread);
        return buf.toString();
    }

}
