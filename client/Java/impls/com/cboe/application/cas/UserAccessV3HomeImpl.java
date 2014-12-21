package com.cboe.application.cas;


import com.cboe.application.shared.RemoteConnectionFactory;

import com.cboe.application.shared.POANameHelper;
import com.cboe.delegates.application.UserAccessV3Delegate;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserAccessV3Home;
import com.cboe.interfaces.application.UserAccessV3;
import com.cboe.idl.cmiV3.UserAccessV3Helper;

public class UserAccessV3HomeImpl extends UserAccessBaseHomeImpl implements UserAccessV3Home
{

    private UserAccessV3 userAccessV3;
    private com.cboe.idl.cmiV3.UserAccessV3 userAccessV3Corba;

    public UserAccessV3HomeImpl()
    {
        super();
    }

    public UserAccessV3 create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessV3Impl");
        }
        if (userAccessV3 == null)
        {
            UserAccessV3Impl bo = new UserAccessV3Impl(sessionMode, heartbeatTimeout, cmiVersion);
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            UserAccessV3Interceptor boi = null;
            try
            {
                boi = (UserAccessV3Interceptor) this.createInterceptor(bo);
            } catch (Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessV3", ex);
                return null;
            }

            //Every BOObject create MUST have a name...if the object is to be a managed object.
            userAccessV3 = boi;
        }
        return userAccessV3;
    }

    public UserAccessV3 find()
    {
        return create();
    }

    public String objectToString()
    {
        return objectToString(userAccessV3Corba);
    }

    public void clientStart()
            throws Exception
    {
        userAccessV3 = find();
        String poaName = POANameHelper.getPOAName(this);
        UserAccessV3Delegate delegate = new UserAccessV3Delegate(userAccessV3);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessV3Corba = UserAccessV3Helper.narrow(obj);
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
