package com.cboe.domain.util;

import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.interfaces.domain.UserData;

import java.text.ParseException;
import java.util.*;

/**
 * InstrumentorUserDataHelper
 *
 * A class to encode user data as a string for instrumentation.
 * Currently used for CAS Monitoring.
 *
 * This class supports key-value pairs and multiple values per key.
 * The keys and values are all stored as strings.  If a value for
 * any given key is duplicated, only one copy of that pair will be kept.
 *
 * Null or empty keys or values are not allowed.  The field delimiter
 * that is used is Instrumentor.USER_DATA_DELIMITER; this currently cannot
 * be changed.  Any attempt to use the field delimiter in a key or value is
 * not allowed.
 *
 * An '=' character may appear in a value; only the first '=' character is
 * considered the key-value delimiter.
 *
 * Access to the underlying data map is synchronized.  All operations synchronize
 * on the hash map in which the data is stored, so synchronizations on the
 * value sets for a key are not necessary.
 *
 * @author Jing Chen
 * @author Eric Fredericks
 */
public final class InstrumentorUserData implements UserData
{
    private final String FIELD_DELIMITER = Instrumentor.USER_DATA_DELIMITER;
    private final String KEY_VALUE_DELIMITER = "=";

    private Map dataMap;


    public InstrumentorUserData()
    {
        dataMap = new HashMap();
    }

    /**
     * Constructor which takes a previously encoded user data string and parses it.
     * The fields in the string are stored in our map.  A ParseException is thrown
     * in the following cases: No fields to parse, no key value delimiter detected,
     * no value detected for a field, empty key, empty value.
     *
     * @param data
     * @throws ParseException
     * @throws IllegalArgumentException
     */
    public InstrumentorUserData(String data) throws ParseException
    {
        this();
        if(data == null || data.length() == 0)
        {
            throw new IllegalArgumentException("Data string cannot be null");
        }

        parseFields(data);
    }

    private void parseFields(String data) throws ParseException
    {
        StringTokenizer tokens = new StringTokenizer(data, FIELD_DELIMITER);

        if(tokens.countTokens() == 0)
        {
            throw new ParseException("No fields to parse.", 0);
        }

        for(int i = 0; tokens.hasMoreTokens(); i++)
        {
            String keyValuePair = tokens.nextToken();

            int splitIndex = keyValuePair.indexOf(KEY_VALUE_DELIMITER);
            if(splitIndex == -1)
            {
                throw new ParseException("No key value delimiter detected for field " + i, i);
            }
            if(splitIndex == keyValuePair.length() - 1)
            {
                throw new ParseException("No value detected for field " + i, i);
            }

            String key = keyValuePair.substring(0, splitIndex).trim();
            String value = keyValuePair.substring(splitIndex + 1).trim();

            if(key.length() == 0)
            {
                throw new ParseException("Key cannot be empty for field " + i, i);
            }

            if(value.length() == 0)
            {
                throw new ParseException("Value cannot be empty for field " + i, i);
            }

            addValue(key, value);
        }
    }

    /** Add this value for this key in the map.  Duplicate
     *  values for any given key are not supported.  An
     *  IllegalArgumentException is thrown in the case of null
     *  or empty keys or values, if either key or value contains
     *  a field delimiter character, or if the key contains a key/value
     *  delimeter character.
     *
     * @param key
     * @param value
     * @throws IllegalArgumentException
     */
    public void addValue(String key, String value)
    {
        if(key == null || value == null)
        {
            throw new IllegalArgumentException("Key or value cannot be null");
        }

        key = key.trim();
        value = value.trim();

        if(key.length() == 0 || value.length() == 0)
        {
            throw new IllegalArgumentException("Key or value cannot be empty");
        }

        if(key.indexOf(FIELD_DELIMITER) != -1 || key.indexOf(KEY_VALUE_DELIMITER) != -1 || value.indexOf(FIELD_DELIMITER) != -1)
        {
            throw new IllegalArgumentException("Neither key nor value can contain the field delimiter or key value delimiter");
        }

        synchronized(dataMap)
        {
            HashSet values = (HashSet) dataMap.get(key);
            if(values == null)
            {
                values = new HashSet();
                dataMap.put(key, values);
            }

            values.add(value);
        }
    }

