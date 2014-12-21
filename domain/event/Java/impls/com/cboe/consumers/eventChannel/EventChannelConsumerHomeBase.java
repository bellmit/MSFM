package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.EventChannelConsumerManager;
import com.cboe.interfaces.events.EventChannelConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.eventService.ConsumerFilter;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.systemsManagementService.PropertyQuery;
import org.omg.PortableServer.Servant;

import java.util.HashMap;

/**
 * This class exists to replace the EventChannelFilterHelper.  See com.cboe.mdx.consumers.eventChannel for
 * an example of how to use this class to replace the EventChannelFilterHelper.
 * 
 * @author Eric Fredericks
 */
public abstract class EventChannelConsumerHomeBase extends ClientBOHome implements EventChannelConsumerHome
{
    private HashMap filters;
    private String cachedFullChannelName;
    private String cachedDefaultMethod;
    private ClientBOHome eventChannelHome;
    private EventService eventService;

    private static final String EVENT_CHANNEL = "EventChannel";
    private static final String NO_EVENTS_CONSTRAINT = "0";

    protected EventChannelConsumerHomeBase()
    {
        filters = new HashMap();
        eventChannelHome = null;
        cachedFullChannelName = null;
        cachedDefaultMethod = null;
        eventService = null;
    }
    
    protected void setupEventConsumer() throws Exception
    {
        if(eventService == null)
        {
            eventService = FoundationFramework.getInstance().getEventService();
            Servant servant = getServant();
            String interfaceRepId = getInterfaceRepId();
            String fullChannelName = getFullChannelName();
            String defaultMethod = getDefaultMethod();
            
            if(Log.isDebugOn())
            {
                Log.debug(this, "connect event service: servant=" + servant + " interfaceRepId=" + interfaceRepId + " fullChannelName=" + fullChannelName + " defaultMethod=" + defaultMethod + " constraint=" + NO_EVENTS_CONSTRAINT);
                Log.debug(this, "Turning off event channel : " + fullChannelName + " : " + interfaceRepId);
            }
            
            ConsumerFilter inclusionFilter = eventService.createNewInclusionFilter(servant, interfaceRepId, defaultMethod, NO_EVENTS_CONSTRAINT, fullChannelName);
            eventService.applyFilter(inclusionFilter);
            eventService.connectTypedNotifyChannelConsumer(fullChannelName, interfaceRepId, servant);
        }
    }
    
    public synchronized void addFilter(ChannelKey filterKey)
    {
        EventChannelFilter filterWrapper = (EventChannelFilter) filters.get(filterKey);

        if(filterWrapper != null)
        {
            filterWrapper.increase();
            return;
        }
        
        try
        {
            String keyString = filterKey.toString();
            StringBuilder addFilter = new StringBuilder(keyString.length()+30);
            addFilter.append("Attempting to add filter for ").append(keyString);
            Log.information(this, addFilter.toString());
    
            Servant servant = getServant();
            String interfaceRepId = getInterfaceRepId();
            String fullChannelName = getFullChannelName();
            String methodName = getMethodForChannelType(filterKey.channelType);
            String constraintString = getConstraintString(filterKey);
            
            if(Log.isDebugOn())
            {
                Log.debug(this, "add filter: servant=" + servant + " interfaceRepId=" + interfaceRepId + " fullChannelName=" + fullChannelName + " methodName=" + methodName + " constraint=" + constraintString);
            }

            ConsumerFilter eventFilter = eventService.createNewInclusionFilter(servant, interfaceRepId, methodName, constraintString, fullChannelName);
            eventService.applyFilter(eventFilter);
            
            filterWrapper = new EventChannelFilter(eventFilter);
            filters.put(filterKey, filterWrapper);
        }
        catch(Exception e)
        {
            Log.exception(this, "Error adding event filter. " + e.getMessage(), e);
        }
    }

    public synchronized void removeFilter(ChannelKey filterKey)
    {
        EventChannelFilter filterWrapper = (EventChannelFilter) filters.get(filterKey);

        if(filterWrapper == null)
        {
            Log.alarm(this, "Error removing filter for filter key=" + filterKey.toString() + " - Filter does not exist");
            return;
        }
        
        int remainingCount = filterWrapper.decrease();
        if(remainingCount == 0)
        {
            eventService.removeFilter(filterWrapper.getConsumerFilter());
            filters.remove(filterKey);
        }
    }
    
    protected abstract String getChannel();
    protected abstract String getInterfaceRepId();
    protected abstract Servant getServant();
    protected abstract String getMethodForChannelType(final int channelType) throws Exception;
    protected abstract String getConstraintString(final ChannelKey filterKey) throws Exception;

    protected String getDefaultMethod() throws Exception
    {
        if(cachedDefaultMethod == null)
        {
            PropertyQuery pq = PropertyQuery.queryFor("defaultMethod").from(EVENT_CHANNEL, getChannel());
            cachedDefaultMethod = getEventChannelHome().getProperty( pq.queryString() );
        }
        return cachedDefaultMethod;
    }
    
    public String getFullChannelName() throws Exception
    {
        if(cachedFullChannelName == null)
        {
            PropertyQuery pq = PropertyQuery.queryFor("channelName").from(EVENT_CHANNEL, getChannel());
            cachedFullChannelName = getEventChannelHome().getProperty(pq.queryString());
        }
        return cachedFullChannelName;
    }
    
    private ClientBOHome getEventChannelHome() throws Exception
    {
        if(eventChannelHome == null)
        {
            eventChannelHome = ((ClientBOHome) HomeFactory.getInstance().findHome(EventChannelHome.HOME_NAME));
        }
        return eventChannelHome;
    }
    
}
