package com.cboe.application.tradingSession.adapter;

import com.cboe.interfaces.application.TradingSessionServiceAdapterHome;
import com.cboe.interfaces.application.TradingSessionServiceAdapter;
import com.cboe.domain.startup.ClientBOHome;

public final class TradingSessionServiceAdapterHomeImpl extends ClientBOHome implements TradingSessionServiceAdapterHome
{
    private TradingSessionServiceAdapterImpl instance;

    public void clientInitialize() throws Exception
    {
        super.clientInitialize();
        create();
        instance.foundationFrameworkInitialize(); 
    }
    
    public TradingSessionServiceAdapter create()
    {
        if(instance == null)
        {
            instance = new TradingSessionServiceAdapterImpl();
            instance.create("TradingSessionServiceAdapter");
            addToContainer(instance);
        }
        return instance;
    }

    public TradingSessionServiceAdapter find()
    {
        return create();
    }
}