    /** Remove this value for this key in the map.  False is returned in
     *  the case of null or empty keys or values, if either key or value
     *  contains a field delimiter character, or if the key contains a key/value
     *  delimeter character, or if the removal has no effect.
     *
     * @param key
     * @param value
     * @return true if the value is removed
     */
    public boolean removeValueForKey(String key, String value)
    {
        if(key == null || value == null)
        {
            return false;
        }

        key = key.trim();
        value = value.trim();

        if(key.length() == 0 || value.length() == 0)
        {
            return false;
        }

        boolean returnValue = false;

        synchronized(dataMap)
        {
            Set values = (Set) dataMap.get(key);
            if(values != null)
            {
                returnValue = values.remove(value);
                if(values.isEmpty())
                {
                    dataMap.remove(key);
                }
            }
        }

        return returnValue;
    }

    /** Remove all values for this key.  An empty string is returned in the
     *  case of a null or empty key, or if the removal has no effect.
     * @param key
     * @return
     */
    public String[] removeAllValuesForKey(String key)
    {
        if(key == null)
        {
            return new String[0];
        }

        key = key.trim();
        if(key.length() == 0)
        {
            return new String[0];
        }

        Set values;
        synchronized(dataMap)
        {
            values = (Set) dataMap.remove(key);
        }

        String[] returnValue;
        if(values == null)
        {
            returnValue = new String[0];
        }
        else
        {
            returnValue = new String[values.size()];
            values.toArray(returnValue);
        }

        return returnValue;
    }

    /**
     * Get all keys in this user data object.
     * @return the keys as Strings
     */
    public String[] getAllKeys()
    {
        String[] result;
        synchronized(dataMap)
        {
            result = new String[dataMap.size()];
            dataMap.keySet().toArray(result);
        }

        return result;
    }

    public String[] getValues(String key)
    {
        if(key == null)
        {
            throw new IllegalArgumentException("Key cannot be null");
        }

        key = key.trim();
        if(key.length() == 0)
        {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        String[] values;
        synchronized(dataMap)
        {
            HashSet valueSet = (HashSet) dataMap.get(key);

            if(valueSet != null)
            {
                values = new String[valueSet.size()];
                valueSet.toArray(values);
            }
            else
            {
                values = new String[0];
            }
        }

        return values;
    }


    /**
     * Convert the data to a string representation.  The data is represented as
     * <fieldName>=<value> with an optional separator if there is more than one
     * field.
     * @return
     */
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();

        synchronized(dataMap)
        {
            Iterator keyIterator = dataMap.keySet().iterator();
            while(keyIterator.hasNext())
            {
                String key = (String) keyIterator.next();
                HashSet values = (HashSet) dataMap.get(key);

                Iterator valueIterator = values.iterator();
                while(valueIterator.hasNext())
                {
                    String value = (String) valueIterator.next();
                    buffer.append(key);
                    buffer.append(KEY_VALUE_DELIMITER);
                    buffer.append(value);
                    if(valueIterator.hasNext())
                    {
                        buffer.append(FIELD_DELIMITER);
                    }
                }

                if(keyIterator.hasNext())
                {
                    buffer.append(FIELD_DELIMITER);
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Convert the data to a string representation.  The data is represented as
     * <fieldName>=<value> with an optional separator if there is more than one
     * field.
     * @return
     */
    public String[] toStringArray()
    {
        Vector result = new Vector();
        synchronized(dataMap)
        {
            int i = 0;
            Iterator keyIterator = dataMap.keySet().iterator();
            while(keyIterator.hasNext())
            {
                String key = (String) keyIterator.next();
                HashSet values = (HashSet) dataMap.get(key);

                Iterator valueIterator = values.iterator();
                while(valueIterator.hasNext())
                {
                    String value = (String) valueIterator.next();
                    result.addElement(key+KEY_VALUE_DELIMITER+value);
                }
            }
        }
        String[] resultArray = new String[result.size()];
        result.toArray(resultArray);
        return resultArray;
    }

    public boolean containsKey(String key)
    {
        if(key == null)
        {
            return false;
        }

        key = key.trim();
        if(key.length() == 0)
        {
            return false;
        }

        synchronized(dataMap)
        {
            return dataMap.containsKey(key);
        }
    }
}
