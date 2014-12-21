//
// ------------------------------------------------------------------------
// FILE: UserAccessV2Factory.java
//
// PACKAGE: com.cboe.presentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.presentation.api;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.UserAccessV2Helper;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.presentation.common.logging.GUILoggerHome;
import org.omg.CORBA.OBJECT_NOT_EXIST;

/**
 * @author torresl@cboe.com
 */
public class UserAccessV2Factory
{
    static private UserAccessV2 userAccessV2;
    public UserAccessV2Factory()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        userAccessV2 = null;
    }
    public static UserAccessV2 getUserAccess()
    {
        if (userAccessV2 == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_V2_object();
                userAccessV2 = UserAccessV2Helper.narrow((org.omg.CORBA.Object) obj);
            }
            catch (OBJECT_NOT_EXIST e)
            {
                // this indicates that the CAS is not available... propagate it
                // so that the GUI can display an appropriate message
                throw e;
            }
            catch (Throwable e)
            {
                GUILoggerHome.find().exception("com.cboe.presentation.api.UserAccessV2Factory.find()", "UserAccessV2 remote object connection exception", e);
            }
        }
        return userAccessV2;
    }
    public static UserSessionManagerV2 getUserSessionManagerV2(
            UserSessionManager userSessionManager)
            throws AuthorizationException, CommunicationException, SystemException, NotFoundException
    {
        return getUserAccess().getUserSessionManagerV2(userSessionManager);
    }

}
