package com.cboe.util;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;

public class ObjectPooInitializerHome  extends BOHome 
{

    public void initialize()
    {
        try 
        {
            Class.forName("com.cboe.businessServices.brokerService.BrokerQuoteLockedCommand");
            Class.forName("com.cboe.businessServices.brokerService.BrokerEndingHoldCommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptInternalizedOrderCommand");
            Class.forName("com.cboe.businessServices.brokerService.BrokerInternalizedOrderTimeoutCommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptCancelCommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptCancelReplaceCommand");
            Class.forName("com.cboe.businessServices.brokerService.ReturnHybridOrderCancelReplaceCommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptOrderCommand");
            Class.forName("com.cboe.businessServices.brokerService.BrokerExpireQuoteTriggerCommand");
            Class.forName("com.cboe.businessServices.brokerService.BrokerAuctionTimerCommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptQuoteCommand");
            Class.forName("com.cboe.businessServices.recoaService.ReCOACommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptQuoteBlockCommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptCancelQuoteCommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptQuoteUpdateCommand");
            Class.forName("com.cboe.businessServices.brokerService.AcceptQuoteSequenceCommand");
            Class.forName("com.cboe.businessServices.brokerService.BrokerHandleLegBestBookChangeCommand");
            Class.forName("com.cboe.server.marketBuffer.MarketDataWriteBuffer");
            Class.forName("com.cboe.businessServices.brokerService.AcceptAsyncCancelQuoteCommand");
            Class.forName("com.cboe.businessServices.brokerService.PreProcessCancelQuoteForClassCommand");
            Class.forName("com.cboe.server.events.CurrentMarketsHolder");
            Class.forName("com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateImpl");
            Class.forName("com.cboe.businessServices.orderBookService.OrderBookPriceItem");
            Class.forName("com.cboe.businessServices.orderBookService.OrderBookPriceDetailImpl");
            Class.forName("com.cboe.businessServices.marketMakerQuoteService.QuoteImpl");
            Class.forName("com.cboe.businessServices.marketMakerQuoteService.QuoteSideImpl");
        }
        catch(ClassNotFoundException cnfe)
        {
            throw new FatalFoundationFrameworkException(cnfe, "Invalid Class Name, Could not initialize Object Pool");
        }
    }
}

