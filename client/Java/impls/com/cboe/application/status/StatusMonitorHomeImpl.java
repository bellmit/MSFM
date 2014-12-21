package com.cboe.application.status;

import com.cboe.interfaces.application.StatusMonitorHome;
import com.cboe.interfaces.application.StatusMonitor;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class StatusMonitorHomeImpl extends ClientBOHome implements StatusMonitorHome
{

    public StatusMonitor create()
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "Creating StatusMonitorImpl");
        }
        StatusMonitorImpl bo = new StatusMonitorImpl();

        //Every bo object must be added to the container.
        addToContainer(bo);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        bo.queueMonitorOnCallback("100");

        return bo;
    }

    public void clientStart()
    {
        create();
    }
}
