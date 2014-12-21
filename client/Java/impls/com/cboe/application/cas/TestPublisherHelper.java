/**
 * This class is a helper class which returns 
 * CBOE event channel publishers
 * @author Connie Feng
 * @author Thomas Lynch
 */
package com.cboe.application.cas;

import com.cboe.idl.events.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;

public class TestPublisherHelper
{
    public static final String EVENT_CHANNEL = "EventChannel";
    
    private static  EventService                    eventService;
    private static  OrderStatusEventConsumer        orderStatusConsumer;
    private static  QuoteStatusEventConsumer        quoteStatusConsumer;
    private static  CurrentMarketEventConsumer      marketConsumer;
    private static  ProductStatusEventConsumer      productStatusConsumer;
    private static  RFQEventConsumer                rfqConsumer;
    private static  TickerEventConsumer             tickerConsumer;
    private static  RecapEventConsumer              recapConsumer;

    /**
    * This method will return the OrderStatus event channel, initializing it if necessary
    * @return com.cboe.events.interfaces.OrderStatusEventConsumer
    */

    public static OrderStatusEventConsumer getOrderStatusChannel()
    {
        if ( orderStatusConsumer == null)
        {
            org.omg.CORBA.Object obj;
            try
            {
                String eventChannelName = getChannelName("OrderStatus");
                String repID = OrderStatusEventConsumerHelper.id();
                obj = getEventService().getEventChannelSupplierStub( eventChannelName, repID );
                orderStatusConsumer = OrderStatusEventConsumerHelper.narrow( obj );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                return null;
            }
        }
        return orderStatusConsumer;
    }
    /**
    * This method will return the QuoteStatus event channel, initializing it if necessary
    * @return com.cboe.events.interfaces.QuoteStatusEventConsumer
    */

    public static QuoteStatusEventConsumer getQuoteStatusChannel()
    {
        if ( quoteStatusConsumer == null)
        {
            org.omg.CORBA.Object obj;
            try
            {
                String eventChannelName = getChannelName("QuoteStatus");
                String repID = QuoteStatusEventConsumerHelper.id();
                obj = getEventService().getEventChannelSupplierStub( eventChannelName, repID );
                quoteStatusConsumer = QuoteStatusEventConsumerHelper.narrow( obj );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                return null;
            }
        }
        return quoteStatusConsumer;
    }

    /**
    * This method will return the MarketBestEventConsumer event supplier, initializing it if necessary
    * @return com.cboe.events.interfaces.MarketBestEventConsumer
    */

    public static CurrentMarketEventConsumer getCurrentMarketChannel()
    {
        if ( marketConsumer == null)
        {
            org.omg.CORBA.Object obj;
            try
            {
                String eventChannelName = getChannelName("CurrentMarket");
                String repID = CurrentMarketEventConsumerHelper.id();
                obj = getEventService().getEventChannelSupplierStub( eventChannelName, repID );
                marketConsumer = CurrentMarketEventConsumerHelper.narrow( obj );
            }
            catch ( Exception e ) {
                e.printStackTrace();
                return null;
            }
        }
        return marketConsumer;
    }

    /**
    * This method will return the ProductStatusEventConsumer event supplier, initializing it if necessary
    * @return com.cboe.events.interfaces.ProductStatusEventConsumer
    */

    public static ProductStatusEventConsumer getProductStatusChannel()
    {
        if ( productStatusConsumer == null)
        {
            org.omg.CORBA.Object obj;
            try
            {
                String eventChannelName = getChannelName("ProductStatus");
                String repID = ProductStatusEventConsumerHelper.id();
                obj = getEventService().getEventChannelSupplierStub( eventChannelName, repID );
                productStatusConsumer = ProductStatusEventConsumerHelper.narrow( obj );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                return null;
            }
        }
        return productStatusConsumer;
    }

    /**
    * This method will return the RFQEventConsumer event supplier, initializing it if necessary
    * @return com.cboe.events.interfaces.RFQEventConsumer
    */

    public static RFQEventConsumer getRFQChannel()
    {
        if ( rfqConsumer == null)
        {
            org.omg.CORBA.Object obj;
            try
            {
                String eventChannelName = getChannelName("RFQ");
                String repID = RFQEventConsumerHelper.id();

                    obj = getEventService().getEventChannelSupplierStub( eventChannelName, repID );
                    rfqConsumer = RFQEventConsumerHelper.narrow( obj );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                return null;
            }
        }
        return rfqConsumer;
    }
 
    /**
    * This method will return the UnderlyingTickerEventConsumer event supplier, initializing it if necessary
    * @return com.cboe.events.interfaces.UnderlyingTickerEventConsumer
    */

    public static TickerEventConsumer getTickerChannel()
    {
            if ( tickerConsumer == null)
            {
                org.omg.CORBA.Object obj;
                try
                {
                    String eventChannelName = getChannelName("Ticker");
                    String repID = TickerEventConsumerHelper.id();
                    obj = getEventService().getEventChannelSupplierStub( eventChannelName, repID );
                    tickerConsumer = TickerEventConsumerHelper.narrow( obj );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                    return null;
                }
            }
            return tickerConsumer;
    }

    /**
    * This method will return the v event supplier, initializing it if necessary
    * @return com.cboe.events.interfaces.UnderlyingRecapEventConsumer
    */

    public static RecapEventConsumer getRecapChannel()
    {
        try
        {
            if ( recapConsumer == null)
            {
                org.omg.CORBA.Object obj;
                try
                {
                    String eventChannelName = getChannelName("Recap");
                    String repID = RecapEventConsumerHelper.id();
                    obj = getEventService().getEventChannelSupplierStub( eventChannelName, repID );
                    recapConsumer = RecapEventConsumerHelper.narrow( obj );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                    return null;
                }
            }
            return recapConsumer;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
            return null;
        }
    }
    
    /**
    * Gets the connection to the event service
    */
    public static EventService getEventService()
    {
        if(eventService == null)
        {
            connectEventService();
        }
        return eventService;
    }

    /**
    * Makes the connection to the event service
    */
    public static void connectEventService()
    {
        FoundationFramework ff = FoundationFramework.getInstance();

        eventService = ff.getEventService();
    }

    /**
    * Returns the event channel name from configuration
    */
    public static String getChannelName(String channel)
    {
        ConfigurationService configService = FoundationFramework.getInstance().getConfigService();
        return configService.getProperty(EVENT_CHANNEL + "." + channel ,"default");
    }

}
