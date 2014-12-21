package com.cboe.domain.util;

import com.cboe.idl.cmiErrorCodes.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.exceptions.*;
import com.cboe.util.*;
import java.util.*;

public class UserPreferenceCache
{
    private Map users;

    public UserPreferenceCache()
    {
        users = new HashMap();
    }

    private Map findUserMap(String userName)
    {
        return (Map) users.get(userName);
    }

    private Map makeUserMap(String userName)
    {
        Map userMap = findUserMap(userName);
        if (userMap == null)
        {
            userMap = new HashMap();
            users.put(userName, userMap);
        }
        return userMap;
    }

    /**
     * Returns a sequence of preferences that match the search path criteria.
     *
     * @author Derek T. Chambers-Boucher
     * @param path the complete path of the preference to find.
     */
    public synchronized PreferenceStruct[] getPreferences(String userName, String path, boolean includeSubPath)
    {
        Map userMap = findUserMap(userName);
        if (userMap == null)
            return new PreferenceStruct[0];

        ArrayList list = new ArrayList();
        if (includeSubPath)
        {
            String[] paths = (String[]) userMap.keySet().toArray(new String[0]);
            for (int i = 0; i < paths.length; i++)
            {
                if (paths[i].startsWith(path))
                    list.add(userMap.get(paths[i]));
            }
        }
        else
        {
            PreferenceStruct struct = (PreferenceStruct) userMap.get(path);
            if (struct != null)
                list.add(struct);
        }

        return (PreferenceStruct[]) list.toArray(new PreferenceStruct[0]);
    }

    /**
     * This method adds or updates a preference to the cache.
     *
     * @author Derek T. Chambers-Boucher
     * @param preference the PreferenceStruct to set.
     */
    public synchronized void setPreference(String userName, PreferenceStruct preference) throws DataValidationException
    {
        makeUserMap(userName).put(preference.name, preference);
    }

    /**
     * Removes the given preference from the cache.
     *
     * @param preference the preference to remove from the cache.
     */
    public synchronized void removePreference(String userName, String path)
    {
        Map userMap = findUserMap(userName);
        if (userMap != null)
        {
            String[] paths = (String[]) userMap.keySet().toArray(new String[0]);
            for (int i = 0; i < paths.length; i++)
            {
                if (paths[i].startsWith(path))
                    userMap.remove(paths[i]);
            }
            if (userMap.isEmpty())
                users.remove(userName);
        }
    }
}