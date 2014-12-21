package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataDispatcherIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;

public interface CfixMarketDataDispatcherIF extends InstrumentedEventChannelListener
{
    public static final int NumberSubscriptionMarketDataTypes            = 7;
    public static final int NumberSnapshotMarketDataTypes                = 2;
    public static final int NumberMarketDataTypes                        = NumberSubscriptionMarketDataTypes + NumberSnapshotMarketDataTypes;

    public static final int MarketDataType_Unknown                       = 0;
    public static final int MarketDataType_All                           = ~0;

    public static final int MarketDataType_Raw                           = 1 << 29;
    public static final int MarketDataType_ByProduct                     = 1 << 30;
    public static final int MarketDataType_ByClass                       = 1 << 31;

    public static final int MarketDataType_CurrentMarket_index           = 0;
    public static final int MarketDataType_CurrentMarket                 = 1 << MarketDataType_CurrentMarket_index;
    public static final int MarketDataType_CurrentMarketByProduct        = MarketDataType_CurrentMarket        |  MarketDataType_ByProduct;
    public static final int MarketDataType_CurrentMarketByClass          = MarketDataType_CurrentMarket        |  MarketDataType_ByClass;
    public static final int MarketDataType_CurrentMarketRaw              = MarketDataType_CurrentMarket        |  MarketDataType_Raw;

    public static final int MarketDataType_Nbbo_index                    = 1;
    public static final int MarketDataType_Nbbo                          = 1 << MarketDataType_Nbbo_index;
    public static final int MarketDataType_NbboByProduct                 = MarketDataType_Nbbo                 |  MarketDataType_ByProduct;
    public static final int MarketDataType_NbboByClass                   = MarketDataType_Nbbo                 |  MarketDataType_ByClass;
    public static final int MarketDataType_NbboRaw                       = MarketDataType_Nbbo                 |  MarketDataType_Raw;

    public static final int MarketDataType_Recap_index                   = 2;
    public static final int MarketDataType_Recap                         = 1 << MarketDataType_Recap_index;
    public static final int MarketDataType_RecapByProduct                = MarketDataType_Recap                |  MarketDataType_ByProduct;
    public static final int MarketDataType_RecapByClass                  = MarketDataType_Recap                |  MarketDataType_ByClass;
    public static final int MarketDataType_RecapRaw                      = MarketDataType_Recap                |  MarketDataType_Raw;

    public static final int MarketDataType_BookDepth_index               = 3;
    public static final int MarketDataType_BookDepth                     = 1 << MarketDataType_BookDepth_index;
    public static final int MarketDataType_BookDepthByProduct            = MarketDataType_BookDepth            |  MarketDataType_ByProduct;
    public static final int MarketDataType_BookDepthByClass              = MarketDataType_BookDepth            |  MarketDataType_ByClass;
    public static final int MarketDataType_BookDepthRaw                  = MarketDataType_BookDepth            |  MarketDataType_Raw;

    public static final int MarketDataType_BookDepthUpdate_index         = 4;
    public static final int MarketDataType_BookDepthUpdate               = 1 << MarketDataType_BookDepthUpdate_index;
    public static final int MarketDataType_BookDepthUpdateByProduct      = MarketDataType_BookDepthUpdate      |  MarketDataType_ByProduct;
    public static final int MarketDataType_BookDepthUpdateByClass        = MarketDataType_BookDepthUpdate      |  MarketDataType_ByClass;
    public static final int MarketDataType_BookDepthUpdateRaw            = MarketDataType_BookDepthUpdate      |  MarketDataType_Raw;

    public static final int MarketDataType_Ticker_index                  = 5;
    public static final int MarketDataType_Ticker                        = 1 << MarketDataType_Ticker_index;
    public static final int MarketDataType_TickerByProduct               = MarketDataType_Ticker               |  MarketDataType_ByProduct;
    public static final int MarketDataType_TickerByClass                 = MarketDataType_Ticker               |  MarketDataType_ByClass;
    public static final int MarketDataType_TickerRaw                     = MarketDataType_Ticker               |  MarketDataType_Raw;

    public static final int MarketDataType_ExpectedOpeningPrice_index    = 6;
    public static final int MarketDataType_ExpectedOpeningPrice          = 1 << MarketDataType_ExpectedOpeningPrice_index;
    public static final int MarketDataType_ExpectedOpeningPriceByProduct = MarketDataType_ExpectedOpeningPrice |  MarketDataType_ByProduct;
    public static final int MarketDataType_ExpectedOpeningPriceByClass   = MarketDataType_ExpectedOpeningPrice |  MarketDataType_ByClass;
    public static final int MarketDataType_ExpectedOpeningPriceRaw       = MarketDataType_ExpectedOpeningPrice |  MarketDataType_Raw;

    public static final int MarketDataType_BookDepthSnapshot_index       = 7;
    public static final int MarketDataType_BookDepthSnapshot             = 1 << MarketDataType_BookDepthSnapshot_index;
    public static final int MarketDataType_BookDepthSnapshotByProduct    = MarketDataType_BookDepthSnapshot    |  MarketDataType_ByProduct;
    public static final int MarketDataType_BookDepthSnapshotByClass      = MarketDataType_BookDepthSnapshot    |  MarketDataType_ByClass;
    public static final int MarketDataType_BookDepthSnapshotRaw          = MarketDataType_BookDepthSnapshot    |  MarketDataType_Raw;

    public static final int MarketDataType_Reject_index                  = 8;
    public static final int MarketDataType_Reject                        = 1 << MarketDataType_Reject_index;
    public static final int MarketDataType_RejectRaw                     = MarketDataType_Reject               |  MarketDataType_Raw;

    public static final int MarketDataType_Descriptors                   = MarketDataType_Raw | MarketDataType_ByProduct | MarketDataType_ByClass;

    public static final int DEBUG_OFF                       = 0;        public static final String strDEBUG_OFF                 = "DEBUG_OFF";
    public static final int DEBUG_ALL                       = ~0;       public static final String strDEBUG_ALL                 = "DEBUG_ALL";
    public static final int DEBUG_Subscribe                 = 1 << 20;  public static final String strDEBUG_Subscribe           = "DEBUG_SUBSCRIBE";
    public static final int DEBUG_Accept                    = 1 << 21;  public static final String strDEBUG_Accept              = "DEBUG_ACCEPT";
    public static final int DEBUG_ChannelUpdate             = 1 << 22;  public static final String strDEBUG_ChannelUpdate       = "DEBUG_CHANNELUPDATE";
    public static final int DEBUG_ChannelUpdateDecode       = 1 << 23;  public static final String strDEBUG_ChannelUpdateDecode = "DEBUG_CHANNELUPDATEDECODE";

    public void   subscribeByProduct(CfixMarketDataConsumerHolder   cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void   unsubscribeByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void   subscribeByClass(CfixMarketDataConsumerHolder     cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void   unsubscribeByClass(CfixMarketDataConsumerHolder   cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public void   unsubscribeConsumer(CfixMarketDataConsumer        cfixFixMarketDataConsumer)       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public void   accept(CfixMarketDataDispatcherVisitor            cfixMarketDataDispatcherVisitor) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public int    getHandledChannelType();
    public int    getHandledMarketDataType();
    public String getHandledMarketDataTypeName();

    public CfixMarketDataDispatcherInstrumentation getCfixMarketDataDispatcherInstrumentation();

    public void   debugGetSubscriptionMaps(Object[] twoCfixKeyToConsumersMaps);

    public int    setDebugFlags(int debugFlags);
    public int    getDebugFlags();

    public String getName();
}
