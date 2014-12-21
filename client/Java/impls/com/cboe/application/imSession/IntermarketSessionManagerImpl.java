
package com.cboe.application.imSession;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiIntermarket.NBBOAgentHelper;
import com.cboe.idl.cmiIntermarket.IntermarketQueryHelper;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.interfaces.application.*;
import com.cboe.delegates.application.NBBOAgentDelegate;
import com.cboe.delegates.application.IntermarketQueryDelegate;
import com.cboe.domain.logout.LogoutServiceFactory;


public class IntermarketSessionManagerImpl extends BObject
        implements IntermarketUserSessionManager, UserSessionLogoutCollector
{
    protected com.cboe.idl.cmiIntermarket.IntermarketQuery imQueryCorba                  = null;
    protected com.cboe.idl.cmiIntermarket.NBBOAgent nbboAgentCorba                       = null;
    protected SessionManager sessionManager;
    protected com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager imSessionManager;
    
    // Event Channel Processors
    private UserSessionLogoutProcessor logoutProcessor;

    public IntermarketSessionManagerImpl()
    {
        super();
    }

    public void initialize() throws Exception
    {
        imQueryCorba = initIntermarketQuery();
        nbboAgentCorba = initNBBOAgent();

        //imTradingParmCorba = initIntermarketTradingParameter();
        //not implemented yet
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
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);

        logoutProcessor.setParent(null);
        logoutProcessor = null;

        sessionManager = null;
    }

    /////////////////////// protected methods //////////////////////////////
    protected void unregisterRemoteObjects()
    {
             unregisterIntermarketQuery();
             unregisterNBBOAgent();
             unregisterInterMarketSessionManager();
    }

    public com.cboe.idl.cmiIntermarket.IntermarketQuery getIntermarketQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        if ( imQueryCorba == null )
        {
            imQueryCorba = initIntermarketQuery();
        }
        return imQueryCorba;
     }


    public com.cboe.idl.cmiIntermarket.NBBOAgent getNBBOAgent() throws SystemException, CommunicationException, AuthorizationException
    {
            if ( nbboAgentCorba == null )
            {
                nbboAgentCorba = initNBBOAgent();
            }
            return nbboAgentCorba;

    }

    protected com.cboe.idl.cmiIntermarket.IntermarketQuery initIntermarketQuery()
        throws SystemException, CommunicationException, AuthorizationException
    {
            IntermarketQueryHome home = ServicesHelper.getIntermarketQueryHome();
            String poaName = POANameHelper.getPOAName((BOHome)home);
            IntermarketQuery imQuery = home.create(sessionManager);

            IntermarketQueryDelegate delegate = new IntermarketQueryDelegate(imQuery);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            imQueryCorba = IntermarketQueryHelper.narrow(obj);

            return imQueryCorba;

    }

    protected com.cboe.idl.cmiIntermarket.NBBOAgent initNBBOAgent()
        throws SystemException, CommunicationException, AuthorizationException
    {
             NBBOAgentHome home = ServicesHelper.getNBBOAgentHome();
            String poaName = POANameHelper.getPOAName((BOHome)home);
            NBBOAgent nbboAgent = home.create(sessionManager);

            NBBOAgentDelegate delegate = new NBBOAgentDelegate(nbboAgent);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            nbboAgentCorba = NBBOAgentHelper.narrow(obj);

            return nbboAgentCorba;

    }

    protected void unregisterIntermarketQuery()
    {
        try {
            if (imQueryCorba != null) {
                RemoteConnectionFactory.find().unregister_object(imQueryCorba);
                imQueryCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unregisterIntermarketQuery");
        }
    }

    protected void unregisterNBBOAgent()
    {
        try {
            if (nbboAgentCorba != null) {
                RemoteConnectionFactory.find().unregister_object(nbboAgentCorba);
                nbboAgentCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unregisterNBBOAgent");
        }
    }

    public void setRemoteDelegate(com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager remoteDelegate)
		throws SystemException, CommunicationException, AuthorizationException
	{
		imSessionManager = remoteDelegate;
	}
	
	protected void unregisterInterMarketSessionManager()
	{
	    try {
	        if (imSessionManager != null) {
	            RemoteConnectionFactory.find().unregister_object(imSessionManager);
	            imSessionManager = null;
	        }
	    } catch( Exception e ) {
	        Log.exception( this, e );
	    }
	}
}
