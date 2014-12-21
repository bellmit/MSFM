package com.cboe.cfix.cas.fix42;

/**
 * CfixMarketDataIncrementalMapper.java
 *
 * @author Dmitry Volpyansky
 *
 * WARNING: I NEVER STARTED CODING THIS BECAUSE WE POSTPONED THE INCREMENTAL -- THIS CODE DOES NOT WORK (Dmitry 03/20/2003)
 *
 */

import java.util.*;

import com.cboe.cfix.cas.shared.*;
import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.exceptions.*;
import com.cboe.interfaces.cfix.*;

public class CfixMarketDataIncrementalMapper implements CfixMarketDataMapperIF
{
    public int                                 currentIndex;
    public int                                 maxIndex;
    public OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder;
    public CfixSessionManager                  cfixSessionManager;
    public CfixStrikePriceHelper               cfixStrikePriceHelper;
    public CfixMarketDataMDReqIDHelper         cfixMarketDataMDReqIDHelper;

    public void initialize(CfixSessionManager cfixSessionManager, String propertyPrefix, Properties properties) throws Exception
    {
        this.cfixSessionManager = cfixSessionManager;

        cfixStrikePriceHelper   = CfixServicesHelper.getCfixStrikePriceHelperHome().find();
    }

    public void reset(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder, CfixMarketDataMDReqIDHelper cfixMarketDataMDReqIDHelper) throws Exception
    {
        this.cfixOverlayPolicyMarketDataHolder = cfixOverlayPolicyMarketDataHolder;
        this.currentIndex                      = 0;
        this.maxIndex                          = cfixOverlayPolicyMarketDataHolder.size();
        this.cfixMarketDataMDReqIDHelper       = cfixMarketDataMDReqIDHelper;
    }

    public char[] getMsgTypeAsChars()
    {
        return FixMarketDataIncrementalRefreshMessage.MsgTypeAsChars;
    }

    public boolean build(FixMessageBuilderIF fixMessageBuilder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, AlreadyExistsException
    {
        return false;
    }
}
