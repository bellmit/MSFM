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


public enum MarketDataAwayExchanges
{
    UNSPECIFIED(0, "UNKNWN", (int)'_'),  //'-' will be in the range of 'A' to 'z'(lower case).
    AMEX(1, com.cboe.idl.cmiConstants.ExchangeStrings.AMEX, (int)'A'),
    BOX(2, com.cboe.idl.cmiConstants.ExchangeStrings.BOX, (int)'B'),
    ISE(3, com.cboe.idl.cmiConstants.ExchangeStrings.ISE, (int)'I'),
    NASDQ(4, com.cboe.idl.cmiConstants.ExchangeStrings.NASDAQ, (int)'Q'),
    NYSE(5, com.cboe.idl.cmiConstants.ExchangeStrings.NYSE, (int)'N'),
    PHLX(6, com.cboe.idl.cmiConstants.ExchangeStrings.PHLX, (int)'X'),
    PSE(7, com.cboe.idl.cmiConstants.ExchangeStrings.PSE, (int)'P'),
    CBOE(8, com.cboe.idl.cmiConstants.ExchangeStrings.CBOE, (int)'C'),
    CBOE2(9, com.cboe.idl.cmiConstants.ExchangeStrings.CBOE2, (int)'W'),
    BATS(10, com.cboe.idl.cmiConstants.ExchangeStrings.BATS, (int)'Z'),
    BSE(11, com.cboe.idl.cmiConstants.ExchangeStrings.BSE, (int)'b'),
    CHX(12, com.cboe.idl.cmiConstants.ExchangeStrings.CHX, (int)'c'),
    NSX(13, com.cboe.idl.cmiConstants.ExchangeStrings.NSX, (int)'d'),
    NASD(14, com.cboe.idl.cmiConstants.ExchangeStrings.NASD, (int)'e'),
    ONTD(15, "ONTD", (int)'f'),
    EDGA(16, "EDGA", (int)'g'),
    EDGX(17, "EDGX", (int)'h'),
    TRAC(18, "TRAC", (int)'i'),
    CBOEW(19, "CBOEW", (int) 'j'),
    NBBO(20, "NBBO", (int) 'k'),
    EMPTYSTRING(21, "", (int) 'z')
    ;

    public final int exchangeId;
    public final String exchangeString;
    public final int exchangeChar;
    private static MarketDataAwayExchanges[] enumValues = MarketDataAwayExchanges.values();

    MarketDataAwayExchanges(int exchangeId, String exchangeString)
    {
        this.exchangeId = exchangeId;
        this.exchangeString = exchangeString;
        
        /* default exchange char to 0 */
        this.exchangeChar = (int)'0';
    }

    MarketDataAwayExchanges(int exchangeId, String exchangeString, int exchangeChar)
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
    public static MarketDataAwayExchanges findLinkageExchange(int exchangeId)
    {
        MarketDataAwayExchanges retVal = MarketDataAwayExchanges.UNSPECIFIED;
        for(MarketDataAwayExchanges tmpExchangeId : enumValues)
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
    public static MarketDataAwayExchanges findLinkageExchange(String exchangeString)
    {
        if(exchangeString != null)
        {
            exchangeString = exchangeString.trim();
        }
        
        MarketDataAwayExchanges retVal = MarketDataAwayExchanges.UNSPECIFIED;
        for(MarketDataAwayExchanges tmpExchangeString : enumValues)
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
    public static MarketDataAwayExchanges findLinkageExchange(char exchChar)
    {
        int castedExChar = (int)exchChar;
        MarketDataAwayExchanges retVal = MarketDataAwayExchanges.UNSPECIFIED;
        for(MarketDataAwayExchanges tmpExchangeChar : enumValues)
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
