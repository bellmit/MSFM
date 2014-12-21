package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataDispatcherHomeImpl.java
 *
 * @author Dmitry Volpyansky
 * @author Vivek Beniwal
 *
 */

import java.util.*;

import com.cboe.cfix.startup.*;
import com.cboe.cfix.util.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;

public class CfixMarketDataDispatcherHomeImpl implements CfixMarketDataDispatcherHome
{

    protected Map sessionMap = new HashMap(89);

    //todo - VivekB: We will use new maps per subscription type - we should look into adjusting size from config params.
    /*
    Vivek : New maps are per subscription type. We can have more granularity about subscriptions, on Tourist lookup.
    Assume 300 unique class subscriptions (not user and class, but class alone) - load factor is 0.75 ==> initial size = 800
    More than 600 unique subscriptions of any kind will lead to re-hashing - an expensive operation. This should do for now.
    */

    protected Map sessionMapCM          = new HashMap(800);
    protected Map sessionMapEOP         = new HashMap(600);
    protected Map sessionMapTicker      = new HashMap(600);
    protected Map sessionMapRecap       = new HashMap(600);
    protected Map sessionMapNBBO        = new HashMap(8);
    protected Map sessionMapBD          = new HashMap(30);
    protected Map sessionMapBDUpdate    = new HashMap(30);

    protected static final CfixMarketDataDispatcherHomeImpl instance = new CfixMarketDataDispatcherHomeImpl();

    private CfixMarketDataDispatcherHomeImpl()
    {

    }

    public static CfixMarketDataDispatcherHomeImpl getInstance()
    {
        return instance;
    }

    public CfixMarketDataDispatcherIF create(SessionClassStruct sessionClassStruct, int marketDataType)
    {
        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = internal_find(sessionClassStruct.sessionName, marketDataType);
        if (cfixMarketDataDispatcher == null)
        {
            synchronized(CfixMarketDataDispatcherHomeImpl.class)
            {
                cfixMarketDataDispatcher = internal_find(sessionClassStruct.sessionName, marketDataType);
                if (cfixMarketDataDispatcher == null)
                {
                    String dispatcherName    = makeCfixMarketDataDispatcherName(sessionClassStruct.sessionName, marketDataType);
                    cfixMarketDataDispatcher = internal_createCfixMarketDataDispatcherName(dispatcherName, marketDataType);

                    try
                    {
                        int debugFlags = DebugFlagBuilder.buildDispatcherDebugFlags(CfixHomeImpl.cfixProperties.getProperty("defaults.marketDataDispatcher.debugFlags"));
                        if (debugFlags != 0)
                        {
                            cfixMarketDataDispatcher.setDebugFlags(debugFlags);
                        }
                    }
                    catch (Exception ex)
                    {

                    }

                    try
                    {
                        sessionMap.put(dispatcherName, cfixMarketDataDispatcher);
                    }
                    catch (Exception ex)
                    {
                        Log.exception(ex);
                    }
                }
            }
        }

        return cfixMarketDataDispatcher;
    }

    public CfixMarketDataDispatcherIF create(SessionProductStruct sessionProductStruct, int marketDataType)
    {
        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = internal_find(sessionProductStruct.sessionName, marketDataType);
        if (cfixMarketDataDispatcher == null)
        {
            synchronized(CfixMarketDataDispatcherHomeImpl.class)
            {
                cfixMarketDataDispatcher = internal_find(sessionProductStruct.sessionName, marketDataType);
                if (cfixMarketDataDispatcher == null)
                {
                    String dispatcherName    = makeCfixMarketDataDispatcherName(sessionProductStruct.sessionName, marketDataType);
                    cfixMarketDataDispatcher = internal_createCfixMarketDataDispatcherName(dispatcherName, marketDataType);

                    try
                    {
                        sessionMap.put(dispatcherName, cfixMarketDataDispatcher);
                    }
                    catch (Exception ex)
                    {
                        Log.exception(ex);
                    }
                }
            }
        }

        return cfixMarketDataDispatcher;
    }

    public CfixMarketDataDispatcherIF find(SessionProductStruct sessionProductStruct, int marketDataType)
    {
        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = internal_find(sessionProductStruct.sessionName, marketDataType);
        if (cfixMarketDataDispatcher == null)
        {
            return create(sessionProductStruct, marketDataType);
        }

        return cfixMarketDataDispatcher;
    }

    public CfixMarketDataDispatcherIF find(SessionClassStruct sessionClassStruct, int marketDataType)
    {
        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = internal_find(sessionClassStruct.sessionName, marketDataType);
        if (cfixMarketDataDispatcher == null)
        {
            return create(sessionClassStruct, marketDataType);
        }

        return cfixMarketDataDispatcher;
    }

