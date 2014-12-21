//
// -----------------------------------------------------------------------------------
// Source file: SessionProductClassComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.product.SessionProductClass;

import com.cboe.presentation.common.formatters.ProductTypes;

public class SessionProductClassComparator implements Comparator
{
    public SessionProductClassComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        int result = 0;

        if( o1 != o2 )
        {
            SessionProductClass o1ProductClass = ( SessionProductClass ) o1;
            SessionProductClass o2ProductClass = ( SessionProductClass ) o2;

            if(o1ProductClass.isDefaultProductClass() || o1ProductClass.isAllSelectedProductClass())
            {
                if( o2ProductClass.isDefaultProductClass() || o2ProductClass.isAllSelectedProductClass() )
                {
                    String o1TradingSession = o1ProductClass.getTradingSessionName();
                    String o2TradingSession = o2ProductClass.getTradingSessionName();
                    result = o1TradingSession.compareTo(o2TradingSession);
                }
                else
                {
                    result = -1;
                }
            }
            else if(o2ProductClass.isDefaultProductClass() || o2ProductClass.isAllSelectedProductClass())
            {
                if( o1ProductClass.isDefaultProductClass() || o1ProductClass.isAllSelectedProductClass() )
                {
                    String o1TradingSession = o1ProductClass.getTradingSessionName();
                    String o2TradingSession = o2ProductClass.getTradingSessionName();
                    result = o1TradingSession.compareTo(o2TradingSession);
                }
                else
                {
                    result = 1;
                }
            }
            else
            {
                result = o1ProductClass.getClassSymbol().compareTo(o2ProductClass.getClassSymbol());

                if( result == 0 )
                {
                    if( o1ProductClass.isDefaultSession())
                    {
                        result = -1;
                    }
                    else if( o2ProductClass.isDefaultSession() )
                    {
                        result = 1;
                    }
                    else
                    {
                        String o1TradingSession = o1ProductClass.getTradingSessionName();
                        String o2TradingSession = o2ProductClass.getTradingSessionName();
                        result = o1TradingSession.compareTo(o2TradingSession);

                        if(result == 0)
                        {
                            short o1ProductType = o1ProductClass.getProductType();
                            short o2ProductType = o2ProductClass.getProductType();

                            String o1ProductTypeString = ProductTypes.toString(o1ProductType);
                            String o2ProductTypeString = ProductTypes.toString(o2ProductType);

                            result = o1ProductTypeString.compareTo(o2ProductTypeString);
                        }
                    }
                }
            }
        }
        return result;
    }
}
