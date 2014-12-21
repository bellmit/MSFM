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

public class FirmClassContainer extends Object {
    private String firmNumber;
    private String exchange;
    private int classKey;
    private String displayString;
    private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public FirmClassContainer(ExchangeFirmStruct exchangeFirmStruct, int key) {
		this.firmNumber = exchangeFirmStruct.firmNumber;
		this.exchange   = exchangeFirmStruct.exchange;
        this.classKey     = key;
        hashCode = (firmNumber.hashCode() + exchange.hashCode() + classKey)/3;
    }

    public FirmClassContainer(String exchange, String firmNumber, int key) {
		this.firmNumber = firmNumber;
		this.exchange   = exchange;
        this.classKey   = key;
        hashCode = (firmNumber.hashCode() + exchange.hashCode() + classKey)/3;
    }

    public String getFirmNumber()
    {
        return firmNumber;
    }

    public String getExchange()
    {
        return exchange;
    }

    public int getClassKey()
    {
        return classKey;
    }
    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof FirmClassContainer))
        {
            String firm = ((FirmClassContainer)obj).getFirmNumber();
            String exchange = ((FirmClassContainer)obj).getExchange();
            int key = ((FirmClassContainer)obj).getClassKey();
            return (this.firmNumber.equals(firm)
                    &&  this.exchange.equals(exchange) && (this.classKey == key )
                    );

        }
        return false;
    }

    /**
      * The toString() for the key.
      * @return String
      */
    public String toString() {
        if (displayString == null)
        {
            displayString = this.exchange + ':' + this.firmNumber + ':' + classKey;
        }
        return displayString;
    }
}
