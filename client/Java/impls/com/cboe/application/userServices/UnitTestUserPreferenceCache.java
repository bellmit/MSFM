
/**
 * Title:        null<p>
 * Description:  null<p>
 * Copyright:    null<p>
 * Company:      null<p>
 * @author null
 * @version null
 */
package com.cboe.application.userServices;

import com.cboe.idl.cmiUser.*;
import com.cboe.exceptions.*;
import com.cboe.domain.util.UserPreferenceCache;

public class UnitTestUserPreferenceCache
{
    public UserPreferenceCache cache;

    public UnitTestUserPreferenceCache()
    {
        cache = new UserPreferenceCache();
    }

    public UserPreferenceCache getCache()
    {
        return cache;
    }

    public static void main(String[] args)
    {
        UnitTestUserPreferenceCache inst = new UnitTestUserPreferenceCache();
        try
        {
            inst.getCache().setPreference("User1", new PreferenceStruct("com.cboe.test1.preference1", "the first preference"));
            inst.getCache().setPreference("User1", new PreferenceStruct("com.cboe.test1.preference2", "the second preference"));
            inst.getCache().setPreference("User1", new PreferenceStruct("com.cboe.test2.preference3", "the third preference"));
            inst.getCache().setPreference("User1", new PreferenceStruct("com.cboe.test2.preference4", "the fourth preference"));
        }
        catch(DataValidationException e)
        {
            System.out.println(e.details.message);
        }

        System.out.println("\nTestSub0...\n");
        PreferenceStruct[] prefs = inst.getCache().getPreferences("User2", "com", true);
        System.out.println("Found " + prefs.length + " preferences for User2.");
        for (int i = 0; i < prefs.length; i++)
        {
            System.out.println("Got " + prefs[1].name + "\t" + prefs[i].value);
        }

        System.out.println("\nTest0...\n");
        prefs = inst.getCache().getPreferences("User1", "com", true);
        System.out.println("Found " + prefs.length + " preferences.");
        for (int i = 0; i < prefs.length; i++)
        {
            System.out.println("Got " + prefs[1].name + "\t" + prefs[i].value);
        }

        System.out.println("\nTest1...\n");
        prefs = inst.getCache().getPreferences("User1", "com.cboe.test1", true);
        System.out.println("Found " + prefs.length + " preferences.");
        for (int i = 0; i < prefs.length; i++)
        {
            System.out.println("Got " + prefs[1].name + "\t" + prefs[i].value);
        }

        System.out.println("\nTest2...\n");
        prefs = inst.getCache().getPreferences("User1", "com.cboe.test2", true);
        System.out.println("Found " + prefs.length + " preferences.");
        for (int i = 0; i < prefs.length; i++)
        {
            System.out.println("Got " + prefs[i].name + "\t" + prefs[i].value);
        }

        System.out.println("\nTest3...removing second preference\n");
        inst.getCache().removePreference("User1", "com.cboe.test1.preference2");
        prefs = inst.getCache().getPreferences("User1", "com.cboe.test1", true);
        System.out.println("Found " + prefs.length + " preferences.");
        for (int i = 0; i < prefs.length; i++)
        {
            System.out.println("Got " + prefs[i].name + "\t" + prefs[i].value);
        }

        System.out.println("\nTest4...removing all test2 preferences\n");
        inst.getCache().removePreference("User1", "com.cboe.test2");
        prefs = inst.getCache().getPreferences("User1", "com.cboe.test2", true);
        System.out.println("Found " + prefs.length + " preferences.");
        for (int i = 0; i < prefs.length; i++)
        {
            System.out.println("Got " + prefs[i].name + "\t" + prefs[i].value);
        }

        System.out.println("\nTest5...getting all remaining preferences\n");
        prefs = inst.getCache().getPreferences("User1", "com.", true);
        System.out.println("Found " + prefs.length + " preferences.");
        for (int i = 0; i < prefs.length; i++)
        {
            System.out.println("Got " + prefs[i].name + "\t" + prefs[i].value);
        }

        System.out.println("\nTest6...creating a path using an existing preference name\n");
        System.out.println("\tcreating preference com.cboe.preference5...");
        try
        {
            inst.getCache().setPreference("User1", new PreferenceStruct("com.cboe.preference5", "com.cboe.preference5"));
        }
        catch(DataValidationException e)
        {
            System.out.println(e.details.message);
        }
        System.out.println("\tattempting to create preference com.cboe.preference5.preferenceA");
        try
        {
            inst.getCache().setPreference("User1", new PreferenceStruct("com.cboe.preference5.preferenceA", "com.cboe.preference5.preferenceA"));
        }
        catch(DataValidationException e)
        {
            System.out.println(e.details.message);
        }

        System.out.println("\nTest7...creating a preference using an existing path name\n");
        System.out.println("\tcreating preference com.cboe.preference6.preferenceA");
        try
        {
            inst.getCache().setPreference("User1", new PreferenceStruct("com.cboe.preference6.preferenceA", "com.cboe.preference6.preferenceA"));
        }
        catch(DataValidationException e)
        {
            System.out.println(e.details.message);
        }
        System.out.println("\tattempting to create preference com.cboe.preference6...");
        try
        {
            inst.getCache().setPreference("User1", new PreferenceStruct("com.cboe.preference6", "com.cboe.preference6"));
        }
        catch(DataValidationException e)
        {
            System.out.println(e.details.message);
        }
    }
}