    // Used by Tourist to collect stats on dispatchers.
    public int size()
    {
        return sessionMap.size();
    }

    public void accept(CfixMarketDataDispatcherVisitor cfixMarketDataDispatcherVisitor) throws Exception
    {
        for (Iterator iterator = sessionMap.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                ((CfixMarketDataDispatcherIF) ((Map.Entry) iterator.next()).getValue()).accept(cfixMarketDataDispatcherVisitor);
            }
            catch (Exception ex)
            {
                if (!cfixMarketDataDispatcherVisitor.exceptionHappened(ex))
                {
                    break;
                }
            }
        }
    }

    protected CfixMarketDataDispatcherIF internal_find(String sessionName, int dispatchType)
    {
        String dispatcherName = makeCfixMarketDataDispatcherName(sessionName, dispatchType);

        return (CfixMarketDataDispatcherIF) sessionMap.get(dispatcherName);
    }

    public static String makeCfixMarketDataDispatcherName(String sessionName, int marketDataType)
    {
        StringBuilder name = new StringBuilder(sessionName.length()+30);
        switch (marketDataType)
        {
            case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               return name.append(sessionName).append('{').append(CfixMarketDataTickerDispatcherImpl.MARKET_DATA_TYPE_NAME              ).append('}').toString();
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        return name.append(sessionName).append('{').append(CfixMarketDataCurrentMarketDispatcherImpl.MARKET_DATA_TYPE_NAME       ).append('}').toString();
            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: return name.append(sessionName).append('{').append(CfixMarketDataExpectedOpeningPriceDispatcherImpl.MARKET_DATA_TYPE_NAME).append('}').toString();
            case CfixMarketDataDispatcherIF.MarketDataType_Recap:                return name.append(sessionName).append('{').append(CfixMarketDataRecapDispatcherImpl.MARKET_DATA_TYPE_NAME               ).append('}').toString();
            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 return name.append(sessionName).append('{').append(CfixMarketDataNbboDispatcherImpl.MARKET_DATA_TYPE_NAME                ).append('}').toString();
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            return name.append(sessionName).append('{').append(CfixMarketDataBookDepthDispatcherImpl.MARKET_DATA_TYPE_NAME           ).append('}').toString();
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      return name.append(sessionName).append('{').append(CfixMarketDataBookDepthUpdateDispatcherImpl.MARKET_DATA_TYPE_NAME     ).append('}').toString();
        }

        return null;
    }

    protected static CfixMarketDataDispatcherIF internal_createCfixMarketDataDispatcherName(String dispatcherName, int marketDataType)
    {
        switch (marketDataType)
        {
            case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               return new CfixMarketDataTickerDispatcherImpl(dispatcherName);
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        return new CfixMarketDataCurrentMarketDispatcherImpl(dispatcherName);
            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: return new CfixMarketDataExpectedOpeningPriceDispatcherImpl(dispatcherName);
            case CfixMarketDataDispatcherIF.MarketDataType_Recap:                return new CfixMarketDataRecapDispatcherImpl(dispatcherName);
            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 return new CfixMarketDataNbboDispatcherImpl(dispatcherName);
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            return new CfixMarketDataBookDepthDispatcherImpl(dispatcherName);
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      return new CfixMarketDataBookDepthUpdateDispatcherImpl(dispatcherName);
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------------------------

    // Following size methods for MDX enabled CFIX Tourist
    public int sizeCM()
    {
        return sessionMapCM.size();
    }

    public int sizeEOP()
    {
        return sessionMapEOP.size();
    }

    public int sizeTicker()
    {
        return sessionMapTicker.size();
    }

    public int sizeRecap()
    {
        return sessionMapRecap.size();
    }

    public int sizeNBBO()
    {
        return sessionMapNBBO.size();
    }

    public int sizeBD()
    {
        return sessionMapBD.size();
    }

    public int sizeBDUpdate()
    {
        return sessionMapBDUpdate.size();
    }

    public CfixMDXMarketDataDispatcherIF create(SessionClassStruct sessionClassStruct, int classKey, int marketDataType)
    {
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = internal_find(sessionClassStruct.sessionName, classKey, marketDataType);
        if (cfixMDXMarketDataDispatcher == null)
        {
            synchronized(CfixMarketDataDispatcherHomeImpl.class)
            {
                cfixMDXMarketDataDispatcher = internal_find(sessionClassStruct.sessionName, classKey, marketDataType);
                if (cfixMDXMarketDataDispatcher == null)
                {
                    String dispatcherName    = makeCfixMarketDataDispatcherName(classKey, sessionClassStruct.sessionName);
                    Map specificSessionMap = this.getMarketDataSpecificSessionMap(marketDataType);

                    cfixMDXMarketDataDispatcher = internal_createCfixMarketDataDispatcherName(dispatcherName, classKey, marketDataType);

                    try
                    {
                        int debugFlags = DebugFlagBuilder.buildDispatcherDebugFlags(CfixHomeImpl.cfixProperties.getProperty("defaults.marketDataDispatcher.debugFlags"));
                        if (debugFlags != 0)
                        {
                            cfixMDXMarketDataDispatcher.setDebugFlags(debugFlags);
                        }
                    }
                    catch (Exception ex)
                    {

                    }

                    try
                    {
                        specificSessionMap.put(dispatcherName, cfixMDXMarketDataDispatcher);
                    }
                    catch (Exception ex)
                    {
                        Log.exception(ex);
                    }
                }
            }
        }

        return cfixMDXMarketDataDispatcher;
    }

    public CfixMDXMarketDataDispatcherIF create(SessionProductStruct sessionProductStruct, int classKey, int marketDataType)
    {
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = internal_find(sessionProductStruct.sessionName, classKey, marketDataType);
        if (cfixMDXMarketDataDispatcher == null)
        {
            synchronized(CfixMarketDataDispatcherHomeImpl.class)
            {
                cfixMDXMarketDataDispatcher = internal_find(sessionProductStruct.sessionName, classKey, marketDataType);
                if (cfixMDXMarketDataDispatcher == null)
                {
                    String dispatcherName    = makeCfixMarketDataDispatcherName( classKey, sessionProductStruct.sessionName);
                    Map specificSessionMap = this.getMarketDataSpecificSessionMap(marketDataType);

                    cfixMDXMarketDataDispatcher = internal_createCfixMarketDataDispatcherName(dispatcherName, classKey, marketDataType);

                    try
                    {
                        specificSessionMap.put(dispatcherName, cfixMDXMarketDataDispatcher);
                    }
                    catch (Exception ex)
                    {
                        Log.exception(ex);
                    }
                }
            }
        }

        return cfixMDXMarketDataDispatcher;
    }

    public CfixMDXMarketDataDispatcherIF find(SessionProductStruct sessionProductStruct, int classKey, int marketDataType)
    {
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = internal_find(sessionProductStruct.sessionName, classKey, marketDataType);
        if (cfixMDXMarketDataDispatcher == null)
        {
            return create(sessionProductStruct, classKey, marketDataType);
        }

        return cfixMDXMarketDataDispatcher;
    }

    public CfixMDXMarketDataDispatcherIF find(SessionClassStruct sessionClassStruct, int classKey, int marketDataType)
    {
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = internal_find(sessionClassStruct.sessionName, classKey, marketDataType);
        if (cfixMDXMarketDataDispatcher == null)
        {
            return create(sessionClassStruct, classKey, marketDataType);
        }

        return cfixMDXMarketDataDispatcher;
    }

    // todo - what is this for? - now that we have more than one map, we will need to iterate over many maps - think about this
    public void accept(CfixMDXMarketDataDispatcherVisitor cfixMDXMarketDataDispatcherVisitor) throws Exception
    {
        for (Iterator iterator = sessionMapCM.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                ((CfixMDXMarketDataDispatcherIF) ((Map.Entry) iterator.next()).getValue()).accept(cfixMDXMarketDataDispatcherVisitor);
            }
            catch (Exception ex)
            {
                if (!cfixMDXMarketDataDispatcherVisitor.exceptionHappened(ex))
                {
                    break;
                }
            }
        }

        for (Iterator iterator = sessionMapEOP.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                ((CfixMDXMarketDataDispatcherIF) ((Map.Entry) iterator.next()).getValue()).accept(cfixMDXMarketDataDispatcherVisitor);
            }
            catch (Exception ex)
            {
                if (!cfixMDXMarketDataDispatcherVisitor.exceptionHappened(ex))
                {
                    break;
                }
            }
        }

        for (Iterator iterator = sessionMapTicker.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                ((CfixMDXMarketDataDispatcherIF) ((Map.Entry) iterator.next()).getValue()).accept(cfixMDXMarketDataDispatcherVisitor);
            }
            catch (Exception ex)
            {
                if (!cfixMDXMarketDataDispatcherVisitor.exceptionHappened(ex))
                {
                    break;
                }
            }
        }

        for (Iterator iterator = sessionMapRecap.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                ((CfixMDXMarketDataDispatcherIF) ((Map.Entry) iterator.next()).getValue()).accept(cfixMDXMarketDataDispatcherVisitor);
            }
            catch (Exception ex)
            {
                if (!cfixMDXMarketDataDispatcherVisitor.exceptionHappened(ex))
                {
                    break;
                }
            }
        }

        for (Iterator iterator = sessionMapNBBO.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                ((CfixMDXMarketDataDispatcherIF) ((Map.Entry) iterator.next()).getValue()).accept(cfixMDXMarketDataDispatcherVisitor);
            }
            catch (Exception ex)
            {
                if (!cfixMDXMarketDataDispatcherVisitor.exceptionHappened(ex))
                {
                    break;
                }
            }
        }

        for (Iterator iterator = sessionMapBD.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                ((CfixMDXMarketDataDispatcherIF) ((Map.Entry) iterator.next()).getValue()).accept(cfixMDXMarketDataDispatcherVisitor);
            }
            catch (Exception ex)
            {
                if (!cfixMDXMarketDataDispatcherVisitor.exceptionHappened(ex))
                {
                    break;
                }
            }
        }

        for (Iterator iterator = sessionMapBDUpdate.entrySet().iterator(); iterator.hasNext(); )
        {
            try
            {
                ((CfixMDXMarketDataDispatcherIF) ((Map.Entry) iterator.next()).getValue()).accept(cfixMDXMarketDataDispatcherVisitor);
            }
            catch (Exception ex)
            {
                if (!cfixMDXMarketDataDispatcherVisitor.exceptionHappened(ex))
                {
                    break;
                }
            }
        }
    }

    protected CfixMDXMarketDataDispatcherIF internal_find(String sessionName, int classKey, int dispatchType)
    {
        String dispatcherName = makeCfixMarketDataDispatcherName(classKey, sessionName);
        Map specificSessionMap = this.getMarketDataSpecificSessionMap(dispatchType);

        return (CfixMDXMarketDataDispatcherIF) specificSessionMap.get(dispatcherName);
    }

    public static String makeCfixMarketDataDispatcherName(int classKey, String sessionName)
    {
        return sessionName + '_' + classKey;
    }

    protected Map getMarketDataSpecificSessionMap(int marketDataType)
    {
        switch (marketDataType)
        {
            case CfixMDXMarketDataDispatcherIF.MarketDataType_Ticker:               return this.sessionMapTicker;
            case CfixMDXMarketDataDispatcherIF.MarketDataType_CurrentMarket:        return this.sessionMapCM;
            case CfixMDXMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: return this.sessionMapEOP;
            case CfixMDXMarketDataDispatcherIF.MarketDataType_Recap:                return this.sessionMapRecap;
            case CfixMDXMarketDataDispatcherIF.MarketDataType_Nbbo:                 return this.sessionMapNBBO;
            case CfixMDXMarketDataDispatcherIF.MarketDataType_BookDepth:            return this.sessionMapBD;
            case CfixMDXMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      return this.sessionMapBDUpdate;
        }

        return null;
    }

    protected static CfixMDXMarketDataDispatcherIF internal_createCfixMarketDataDispatcherName(String dispatcherName, int classKey, int marketDataType)
    {
        switch (marketDataType)
        {
            case CfixMDXMarketDataDispatcherIF.MarketDataType_Ticker:               return new CfixMDXMarketDataTickerDispatcherImpl(dispatcherName, classKey);
            case CfixMDXMarketDataDispatcherIF.MarketDataType_CurrentMarket:        return new CfixMDXMarketDataCurrentMarketDispatcherImpl(dispatcherName, classKey);
            case CfixMDXMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: return new CfixMDXMarketDataExpectedOpeningPriceDispatcherImpl(dispatcherName, classKey);
            case CfixMDXMarketDataDispatcherIF.MarketDataType_Recap:                return new CfixMDXMarketDataRecapDispatcherImpl(dispatcherName, classKey);
            case CfixMDXMarketDataDispatcherIF.MarketDataType_Nbbo:                 return new CfixMDXMarketDataNbboDispatcherImpl(dispatcherName, classKey);
            case CfixMDXMarketDataDispatcherIF.MarketDataType_BookDepth:            return new CfixMDXMarketDataBookDepthDispatcherImpl(dispatcherName, classKey);
            case CfixMDXMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      return new CfixMDXMarketDataBookDepthUpdateDispatcherImpl(dispatcherName, classKey);
        }

        return null;
    }

}