package com.cboe.domain.util;

import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.interfaces.domain.Price;

/**
 * Cabinet price type
 */
public class CabinetPrice extends PriceBaseImpl
{
    /**
     * Compares an object to this price.
     *
     * @return true if object is semantically equivalent to this object
     *
     * @param anObject - object to be tested
     */
    public boolean equals(Object anObject)
    {
        boolean result = false;
        // handles case where object may be a PriceSqlType wrapping a Price
        if (anObject instanceof Price)
        {
            result = ((Price) anObject).isCabinetPrice();
        }
        return result;
    }
    /**
     * Calculates hash value for this price.
     *
     * @return hash value of cabinet prices
     */
    public int hashCode()
    {
        return 0;
    }
    /**
     * Checks to see if this price is a cabinet price.
     *
     * @return true - all instances are cabinet prices
     */
    public boolean isCabinetPrice()
    {
        return true;
    }
    /**
     * Converts a price to a printable string
     *
     * @return formatted string
     */
    public String toString()
    {
        return CABINET_STRING;
    }
    /**
     * Converts this price to a price structure.
     *
     * @return price structure for this price.
     */
    public PriceStruct toStruct()
    {
        PriceStruct aStruct = new PriceStruct();
        aStruct.type = PriceTypes.CABINET;
        aStruct.whole = 0;
        aStruct.fraction = 0;
        return aStruct;
    }
}
