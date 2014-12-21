//
// -----------------------------------------------------------------------------------
// Source file: ExchangeFirm.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

public class ExchangeFirm
{
    public String desc;
    public String exchangeAcronym;
    public String firmAcronym;
    public String firmNumber;

    public ExchangeFirm(String exchangeAcronym, String firmAcronym)
    {
        this.exchangeAcronym = exchangeAcronym;
        this.firmAcronym = firmAcronym;
    }

    public ExchangeFirm(String exchangeAcronym, String firmAcronym, String firmNumber)
    {
        this(exchangeAcronym, firmAcronym);
        this.firmNumber = firmNumber;
    }

    public String toString()
    {
        if(desc == null)
        {
            if(firmNumber != null && firmNumber.length() > 0)
            {
                desc = firmAcronym + ":" + firmNumber + "(" + exchangeAcronym + ")";
            }
            else
            {
                desc = firmAcronym + ":" + exchangeAcronym;
            }
        }
        return desc;
    }
}
