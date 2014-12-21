//
// -----------------------------------------------------------------------------------
// Source file: UserAccessFloorFactory.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.idl.floorApplication.UserAccessFloor;
import com.cboe.idl.floorApplication.UserAccessFloorHelper;
import com.cboe.idl.floorApplication.FloorSessionManager;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.NotFoundException;
import org.omg.CORBA.OBJECT_NOT_EXIST;

public class UserAccessFloorFactory {
    private static UserAccessFloor userAccess;
    private static FloorSessionManager sessionManager = null;

    public UserAccessFloorFactory()
    {
        super();
    }

    public static UserAccessFloor find()
    {
        if(userAccess == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_Floor_object();
                userAccess = UserAccessFloorHelper.narrow((org.omg.CORBA.Object) obj);
            }
            catch(OBJECT_NOT_EXIST e)
            {
                // this indicates that the CAS is not available... propagate it
                // so that the GUI can display an appropriate message
                throw e;
            }
            catch(Throwable e)
            {
                GUILoggerHome.find().exception("com.cboe.presentation.api.UserAccessFloorFactory.find()",
                                               "UserAccess remote object connection exception", e);
            }
        }
        return userAccess;
    }

    public static FloorSessionManager getUserSessionManager(UserSessionManager userSessionManager)
            throws AuthorizationException, CommunicationException, SystemException, NotFoundException {
        UserAccessFloor userAccess = find();
        if(sessionManager == null)
        {
            if(userAccess != null)
            {
                sessionManager = userAccess.getSessionManager(userSessionManager);
            }
        }
        return sessionManager;
    }
}
