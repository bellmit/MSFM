package com.cboe.application.cas;


import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.delegates.application.IntermarketUserAccessDelegate;
import com.cboe.idl.cmiIntermarket.IntermarketUserAccessHelper;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.IntermarketUserAccess;
import com.cboe.interfaces.application.IntermarketUserAccessHome;
import com.cboe.domain.startup.ClientBOHome;

public class IntermarketUserAccessHomeImpl extends ClientBOHome implements IntermarketUserAccessHome
{

    private IntermarketUserAccess imUserAccess;
    private com.cboe.idl.cmiIntermarket.IntermarketUserAccess imUserAccessCorba;
    private String sessionId;
    private char sessionMode;

    public IntermarketUserAccess create()
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "Creating IntermarketUserAccess");
        }
        if (imUserAccess == null)
        {
            IntermarketUserAccessImpl bo = new IntermarketUserAccessImpl();
            bo.create(String.valueOf(bo.hashCode()));
            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            IntermarketUserAccessInterceptor boi = null;
            try
            {
                boi = (IntermarketUserAccessInterceptor) this.createInterceptor(bo);
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
            imUserAccess = boi;
        }
        return imUserAccess;
    }

    public IntermarketUserAccess find()
    {
        return create();
    }

    public String objectToString()
    {
        try {
            if (imUserAccessCorba != null)
            {
                FoundationFramework ff = FoundationFramework.getInstance();
                return ff.getOrbService().getOrb().object_to_string(imUserAccessCorba);
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
        imUserAccess = find();

        String poaName = POANameHelper.getPOAName(this);

        IntermarketUserAccessDelegate delegate = new IntermarketUserAccessDelegate(imUserAccess);

        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                register_object(delegate, poaName);
        imUserAccessCorba = IntermarketUserAccessHelper.narrow(obj);
    }

    public void clientInitialize()
            throws Exception
    {
        create();
        if (Log.isDebugOn()) {
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }
    }

    public void clientShutdown()
    {
    }
}
