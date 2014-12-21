/*
 * Created on May 2, 2005
 */
package com.cboe.domain.util;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.tradingProperty.ExchangePrescribedWidth;

import com.cboe.util.ExceptionBuilder;

/**
 * @author saborm
 *
 * TODO What is this type for?
 */
public class OpeningExchangePrescribedWidthHelper
{
    private static final int INVALID_PRICE = com.cboe.idl.cmiErrorCodes.DataValidationCodes.INVALID_PRICE;

    /**
     *  Validates that the structArray's values fully describe the spread.
     *  The min:max pairs should 'chain' to cover the minimum required range: ex,
     *  <pre>0:0.5, 0.5:1.5, 1.5:3</pre> is a legal sequence of values if minSpreadRequired is leq 3.
     *  The values are not required to be in ascending order.
     *
     *  <ul>
     *      <li> all values must be positive
     *      <li> for an ExchangePrescribedWidth, min must be less than max
     *      <li> no [minBid:maxBid) range can overlap with another.
     *      <li> all values from 0 to minSpreadAllowable must be covered by the ranges described in the array.
     *      <li> all maximumAllowableSpread values must be leq maxLegalAllowableSpread
     *  </ul>
     *
     *  @param structArray - the ExchangePrescribedWidth array which should fully described the price range [0:minSpreadRequired)
     *  @param minSpreadRequired - the <code>structArray</code> must fully cover [0:minSpreadRequired).
     *  @param maxLegalAllowableSpread - ignored if negative.  All ExchangePrescribedWidth.maximumAllowableSpread values must
     *      be leq this value.
     *  @exception DataValidationException - thrown if any validation conditions are violated.
     */
    static public void validateEPW(ExchangePrescribedWidth[] structArray, double minSpreadRequired, double maxLegalAllowableSpread)
        throws DataValidationException
    {
        int indexOf0MinBid = -1;    // -1 == not found
        int indexOfMaxBid = -1;     // -1 == not found
        double maxBidValue = 0.0;   // used to find max maxBid value

        // Preliminary validation: check everything except overlap and full range coverage.  Also find the min/max struct elements.
        //
        for (int i=0; i < structArray.length; i++)
        {
            ExchangePrescribedWidth epw = structArray[i];
            if (epw.getMinimumBidRange() < 0 || epw.getMaximumBidRange() < 0 || epw.getMaximumAllowableSpread() < 0)
            {
                throw ExceptionBuilder.dataValidationException("Negative value in ExchangePrescribedWidth["+i+"]", INVALID_PRICE);
            }
            if (epw.getMinimumBidRange() >= epw.getMaximumBidRange())
            {
                throw ExceptionBuilder.dataValidationException("minBid >= maxBid in ExchangePrescribedWidth["+i+"]", INVALID_PRICE);
            }
            if (maxLegalAllowableSpread >= 0 && epw.getMaximumAllowableSpread() > maxLegalAllowableSpread)
            {
                throw ExceptionBuilder.dataValidationException("max allowable " + epw.getMaximumAllowableSpread() + " > max legal " + maxLegalAllowableSpread + " in ExchangePrescribedWidth["+i+"]", INVALID_PRICE);
            }
            if (epw.getMinimumBidRange() == 0.0)
            {
                indexOf0MinBid = i;
            }
            if (epw.getMaximumBidRange() > maxBidValue)
            {
                indexOfMaxBid = i;
                maxBidValue = epw.getMaximumBidRange();
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
            ExchangePrescribedWidth currEPW = structArray[index];
            int nextIndex = -1;
            for (int i=0; i < structArray.length; i++)
            {
                if (i != index)
                {
                    ExchangePrescribedWidth epw = structArray[i];
                    if (epw.getMinimumBidRange() == currEPW.getMaximumBidRange()) // we have a candidate for the next index.
                    {
                        nextIndex = i; // don't break out of the "for" loop since we still want to check for overlapping ranges.
                    }
                    if (!(currEPW.getMaximumBidRange() <= epw.getMinimumBidRange() || currEPW.getMinimumBidRange() >= epw.getMaximumBidRange()))
                    {
                        throw ExceptionBuilder.dataValidationException("Ranges (" + currEPW.getMinimumBidRange() + ":" + currEPW.getMaximumBidRange() + ") " +
                                                                       "and (" +epw.getMinimumBidRange() + ":" + epw.getMaximumBidRange() + ") overlap.", INVALID_PRICE);
                    }
                }
            }
            if (nextIndex < 0)
            {
                throw ExceptionBuilder.dataValidationException("intra range gap starting at " + currEPW.getMaximumBidRange(), INVALID_PRICE);
            }
            index = nextIndex;
        }
    }

    /**
     *  Return the maximum allowable spread value for the given value in the EPW array provided.
     *
     *  @param value - the value to find the max allowable spread for.
     *  @param defaultMaxAllowable - if the value does not fall into any of the minBid:maxBid ranges
     *      in the <code>structArray</code>, then this value will be returned.
     *  @param structArray - the <code>ExchangePrescribedWidth</code> to use to find the max allowable pread value.
     *
     *  @return the max allowable spread for the given value, or <code>defaultMaxAllowable</code> if
     *      <code>value</code> does not fall into the ranges of <code>structArray</code>.
     */
    static public double getMaximumAllowableSpread(double value, double defaultMaxAllowable, ExchangePrescribedWidth[] structArray)
    {
        int indexOfMaxMaxBid = -1;
        double maxMaxBid = 0.0;
        for (int i=0; i < structArray.length; i++)
        {
            ExchangePrescribedWidth epw = structArray[i];
            if (epw.getMinimumBidRange() <= value && value < epw.getMaximumBidRange())
            {
                return epw.getMaximumAllowableSpread();
            }
            if (epw.getMaximumBidRange() > maxMaxBid)
            {
                indexOfMaxMaxBid = i;
                maxMaxBid = epw.getMaximumBidRange();
            }
        }
        if (indexOfMaxMaxBid >= 0 && value == maxMaxBid) // allow the exact max value for the largest startArray value.
        {
            return structArray[indexOfMaxMaxBid].getMaximumAllowableSpread();
        }
        return defaultMaxAllowable;
    }

    static public String toString(ExchangePrescribedWidth struct)
    {
        if(struct == null)
        {
            return " NULL";
        }

        StringBuilder buf = new StringBuilder(100);
        buf.append(" minimumBidRange: " )
        .append(struct.getMinimumBidRange())
        .append(" maximumBidRange: ")
        .append(struct.getMaximumBidRange())
        .append(" maximumAllowableSpread: ")
        .append(struct.getMaximumAllowableSpread());
        return buf.toString();
    }


}
