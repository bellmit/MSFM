package com.cboe.application.cas;


import com.cboe.application.shared.ServicesHelper;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserAccess;
import com.cboe.interfaces.application.UserAccessHome;
import com.cboe.interfaces.application.UserAccessV2;


/**
 * Implementation of UserAcccessV2.
 * If a user need to get hybrid services in cmiV2,
 * UserAccessV2 object is the first object the user
 * need to get. This UserAccessV2 object will be published by
 * a stringified IOR.
 * @author Emily Huang
 */

public class UserAccessV2Impl extends BObject implements UserAccessV2
{
    public UserAccessV2Impl()
    {
        super();
    }

    public SessionManagerStructV2 logon(
                                    UserLogonStruct logonStruct,
                                    short sessionType,
                                    CMIUserSessionAdmin clientListener,
                                    boolean gmdTextMessaging )
        throws  SystemException, CommunicationException, AuthorizationException, AuthenticationException,  DataValidationException, NotFoundException
    {
        try {
            UserAccessHome userAccessHome = (UserAccessHome)HomeFactory.getInstance().findHome(UserAccessHome.HOME_NAME);

            UserAccess userAccess = userAccessHome.find();

            com.cboe.idl.cmi.UserSessionManager corbaSession = userAccess.logon(logonStruct, sessionType, clientListener, gmdTextMessaging);
            com.cboe.idl.cmiV2.UserSessionManagerV2 corbaSessionV2 = getUserSessionManagerV2(corbaSession);
            SessionManagerStructV2 sessionStructV2 = new  SessionManagerStructV2(corbaSession, corbaSessionV2);
            return sessionStructV2;

        } catch ( CBOELoggableException e)
        {
            Log.exception(this, "could not find UserAccessHome", e);

            // Do we want to throw NullPointerException here?
            throw new NullPointerException("Could not find User Access Home");
        }

    }

    /**
     *  build an IntermarketUserSessionManager corba object given an existing com.cboe.idl.cmi.UserSessionManager
     */
    public com.cboe.idl.cmiV2.UserSessionManagerV2 getUserSessionManagerV2(
                       com.cboe.idl.cmi.UserSessionManager session)
                       throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "getUserSessionManagerV2 :" + session );
        }
        SessionManager sessionManager = ServicesHelper.getRemoteSessionManagerHome().findRemoteSession(session.getValidSessionProfileUser().userId, session);
        return sessionManager.getUserSessionManagerV2();
    }

}
