package com.cboe.domain.util;

import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
/**
 * This is a hashable container class for our data struct.
 * @author Keith A. Korecky
 */
public class ExchangeAcronymContainer
{
    private String exchange;
	private String acronym;
	private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public ExchangeAcronymContainer(String exchange, String acronym)
    {
        this.exchange = exchange;
		this.acronym = acronym;
		hashCode = (exchange.hashCode() + acronym.hashCode()) >> 1;
    }

    public ExchangeAcronymContainer(ExchangeAcronymStruct exchangeAcronymStruct)
    {
        this.exchange = exchangeAcronymStruct.exchange;
        this.acronym  = exchangeAcronymStruct.acronym;
        hashCode = (exchange.hashCode() + acronym.hashCode()) >> 1;
    }
    
    public String getExchange()
    {
        return exchange;
    }

    public String getAcronym()
    {
        return acronym;
    }

    /**
     * The equals for the key.
     * this is a multipart type comparison to help with conversion
     * and cross ref. type needs.
     * allows "lookup" by just user, exchange & acronym or full key
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof ExchangeAcronymContainer))
        {
            ExchangeAcronymContainer compareObj = (ExchangeAcronymContainer)obj;
            return (exchange.equals(compareObj.exchange)
                    && acronym.equals(compareObj.acronym)
                   );
        }
        return false;
    }

    /**
     * Perform a partial comparison of this object with the ExchangeAcronymContainers argument object.
     * Only the objects' acronyms are compared. The exchanges are ignored. 
     * @param obj - the ExchangeAcronymContainer to compare to
     * @return true if acronym value is the same in both ExchangeAcronymContainers. False otherwise.
     */
    public boolean equalsIgnoringExchange(Object obj)
    {
        if ((obj != null) && (obj instanceof ExchangeAcronymContainer))
        {
            ExchangeAcronymContainer compareObj = (ExchangeAcronymContainer)obj;
            return (acronym.equals(compareObj.acronym));
        }
        return false;
    }    
    
    /**
      * The hashCode for the key.
      * @return int
      */
    public int hashCode()
    {
        return hashCode;
    }

    /**
      * The toString() for the key.
      * @return String
      */
    public String toString()
    {
        StringBuilder buf = new StringBuilder(25);
        buf.append(" Exchange: ")
        .append(exchange)
        .append(" Acronym: ")
        .append(acronym);
        return buf.toString();
    }
}
