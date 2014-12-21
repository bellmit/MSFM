package com.cboe.application.cas;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.UserAccessOMTHome;
import com.cboe.interfaces.application.UserAccessOMT;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.application.UserAccessOMTDelegate;
import com.cboe.idl.omt.UserAccessOMTHelper;

public class UserAccessOMTHomeImpl extends ClientBOHome implements UserAccessOMTHome
{
    private UserAccessOMT userAccess;
    private com.cboe.idl.omt.UserAccessOMT userAccessCorba;

    public UserAccessOMT create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessOMT");
        }
        if(userAccess == null)
        {
            UserAccessOMTImpl bo = new UserAccessOMTImpl();
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            UserAccessOMTInterceptor boi;
            try
            {
                boi = (UserAccessOMTInterceptor) this.createInterceptor(bo);
                if(getInstrumentationEnablementProperty())
                {
                    boi.startInstrumentation(getInstrumentationProperty());
                }
            }
            catch(Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessOMT", ex);
                return null;
            }

            //Every BOObject create MUST have a name...if the object is to be a managed object.
            userAccess = boi;
        }
        return userAccess;
    }

    public UserAccessOMT find()
    {
        return create();
    }

    public String objectToString()
    {
        try
        {
            if(userAccessCorba != null)
            {
                FoundationFramework ff = FoundationFramework.getInstance();
                return ff.getOrbService().getOrb().object_to_string(userAccessCorba);
            }
            else
            {
                return null;
            }
        }
        catch(Exception e)
        {
            Log.exception(this, "Could not stringify the userAccess object", e);
            return null;
        }
    }

    public void clientStart() throws Exception
    {
        userAccess = find();

        String poaName = POANameHelper.getPOAName(this);

        UserAccessOMTDelegate delegate = new UserAccessOMTDelegate(userAccess);

        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = UserAccessOMTHelper.narrow(obj);
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
