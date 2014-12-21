package com.cboe.application.inprocess;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.TestInProcessEntriesHome;
import com.cboe.interfaces.application.inprocess.TestInProcessEntries;
import com.cboe.domain.startup.ClientBOHome;


/**
 * @author Jing Chen
 */
public class TestInProcessEntriesHomeImpl extends ClientBOHome implements TestInProcessEntriesHome
{
    public TestInProcessEntries create()
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "Creating TestInProcessEntriesImpl");
        }
        TestInProcessEntriesImpl bo = new TestInProcessEntriesImpl();

        //Every bo object must be added to the container.
        addToContainer(bo);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        return bo;
    }

    public void clientStart()
    {
        create();
    }
}
