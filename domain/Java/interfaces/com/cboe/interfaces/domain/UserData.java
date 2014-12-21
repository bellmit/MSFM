package com.cboe.interfaces.domain;

/**
 * Created by IntelliJ IDEA.
 * User: brazhni
 * Date: Aug 23, 2006
 * Time: 4:26:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserData
{
    void addValue(String key, String value);

    boolean removeValueForKey(String key, String value);

    String[] removeAllValuesForKey(String key);

    String[] getAllKeys();

    String[] getValues(String key);

    String toString();

    String[] toStringArray();

    boolean containsKey(String key);
}
