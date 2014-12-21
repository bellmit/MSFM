//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionFactory.java
//
// PACKAGE: com.cboe.presentation.tradingSession
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.tradingSession;

import com.cboe.idl.cmiSession.TradingSessionStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.tradingSession.TradingSession;
import com.cboe.interfaces.presentation.product.ProductType;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public abstract class TradingSessionFactory
{
    private static TradingSession ALL_SESSION_TRADING_SESSION = null;

    public static TradingSession create(TradingSessionStruct struct)
    {
        return new TradingSessionImpl(struct);
    }

    public static synchronized TradingSession createDefault()
    {
        if(ALL_SESSION_TRADING_SESSION == null)
        {
            ALL_SESSION_TRADING_SESSION =
                    new TradingSessionImpl(APIHome.findTradingSessionAPI().getAllSessionsTradingSession());
        }
        return ALL_SESSION_TRADING_SESSION;
    }

    public static boolean doesTradingSessionExist(String sessionName) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
        boolean doesExist = false;
        TradingSessionStruct[] tradingSessions = APIHome.findTradingSessionAPI().getCurrentTradingSessions(null);
        for (TradingSessionStruct tradingSession : tradingSessions)
        {
            if (tradingSession.sessionName.equals(sessionName))
            {
                doesExist = true;
                break;
            }
        }
        return doesExist;
    }

    public static boolean isTradingSessionProductValid(String pTradingSession, short product)
    {
        boolean valid = false;
        try
        {
            TradingSessionStruct[] tradingSessions =
                    APIHome.findTradingSessionAPI().getCurrentTradingSessions(null);
            for(TradingSessionStruct tradingSession : tradingSessions)
            {
                if(tradingSession.sessionName.equals(pTradingSession))
                {
                    valid = true;
                    break;
                }
            }
            if(valid)
            {
                ProductType[] productTypes =
                        APIHome.findProductQueryAPI().getProductTypesForSession(pTradingSession);
                for(ProductType productType : productTypes)
                {
                    if(productType.getType() == product)
                    {
                        valid = true;
                        break;
                    }
                    else
                    {
                        valid = false;
                    }
                }
            }
        }
        catch(SystemException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        catch(CommunicationException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        catch(AuthorizationException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        catch(DataValidationException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        return valid;
    }
}
