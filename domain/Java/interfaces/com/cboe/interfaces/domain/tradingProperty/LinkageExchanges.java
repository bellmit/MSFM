//
// -----------------------------------------------------------------------------------
// Source file: LinkageExchanges.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

public enum LinkageExchanges
{
    UNSPECIFIED(0, "UNKNWN", (int)'0'),
    AMEX(1, com.cboe.idl.cmiConstants.ExchangeStrings.AMEX, (int)'A'),
    BOX(2, com.cboe.idl.cmiConstants.ExchangeStrings.BOX, (int)'B'),
    ISE(3, com.cboe.idl.cmiConstants.ExchangeStrings.ISE, (int)'I'),
    NASDQ(4, com.cboe.idl.cmiConstants.ExchangeStrings.NASDAQ, (int)'Q'),
    NYSE(5, com.cboe.idl.cmiConstants.ExchangeStrings.NYSE, (int)'N'),
    PHLX(6, com.cboe.idl.cmiConstants.ExchangeStrings.PHLX, (int)'X'),
    PSE(7, com.cboe.idl.cmiConstants.ExchangeStrings.PSE, (int)'P'),
    CBOE(8, com.cboe.idl.cmiConstants.ExchangeStrings.CBOE, (int)'C'),
    CBOE2(9, com.cboe.idl.cmiConstants.ExchangeStrings.CBOE2, (int)'W'),
    BATS(10, com.cboe.idl.cmiConstants.ExchangeStrings.BATS, (int)'Z');

    public final int exchangeId;
    public final String exchangeString;
    public final int exchangeChar;
    

    LinkageExchanges(int exchangeId, String exchangeString)
    {
        this.exchangeId = exchangeId;
        this.exchangeString = exchangeString;
        
        /* default exchange char to 0 */
        this.exchangeChar = (int)'0';
    }

    LinkageExchanges(int exchangeId, String exchangeString, int exchangeChar)
    {
        this.exchangeId = exchangeId;
        this.exchangeString = exchangeString;
        this.exchangeChar = exchangeChar;
    }
    public int getExchangeId()
    {
        return exchangeId;
    }

    public String toString()
    {
        return exchangeString;
    }

    public int getExchangeChar()
    {
        return exchangeChar;
    }


    // convenience method to provide lookup from the IDL constant to the enum
    public static LinkageExchanges findLinkageExchange(int exchangeId)
    {
        LinkageExchanges retVal = LinkageExchanges.UNSPECIFIED;
        for(LinkageExchanges tmpExchangeId : LinkageExchanges.values())
        {
            if(exchangeId == tmpExchangeId.getExchangeId())
            {
                retVal = tmpExchangeId;
                break;
            }
        }
        return retVal;
    }
    
    /**
     * Convenience method to provide lookup from name to the enum
     * @param orderLocation
     * @return
     */
    public static LinkageExchanges findLinkageExchange(String exchangeString)
    {
        LinkageExchanges retVal = LinkageExchanges.UNSPECIFIED;
        for(LinkageExchanges tmpExchangeString : LinkageExchanges.values())
        {
            if(tmpExchangeString.toString().equalsIgnoreCase(exchangeString))
            {
                retVal = tmpExchangeString;
                break;
            }
        }
        return retVal;
    }
    
    /**
     * Convenience method to provide lookup from single char exch name to the enum
     * @param orderLocation
     * @return
     */
    public static LinkageExchanges findLinkageExchange(char exchChar)
    {
        int castedExChar = (int)exchChar;
        LinkageExchanges retVal = LinkageExchanges.UNSPECIFIED;
        for(LinkageExchanges tmpExchangeChar : LinkageExchanges.values())
        {
            if(castedExChar == tmpExchangeChar.getExchangeChar())
            {
                retVal = tmpExchangeChar;
                break;
            }
        }
        return retVal;
    }

}
