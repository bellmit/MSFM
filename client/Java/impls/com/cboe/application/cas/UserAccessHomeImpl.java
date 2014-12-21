package com.cboe.application.cas;

/**
 * This type was created in VisualAge.
 */

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.POANameHelper;
import com.cboe.delegates.application.UserAccessDelegate;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.startup.StartupHelper;
import com.cboe.idl.cmi.UserAccessHelper;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.interfaces.application.UserAccess;
import com.cboe.interfaces.application.UserAccessHome;

public class UserAccessHomeImpl extends UserAccessBaseHomeImpl implements UserAccessHome
{

    private UserAccess userAccess;
    private com.cboe.idl.cmi.UserAccess userAccessCorba;
    private String sessionId;

    public UserAccessHomeImpl()
    {
        super();
    }

    public UserAccess create()
    {
        if (userAccess == null)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Creating UserAccessImpl");
            }
            UserAccessImpl bo = new UserAccessImpl(sessionMode, heartbeatTimeout, cmiVersion);
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            UserAccessInterceptor boi = null;
            try
            {
                boi = (UserAccessInterceptor) this.createInterceptor(bo);
            } catch (Exception ex)
            {
                Log.exception(this, "Failed to create UserAccess", ex);
                return null;
            }
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
            //Every BOObject create MUST have a name...if the object is to be a managed object.
            userAccess = boi;
        }
        return userAccess;
    }

    public UserAccess find()
    {
        return create();
    }

    public String objectToString()
    {
        return super.objectToString(userAccessCorba);
    }

    public void clientStart()
            throws Exception
    {
        UserAccess userAccess = find();
        String poaName = POANameHelper.getPOAName(this);
        UserAccessDelegate delegate = new UserAccessDelegate(userAccess);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        userAccessCorba = UserAccessHelper.narrow(obj);
    }

    public void clientInitialize()
            throws Exception
    {
        StartupHelper.setStartupStatus(StartupHelper.STARTING);

        if (Log.isDebugOn())
        {
            Log.debug(this, "Authenticating CAS with Security Service...");
        }
        sessionId = FoundationFramework.getInstance().getSecurityService().authenticateWithCertificate();
        if (sessionId == null)
        {
            Log.alarm("Authentication Failed!");
        }
        /*if (Log.isDebugOn())
        {
            //Log.debug(this, "Creating Client interceptor for CAS...");        	
        }*/
        //boolean interceptorReturnCode = FoundationFramework.getInstance().getSecurityService().createCasClientInterceptor(sessionId);
        if (Log.isDebugOn())
        {
        	//Log.debug(this, "Created Client interceptor for CAS:" + interceptorReturnCode);
        	Log.debug(this, "Skipped creating Client interceptor for CAS:");            
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
        super.clientInitialize();
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating User Access Object to be bound with the Orb...");
        }
        create();
    }

    public void clientShutdown()
    {
        super.clientShutdown();
    }
}
