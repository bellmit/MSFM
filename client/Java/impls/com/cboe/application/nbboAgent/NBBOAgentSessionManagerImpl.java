
package com.cboe.application.nbboAgent;

import com.cboe.interfaces.application.*;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdmin;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumer;
import com.cboe.idl.cmiIntermarket.IntermarketManualHandlingHelper;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

import com.cboe.application.shared.consumer.*;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.POANameHelper;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ExceptionBuilder;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.delegates.application.IntermarketManualHandlingDelegate;

public class NBBOAgentSessionManagerImpl extends BObject
    implements NBBOAgentSessionManager, UserSessionLogoutCollector

{
    protected UserSessionLogoutProcessor logoutProcessor;
    protected CMINBBOAgentSessionAdmin nbboAdmin;
    protected CMIIntermarketOrderStatusConsumer imOrderStatus;
    protected com.cboe.idl.cmiIntermarket.IntermarketManualHandling imManualHandlingCorba;
    protected com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager nbboAgentSessionCorba;
    protected SessionManager sessionManager;

    public NBBOAgentSessionManagerImpl()
    {
        super();
        imManualHandlingCorba = null;
        nbboAgentSessionCorba = null;
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }

        // clean up corba objects created
        unregisterNBBOAgentSessionManager();
        unregisterIntermarketManualHandling();

        EventChannelAdapterFactory.find().removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeChannel(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);


        // Clean up the processors
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        // Clean up instance variables.
        sessionManager = null;

    }

    public void create( String name )
    {
        super.create(name);
    }

    public void setSessionManager(SessionManager theSession)
    {
        this.sessionManager = theSession;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, theSession);
        LogoutServiceFactory.find().addLogoutListener(theSession, this);
    }

    public void initialize()
    {
         try {
             imManualHandlingCorba = initIntermarketManualHandling();
        } catch (Exception e) {
            Log.exception(this, "session : " + this, e);
        }

    }
    public com.cboe.idl.cmiIntermarket.IntermarketManualHandling getIntermarketManualHandling()
    throws SystemException, CommunicationException, AuthorizationException
    {
            if ( imManualHandlingCorba == null )
            {
                imManualHandlingCorba = initIntermarketManualHandling();
            }
            return imManualHandlingCorba;
    }

    protected com.cboe.idl.cmiIntermarket.IntermarketManualHandling initIntermarketManualHandling()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {

            IntermarketManualHandlingHome home = (IntermarketManualHandlingHome)HomeFactory.getInstance().findHome(IntermarketManualHandlingHome.HOME_NAME);
            String poaName = POANameHelper.getPOAName((BOHome)home);

            IntermarketManualHandling manualHandling = home.create(sessionManager);

            IntermarketManualHandlingDelegate delegate = new IntermarketManualHandlingDelegate(manualHandling);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);

            imManualHandlingCorba = IntermarketManualHandlingHelper.narrow(obj);

            return imManualHandlingCorba;

        }
        catch( CBOELoggableException e )
        {
            throw ExceptionBuilder.systemException("Could not get IntermarketManualHandling service", 1);
        }
    }

    public void setRemoteDelegate(Object remoteDelegate)
        throws SystemException, CommunicationException, AuthorizationException
    {
        nbboAgentSessionCorba = (com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager) remoteDelegate;
    }

    protected void unregisterNBBOAgentSessionManager()
    {
        try {
            if (nbboAgentSessionCorba != null) {
                RemoteConnectionFactory.find().unregister_object(nbboAgentSessionCorba);
                nbboAgentSessionCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }
    protected void unregisterIntermarketManualHandling()
    {
        try {
            if (imManualHandlingCorba != null) {
                RemoteConnectionFactory.find().unregister_object(imManualHandlingCorba);
                imManualHandlingCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

}
