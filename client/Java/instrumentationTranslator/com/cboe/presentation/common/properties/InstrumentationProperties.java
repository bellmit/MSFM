//
// -----------------------------------------------------------------------------------
// Source file: InstrumentationProperties.java
//
// PACKAGE: com.cboe.presentation.common.properties;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.properties;

import java.awt.*;
import java.io.IOException;
import java.util.prefs.*;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * This class is used to get the default values for these properties. It should not be used to set the values
 * because they will be saved in the properties file and replaced with the next install. Use InstrumentationBusinessPreferenceHelper
 * for getting and setting the values.
 */
public class InstrumentationProperties
{
    public static final String ALARM_NOTIFICATION_SECTION_NAME = "AlarmNotification";

    public static final String LOW_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME = "NotificationIndicatorLowClearTimeout";
    public static final String MEDIUM_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME = "NotificationIndicatorMediumClearTimeout";
    public static final String HIGH_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME = "NotificationIndicatorHighClearTimeout";
    public static final String NOTIFICATION_EXPIRE_MEMORY_USAGE_PERCENTAGE = "NotificationExpireMemoryUsagePercentage";

    public static final String HIGH_NOTIFICATION_EVICTION_TIME_KEY_NAME = "NotificationCacheHighEvictTime";
    public static final String MEDIUM_NOTIFICATION_EVICTION_TIME_KEY_NAME = "NotificationCacheMediumEvictTime";

    public static final String LOW_NOTIFICATION_COLOR_KEY_NAME = "NotificationIndicatorLowColor";
    public static final String MEDIUM_NOTIFICATION_COLOR_KEY_NAME = "NotificationIndicatorMediumColor";
    public static final String HIGH_NOTIFICATION_COLOR_KEY_NAME = "NotificationIndicatorHighColor";

    public static final String NOTIFICATION_PLAY_SOUND_KEY_NAME = "NotificationPlaySound";

    public static final String CACHE_EXPIRE_COUNT_PROPERTY_KEY = "CacheExpireCount";
    public static final String SUBSCRIBE_FOR_NOTIFICATIONS_KEY = "SubscribeForNotifications";

    public static final String ALARM_QUERY_WINDOW_BEGIN_KEY = "AlarmQueryWindowBegin";
    public static final String ALARM_QUERY_WINDOW_END_KEY = "AlarmQueryWindowEnd";
    public static final String MAX_ALARM_QUERY_WINDOW_BEGIN_KEY = "MaxAlarmQueryWindowBegin";
    public static final String MAX_ALARM_QUERY_WINDOW_END_KEY = "MaxAlarmQueryWindowEnd";


    public static final int DEFAULT_CACHE_EXPIRE_COUNT = 0;

    private InstrumentationProperties() {}

