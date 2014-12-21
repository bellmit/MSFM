//
// -----------------------------------------------------------------------------------
// Source file: SessionManagerV4Impl.java
//
// PACKAGE: com.cboe.expressApplication.session
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.expressApplication.session;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.expressApplication.MarketQueryV4Home;
import com.cboe.interfaces.expressApplication.MarketQueryV4;
import com.cboe.interfaces.expressApplication.SessionManagerV4;

import com.cboe.util.ExceptionBuilder;

import com.cboe.application.session.SessionManagerImpl;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.delegates.expressApplication.MarketQueryDelegate;

/**
 * Extends SessionManagerImpl, adding an interface to get the MarketQueryV4 service for the user.
 */
public class SessionManagerV4Impl extends SessionManagerImpl implements SessionManagerV4
{
    protected com.cboe.idl.cmiV4.MarketQuery expressMarketCorba;

    protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey,
                                           boolean ifLazyInitialization,
                                           CMIUserSessionAdmin clientListener, short sessionType,
                                           boolean gmdTextMessaging, boolean addUserInterest)
            throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        super.initialize(validUser, sessionId, sessionKey, ifLazyInitialization, clientListener, sessionType, gmdTextMessaging, addUserInterest);
        expressMarketCorba = initExpressMarketQuery();
    }

    public com.cboe.idl.cmiV4.MarketQuery getMarketQueryV4()
            throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if(expressMarketCorba == null)
            {
                expressMarketCorba = initExpressMarketQuery();
            }
            return expressMarketCorba;
        }

        catch(Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get market query V4 " + e.toString(), 0);
        }
    }

    /**
     * Initializes the v4 market query reference.
     */
    protected com.cboe.idl.cmiV4.MarketQuery initExpressMarketQuery()
            throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            MarketQueryV4Home home = ServicesHelper.getMarketQueryV4Home();
            String poaName = getPOA((BOHome) home);
            MarketQueryV4 expressMarket = home.createMarketQueryV4(this);
            MarketQueryDelegate delegate = new MarketQueryDelegate(expressMarket);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
            expressMarketCorba = com.cboe.idl.cmiV4.MarketQueryHelper.narrow(obj);

            return expressMarketCorba;
        }
        catch(Exception poae)
        {
            throw ExceptionBuilder.systemException("Could not bind Market Query V4", 1);

        }
    }
    
    protected void unregisterRemoteObjects()
    {
        String us = this.toString();
        StringBuilder unregister = new StringBuilder(us.length()+40);
        unregister.append("Unregister remote objects for session:").append(us);
        Log.information(this, unregister.toString());
        try {
            unregisterMarketQuery();
            super.unregisterRemoteObjects();
        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    protected void unregisterMarketQuery()
    {
        try {
            if (expressMarketCorba != null) {
                RemoteConnectionFactory.find().unregister_object(expressMarketCorba);
                expressMarketCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }


}
