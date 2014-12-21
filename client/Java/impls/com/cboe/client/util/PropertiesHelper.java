package com.cboe.client.util;

/**
 * PropertiesHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Helper file for properties manipulation
 *
 */

import java.util.Properties;

public final class PropertiesHelper
{
    public  static final int STRIP_NONE        = 0;
    private static final int FRONT_BASE        = 0;
    public  static final int STRIP_ONE_FRONT   = FRONT_BASE + 1;
    public  static final int STRIP_TWO_FRONT   = FRONT_BASE + 2;
    public  static final int STRIP_THREE_FRONT = FRONT_BASE + 3;
    public  static final int STRIP_ALL_FRONT   = FRONT_BASE + 10;
    private static final int BACK_BASE         = 40;
    public  static final int STRIP_ONE_BACK    = BACK_BASE  + 1;
    public  static final int STRIP_TWO_BACK    = BACK_BASE  + 2;
    public  static final int STRIP_THREE_BACK  = BACK_BASE  + 3;
    public  static final int STRIP_ALL_BACK    = BACK_BASE  + 10;

    public int keyBuilder = STRIP_NONE;
    protected Properties storedProperties;
    protected String storedPrefix;

    public static final PropertiesHelper instance = new PropertiesHelper();

    public PropertiesHelper()
    {

    }

    public PropertiesHelper(int keyBuilder)
    {
        this.keyBuilder = keyBuilder;
    }

    public PropertiesHelper(Properties properties)
    {
        storedProperties = properties;
    }

    public PropertiesHelper(Properties properties, String prefix)
    {
        storedProperties = properties;
        storedPrefix     = prefix;
    }

    public PropertiesHelper(Properties properties, String prefix, int keyBuilder)
    {
        storedProperties = properties;
        storedPrefix     = prefix;
        this.keyBuilder  = keyBuilder;
    }

    public Properties getProperties()
    {
        return storedProperties;
    }

    public static PropertiesHelper instance()
    {
        return instance;
    }

    public void setPrefix(String prefix)
    {
        storedPrefix = prefix;
    }

    public String getPrefix()
    {
        return storedPrefix;
    }

    public void setKeyBuilder(int keyBuilder)
    {
        this.keyBuilder = keyBuilder;
    }

    /* If no prefix, look up key and return value or null.
     * Else look up prefix.key; if not found, look up prefix.defaultsPrefix
     * (call this x) and look up x.key. If not found, look up
     * defaults.prefix.defaultsPrefix (call it y) and look up y.key */
    private String internal_getProperty(Properties properties, String prefix, String key)
    {
        if (prefix == null || prefix.length() == 0)
        {
            return properties.getProperty(key);
        }

        StringBuilder sb = new StringBuilder(prefix.length()+key.length()+20);

        if (!prefix.endsWith("."))
        {
            sb.setLength(0);
            sb.append(prefix).append('.');
            prefix = sb.toString();
        }

        sb.setLength(0);
        sb.append(prefix).append(key);
        String value = properties.getProperty(sb.toString());
        if (value != null)
        {
            return value;
        }

        sb.setLength(0);
        sb.append(prefix).append("defaultsPrefix");
        String defaultsPrefix = properties.getProperty(sb.toString());
        if (defaultsPrefix != null)
        {
            if (!defaultsPrefix.endsWith("."))
            {
                sb.setLength(0);
                sb.append(defaultsPrefix).append('.');
                defaultsPrefix = sb.toString();
            }

            sb.setLength(0);
            sb.append(defaultsPrefix).append(key);
            value = properties.getProperty(sb.toString());
            if (value != null)
            {
                return value;
            }
        }

        sb.setLength(0);
        sb.append("defaults.").append(prefix.substring(0, prefix.indexOf('.') + 1)).append("defaultsPrefix");
        defaultsPrefix = properties.getProperty(sb.toString());
        if (defaultsPrefix != null)
        {
            if (!defaultsPrefix.endsWith("."))
            {
                sb.setLength(0);
                sb.append(defaultsPrefix).append('.');
                defaultsPrefix = sb.toString();
            }

            sb.setLength(0);
            sb.append(defaultsPrefix).append(key);
            value = properties.getProperty(sb.toString());
            if (value != null)
            {
                return value;
            }
        }

        return null;
    }

    public String getProperty(Properties properties, String prefix, String key)
    {
        return getProperty(properties, prefix, key, null);
    }

    public String getProperty(String key)
    {
        return getProperty(storedProperties, storedPrefix, key, null);
    }

    public int getPropertyInt(String key)
    {
        return getPropertyInt(storedProperties, storedPrefix, key, "0");
    }

