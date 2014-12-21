package com.cboe.routing;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.startup.ClientRoutingBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingService;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingServiceHome;
import com.cboe.util.*;

/**
 * Class Name:	ProductRoutingServiceClientHomeImpl
 * 
 * @date:	November 17, 2008
**/

public class ProductRoutingServiceClientHomeImpl extends ClientRoutingBOHome implements ProductRoutingServiceHome 
{
    private	ProductRoutingServiceClientImpl prsClientInstance;
    private static final Integer INT_0 = 0;
    
    public	ProductRoutingServiceClientHomeImpl()
    {
        super();   
    }
    
    public void create( String name )
    {     
        if ( prsClientInstance == null )
        {   
            Log.information( this, "Initializing ProductRoutingService");
            prsClientInstance = new ProductRoutingServiceClientImpl();
           
            ((BObject)prsClientInstance).setName( name );
            addToContainer( (BObject)prsClientInstance );  
            prsClientInstance.create(String.valueOf(prsClientInstance.hashCode()));    
            Log.information( this, "Initializing ProductRoutingService Complete");       
        }    
    }
    
    public ProductRoutingService find()
    {
       return prsClientInstance;
    }
    
    public void clientInitialize() throws Exception
    {
        create("ProductRoutingServiceClient");
    }  
   
    public void clientStart() throws Exception
    {
        Log.information(this, "Starting ProductRoutingServiceClient"); 
        prsClientInstance.initialize();        
        subscribeForEvents();
        Log.information( this, "ProductRoutingServiceClient Startup complete");
    }
    
    public  void clientShutdown() throws Exception
    {
        Log.information(this, "Shutting Down ProductRoutingServiceClient"); 
        unsubscribeForEvents();
        Log.information( this, "ProductRoutingServiceClient Shutdown complete");
    }    
    
    private void subscribeForEvents()  
    {
        ChannelKey channelKey;      
       
        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS, INT_0);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(prsClientInstance, prsClientInstance, channelKey);
        
        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, INT_0);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(prsClientInstance, prsClientInstance, channelKey);
    }
    
    private void unsubscribeForEvents()
    {
        ChannelKey channelKey;
        
        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS, INT_0);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(prsClientInstance, prsClientInstance, channelKey);
        
        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, INT_0);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(prsClientInstance, prsClientInstance, channelKey);        
    }
    
}//EOF
