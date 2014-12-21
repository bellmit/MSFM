package com.cboe.client.util.collections;

/**
 * SessionKeyObjectMap.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;

public class SessionKeyObjectMap
{
    protected String[]          sessions      = CollectionHelper.EMPTY_String_ARRAY;
    protected IntObjectMap[]    keys          = EMPTY_IntObjectMap_ARRAY;
    protected int               size;
    protected String            name          = "unnamedSessionKeyObjectMap";
    protected Object            ValueNotFound = null;

    public static final UpdateNullsOnlyIntObjectKeyValuePolicy UpdateIfNullPolicy = new UpdateNullsOnlyIntObjectKeyValuePolicy();

    public static class UpdateNullsOnlyIntObjectKeyValuePolicy extends IntObjectKeyValuePolicy
    {
        public boolean canUpdate(int key, Object newValue, Object oldValue)
        {
            return oldValue == null && newValue != null;
        }
    };

    public static final IntObjectMap[] EMPTY_IntObjectMap_ARRAY = new IntObjectMap[0];

    public static class SessionKeyObjectMapMT extends SessionKeyObjectMap
    {
        public SessionKeyObjectMapMT()                                                                                           {       super();}
        public SessionKeyObjectMapMT(String name)                                                                                {       super(name);}
        public synchronized SessionKeyObjectMap setValueNotFound(Object ValueNotFound)                                           {return super.setValueNotFound(ValueNotFound);}
        public synchronized Object getValueNotFound()                                                                            {return super.getValueNotFound();}
        public synchronized void putKeyValue(String sessionName, int sessionKey, Object value)                                   {       super.putKeyValue(sessionName, sessionKey, value);}
        public synchronized void putKeyValue(String sessionName, int sessionKey, Object value, IntObjectKeyValuePolicyIF policy) {       super.putKeyValue(sessionName, sessionKey, value, policy);}
        public synchronized Object removeKey(String sessionName, int sessionKey)                                                 {return super.removeKey(sessionName, sessionKey);}
        public synchronized int size()                                                                                           {return super.size();}
        public synchronized boolean isEmpty()                                                                                    {return super.isEmpty();}
        public synchronized boolean containsKey(String sessionName, int sessionKey)                                              {return super.containsKey(sessionName, sessionKey);}
        public synchronized boolean containsValue(Object value)                                                                  {return super.containsValue(value);}
        public synchronized void getKeysForValue(Object value, IntArrayHolder arrayHolder)                                       {       super.getKeysForValue(value, arrayHolder);}
        public synchronized Object getValueForKey(String sessionName, int sessionKey)                                            {return super.getValueForKey(sessionName, sessionKey);}
    }

    public SessionKeyObjectMap()
    {

    }

    public SessionKeyObjectMap(String name)
    {
        this.name = name;
    }

    public interface SessionKeyObjectMapVisitorIF extends IntObjectVisitorIF
    {
        public void setSessionName(String sessionName);
    }

    public Object getValueNotFound()
    {
        return ValueNotFound;
    }

    public SessionKeyObjectMap setValueNotFound(Object ValueNotFound)
    {
        this.ValueNotFound = ValueNotFound;

        for (int keyIndex = 0; keyIndex < sessions.length; keyIndex++)
        {
            keys[keyIndex].setValueNotFound(ValueNotFound);
        }

        return this;
    }

    public void putKeyValue(String sessionName, int sessionKey, Object value)
    {
        int keyIndex = findSessionIndex(sessionName);
        if (keyIndex == IntegerHelper.INVALID_VALUE)
        {
            keyIndex = sessions.length;
            sessions = CollectionHelper.arrayclone(sessions, sessions.length + 1);
            keys     = arrayclone(keys,     keys.length + 1);

            sessions[keyIndex] = sessionName;
            keys[keyIndex]     = IntObjectMap.unsynchronizedMap().setValueNotFound(ValueNotFound);
        }

        if (keys[keyIndex].putKeyValue(sessionKey, value) == IntObjectMap.VALUE_ADDED)
        {
            size++;
        }
    }

    public void putKeyValue(String sessionName, int sessionKey, Object value, IntObjectKeyValuePolicyIF policy)
    {
        int keyIndex = findSessionIndex(sessionName);
        if (keyIndex == IntegerHelper.INVALID_VALUE)
        {
            keyIndex = sessions.length;
            sessions = CollectionHelper.arrayclone(sessions, sessions.length + 1);
            keys     = arrayclone(keys,     keys.length + 1);

            sessions[keyIndex] = sessionName;
            keys[keyIndex]     = IntObjectMap.unsynchronizedMap().setValueNotFound(ValueNotFound);
        }

        if (keys[keyIndex].putKeyValue(sessionKey, value, policy) == IntObjectMap.VALUE_ADDED)
        {
            size++;
        }
    }

    public Object removeKey(String sessionName, int sessionKey)
    {
        int keyIndex = findSessionIndex(sessionName);
        if (keyIndex == IntegerHelper.INVALID_VALUE)
        {
            return ValueNotFound;
        }

        Object oldValue = keys[keyIndex].removeKey(sessionKey);

        if (oldValue == ValueNotFound)
        {
            return ValueNotFound;
        }

        synchronized(this)
        {
            size--;

            if (keys[keyIndex].isEmpty())
            {
                sessions = CollectionHelper.arraycloneShrinkGap(sessions, keyIndex, 1);
                keys     = arraycloneRemoveGap(keys,     keyIndex, 1);
            }
        }

        return oldValue;
    }

    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public boolean containsKey(String sessionName, int sessionKey)
    {
        int keyIndex = findSessionIndex(sessionName);
        if (keyIndex != IntegerHelper.INVALID_VALUE)
        {
            return keys[keyIndex].containsKey(sessionKey);
        }

        return false;
    }

    public boolean containsValue(Object value)
    {
        for (int keyIndex = 0; keyIndex < sessions.length; keyIndex++)
        {
            if (keys[keyIndex].containsValue(value))
            {
                return true;
            }
        }

        return false;
    }

    public void getKeysForValue(Object value, IntArrayHolder arrayHolder)
    {
        for (int keyIndex = 0; keyIndex < sessions.length; keyIndex++)
        {
            keys[keyIndex].getKeysForValue(value, arrayHolder);
        }
    }

    public Object getValueForKey(String sessionName, int sessionKey)
    {
        int keyIndex = findSessionIndex(sessionName);
        if (keyIndex == IntegerHelper.INVALID_VALUE)
        {
            return ValueNotFound;
        }

        return keys[keyIndex].getValueForKey(sessionKey);
    }

    protected int findSessionIndex(String sessionName)
    {
        for (int keyIndex = 0; keyIndex < sessions.length; keyIndex++)
        {
            if (sessions[keyIndex].compareTo(sessionName) == 0)
            {
                return keyIndex;
            }
        }

        return IntegerHelper.INVALID_VALUE;
    }

    public SessionKeyObjectMapVisitorIF acceptKeyValueVisitor(SessionKeyObjectMapVisitorIF  visitor)
    {
        for (int keyIndex = 0; keyIndex < sessions.length; keyIndex++)
        {
            visitor.setSessionName(sessions[keyIndex]);

            keys[keyIndex].acceptVisitor(visitor);
        }

        return visitor;
    }

    public void clear()
    {
        sessions = CollectionHelper.EMPTY_String_ARRAY;
        keys     = EMPTY_IntObjectMap_ARRAY;
        size     = 0;
    }

    public static IntObjectMap[] arrayclone(IntObjectMap[] from, int toSize)
    {
        IntObjectMap[] to = new IntObjectMap[toSize];
        System.arraycopy(from, 0, to, 0, from.length);
        return to;
    }

    public static IntObjectMap[] arraycloneRemoveGap(IntObjectMap[] from, int gapOffset, int gapLength)
    {
        IntObjectMap[] to = new IntObjectMap[from.length - gapLength];
        System.arraycopy(from, 0,                     to, 0,         gapOffset);
        System.arraycopy(from, gapOffset + gapLength, to, gapOffset, to.length - gapOffset);
        return to;
    }

    public String toString()
    {
        return name;
    }
}

