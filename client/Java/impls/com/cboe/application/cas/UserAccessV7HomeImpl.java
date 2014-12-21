package com.cboe.application.cas;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserAccessV7Home;
import com.cboe.interfaces.application.UserAccessV7;

public class UserAccessV7HomeImpl extends UserAccessBaseHomeImpl implements UserAccessV7Home {

    private UserAccessV7 userAccess;
    private com.cboe.idl.cmiV7.UserAccessV7 userAccessCorba;

    public UserAccessV7 find()
    {
        return create();
    }

    public UserAccessV7 create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessV7Impl");
        }
        if (userAccess == null)
        {
            com.cboe.application.cas.UserAccessV7Impl bo = new com.cboe.application.cas.UserAccessV7Impl(sessionMode, heartbeatTimeout, cmiVersion);
            bo.create(String.valueOf(bo.hashCode()));
            addToContainer(bo);

            com.cboe.application.cas.UserAccessV7Interceptor boi = null;
            try
            {
                boi = (com.cboe.application.cas.UserAccessV7Interceptor) this.createInterceptor(bo);
            } catch (Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessV7", ex);
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
        com.cboe.delegates.application.UserAccessV7Delegate delegate = new com.cboe.delegates.application.UserAccessV7Delegate(userAccess);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) com.cboe.application.shared.RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = com.cboe.idl.cmiV7.UserAccessV7Helper.narrow(obj);
    }
}
