//
// -----------------------------------------------------------------------------------
// Source file: PCQSSessionManagerImpl.java
//
// PACKAGE: com.cboe.application.pcqsSession
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.pcqsSession;

import com.cboe.idl.internalBusinessServices.ProductConfigurationQueryServiceHelper;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

import com.cboe.interfaces.application.PCQSSessionManager;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.ProductConfigurationQueryServiceHome;
import com.cboe.interfaces.application.ProductConfigurationQueryService;

import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.delegates.application.ProductConfigurationQueryServiceDelegate;

public class PCQSSessionManagerImpl extends BObject implements PCQSSessionManager, UserSessionLogoutCollector
{
    protected com.cboe.idl.internalBusinessServices.ProductConfigurationQueryService pcqsCorba;
    protected SessionManager sessionManager;

    // Event Channel Processors
    private UserSessionLogoutProcessor logoutProcessor;
    protected com.cboe.idl.pcqs.PCQSSessionManager pcqsSessionManager;

    public PCQSSessionManagerImpl()
    {
        super();
    }

    public void initialize() throws Exception
    {
        pcqsCorba = initProductConfigurationQueryService();
    }

    public void setSessionManager(SessionManager theSession)
    {
        sessionManager = theSession;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);

        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, sessionManager);
        LogoutServiceFactory.find().addLogoutListener(sessionManager, this);
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }

        unregisterRemoteObjects();

        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeChannel(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager, this);
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        sessionManager = null;
    }

    /////////////////////// protected methods //////////////////////////////
    protected void unregisterRemoteObjects()
    {
        unregisterProductConfigurationQueryService();
        unregisterPCQSSessionManager();
    }

    public com.cboe.idl.internalBusinessServices.ProductConfigurationQueryService getProductConfigurationQueryService()
            throws SystemException, CommunicationException, AuthorizationException
    {
        if(pcqsCorba == null)
        {
            pcqsCorba = initProductConfigurationQueryService();
        }
        return pcqsCorba;
    }


    protected com.cboe.idl.internalBusinessServices.ProductConfigurationQueryService initProductConfigurationQueryService()
            throws SystemException, CommunicationException, AuthorizationException
    {
        ProductConfigurationQueryServiceHome home = ServicesHelper.getProductConfigurationQueryServiceHome();
        String poaName = POANameHelper.getPOAName((BOHome) home);
        ProductConfigurationQueryService pcqs = home.create(sessionManager);

        ProductConfigurationQueryServiceDelegate delegate = new ProductConfigurationQueryServiceDelegate(pcqs);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
        pcqsCorba = ProductConfigurationQueryServiceHelper.narrow(obj);

        return pcqsCorba;
    }

    protected void unregisterProductConfigurationQueryService()
    {
        try
        {
            if(pcqsCorba != null)
            {
                RemoteConnectionFactory.find().unregister_object(pcqsCorba);
                pcqsCorba = null;
            }
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unregisterProductConfigurationQueryService");
        }
    }
    
    public void setRemoteDelegate(com.cboe.idl.pcqs.PCQSSessionManager remoteDelegate)
		throws SystemException, CommunicationException, AuthorizationException
	{
		pcqsSessionManager = remoteDelegate;
	}
	
	protected void unregisterPCQSSessionManager()
	{
	    try {
	        if (pcqsSessionManager != null) {
	            RemoteConnectionFactory.find().unregister_object(pcqsSessionManager);
	            pcqsSessionManager = null;
	        }
	    } catch( Exception e ) {
	        Log.exception( this, e );
	    }
	}


}
