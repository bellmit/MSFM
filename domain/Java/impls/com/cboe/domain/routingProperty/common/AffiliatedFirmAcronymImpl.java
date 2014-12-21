//
//-----------------------------------------------------------------------------------
//Source file: AffiliatedFirmAcronymImpl.java
//
//PACKAGE: com.cboe.domain.routingProperty.common
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

package com.cboe.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.common.AffiliatedFirmAcronym;

/**
 * Wrapper for String value representing an affiliated firm number (acronym)
 */
public class AffiliatedFirmAcronymImpl implements AffiliatedFirmAcronym,Comparable<AffiliatedFirmAcronym>
{
    private String affiliatedFirmAcronym;
    private String exchange;

    public AffiliatedFirmAcronymImpl(String firm, String exchange)
    {
        affiliatedFirmAcronym = firm;
        this.exchange = exchange;
    }


    public void setFirmAcronym(String value)
    {
        affiliatedFirmAcronym = value;
    }

    public String getFirmAcronym()
    {
        return affiliatedFirmAcronym;
    }

    public String getExchangeAcronym()
    {
        return exchange;
    }

    public void setExchangeAcronym(String exchange)
    {
        this.exchange = exchange;
    }


    public String toString()
    {
        return affiliatedFirmAcronym + " (" + exchange + ") ";
    }


    public int compareTo(AffiliatedFirmAcronym o)
    {
        return getFirmAcronym().compareTo(o.getFirmAcronym());
    }

    /**
     * Determines if this object is equal to passed object. Comparison will be done on instance,
     * type, firmKey.
     * @param obj to compare this with
     * @return True if all equal, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual;

        if(this == obj)
        {
            isEqual = true;
        }
        else if(obj == null)
        {
            isEqual = false;
        }
        else if(obj instanceof AffiliatedFirmAcronym)
        {
            AffiliatedFirmAcronym castedObj = (AffiliatedFirmAcronym) obj;
            isEqual = getFirmAcronym().equals(castedObj.getFirmAcronym());
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getFirmAcronym().hashCode();
    }
}
