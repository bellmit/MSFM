package com.cboe.interfaces.domain.instrumentedChannel;

/**
 * @author Jing Chen
 */
public interface InstrumentationUserData
{
    public Object getUserData();
    public void addUserData(String key, String value);
    public void removeUserData(String key, String value);
}
