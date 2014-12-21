package com.cboe.domain.util;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.interfaces.domain.DependencyCondition;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.State;

public class EventChannelConsumerActivePOACondition implements DependencyCondition
{
    private final POAManager poaManager;

    public EventChannelConsumerActivePOACondition(final String channelName)
    {
        poaManager = FoundationFramework.getInstance().getOrbService().getChannelPOA(channelName).the_POAManager();
    }

    public boolean conditionMet()
    {
        return poaManager.get_state().value() == State._ACTIVE;
    }
}
