package com.cboe.application.omtSession;

import com.cboe.interfaces.application.*;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.omt.OrderManagementServiceHelper;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.delegates.application.OrderManagementServiceDelegate;

public class OMTSessionManagerImpl extends BObject implements OMTSessionManager, UserSessionLogoutCollector
{
    protected com.cboe.idl.omt.OrderManagementService omsCorba;
    protected SessionManager sessionManager;
    protected com.cboe.idl.omt.OMTSessionManager omtSessionManager;

    // Event Channel Processors
    private UserSessionLogoutProcessor logoutProcessor;

    public OMTSessionManagerImpl()
    {
        super();
    }

    public void initialize() throws Exception
    {
        omsCorba = initOrderManagementService();
    }

     public com.cboe.idl.omt.OrderManagementService getOrderManagementService()
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        if(omsCorba == null)
        {
            omsCorba = initOrderManagementService();
        }
        return omsCorba;
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

    protected void unregisterRemoteObjects()
    {
        unregisterOrderManagementService();
        unregisterOMTSessionManager();
    }

    protected com.cboe.idl.omt.OrderManagementService initOrderManagementService()
            throws SystemException, CommunicationException, AuthorizationException
    {
        OrderManagementServiceHome home = ServicesHelper.getOrderManagementServiceHome();
        String poaName = POANameHelper.getPOAName((BOHome) home);
        OrderManagementService oms = home.create(sessionManager);

        OrderManagementServiceDelegate delegate = new OrderManagementServiceDelegate(oms);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
        omsCorba = OrderManagementServiceHelper.narrow(obj);

        return omsCorba;
    }

    protected void unregisterOrderManagementService()
    {
        try
        {
            if(omsCorba != null)
            {
                RemoteConnectionFactory.find().unregister_object(omsCorba);
                omsCorba = null;
            }
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unregisterOrderManagementService");
        }
    }

    public void setRemoteDelegate(com.cboe.idl.omt.OMTSessionManager remoteDelegate)
		throws SystemException, CommunicationException, AuthorizationException
	{
    	omtSessionManager = remoteDelegate;
	}
    
    protected void unregisterOMTSessionManager()
    {
        try {
            if (omtSessionManager != null) {
                RemoteConnectionFactory.find().unregister_object(omtSessionManager);
                omtSessionManager = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }
    
}
