package com.cboe.cfix.util.junit;

/**
 * junitOverlayPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.cfix.util.*;
import com.cboe.client.util.junit.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class junitOverlayPolicy extends JunitTestCase
{
    public void testCurrentMarketStructPolicy() throws Exception
    {
        String                mdReqID          = "DELL_CURRENT_MARKET";
        debugMarketDataMapper marketDataMapper = new debugMarketDataMapper();
        int                   dellClassKey     = 2755707;
        int[]                 dellProductKeys  = {18614335, 18614338, 18614337, 18614336, 18614339, 18614340, 18614341, 18614342, 18614343, 18614344, 18614345, 18614346, 21240674, 21240675, 21240676, 21240677, 21240678, 21240679, 21240680, 21240681, 21240682, 21240683, 18614347, 18614348, 18614349, 18614350, 18614351, 18614352, 18614353, 18614354, 18614355, 18614356, 18614357, 18614358, 21240684, 21240685, 21240686, 21240687, 21240688, 21240689, 21240690, 21240691, 21240692, 21240693, 21240694, 21240695, 18614379, 18614380, 18614381, 18614382, 18614383, 18614384, 21240696, 21240697, 21240698, 21240699, 18614385, 18614386, 18614387, 18614388, 18614389, 18614390, 21240700, 21240701, 21240702, 21240703, 10421358, 10421359, 10421360, 10421361, 10421362, 10421363, 10421364, 10421365, 10421366, 10421367, 10421368, 10421369, 10421370, 10421371, 10421372, 10421373, 10421374, 10421375, 10421376, 10421377, 10421378, 10421379, 21248528, 21248529, 10421380, 10421381, 10421382, 10421383, 18617125, 18617126, 10421384, 10421385, 10421356, 10421357, 18623619, 18623620, 18623621, 18623622, 18623623, 18623624, 18623625, 18623626, 19614583, 19614584, 18623627, 18623628, 21264878, 21264879, 18623629, 18623630, 21264880, 21264881, 19614585, 19614586, 19614587, 19614588, 19614589, 19614590};

        CfixMarketDataConsumer cfixMarketDataConsumer = new CfixMarketDataConsumer()
        {
            public void acceptMarketDataCurrentMarket(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
            {

            }

            public void acceptMarketDataBookDepth(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
            {

            }

            public void acceptMarketDataBookDepthUpdate(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
            {

            }

            public void acceptMarketDataExpectedOpeningPrice(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
            {

            }

            public void acceptMarketDataNbbo(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
            {

            }

            public void acceptMarketDataRecap(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
            {

            }

            public void acceptMarketDataTicker(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList)
            {

            }

            public void acceptMarketDataReject(CfixMarketDataRejectStruct cfixFixMarketDataRejectStruct)
            {

            }

            public boolean isAcceptingMarketData()
            {
                return true;
            }
        };

        OverlayPolicyMarketDataHolder holder   = new OverlayPolicyMarketDataHolder();

        CurrentMarketStruct[] currentMarketStructs = marketDataMapper.makeCurrentMarketStruct(0, "W_MAIN", ProductTypes.OPTION, dellClassKey, dellProductKeys, true);

        OverlayPolicyFactory overlayPolicyFactory = new OverlayPolicyFactory();

        OverlayPolicyMarketDataCurrentMarketStructListIF currentMarketStructList = overlayPolicyFactory.createAlwaysOverlayPolicyMarketDataCurrentMarketStructList(mdReqID);

        assertEquals(mdReqID, currentMarketStructList.getMdReqID());

        assertEquals(0, holder.size());
        assertEquals(0, currentMarketStructList.size());

        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[0]);

        assertEquals(0, holder.size());
        assertEquals(1, currentMarketStructList.size());

        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[0]);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[0]);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[0]);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[1]);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[2]);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[3]);

        assertEquals(0, holder.size());
        assertEquals(4, currentMarketStructList.size());

        currentMarketStructList.remove(holder);

        assertEquals(0, currentMarketStructList.size());
        assertEquals(4, holder.size());
        assertEquals(mdReqID, holder.getMdReqID());

        assertEquals(CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket, holder.getMarketDataType());
        assertEquals(3, holder.getOverlaid().timesChanged(0));

        holder.clear();
        assertEquals(0, holder.size());
        assertEquals(0, currentMarketStructList.size());

        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs, 0, currentMarketStructs.length);

        currentMarketStructList.remove(holder);

        assertEquals(0, currentMarketStructList.size());
        assertEquals(currentMarketStructs.length, holder.size());
        assertEquals(mdReqID, holder.getMdReqID());

        assertEquals(CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket, holder.getMarketDataType());
        assertEquals(0, holder.getOverlaid().timesChanged(0));

        holder.clear();
        assertEquals(0, holder.size());
        assertEquals(0, currentMarketStructList.size());

        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[0]);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs, 0, currentMarketStructs.length);

        currentMarketStructList.remove(holder);

        assertEquals(0, currentMarketStructList.size());
        assertEquals(currentMarketStructs.length, holder.size());
        assertEquals(mdReqID, holder.getMdReqID());

        assertEquals(CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket, holder.getMarketDataType());
        assertEquals(1, holder.getOverlaid().timesChanged(0));
        assertEquals(0, holder.getOverlaid().timesChanged(1));

        holder.clear();
        assertEquals(0, holder.size());
        assertEquals(0, currentMarketStructList.size());

        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs[0]);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs, 0, 1);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs, 0, 1);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs, 0, 1);
        currentMarketStructList.add(cfixMarketDataConsumer, currentMarketStructs, 0, currentMarketStructs.length);

        currentMarketStructList.remove(holder);

        assertEquals(0, currentMarketStructList.size());
        assertEquals(currentMarketStructs.length, holder.size());
        assertEquals(mdReqID, holder.getMdReqID());

        assertEquals(CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket, holder.getMarketDataType());
        assertEquals(4, holder.getOverlaid().timesChanged(0));
        assertEquals(0, holder.getOverlaid().timesChanged(1));

        holder.clear();
        assertEquals(0, holder.size());
        assertEquals(0, currentMarketStructList.size());
    }
}
