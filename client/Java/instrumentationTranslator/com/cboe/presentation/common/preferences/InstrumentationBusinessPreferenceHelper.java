//
// -----------------------------------------------------------------------------------
// Source file: InstrumentationBusinessPreferenceHelper.java
//
// PACKAGE: com.cboe.presentation.dbQueryBuilder.preferences
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.preferences;

import static com.cboe.interfaces.presentation.preferences.PreferenceConstants.DEFAULT_PATH_SEPARATOR;
import static com.cboe.presentation.common.properties.InstrumentationProperties.ALARM_NOTIFICATION_SECTION_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.ALARM_QUERY_WINDOW_BEGIN_KEY;
import static com.cboe.presentation.common.properties.InstrumentationProperties.ALARM_QUERY_WINDOW_END_KEY;
import static com.cboe.presentation.common.properties.InstrumentationProperties.CACHE_EXPIRE_COUNT_PROPERTY_KEY;
import static com.cboe.presentation.common.properties.InstrumentationProperties.HIGH_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.HIGH_NOTIFICATION_COLOR_KEY_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.LOW_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.LOW_NOTIFICATION_COLOR_KEY_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.MEDIUM_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.MEDIUM_NOTIFICATION_COLOR_KEY_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.NOTIFICATION_PLAY_SOUND_KEY_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.HIGH_NOTIFICATION_EVICTION_TIME_KEY_NAME;
import static com.cboe.presentation.common.properties.InstrumentationProperties.MEDIUM_NOTIFICATION_EVICTION_TIME_KEY_NAME;

import java.awt.Color;
import java.util.prefs.InvalidPreferencesFormatException;

import com.cboe.interfaces.presentation.preferences.BusinessPreferenceManager;
import com.cboe.interfaces.presentation.preferences.PreferenceNotFoundException;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.InstrumentationProperties;

public class InstrumentationBusinessPreferenceHelper
{
    public static final String ALARM_QUERY_WINDOW_BEGIN = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + ALARM_QUERY_WINDOW_BEGIN_KEY;
    public static final String ALARM_QUERY_WINDOW_END = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + ALARM_QUERY_WINDOW_END_KEY;
    public static final String LOW_NOTIFICATION_CLEAR_TIMOUT = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + LOW_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME;
    public static final String MEDIUM_NOTIFICATION_CLEAR_TIMOUT = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + MEDIUM_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME;
    public static final String HIGH_NOTIFICATION_CLEAR_TIMOUT = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + HIGH_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME;
    public static final String LOW_NOTIFICATION_COLOR = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + LOW_NOTIFICATION_COLOR_KEY_NAME;
    public static final String MEDIUM_NOTIFICATION_COLOR = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + MEDIUM_NOTIFICATION_COLOR_KEY_NAME;
    public static final String HIGH_NOTIFICATION_COLOR = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + HIGH_NOTIFICATION_COLOR_KEY_NAME;
    public static final String NOTIFICATION_PLAY_SOUND = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + NOTIFICATION_PLAY_SOUND_KEY_NAME;
    public static final String CACHE_EXPIRE_COUNT = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + CACHE_EXPIRE_COUNT_PROPERTY_KEY;
    public static final String HIGH_NOTIFICATION_EVICTION_TIME = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + HIGH_NOTIFICATION_EVICTION_TIME_KEY_NAME;
    public static final String MEDIUM_NOTIFICATION_EVICTION_TIME = ALARM_NOTIFICATION_SECTION_NAME + DEFAULT_PATH_SEPARATOR + MEDIUM_NOTIFICATION_EVICTION_TIME_KEY_NAME;

    private InstrumentationBusinessPreferenceHelper() {}

