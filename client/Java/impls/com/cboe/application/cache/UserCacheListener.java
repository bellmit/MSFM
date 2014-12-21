package com.cboe.application.cache;

import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Hashtable;

/**
 * @author Jing Chen
 */
public abstract class UserCacheListener implements InstrumentedEventChannelListener
{
    String name;
    InstrumentorUserData userData;

    public UserCacheListener(String userId)
    {
        String className = this.getClass().getName();
        name = InstrumentorNameHelper.createInstrumentorName(new String[]{userId,className.substring(className.lastIndexOf('.')+1)}, this);
        userData = new InstrumentorUserData();
    }

    public UserCacheListener(String userId, Hashtable userTable)
    {
        // not all subclasseses will use this one
        String className = this.getClass().getName();
        name = InstrumentorNameHelper.createInstrumentorName(new String[]{userId,className.substring(className.lastIndexOf('.')+1)}, this);
        userData = new InstrumentorUserData();
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
    {

    }
}
