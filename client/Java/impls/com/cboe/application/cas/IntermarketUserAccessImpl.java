package com.cboe.application.cas;

import com.cboe.idl.cmiIntermarket.IntermarketSessionManagerStruct;
import com.cboe.idl.cmiIntermarket.IntermarketSessionManagerStructHelper;
import com.cboe.idl.cmiIntermarket.IntermarketUserSessionManagerHelper;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.POANameHelper;
import com.cboe.delegates.application.IntermarketUserSessionManagerDelegate;

import com.cboe.exceptions.*;
import com.cboe.idl.cmi.UserSessionManagerHelper;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.interfaces.application.*;
import com.cboe.util.ExceptionBuilder;

/**
 * Implementation of IntermarketUserAccess.
 * If a user need to get CAS intermarket services, such as IntermarketQuery, NBBO Agent Service, etc,
 * IntermarketUserAccess object is the first object the user
 * need to get. This IntermarketUserAccess object will be published by
 * a stringified IOR.
 * @author Emily Huang
 */

public class IntermarketUserAccessImpl extends BObject implements IntermarketUserAccess
{

    com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager corbaImSession;
    com.cboe.idl.cmi.UserSessionManager corbaSession;

    public IntermarketUserAccessImpl() {
        super();
     }

    public IntermarketSessionManagerStruct logon( UserLogonStruct logonStruct, short sessionType, CMIUserSessionAdmin clientListener, boolean gmdTextMessaging )
        throws  SystemException, CommunicationException,
                AuthorizationException, AuthenticationException,
                DataValidationException
    {
        try {
            UserAccessHome userAccessHome = (UserAccessHome)HomeFactory.getInstance().findHome(UserAccessHome.HOME_NAME);

            UserAccess userAccess = userAccessHome.find();

            corbaSession = userAccess.logon(logonStruct, sessionType, clientListener, gmdTextMessaging);
        } catch ( CBOELoggableException e)
        {
            Log.exception(this, "could not find UserAccessHome", e);
            throw new NullPointerException("Could not find User Access Home");
        }
        try {
            corbaImSession = getIntermarketUserSessionManager(corbaSession);
        } catch ( NotFoundException e)
        {
            throw ExceptionBuilder.systemException("existing user session not found", 0);
        }

        IntermarketSessionManagerStruct imSession = new  IntermarketSessionManagerStruct(corbaSession, corbaImSession);

        return imSession;

    }

   protected com.cboe.interfaces.application.IntermarketUserSessionManager getImUserSession(com.cboe.idl.cmi.UserSessionManager manager)
                      throws SystemException, CommunicationException, AuthorizationException, NotFoundException
   {

       IntermarketUserSessionManager imSession;

       SessionManager session = ServicesHelper.getRemoteSessionManagerHome().findRemoteSession(manager.getValidSessionProfileUser().userId, manager);

       if ( session == null)
       {
           if (Log.isDebugOn()) { 
               Log.debug(this, "Could not find existing User Session.");
           }
           throw ExceptionBuilder.notFoundException("existing user session not found", 0);
       }
       else
       {
           IntermarketSessionManagerHome imSessionHome = ServicesHelper.getIntermarketSessionManagerHome() ;
           imSession = imSessionHome.create(session);
           return imSession;
       }
    }

    /**
     *  build an IntermarketUserSessionManager corba object given an existing com.cboe.idl.cmi.UserSessionManager
     */
    public com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager getIntermarketUserSessionManager(
                       com.cboe.idl.cmi.UserSessionManager manager)
                       throws SystemException, CommunicationException, AuthorizationException,  NotFoundException
    {
        String poaName = POANameHelper.getPOAName((BOHome)ServicesHelper.getIntermarketSessionManagerHome());
        IntermarketUserSessionManager imSession = getImUserSession(manager);
        IntermarketUserSessionManagerDelegate delegate = new IntermarketUserSessionManagerDelegate(imSession);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
        corbaImSession = IntermarketUserSessionManagerHelper.narrow(obj);
        imSession.setRemoteDelegate(corbaImSession);
        return corbaImSession;

    }

}
