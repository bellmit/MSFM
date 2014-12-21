package com.cboe.cfix.cas.marketData;

/**
 * CfixStrikePriceHelperImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.client.util.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.interfaces.cfix.*;

public final class CfixStrikePriceHelperImpl implements CfixStrikePriceHelper
{
    protected Map strikePriceTable;

    public void setStrikePriceTable(Map strikePriceTable)
    {
        this.strikePriceTable = strikePriceTable;
    }

    public Map getStrikePriceTable()
    {
        return strikePriceTable;
    }

    public int getStrikePriceAdjustment(String classSymbol)
    {
        return IntegerHelper.zeroIfNull((Integer) strikePriceTable.get(classSymbol));
    }

    public PriceStruct adjustStrikePrice(String classSymbol, PriceStruct strikePriceToAdjust)
    {
        Integer adjustment = (Integer) strikePriceTable.get(classSymbol);

        if (adjustment == null || adjustment.intValue() == 0)
        {
            return strikePriceToAdjust;
        }

        return PriceHelper.createPriceStruct(strikePriceToAdjust.whole + adjustment.intValue(), strikePriceToAdjust.fraction);
    }
}
