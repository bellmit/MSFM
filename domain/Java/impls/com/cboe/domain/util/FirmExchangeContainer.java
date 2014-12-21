package com.cboe.domain.util;

public class FirmExchangeContainer extends Object {
    private String firmAcronym;
    private String exchangeAcronym;
    private int hashcode;

    /**
      * Sets the internal fields to the passed values
      */
    public FirmExchangeContainer(String firmAcronym, String exchangeAcronym) {
		this.firmAcronym = firmAcronym;
		this.exchangeAcronym = exchangeAcronym;
		hashcode = firmAcronym.hashCode()+exchangeAcronym.hashCode();
    }
    public String getFirmAcronym()
    {
        return firmAcronym;
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
        if ((obj != null) && (obj instanceof FirmExchangeContainer))
        {
            String firmAcr = ((FirmExchangeContainer)obj).getFirmAcronym();
            String exchangeAcr = ((FirmExchangeContainer)obj).getExchangeAcronym();
            return (this.firmAcronym.equals(firmAcr)
                    &&  this.exchangeAcronym.equals(exchangeAcr)
                    );

        }
        return false;
    }
}
