package com.cboe.floorApplication.userAccess;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.floorApplication.UserAccessFloorHome;
import com.cboe.interfaces.floorApplication.UserAccessFloor;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.floorApplication.UserAccessFloorDelegate;
import com.cboe.idl.floorApplication.UserAccessFloorHelper;

/**
 * Author: mahoney
 * Date: Jul 17, 2007
 */
public class UserAccessFloorHomeImpl extends ClientBOHome implements UserAccessFloorHome
{
    private UserAccessFloor userAccess;
    private com.cboe.idl.floorApplication.UserAccessFloor userAccessCorba;

    public UserAccessFloor create()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserAccessFloor");
        }
        if(userAccess == null)
        {
            UserAccessFloorImpl bo = new UserAccessFloorImpl();
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            UserAccessFloorInterceptor boi;
            try
            {
                boi = (UserAccessFloorInterceptor) this.createInterceptor(bo);
                if(getInstrumentationEnablementProperty())
                {
                    boi.startInstrumentation(getInstrumentationProperty());
                }
            }
            catch(Exception ex)
            {
                Log.exception(this, "Failed to create UserAccessFloor", ex);
                return null;
            }

            //Every BOObject create MUST have a name...if the object is to be a managed object.
            userAccess = boi;
        }
        return userAccess;
    }

    public UserAccessFloor find()
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

        UserAccessFloorDelegate delegate = new UserAccessFloorDelegate(userAccess);

        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = UserAccessFloorHelper.narrow(obj);
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
}
