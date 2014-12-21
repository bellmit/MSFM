package com.cboe.cfix.util;

/**
 * OverlayPolicyFactory.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.interfaces.cfix.*;

public class OverlayPolicyFactory
{
    protected int overlayPolicy = OverlayPolicyMarketDataListIF.NEVER_OVERLAY_POLICY;

    public void setOverlayPolicy(int policy)
    {
        if (policy == OverlayPolicyMarketDataListIF.NEVER_OVERLAY_POLICY ||
            policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
        {
            overlayPolicy = policy;
        }
    }

    public void setOverlayPolicy(String policy)
    {
        if (OverlayPolicyMarketDataListIF.strALWAYS_OVERLAY_POLICY.equalsIgnoreCase(policy))
        {
            overlayPolicy = OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY;
        }
        else
        {
            overlayPolicy = OverlayPolicyMarketDataListIF.NEVER_OVERLAY_POLICY;
        }
    }

    public int getOverlayPolicy()
    {
         return overlayPolicy;
    }

    public String getOverlayPolicyAsString()
    {
        if (overlayPolicy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
        {
            return OverlayPolicyMarketDataListIF.strALWAYS_OVERLAY_POLICY;
        }

        return OverlayPolicyMarketDataListIF.strNEVER_OVERLAY_POLICY;
    }

    public OverlayPolicyMarketDataBookDepthStructListIF createOverlayPolicyMarketDataBookDepthStructList(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataBookDepthStructList(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataBookDepthStructList(mdReqID);
    }


    public OverlayPolicyMarketDataBookDepthStructListIF createAlwaysOverlayPolicyMarketDataBookDepthStructList(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataBookDepthStructList(mdReqID);
    }

    public OverlayPolicyMarketDataBookDepthStructListIF createNeverOverlayPolicyMarketDataBookDepthStructList(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataBookDepthStructList(mdReqID);
    }

    public OverlayPolicyMarketDataCurrentMarketStructListIF createOverlayPolicyMarketDataCurrentMarketStructList(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructList(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList(mdReqID);
    }

    public OverlayPolicyMarketDataCurrentMarketStructListIF createAlwaysOverlayPolicyMarketDataCurrentMarketStructList(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructList(mdReqID);
    }

    public OverlayPolicyMarketDataCurrentMarketStructListIF createNeverOverlayPolicyMarketDataCurrentMarketStructList(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList(mdReqID);
    }

    public OverlayPolicyMarketDataCurrentMarketStructV4ListIF createOverlayPolicyMarketDataCurrentMarketStructV4List(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructV4List(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List(mdReqID);
    }

    public OverlayPolicyMarketDataCurrentMarketStructV4ListIF createAlwaysOverlayPolicyMarketDataCurrentMarketStructV4List(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataCurrentMarketStructV4List(mdReqID);
    }

    public OverlayPolicyMarketDataCurrentMarketStructV4ListIF createNeverOverlayPolicyMarketDataCurrentMarketStructV4List(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructV4List(mdReqID);
    }

    public OverlayPolicyMarketDataExpectedOpeningPriceStructListIF createOverlayPolicyMarketDataExpectedOpeningPriceStructList(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataExpectedOpeningPriceStructList(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList(mdReqID);
    }

    public OverlayPolicyMarketDataExpectedOpeningPriceStructListIF createAlwaysOverlayPolicyMarketDataExpectedOpeningPriceStructList(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataExpectedOpeningPriceStructList(mdReqID);
    }

    public OverlayPolicyMarketDataExpectedOpeningPriceStructListIF createNeverOverlayPolicyMarketDataExpectedOpeningPriceStructList(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataExpectedOpeningPriceStructList(mdReqID);
    }

    public OverlayPolicyMarketDataNbboStructListIF createOverlayPolicyMarketDataNbboStructList(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataNbboStructList(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataNbboStructList(mdReqID);
    }

    public OverlayPolicyMarketDataNbboStructListIF createAlwaysOverlayPolicyMarketDataNbboStructList(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataNbboStructList(mdReqID);
    }

    public OverlayPolicyMarketDataNbboStructListIF createNeverOverlayPolicyMarketDataNbboStructList(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataNbboStructList(mdReqID);
    }

    public OverlayPolicyMarketDataRecapStructListIF createOverlayPolicyMarketDataRecapStructList(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataRecapStructList(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataRecapStructList(mdReqID);
    }

    public OverlayPolicyMarketDataRecapStructListIF createAlwaysOverlayPolicyMarketDataRecapStructList(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataRecapStructList(mdReqID);
    }

    public OverlayPolicyMarketDataRecapStructListIF createNeverOverlayPolicyMarketDataRecapStructList(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataRecapStructList(mdReqID);
    }

    public OverlayPolicyMarketDataRecapStructV4ListIF createOverlayPolicyMarketDataRecapStructV4List(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataRecapStructV4List(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataRecapStructV4List(mdReqID);
    }

    public OverlayPolicyMarketDataRecapStructV4ListIF createAlwaysOverlayPolicyMarketDataRecapStructV4List(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataRecapStructV4List(mdReqID);
    }

    public OverlayPolicyMarketDataRecapStructV4ListIF createNeverOverlayPolicyMarketDataRecapStructV4List(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataRecapStructV4List(mdReqID);
    }

    public OverlayPolicyMarketDataRecapContainerV4ListIF createOverlayPolicyMarketDataRecapContainerV4List(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataRecapContainerV4List(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataRecapContainerV4List(mdReqID);
    }

    public OverlayPolicyMarketDataRecapContainerV4ListIF createAlwaysOverlayPolicyMarketDataRecapContainerV4List(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataRecapContainerV4List(mdReqID);
    }

    public OverlayPolicyMarketDataRecapContainerV4ListIF createNeverOverlayPolicyMarketDataRecapContainerV4List(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataRecapContainerV4List(mdReqID);
    }


    public OverlayPolicyMarketDataTickerStructListIF createOverlayPolicyMarketDataTickerStructList(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataTickerStructList(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataTickerStructList(mdReqID);
    }

    public OverlayPolicyMarketDataTickerStructListIF createAlwaysOverlayPolicyMarketDataTickerStructList(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataTickerStructList(mdReqID);
    }

    public OverlayPolicyMarketDataTickerStructListIF createNeverOverlayPolicyMarketDataTickerStructList(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataTickerStructList(mdReqID);
    }

    public OverlayPolicyMarketDataTickerStructV4ListIF createOverlayPolicyMarketDataTickerStructV4List(int policy, String mdReqID)
    {
         if (policy == OverlayPolicyMarketDataListIF.ALWAYS_OVERLAY_POLICY)
         {
             return new OverlayPolicyAlwaysOverlayMarketDataTickerStructV4List(mdReqID);
         }

         return new OverlayPolicyNeverOverlayMarketDataTickerStructV4List(mdReqID);
    }

    public OverlayPolicyMarketDataTickerStructV4ListIF createAlwaysOverlayPolicyMarketDataTickerStructV4List(String mdReqID)
    {
         return new OverlayPolicyAlwaysOverlayMarketDataTickerStructV4List(mdReqID);
    }

    public OverlayPolicyMarketDataTickerStructV4ListIF createNeverOverlayPolicyMarketDataTickerStructV4List(String mdReqID)
    {
         return new OverlayPolicyNeverOverlayMarketDataTickerStructV4List(mdReqID);
    }
}