    public static int getAlarmQueryWindowBeginMillis() throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, ALARM_QUERY_WINDOW_BEGIN_KEY);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        ALARM_QUERY_WINDOW_BEGIN_KEY +
                                                        " contained an invalid value.", e);
        }
    }

    public static int getAlarmQueryWindowEndMillis() throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, ALARM_QUERY_WINDOW_END_KEY);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        ALARM_QUERY_WINDOW_END_KEY +
                                                        " contained an invalid value.", e);
        }
    }

    public static int getMaxAlarmQueryWindowBeginMillis() throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, MAX_ALARM_QUERY_WINDOW_BEGIN_KEY);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        MAX_ALARM_QUERY_WINDOW_BEGIN_KEY +
                                                        " contained an invalid value.", e);
        }
    }

    public static int getMaxAlarmQueryWindowEndMillis() throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, MAX_ALARM_QUERY_WINDOW_END_KEY);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        MAX_ALARM_QUERY_WINDOW_END_KEY +
                                                        " contained an invalid value.", e);
        }
    }

    public static boolean isSubscribeForNotifications()
    {
        boolean subscribeForNotifications = true;
        try
        {
            String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, SUBSCRIBE_FOR_NOTIFICATIONS_KEY);
            if(property != null)
            {
                subscribeForNotifications = Boolean.valueOf(property);
            }
        }
        catch (InvalidPreferencesFormatException e)
        {
            GUILoggerHome.find().exception("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                           SUBSCRIBE_FOR_NOTIFICATIONS_KEY +
                                           " contained an invalid value.", e);
        }
        return subscribeForNotifications;
    }

    public static void setSubscribeForNotifications(boolean subscribeForNotifications)
    {
        try
        {
            setProperty(ALARM_NOTIFICATION_SECTION_NAME, SUBSCRIBE_FOR_NOTIFICATIONS_KEY,
                        String.valueOf(subscribeForNotifications));
        }
        catch (IOException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not set the Application Properties.");
        }
    }

    public static double getNotificationExpireMemoryUsagePercentage()
    {
        double returnValue = 80.0;
        try
        {
            String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, NOTIFICATION_EXPIRE_MEMORY_USAGE_PERCENTAGE);
            returnValue = Double.parseDouble(property);
            if(returnValue>100)
            {
                returnValue = 100;
            }
            else if (returnValue < 0)
            {
                returnValue = 0;
            }
        }
        catch (NumberFormatException e)
        {
            GUILoggerHome.find().exception("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                           NOTIFICATION_EXPIRE_MEMORY_USAGE_PERCENTAGE +
                                           " contained an invalid value.", e);
        }
        catch (InvalidPreferencesFormatException e)
        {
            GUILoggerHome.find().exception("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                           NOTIFICATION_EXPIRE_MEMORY_USAGE_PERCENTAGE +
                                           " contained an invalid value.", e);
        }
        return returnValue;
    }

    public static int getNotificationCacheExpireCount()
    {
        int returnValue = DEFAULT_CACHE_EXPIRE_COUNT;
        try
        {
            String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, CACHE_EXPIRE_COUNT_PROPERTY_KEY);
            returnValue = Integer.parseInt(property);
        }
        catch (NumberFormatException e)
        {
            GUILoggerHome.find().exception("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                           CACHE_EXPIRE_COUNT_PROPERTY_KEY +
                                           " contained an invalid value.", e);
        }
        catch (InvalidPreferencesFormatException e)
        {
            GUILoggerHome.find().exception("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                           CACHE_EXPIRE_COUNT_PROPERTY_KEY +
                                           " contained an invalid value.", e);
        }
        return returnValue;
    }

    public static boolean isNotificationPlaySoundEnabled()
    {
        boolean playSound = false;

        try
        {
            String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, NOTIFICATION_PLAY_SOUND_KEY_NAME);
            playSound = Boolean.valueOf(property);
        }
        catch(InvalidPreferencesFormatException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        return playSound;
    }

    public static int getLowNotificationClearTimoutMillis() throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, LOW_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        LOW_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME +
                                                        " contained an invalid value.", e);
        }
    }

    public static int getMediumNotificationClearTimoutMillis()
            throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, MEDIUM_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        MEDIUM_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME +
                                                        " contained an invalid value.", e);
        }
    }

    public static int getHighNotificationClearTimoutMillis()
            throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, HIGH_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        HIGH_NOTIFICATION_CLEAR_TIMOUT_KEY_NAME +
                                                        " contained an invalid value.", e);
        }
    }

    public static Color getLowNotificationColor()
            throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, LOW_NOTIFICATION_COLOR_KEY_NAME);
        try
        {
            int sRGB = Integer.parseInt(property);
            return new Color(sRGB);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        LOW_NOTIFICATION_COLOR_KEY_NAME +
                                                        " contained an invalid value.", e);
        }
    }

    public static Color getMediumNotificationColor()
            throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, MEDIUM_NOTIFICATION_COLOR_KEY_NAME);
        try
        {
            int sRGB = Integer.parseInt(property);
            return new Color(sRGB);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        MEDIUM_NOTIFICATION_COLOR_KEY_NAME +
                                                        " contained an invalid value.", e);
        }
    }

    public static Color getHighNotificationColor()
            throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, HIGH_NOTIFICATION_COLOR_KEY_NAME);
        try
        {
            int sRGB = Integer.parseInt(property);
            return new Color(sRGB);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        HIGH_NOTIFICATION_COLOR_KEY_NAME +
                                                        " contained an invalid value.", e);
        }
    }

    public static int getHighNotificationEvictTime()
            throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, HIGH_NOTIFICATION_EVICTION_TIME_KEY_NAME);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        HIGH_NOTIFICATION_EVICTION_TIME_KEY_NAME +
                                                        " contained an invalid value.", e);
        }
    }

    public static int getMediumNotificationEvictTime()
            throws InvalidPreferencesFormatException
    {
        String property = getProperty(ALARM_NOTIFICATION_SECTION_NAME, MEDIUM_NOTIFICATION_EVICTION_TIME_KEY_NAME);
        try
        {
            return Integer.parseInt(property);
        }
        catch(NumberFormatException e)
        {
            throw new InvalidPreferencesFormatException("Property: [" + ALARM_NOTIFICATION_SECTION_NAME + ']' +
                                                        MEDIUM_NOTIFICATION_EVICTION_TIME_KEY_NAME +
                                                        " contained an invalid value.", e);
        }
    }

    private static String getProperty(String section, String key)
            throws InvalidPreferencesFormatException
    {
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String property = AppPropertiesFileFactory.find().getValue(section, key);
            if(property == null || property.length() == 0)
            {
                throw new InvalidPreferencesFormatException("Property: [" + section + ']' + key + " did not exist.");
            }
            return property;
        }
        else
        {
            throw new InvalidPreferencesFormatException("Application Properties were not available.");
        }
    }

    private static void setProperty(String section, String key, String value)
            throws IOException
    {
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            AppPropertiesFileFactory.find().addValue(section, key, value);
            AppPropertiesFileFactory.find().save();
        }
    }
}
