package com.cboe.cfix.cas.base;

/**
 * CfixMapperFactory.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;

public final class CfixMapperFactory
{
    public static CfixMarketDataMapperIF createMarketDataMapper(String klass) throws Exception
    {
        return (CfixMarketDataMapperIF) ClassHelper.loadClassWithExceptions(klass);
    }
}
