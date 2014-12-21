package com.cboe.application.cas;

import com.cboe.interfaces.application.UserAccessV9Home;
import com.cboe.interfaces.application.UserAccessV9;

public class UserAccessV9HomeImpl extends UserAccessBaseHomeImpl implements UserAccessV9Home
{

    private UserAccessV9 userAccess;
    private com.cboe.idl.cmiV9.UserAccessV9 userAccessCorba;

    public UserAccessV9 find()
    {
        return create();
    }

    public UserAccessV9 create()
    {
        com.cboe.infrastructureServices.foundationFramework.utilities.Log.debug(this, "Creating UserAccessV9Impl");
        if (userAccess == null)
        {
            UserAccessV9Impl bo = new UserAccessV9Impl(sessionMode, heartbeatTimeout, cmiVersion);
            bo.create(String.valueOf(bo.hashCode()));
            addToContainer(bo);

            UserAccessV9Interceptor boi = null;
            try
            {
                boi = (UserAccessV9Interceptor) this.createInterceptor(bo);
            } catch (Exception ex)
            {
                com.cboe.infrastructureServices.foundationFramework.utilities.Log.exception(this, "Failed to create UserAccessV9", ex);
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
        com.cboe.infrastructureServices.foundationFramework.utilities.Log.debug(this, "SMA Type = " + this.getSmaType());
        super.clientInitialize();
        create();
    }

    public void clientStart() throws Exception
    {
        userAccess = find();
        String poaName = com.cboe.application.shared.POANameHelper.getPOAName(this);
        com.cboe.delegates.application.UserAccessV9Delegate delegate = new com.cboe.delegates.application.UserAccessV9Delegate(userAccess);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) com.cboe.application.shared.RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = com.cboe.idl.cmiV9.UserAccessV9Helper.narrow(obj);
    }

}