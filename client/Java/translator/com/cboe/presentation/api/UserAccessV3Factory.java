//
// ------------------------------------------------------------------------
// FILE: UserAccessV3Factory.java
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
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserAccessV3Helper;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.presentation.common.logging.GUILoggerHome;
import org.omg.CORBA.OBJECT_NOT_EXIST;

/**
 */
public class UserAccessV3Factory
{
    static private UserAccessV3 userAccessV3;
    public UserAccessV3Factory()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        userAccessV3 = null;
    }
    public static UserAccessV3 getUserAccess()
    {
        if (userAccessV3 == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_V3_object();
                userAccessV3 = UserAccessV3Helper.narrow((org.omg.CORBA.Object) obj);
            }
            catch (OBJECT_NOT_EXIST e)
            {
                // this indicates that the CAS is not available... propagate it
                // so that the GUI can display an appropriate message
                throw e;
            }
            catch (Throwable e)
            {
                GUILoggerHome.find().exception("com.cboe.presentation.api.UserAccessV3Factory.find()", "UserAccessV3 remote object connection exception", e);
            }
        }
        return userAccessV3;
    }

}
