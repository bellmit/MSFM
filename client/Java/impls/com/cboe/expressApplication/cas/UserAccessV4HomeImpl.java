//
// -----------------------------------------------------------------------------------
// Source file: UserAccessV4HomeImpl.java
//
// PACKAGE: com.cboe.expressApplication.cas
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.expressApplication.cas;

import com.cboe.idl.cmiV4.UserAccessV4Helper;

import com.cboe.interfaces.expressApplication.UserAccessV4;
import com.cboe.interfaces.expressApplication.UserAccessV4Home;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.cas.UserAccessBaseHomeImpl;
import com.cboe.delegates.expressApplication.UserAccessV4Delegate;

public class UserAccessV4HomeImpl extends UserAccessBaseHomeImpl implements UserAccessV4Home
{
    private UserAccessV4 userAccessV4;
    private com.cboe.idl.cmiV4.UserAccessV4 userAccessV4Corba;

    public UserAccessV4HomeImpl()
    {
        super();
    }

    public UserAccessV4 create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessV4Impl");
        }
        if(userAccessV4 == null)
        {
            UserAccessV4Impl bo = new UserAccessV4Impl(sessionMode, heartbeatTimeout, cmiVersion);
            //Every BObject created MUST have a name if the object is to be a managed object.
            bo.create(String.valueOf(bo.hashCode()));
            //Every BObject must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            UserAccessV4Interceptor boi;
            try
            {
                boi = (UserAccessV4Interceptor) this.createInterceptor(bo);
            }
            catch(Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessV4", ex);
                return null;
            }

            userAccessV4 = boi;
        }
        return userAccessV4;
    }

    public UserAccessV4 find()
    {
        return create();
    }

    public String objectToString()
    {
        return objectToString(userAccessV4Corba);
    }

    public void clientStart()
            throws Exception
    {
        userAccessV4 = find();
        String poaName = POANameHelper.getPOAName(this);
        UserAccessV4Delegate delegate = new UserAccessV4Delegate(userAccessV4);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
        userAccessV4Corba = UserAccessV4Helper.narrow(obj);
    }

    public void clientInitialize()
            throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
        super.clientInitialize();
        create();
    }
}
