/**
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Feb 19, 2003
 * Time: 10:45:01 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.interfaces.cfix;

import com.cboe.exceptions.*;

public interface CfixMarketDataQueryProxyHome {
    public final static String HOME_NAME = "CfixMarketDataQueryProxyHome";
    public CfixMarketDataQueryIF create(CfixSessionManager sessionManager) throws SystemException, CommunicationException, AuthorizationException;
}
