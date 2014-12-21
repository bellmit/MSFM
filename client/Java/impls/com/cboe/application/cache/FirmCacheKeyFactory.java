package com.cboe.application.cache;

import com.cboe.interfaces.application.CacheKeyGenerator;
import com.cboe.domain.util.FirmNumberContainer;
import com.cboe.domain.util.FirmExchangeContainer;

import com.cboe.idl.firm.*;

public abstract class FirmCacheKeyFactory
{
    private static CacheKeyGenerator primaryKey = null;
    private static CacheKeyGenerator numberKey  = null;
    private static CacheKeyGenerator acronymKey = null;

    public static synchronized CacheKeyGenerator getPrimaryKey()
    {
        if (primaryKey == null)
        {
            primaryKey = new AbstractCacheKeyGenerator()
                         {
                             public Object generateKey(Object fromObject)
                             {
                                 return Integer.valueOf(((FirmStruct) fromObject).firmKey);
                             }
                         };
        }
        return primaryKey;
    }

    public static synchronized CacheKeyGenerator getNumberKey()
    {
        if (numberKey == null)
        {
            numberKey = new AbstractCacheKeyGenerator()
                         {
                             public Object generateKey(Object fromObject)
                             {
                                 FirmNumberContainer firmNumber = new FirmNumberContainer(((FirmStruct) fromObject).firmNumber);
                                 return firmNumber;
                             }
                         };
        }
        return numberKey;
    }

    public static synchronized CacheKeyGenerator getAcronymKey()
    {
        if (acronymKey == null)
        {
            acronymKey = new AbstractCacheKeyGenerator()
                         {
                             public Object generateKey(Object fromObject)
                             {
                                 FirmExchangeContainer firmExchange = new FirmExchangeContainer(((FirmStruct) fromObject).firmAcronym, ((FirmStruct) fromObject).firmNumber.exchange);
                                 return firmExchange;
                             }
                         };
        }
        return acronymKey;
    }
}
