package com.cboe.consumers.eventChannel;

/**
 * Best book listener object listens on the CBOE event channel as an BBBOConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Jeff Illian
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class CurrentMarketEventConsumerImpl extends com.cboe.idl.events.POA_CurrentMarketEventConsumer
                                            implements CurrentMarketConsumer
{
    private CurrentMarketConsumer delegate;
    /**
     * MarketBestListener constructor comment.
     */
    public CurrentMarketEventConsumerImpl(CurrentMarketConsumer currentMarketConsumer) {
        super();
        delegate = currentMarketConsumer;
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
        delegate.acceptCurrentMarketsForClass(routing,bestMarkets,bestLimitMarkets,nbbos,markets,bestPublicMarkets, 
                                                                        bestPublicMarketsAtTop, shortSaleTriggeredMode);
    }

/**
    public void acceptCurrentMarketsForClass(RoutingParameterStruct routing,
            CurrentMarketStruct[] contingentMarkets,
            CurrentMarketStruct[] nonContingentMarkets,
            NBBOStruct[] nbbos,
            CurrentMarketStructV2[] markets)
    {
        delegate.acceptCurrentMarketsForClass(routing, contingentMarkets, nonContingentMarkets, nbbos, markets);
    }
**/    
    
    
    public void acceptExpectedOpeningPricesForClass(RoutingParameterStruct routing, ExpectedOpeningPriceStruct[] expectedOpeningPrices)
    {
        delegate.acceptExpectedOpeningPricesForClass(routing,expectedOpeningPrices );
    }

    /**
     * This method is called by the CORBA event channel when a BBBO event is
     * generated.  It adds the event to the queue and wakes the queue processing
     * thread up.
     *
     * @author Jeff Illian
     */
    public void acceptCurrentMarket(int[] groups, CurrentMarketStruct contingentMarket, CurrentMarketStruct nonContingentMarket) {
        delegate.acceptCurrentMarket(groups, contingentMarket, nonContingentMarket);
    }

    public void acceptExpectedOpeningPrice(int[] groups, ExpectedOpeningPriceStruct parm1) {
        delegate.acceptExpectedOpeningPrice(groups, parm1);
    }

    public void acceptNBBO(int[] groups, NBBOStruct parm1) {
        delegate.acceptNBBO(groups, parm1);
    }

    public void acceptCurrentMarketAndNBBO(int[] groups, CurrentMarketStruct contingentMarket, CurrentMarketStruct nonContingentMarket, NBBOStruct nbbo) {
        delegate.acceptCurrentMarketAndNBBO(groups, contingentMarket, nonContingentMarket, nbbo);
    }

    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }
}
