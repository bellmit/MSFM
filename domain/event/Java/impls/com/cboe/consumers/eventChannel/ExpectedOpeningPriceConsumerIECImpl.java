package com.cboe.consumers.eventChannel;

import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.util.CurrentMarketContainerImpl;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV2;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.CurrentMarketConsumer;
import com.cboe.interfaces.events.ExpectedOpeningPriceConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

public class ExpectedOpeningPriceConsumerIECImpl 
    extends CurrentMarketConsumerIECImpl
    implements ExpectedOpeningPriceConsumer     
{
    public ExpectedOpeningPriceConsumerIECImpl()
    {
        super();
    }
    
    public void acceptCurrentMarketsForClass(RoutingParameterStruct routing,
            CurrentMarketStruct[] bestMarkets,
            CurrentMarketStruct[] bestLimitMarkets,
            NBBOStruct[] nbbos,
            CurrentMarketStructV2[] markets,
            CurrentMarketStruct[] bestPublicMarkets,
            CurrentMarketStruct[] bestPublicMarketsAtTop
            )
    {
        // does nothing
    }

    public void acceptCurrentMarket(int[] groups, CurrentMarketStruct contingentMarket, CurrentMarketStruct nonContingentMarket)
    {
        // does nothing
    }

    public void acceptNBBO(int[] groups, NBBOStruct NBBO)
    {
        // does nothing
    }

    public void acceptCurrentMarketAndNBBO(int[] groups, CurrentMarketStruct contingentMarket, CurrentMarketStruct nonContingentMarket, NBBOStruct NBBO)
    {
        // does nothing
    }

}
