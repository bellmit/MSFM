package com.cboe.application.shared.consumer;

import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.interfaces.domain.session.SessionBasedCollector;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import org.omg.CORBA.UserException;

/**
 * @author Jing Chen
 */
public abstract class InstrumentedProcessor implements InstrumentedEventChannelListener
{
    private final static String PROCESSOR_NAME = "Processor";
    protected String name;
    protected InstrumentorUserData userData;
    public InstrumentedProcessor(SessionBasedCollector collector)
    {
        try
        {
            name = InstrumentorNameHelper.createInstrumentorName(new String[]{
                collector.getSessionManager().getInstrumentorName(),
                getMessageType(),
                PROCESSOR_NAME}, this);
        }
        catch(UserException e)
        {
            Log.exception(e);
        }
    }

    public String getName()
    {
        return name;
    }

    public Object getUserData()
    {
        return userData;
    }

    public void addUserData(String key, String value)
    {
        if(userData == null)
        {
            userData = new InstrumentorUserData();
        }

        try
        {
            userData.addValue(key, value);
        }
        catch(IllegalArgumentException e)
        {
            Log.exception("Exception adding user data key=\"" + key + "\" value=\"" + value + "\"", e);
        }
    }

    public void removeUserData(String key, String value)
    {
        if(userData == null || !userData.removeValueForKey(key, value))
        {
            Log.information("Unable to remove user data key=\"" + key + "\" value=\"" + value + "\"");
        }
    }
    public void queueInstrumentationInitiated()
    {}
    public abstract String getMessageType();
}
