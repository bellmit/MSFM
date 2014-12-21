package com.cboe.application.cas;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserAccessV8Home;
import com.cboe.interfaces.application.UserAccessV8;

public class UserAccessV8HomeImpl extends UserAccessBaseHomeImpl implements UserAccessV8Home {

    private UserAccessV8 userAccess;
    private com.cboe.idl.cmiV8.UserAccessV8 userAccessCorba;

    public UserAccessV8 find()
    {
        return create();
    }

    public UserAccessV8 create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessV8Impl");
        }
        if (userAccess == null)
        {
            com.cboe.application.cas.UserAccessV8Impl bo = new com.cboe.application.cas.UserAccessV8Impl(sessionMode, heartbeatTimeout, cmiVersion);
            bo.create(String.valueOf(bo.hashCode()));
            addToContainer(bo);

            com.cboe.application.cas.UserAccessV8Interceptor boi = null;
            try
            {
                boi = (com.cboe.application.cas.UserAccessV8Interceptor) this.createInterceptor(bo);
            } catch (Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessV8", ex);
                return null;
            }

            userAccess = boi;
        }
        return userAccess;
    }

    public String objectToString()
    {
        return super.objectToString(userAccessCorba);
    }

    public void clientInitialize() throws Exception
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
        super.clientInitialize();
        create();
    }

    public void clientStart() throws Exception
    {
        userAccess = find();
        String poaName = com.cboe.application.shared.POANameHelper.getPOAName(this);
        com.cboe.delegates.application.UserAccessV8Delegate delegate = new com.cboe.delegates.application.UserAccessV8Delegate(userAccess);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) com.cboe.application.shared.RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = com.cboe.idl.cmiV8.UserAccessV8Helper.narrow(obj);
    }
}
