package com.cboe.domain.util;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;

public class FirmNumberContainer extends Object {
    private String firmNumber;
    private String exchangeAcronym;
    private int hashcode;

    /**
      * Sets the internal fields to the passed values
      */
    public FirmNumberContainer(ExchangeFirmStruct exchangeFirm) {
		this.firmNumber = exchangeFirm.firmNumber;
		this.exchangeAcronym = exchangeFirm.exchange;
		hashcode = firmNumber.hashCode()+exchangeAcronym.hashCode();
    }
    public String getFirmNumber()
    {
        return firmNumber;
    }

    public String getExchangeAcronym()
    {
        return exchangeAcronym;
    }

    public int hashCode()
    {
        return hashcode;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof FirmNumberContainer))
        {
            String firm = ((FirmNumberContainer)obj).getFirmNumber();
            String exchangeAcr = ((FirmNumberContainer)obj).getExchangeAcronym();
            return (this.firmNumber.equals(firm)
                    &&  this.exchangeAcronym.equals(exchangeAcr)
                    );

        }
        return false;
    }
}
