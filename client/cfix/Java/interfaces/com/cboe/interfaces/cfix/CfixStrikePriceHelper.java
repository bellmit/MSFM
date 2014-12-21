package com.cboe.interfaces.cfix;

/**
 * CfixStrikePriceHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.idl.cmiUtil.*;

public interface CfixStrikePriceHelper
{
    public int         getStrikePriceAdjustment(String classSymbol);
    public PriceStruct adjustStrikePrice(String classSymbol, PriceStruct strikePriceToAdjust);
    public void        setStrikePriceTable(Map strikePriceTable);
    public Map         getStrikePriceTable();
}
