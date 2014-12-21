package com.cboe.domain.util;

import com.cboe.interfaces.domain.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;

/**
 * This is a hashable container class for our CboeID key struct.
 * @author Keith A. Korecky
 */

public class CboeOrderIdStructContainer implements BaseOrderIdStructContainer
{
    private int highCboeId;
    private int lowCboeId;
    private int hashcode;

    /**
      * Sets the internal fields to the passed values ontained by the order
      * identification data structure.
      */
    public CboeOrderIdStructContainer(OrderIdStruct orderKey)
    {
        highCboeId            = orderKey.highCboeId;
        lowCboeId             = orderKey.lowCboeId;
        hashcode = ( ( highCboeId + lowCboeId ) / 5 );
    }

    /**
      * Sets the internal fields to the passed values ontained by the order
      * identification data structure.
      */
    public CboeOrderIdStructContainer(int highCboeUd, int lowCboeId )
    {
        highCboeId  = highCboeId;
        lowCboeId   = lowCboeId;
        hashcode = ( ( highCboeId + lowCboeId ) / 5 );
    }

    /**
      * The equals for the key.
      * @param obj Object
      * @return boolean
      */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof CboeOrderIdStructContainer))
        {
            return (    (highCboeId == ( ((CboeOrderIdStructContainer) obj).highCboeId))
                    &&  (lowCboeId == ( ((CboeOrderIdStructContainer) obj).lowCboeId))
                    );
         }
        return false;
    }

    /**
      * The hashCode for the key.
      * @return int
      */
    public int hashCode()
    {
        return hashcode;
    }

    /**
      * The toString() for the key.
      * @return String
      */
    public String toString()
    {
        StringBuilder buf = new StringBuilder(20);

        buf.append("h=")
        .append(highCboeId)
        .append(": l=")
        .append(lowCboeId);

        return buf.toString();
    }

    public boolean isValid()
    {
        return true;
        /*
        return (( highCboeId != 0 ) || ( lowCboeId != 0 ));
        */
    }
}
