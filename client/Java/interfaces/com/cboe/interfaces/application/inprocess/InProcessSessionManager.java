package com.cboe.interfaces.application.inprocess;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.application.*;


/**
 * @author Jing Chen
 */
public interface InProcessSessionManager extends SessionManager, SessionManagerV6
{
    public InProcessTradingSession getInProcessTradingSession() throws SystemException, CommunicationException, AuthorizationException;
    public ProductQueryManager getInProcessProductQuery() throws SystemException, CommunicationException, AuthorizationException;
    public QuoteEntry getInProcessQuoteEntry() throws SystemException, CommunicationException, AuthorizationException;
    public OrderEntry getInProcessOrderEntry() throws SystemException, CommunicationException, AuthorizationException;
    public QuoteQuery getInProcessQuoteQuery() throws SystemException, CommunicationException, AuthorizationException;
    public UserOrderQuery getInProcessOrderQuery() throws SystemException, CommunicationException, AuthorizationException;
    public Administrator getInProcessAdministrator() throws SystemException, CommunicationException, AuthorizationException;
    public ProductDefinition getInProcessProductDefinition() throws SystemException, CommunicationException, AuthorizationException;
    public MarketQuery getInProcessMarketQuery() throws SystemException, CommunicationException, AuthorizationException;
    public UserTradingParametersV5 getInProcessUserTradingParameters() throws SystemException, CommunicationException, AuthorizationException;
    // MWM - the following method will need to be dealt with when IPD merges in
    public RemoteMarketQuery getInProcessRemoteMarketQuery() throws SystemException, CommunicationException, AuthorizationException;

    public FloorTradeConsumer getInProcessFloorTrade() throws SystemException, CommunicationException, AuthorizationException;
}
