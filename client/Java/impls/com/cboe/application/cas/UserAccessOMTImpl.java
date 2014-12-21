package com.cboe.application.cas;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserAccessOMT;
import com.cboe.interfaces.application.OMTSessionManager;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.omt.OMTSessionManagerHelper;
import com.cboe.idl.pcqs.PCQSSessionManagerHelper;
import com.cboe.delegates.application.OMTSessionManagerDelegate;
import com.cboe.util.ExceptionBuilder;

public class UserAccessOMTImpl extends BObject implements UserAccessOMT
{
    public UserAccessOMTImpl()
    {
        super();
    }

    protected com.cboe.interfaces.application.OMTSessionManager getOMTUserSession(com.cboe.idl.cmi.UserSessionManager manager)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        OMTSessionManager omtSession;

        SessionManager session = ServicesHelper.getRemoteSessionManagerHome().findRemoteSession(
                manager.getValidSessionProfileUser().userId, manager);

        if(session == null)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Could not find existing User Session.");
            }
            throw ExceptionBuilder.notFoundException("existing user session not found", 0);
        }
        else
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Creating OMTSessionManager for User ID " + manager.getValidUser().userId);
            }
            omtSession = ServicesHelper.createOMTSessionManager(session);
            return omtSession;
        }
    }

    public com.cboe.idl.omt.OMTSessionManager getOMTUserSessionManager(com.cboe.idl.cmi.UserSessionManager userSessionManager)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        String poaName = POANameHelper.getPOAName((BOHome) ServicesHelper.getOMTSessionManagerHome());
        OMTSessionManager omtSession = getOMTUserSession(userSessionManager);
        OMTSessionManagerDelegate delegate = new OMTSessionManagerDelegate(omtSession);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        com.cboe.idl.omt.OMTSessionManager corbaObj = OMTSessionManagerHelper.narrow(obj);
        omtSession.setRemoteDelegate(corbaObj);
        return corbaObj;
    }
}
