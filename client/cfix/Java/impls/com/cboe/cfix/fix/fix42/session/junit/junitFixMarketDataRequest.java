package com.cboe.cfix.fix.fix42.session.junit;

/**
 * junitFixMarketDataRequest.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;

import com.cboe.cfix.cas.marketData.*;
import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.fix.fix42.session.*;
import com.cboe.cfix.fix.parser.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;
import com.cboe.client.util.queue.*;
import com.cboe.client.util.collections.*;
import com.cboe.client.util.junit.*;
import com.cboe.interfaces.cfix.*;

public final class junitFixMarketDataRequest extends JunitTestCase
{
    public void testDecoder() throws Exception
    {
        int[]    marketDataTypes =
                 {
                    CfixMarketDataDispatcherIF.MarketDataType_Recap,
                    CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket,
                    CfixMarketDataDispatcherIF.MarketDataType_Ticker,
                    CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice,
                    CfixMarketDataDispatcherIF.MarketDataType_BookDepth
                 };
        String[] marketDateMessages =
                 {
                    "8=FIX.4.29=17935=V49=CD5556=CF1M0134=252=20030101-01:01:01262=AAPL_RECAP_CLASS263=1264=0265=0267=7269=0269=1269=2269=4269=5269=7269=8146=155=AAPL167=CS336=Underlying9315=110=113",
                    "8=FIX.4.29=15835=V49=CD5556=CF1M0134=352=20030101-01:01:01262=AAPL_CURRENT_MARKET_CLASS263=1264=1265=0267=2269=0269=1146=155=AAPL167=CS336=Underlying9315=110=225",
                    "8=FIX.4.29=14435=V49=CD5556=CF1M0134=452=20030101-01:01:01262=AAPL_TICKER_CLASS263=1264=1265=0267=1269=2146=155=AAPL167=CS336=Underlying9315=110=074",
                    "8=FIX.4.29=14735=V49=CD5556=CF1M0134=552=20030101-01:01:01262=AAPL_EOP_CLASS263=1264=0265=0286=3267=1269=4146=155=AAPL167=CS336=Underlying9315=110=130",
                    "8=FIX.4.29=15435=V49=CD5556=CF1M0134=652=20030101-01:01:01262=AAPL_BOOK_DEPTH_CLASS263=1264=0265=0267=2269=0269=1146=155=AAPL167=CS336=Underlying9315=110=152"
                 };

        FixMarketDataRequestMessage fixMarketDataRequestMessage;

        for (int i = 0; i < marketDataTypes.length; i++)
        {
            fixMarketDataRequestMessage = (FixMarketDataRequestMessage) makeFixMessage(marketDateMessages[i]);
            assertEquals(marketDataTypes[i], FixMarketDataRequestDecoder.getMarketDataRequestType(fixMarketDataRequestMessage));
        }

        DoublePriorityEventChannel eventChannel = new DoublePriorityEventChannel();
        OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList cmList = new OverlayPolicyNeverOverlayMarketDataCurrentMarketStructList("test");
        eventChannel.enqueue(cmList, ObjectObjectComparisonPolicy.RejectEqualsObjectComparisonPolicy);
        eventChannel.enqueue(cmList, ObjectObjectComparisonPolicy.RejectEqualsObjectComparisonPolicy);

        SessionKeyObjectMap map = new SessionKeyObjectMap();

        map.putKeyValue("B", 1, "B1");
        map.putKeyValue("B", 5, "B5");
        map.putKeyValue("B", 100, "B100");
        map.putKeyValue("A", 1, "A1");
        map.putKeyValue("A", 5, "A5");
        map.putKeyValue("A", 100, "A100");

        String s = (String) map.getValueForKey("A", 100);

        SessionProductStructCache.getSessionProductStructsFromClassKey("W_MAIN", 1);
        SessionProductStructCache.getSessionProductStructsFromClassKey("W_MAIN", 1);
    }

    protected FixMessageIF makeFixMessage(String message)
    {
        PackedIntArrayIF  foundErrors     = new GrowableIntArray();
        FixPacketParserIF fixPacketParser = new FixPacketParser();
        int               flags           = FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES;
        FixPacketIF       fixPacket;
        FixMessageIF      fixMessage;
        FixMessageFactoryIF fixMessageFactory = new FixMessageFactory();

        fixPacket   = fixPacketParser.parse(new StringBufferInputStream(message), FixMessageIF.VALIDATE_ONLY_USED_FIELDS, flags);
        fixMessage  = fixMessageFactory.createFixMessageFromMsgType(fixPacket.charAt(fixPacket.getValueOffset(2)));
        foundErrors = fixMessage.build(fixPacket, foundErrors, FixMessageIF.VALIDATE_ONLY_USED_FIELDS | FixMessageIF.VALIDATE_UNUSED_FIELDS, flags);

        return fixMessage;
    }
}
