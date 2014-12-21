
package com.cboe.application.cas;

import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.application.UserAccessV2Delegate;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.idl.cmiV2.UserAccessV2Helper;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserAccessV2;
import com.cboe.interfaces.application.UserAccessV2Home;

public class UserAccessV2HomeImpl extends ClientBOHome implements UserAccessV2Home
{

    private UserAccessV2 userAccess;
    private com.cboe.idl.cmiV2.UserAccessV2 userAccessCorba;
    private String sessionId;
    private char sessionMode;

    public UserAccessV2 create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessV2");
        }
        if (userAccess == null)
        {
            UserAccessV2Impl bo = new UserAccessV2Impl();
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            UserAccessV2Interceptor boi = null;
            try
            {
                boi = (UserAccessV2Interceptor) this.createInterceptor(bo);
                if(getInstrumentationEnablementProperty())
                {
                    boi.startInstrumentation(getInstrumentationProperty());
                }
            } catch (Exception ex)
            {
                Log.exception(this, "Failed to create IntermarketUserAccess", ex);
                return null;
            }

            //Every BOObject create MUST have a name...if the object is to be a managed object.
            userAccess = boi;
        }
        return userAccess;
    }

    public UserAccessV2 find()
    {
        return create();
    }

    public String objectToString()
    {
        try {
            if (userAccessCorba != null)
            {
                FoundationFramework ff = FoundationFramework.getInstance();
                return ff.getOrbService().getOrb().object_to_string(userAccessCorba);
            } else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not stringify the imUserAccess object", e);
            return null;
        }
    }

    public void clientStart()
            throws Exception
    {
        userAccess = find();

        String poaName = POANameHelper.getPOAName(this);

        UserAccessV2Delegate delegate = new UserAccessV2Delegate(userAccess);

        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = UserAccessV2Helper.narrow(obj);
    }

    public void clientInitialize()
            throws Exception
    {
        create();
        if (Log.isDebugOn())
        {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public void clientShutdown()
    {
    }
}
