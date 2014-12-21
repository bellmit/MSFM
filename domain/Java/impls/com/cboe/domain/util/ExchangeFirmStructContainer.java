/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Mar 27, 2002
 * Time: 12:41:26 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.domain.util;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.domain.ExchangeFirmStructWrapper;

/**
 * This is a hashable container class for our ExchangeFirmStruct.
 * @author Emily Huang
 */

public class ExchangeFirmStructContainer extends Object implements ExchangeFirmStructWrapper{
    private String firmNumber;
    private String exchange;
    private String displayString;
    private int hashcode;

    /**
      * Sets the internal fields to the passed values
      */
    public ExchangeFirmStructContainer(ExchangeFirmStruct exchangeFirmStruct) {
        this.firmNumber = exchangeFirmStruct.firmNumber;
        this.exchange   = exchangeFirmStruct.exchange;
        hashcode = firmNumber.hashCode() + exchange.hashCode();
    }

    public String getFirmNumber()
    {
        return firmNumber;
    }

    public String getExchange()
    {
        return exchange;
    }

    public int hashCode()
    {
        return hashcode;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof ExchangeFirmStructContainer))
        {
            String firm = ((ExchangeFirmStructContainer)obj).getFirmNumber();
            String exchange = ((ExchangeFirmStructContainer)obj).getExchange();
            return (this.firmNumber.equals(firm)
                    &&  this.exchange.equals(exchange)
                    );

        }
        return false;
    }

    public String toString()
    {
        if (displayString == null)
        {
            displayString = this.exchange + ':' + this.firmNumber;
        }
        return displayString;
    }
}
