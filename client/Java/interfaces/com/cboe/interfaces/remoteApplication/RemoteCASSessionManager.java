package com.cboe.interfaces.remoteApplication;

import com.cboe.interfaces.domain.session.BaseSessionManager;

public interface RemoteCASSessionManager extends BaseSessionManager
{
    public String getCasOrigin();
    public RemoteCASMarketDataService getRemoteCASMarketDataService();
}