    public static int getAlarmQueryWindowBeginMillis()
    {
        int returnValue = 0;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getIntPreference(ALARM_QUERY_WINDOW_BEGIN);
        }
        catch(PreferenceNotFoundException e1)
        {
            try
            {
                returnValue = InstrumentationProperties.getAlarmQueryWindowBeginMillis();
            }
            catch(InvalidPreferencesFormatException e2)
            {
                GUILoggerHome.find().exception("Could not get default value for: " + ALARM_QUERY_WINDOW_BEGIN, e2);
            }
        }
        return returnValue;
    }

    public static void setAlarmQueryWindowBeginMillis(int millis)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(ALARM_QUERY_WINDOW_BEGIN, millis);
    }

    public static int getAlarmQueryWindowEndMillis()
    {
        int returnValue = 0;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getIntPreference(ALARM_QUERY_WINDOW_END);
        }
        catch(PreferenceNotFoundException e1)
        {
            try
            {
                returnValue = InstrumentationProperties.getAlarmQueryWindowEndMillis();
            }
            catch(InvalidPreferencesFormatException e2)
            {
                GUILoggerHome.find().exception("Could not get default value for: " + ALARM_QUERY_WINDOW_END, e2);
            }
        }
        return returnValue;
    }

    public static void setAlarmQueryWindowEndMillis(int millis)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(ALARM_QUERY_WINDOW_END, millis);
    }

    public static int getNotificationCacheExpireCount()
    {
        int returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getIntPreference(CACHE_EXPIRE_COUNT);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getNotificationCacheExpireCount();
        }
        return returnValue;
    }

    public static void setNotificationCacheExpireCount(int count)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(CACHE_EXPIRE_COUNT, count);
    }

    public static boolean isNotificationPlaySoundEnabled()
    {
        boolean returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getBooleanPreference(NOTIFICATION_PLAY_SOUND);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.isNotificationPlaySoundEnabled();
        }
        return returnValue;
    }

    public static void setNotificationPlaySoundEnabled(boolean enabled)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(NOTIFICATION_PLAY_SOUND, enabled);
    }

    public static int getLowNotificationClearTimoutMillis() throws InvalidPreferencesFormatException
    {
        int returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getIntPreference(LOW_NOTIFICATION_CLEAR_TIMOUT);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getLowNotificationClearTimoutMillis();
        }
        return returnValue;
    }

    public static void setLowNotificationClearTimoutMillis(int millis)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(LOW_NOTIFICATION_CLEAR_TIMOUT, millis);
    }

    public static int getMediumNotificationClearTimoutMillis() throws InvalidPreferencesFormatException
    {
        int returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getIntPreference(MEDIUM_NOTIFICATION_CLEAR_TIMOUT);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getMediumNotificationClearTimoutMillis();
        }
        return returnValue;
    }

    public static void setMediumNotificationClearTimoutMillis(int millis)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(MEDIUM_NOTIFICATION_CLEAR_TIMOUT, millis);
    }

    public static int getHighNotificationClearTimoutMillis() throws InvalidPreferencesFormatException
    {
        int returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getIntPreference(HIGH_NOTIFICATION_CLEAR_TIMOUT);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getHighNotificationClearTimoutMillis();
        }
        return returnValue;
    }

    public static void setHighNotificationClearTimoutMillis(int millis)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(HIGH_NOTIFICATION_CLEAR_TIMOUT, millis);
    }

    public static Color getLowNotificationColor() throws InvalidPreferencesFormatException
    {
        Color returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            int intValue = manager.getIntPreference(LOW_NOTIFICATION_COLOR);
            returnValue = new Color(intValue);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getLowNotificationColor();
        }
        return returnValue;
    }

    public static void setLowNotificationColor(Color color)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(LOW_NOTIFICATION_COLOR, color.getRGB());
    }

    public static Color getMediumNotificationColor() throws InvalidPreferencesFormatException
    {
        Color returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            int intValue = manager.getIntPreference(MEDIUM_NOTIFICATION_COLOR);
            returnValue = new Color(intValue);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getMediumNotificationColor();
        }
        return returnValue;
    }

    public static void setMediumNotificationColor(Color color)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(MEDIUM_NOTIFICATION_COLOR, color.getRGB());
    }

    public static Color getHighNotificationColor() throws InvalidPreferencesFormatException
    {
        Color returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            int intValue = manager.getIntPreference(HIGH_NOTIFICATION_COLOR);
            returnValue = new Color(intValue);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getHighNotificationColor();
        }
        return returnValue;
    }

    public static void setHighNotificationColor(Color color)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(HIGH_NOTIFICATION_COLOR, color.getRGB());
    }

    public static int getNotificationCacheHighEvictTime() throws InvalidPreferencesFormatException
    {
        int returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getIntPreference(HIGH_NOTIFICATION_EVICTION_TIME);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getHighNotificationEvictTime();
        }
        return returnValue;
    }

    public static void setNotificationCacheHighEvictTime(int seconds)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(HIGH_NOTIFICATION_EVICTION_TIME, seconds);
    }

    public static int getNotificationCacheMediumEvictTime() throws InvalidPreferencesFormatException
    {
        int returnValue;

        //noinspection UnusedCatchParameter
        try
        {
            BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
            returnValue = manager.getIntPreference(MEDIUM_NOTIFICATION_EVICTION_TIME);
        }
        catch(PreferenceNotFoundException e1)
        {
            returnValue = InstrumentationProperties.getMediumNotificationEvictTime();
        }
        return returnValue;
    }

    public static void setNotificationCacheMediumEvictTime(int seconds)
    {
        BusinessPreferenceManager manager = PreferenceManagerHome.findBusinessPreferenceManager();
        manager.setPreference(MEDIUM_NOTIFICATION_EVICTION_TIME, seconds);
    }
}
