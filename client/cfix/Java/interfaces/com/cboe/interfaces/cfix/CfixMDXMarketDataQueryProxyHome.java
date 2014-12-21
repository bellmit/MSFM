package com.cboe.interfaces.cfix;

/**
 * User: Beniwalv
 */
import com.cboe.exceptions.*;

public interface CfixMDXMarketDataQueryProxyHome {
    public final static String HOME_NAME = "CfixMDXMarketDataQueryProxyHome";
    public CfixMDXMarketDataQueryIF create(CfixSessionManager sessionManager) throws SystemException, CommunicationException, AuthorizationException;
}
