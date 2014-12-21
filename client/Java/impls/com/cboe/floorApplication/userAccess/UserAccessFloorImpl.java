package com.cboe.floorApplication.userAccess;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.floorApplication.UserAccessFloor;
import com.cboe.interfaces.floorApplication.FloorSessionManager;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.idl.floorApplication.FloorSessionManagerHelper;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.util.ExceptionBuilder;
import com.cboe.delegates.floorApplication.FloorSessionManagerDelegate;

/**
 * User: mahoney
 * Date: Jul 17, 2007
 */
public class UserAccessFloorImpl extends BObject implements UserAccessFloor
{
    public UserAccessFloorImpl()
    {
        super();
    }

    protected com.cboe.interfaces.floorApplication.FloorSessionManager getFloorUserSession(com.cboe.idl.cmi.UserSessionManager userSessionManager)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        FloorSessionManager floorSession;

        SessionManager session = ServicesHelper.getRemoteSessionManagerHome().findRemoteSession(
                userSessionManager.getValidSessionProfileUser().userId, userSessionManager);

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
                Log.debug(this, "Creating FloorSessionManager for User ID " + userSessionManager.getValidUser().userId);
            }
            floorSession = ServicesHelper.createFloorSessionManager(session);
            return floorSession;
        }
    }

    public com.cboe.idl.floorApplication.FloorSessionManager getSessionManager(com.cboe.idl.cmi.UserSessionManager userSessionManager)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        String poaName = POANameHelper.getPOAName((BOHome) ServicesHelper.getFloorSessionManagerHome());
        FloorSessionManager omtSession = getFloorUserSession(userSessionManager);
        FloorSessionManagerDelegate delegate = new FloorSessionManagerDelegate(omtSession);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        com.cboe.idl.floorApplication.FloorSessionManager corbaObj = FloorSessionManagerHelper.narrow(obj);
        omtSession.setRemoteDelegate(corbaObj);
        return corbaObj;
    }
}