    public int getPropertyInt(String key, String defaultValue)
    {
        return getPropertyInt(storedProperties, storedPrefix, key, defaultValue);
    }

    public int getPropertyInt(Properties properties, String prefix, String key)
    {
        return getPropertyInt(properties, prefix, key, "0");
    }

    public int getPropertyInt(Properties properties, String prefix, String key, String defaultValue)
    {
        String value = getProperty(properties, prefix, key, defaultValue);
        if (value == null)
        {
            return 0;
        }

        return IntegerHelper.parseInt(value);
    }

    public boolean getPropertyBoolean(String key)
    {
        return getPropertyBoolean(storedProperties, storedPrefix, key, "false");
    }

    public boolean getPropertyBoolean(String key, String defaultValue)
    {
        return getPropertyBoolean(storedProperties, storedPrefix, key, defaultValue);
    }

    public boolean getPropertyBoolean(Properties properties, String prefix, String key)
    {
        return getPropertyBoolean(properties, prefix, key, "false");
    }

    public boolean getPropertyBoolean(Properties properties, String prefix, String key, String defaultValue)
    {
        return "true".equals(getProperty(properties, prefix, key, defaultValue));
    }

    public String getProperty(String key, String defaultValue)
    {
        return getProperty(storedProperties, storedPrefix, key, defaultValue);
    }

    public String getPrefixedProperty(String prefix, String key)
    {
        return getProperty(storedProperties, prefix, key, null);
    }

    public int getPrefixedPropertyInt(String prefix, String key)
    {
        return getPropertyInt(storedProperties, prefix, key, null);
    }

    public String getProperty(Properties properties, String key)
    {
        if (key == null)
        {
            return null;
        }

        if (properties.containsKey(key))
        {
            return properties.getProperty(key);
        }

        int offset;

        switch (keyBuilder)
        {
            case STRIP_ONE_FRONT:
            case STRIP_TWO_FRONT:
            case STRIP_THREE_FRONT:
            case STRIP_ALL_FRONT:
            {
                for (int i = keyBuilder - FRONT_BASE; i > -1 ; i--)
                {
                    offset = key.indexOf('.');
                    if (offset == -1)
                    {
                        break;
                    }

                    key = key.substring(offset + 1);

                    if (properties.containsKey(key))
                    {
                        return properties.getProperty(key);
                    }
                }

                break;
            }
            case STRIP_ONE_BACK:
            case STRIP_TWO_BACK:
            case STRIP_THREE_BACK:
            case STRIP_ALL_BACK:
            {
                for (int i = keyBuilder - BACK_BASE; i > -1 ; i--)
                {
                    offset = key.lastIndexOf('.');
                    if (offset == -1)
                    {
                        break;
                    }

                    key = key.substring(0, offset);

                    if (properties.containsKey(key))
                    {
                        return properties.getProperty(key);
                    }
                }

                break;
            }
        }

        return null;
    }

    public String getProperty(Properties properties, String prefix, String key, String defaultValue)
    {
        if (properties == null || key == null)
        {
            return defaultValue;
        }

        String value;

        if (prefix == null || prefix.length() == 0)
        {
            value = getProperty(properties, key);
            if (value != null)
            {
                return value;
            }

            return defaultValue;
        }

        value = internal_getProperty(properties, prefix, key);
        if (value != null)
        {
            return value;
        }

        int offset;

        switch (keyBuilder)
        {
            case STRIP_NONE:
            {
                break;
            }
            case STRIP_ONE_FRONT:
            case STRIP_TWO_FRONT:
            case STRIP_THREE_FRONT:
            case STRIP_ALL_FRONT:
            {
                for (int i = keyBuilder - FRONT_BASE; i > -1 ; i--)
                {
                    offset = prefix.indexOf('.');
                    if (offset == -1)
                    {
                        break;
                    }

                    prefix = prefix.substring(offset + 1);

                    value = internal_getProperty(properties, prefix, key);
                    if (value != null)
                    {
                        return value;
                    }
                }

                break;
            }
            case STRIP_ONE_BACK:
            case STRIP_TWO_BACK:
            case STRIP_THREE_BACK:
            case STRIP_ALL_BACK:
            {
                for (int i = keyBuilder - BACK_BASE; i > -1 ; i--)
                {
                    offset = prefix.lastIndexOf('.');
                    if (offset == -1)
                    {
                        break;
                    }

                    prefix = prefix.substring(0, offset);

                    value = internal_getProperty(properties, prefix, key);
                    if (value != null)
                    {
                        return value;
                    }
                }

                break;
            }
        }

        return defaultValue;
    }
}
