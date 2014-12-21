package com.cboe.cfix.interfaces;

/**
 * CfixMarketDataMapperIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.exceptions.*;
import com.cboe.interfaces.cfix.*;

public interface CfixMarketDataMapperIF
{
    public char[]  getMsgTypeAsChars();
    public void    initialize(CfixSessionManager cfixSessionManager, String propertyPrefix, Properties properties) throws Exception;
    public void    reset(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder, CfixMarketDataMDReqIDHelper cfixMarketDataMDReqIDHelper) throws Exception;
    public boolean build(FixMessageBuilderIF writer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException;
}
