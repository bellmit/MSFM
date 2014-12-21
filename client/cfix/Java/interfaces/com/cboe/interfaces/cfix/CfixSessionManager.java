/**
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Feb 19, 2003
 * Time: 11:46:11 AM
 * To change this template use Options | File Templates.
 */
package com.cboe.interfaces.cfix;

import com.cboe.exceptions.*;
import com.cboe.interfaces.application.*;

public interface CfixSessionManager extends SessionManager
{
    public TradingSession           getCfixTradingSession()     throws SystemException, CommunicationException, AuthorizationException;
    public ProductQueryManager      getCfixProductQuery()       throws SystemException, CommunicationException, AuthorizationException;
    public CfixMarketDataQueryIF    getCfixMarketDataQuery()    throws SystemException, CommunicationException, AuthorizationException;
    public CfixMDXMarketDataQueryIF getCfixMDXMarketDataQuery() throws SystemException, CommunicationException, AuthorizationException;
}
