package com.cboe.consumers.eventChannel;

/**
 * Best book listener object listens on the CBOE event channel as an BBBOConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Jeff Illian
 */
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.CurrentMarketContainerImpl;
import com.cboe.domain.util.MarketDataARCommandHelper;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV2;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.CurrentMarketConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

public class CurrentMarketConsumerIECImpl extends BObject implements CurrentMarketConsumer{
    private ConcurrentEventChannelAdapter internalEventChannel;

    /**
     * MarketBestListener constructor comment.
     */
    public CurrentMarketConsumerIECImpl() {
        super();
        try
        {
            internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.MARKETDATA_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception("Exception getting MARKETDATA_INSTRUMENTED_IEC!", e);
        }
    }


    public void acceptCurrentMarketsForClass(RoutingParameterStruct routing,
            CurrentMarketStruct[] bestMarkets,
            CurrentMarketStruct[] bestLimitMarkets,
            NBBOStruct[] nbbos,
            CurrentMarketStructV2[] markets,
            CurrentMarketStruct[] bestPublicMarkets,
            CurrentMarketStruct[] bestPublicMarketsAtTop,
            boolean[] shortSaleTriggeredMode
            )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> currentMarket/NBBO : classKey:" + routing.classKey
                            + " for " + routing.sessionName
						    + " bestMarkets length=" + bestMarkets.length
                            + " NBBO length=" + nbbos.length
						    + " currentMarketV2 length=" + markets.length
                            + " bestPublicMarketsAtTop length=" + bestPublicMarketsAtTop.length);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;
        SessionKeyContainer sessionClass = new SessionKeyContainer(routing.sessionName, routing.classKey);

        if(bestMarkets.length > 0)
        {
            channelKey = new ChannelKey(ChannelKey.CURRENT_MARKET_BY_CLASS, sessionClass);
            CurrentMarketContainerImpl currentMarket = new CurrentMarketContainerImpl(bestMarkets);
            currentMarket.setBestPublicMarketsAtTop(bestPublicMarketsAtTop);

            event = internalEventChannel.getChannelEvent(this, channelKey, currentMarket);
            internalEventChannel.dispatch(event);
        }

        if(nbbos.length > 0)
        {
            channelKey = new ChannelKey(ChannelKey.NBBO_BY_CLASS, sessionClass);
            event = internalEventChannel.getChannelEvent(this, channelKey, nbbos);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptExpectedOpeningPricesForClass(RoutingParameterStruct routing, ExpectedOpeningPriceStruct[] expectedOpeningPrices)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> ExpectedOpeningPricess: classKey:"+routing.classKey+" for "+routing.sessionName);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;
        if (MarketDataARCommandHelper.PublishEOPData.isPublishEOP()) {
            SessionKeyContainer sessionClass = new SessionKeyContainer(routing.sessionName, routing.classKey);
            channelKey = new ChannelKey(ChannelKey.OPENING_PRICE_BY_CLASS, sessionClass);
            event = internalEventChannel.getChannelEvent(this, channelKey, expectedOpeningPrices);
            internalEventChannel.dispatch(event);
        }

    }

    /**
     * This method is called by the CORBA event channel when a BBBO event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     * This method should not be called. However in case it is called, a change has been
     * made in order to support the old format with the new processing of the CurrentMarketContainer.
     *
     */
    public void acceptCurrentMarket(int[] groups, CurrentMarketStruct contingentMarket, CurrentMarketStruct nonContingentMarket)
    {
        Log.alarm(this, "acceptCurrentMarket event received on OLD V1 acceptCurrentMarket -> marketData : " + contingentMarket.productKeys.productKey + " for " + contingentMarket.sessionName);
        ChannelKey channelKey = null;
        ChannelEvent event = null;
        SessionKeyContainer sessionClass = new SessionKeyContainer(contingentMarket.sessionName, contingentMarket.productKeys.classKey);
        channelKey = new ChannelKey(ChannelKey.CURRENT_MARKET_BY_CLASS, sessionClass);
        //CurrentMarketStruct[] currentMarkets = {contingentMarket};
        CurrentMarketStruct[] bestPublicMarketsAtTop = new CurrentMarketStruct[0];
        CurrentMarketStruct[] bestMarkets = new CurrentMarketStruct[1];
        bestMarkets[0] = contingentMarket;
        CurrentMarketContainerImpl currentMarkets = new CurrentMarketContainerImpl(bestMarkets, bestPublicMarketsAtTop);
        event = internalEventChannel.getChannelEvent(this, channelKey, currentMarkets);
        internalEventChannel.dispatch(event);
    }

    public void acceptNBBO(int[] groups, NBBOStruct NBBO)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> NBBO : " + NBBO.productKeys.classKey);
        }
        SessionKeyContainer sessionClass = new SessionKeyContainer(NBBO.sessionName, NBBO.productKeys.classKey);
        ChannelKey channelKey = new ChannelKey(ChannelKey.NBBO_BY_CLASS, sessionClass);
        NBBOStruct[] NBBOStructs = {NBBO};
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, NBBOStructs);
        internalEventChannel.dispatch(event);
    }

    public void acceptCurrentMarketAndNBBO(int[] groups, CurrentMarketStruct contingentMarket, CurrentMarketStruct nonContingentMarket, NBBOStruct NBBO)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> CurrentMarket and NBBO : " + contingentMarket.productKeys.classKey);
        }
        acceptNBBO(groups, NBBO);
        acceptCurrentMarket(groups, contingentMarket, nonContingentMarket);
    }

    public void acceptExpectedOpeningPrice(int[] groups, ExpectedOpeningPriceStruct expectedOpeningPrice)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> OpeningPrice : " + expectedOpeningPrice.productKeys.productKey);
        }

        ChannelKey channelKey = null;
        ChannelEvent event = null;
        if(MarketDataARCommandHelper.PublishEOPData.isPublishEOP()){
            SessionKeyContainer sessionClass = new SessionKeyContainer(expectedOpeningPrice.sessionName, expectedOpeningPrice.productKeys.classKey);
            channelKey = new ChannelKey(ChannelKey.OPENING_PRICE_BY_CLASS, sessionClass);
            ExpectedOpeningPriceStruct[] openingPriceStructs = {expectedOpeningPrice};
            event = internalEventChannel.getChannelEvent(this, channelKey, openingPriceStructs);
            internalEventChannel.dispatch(event);
            if (Log.isDebugOn())
            {
                Log.debug(this, "Publishing EOP/EOS Data");
            }
        }
    }
}
