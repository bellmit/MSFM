//
// ------------------------------------------------------------------------
// FILE: SoftHashMap.java
//
// PACKAGE: com.cboe.presentation.collector
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.collector;

import java.util.AbstractMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * Provide a map where values are allowed to be garbage collected when the VM is running low on memory.
 *
 * This code is based on an original implementation by Dr. Heinz Kabutz (h.kabutz@computer.org).
 *
 * @author torresl@cboe.com
 */
public class SoftHashMap extends AbstractMap
{
    /** The internal HashMap that will hold the SoftReference. */
    private final Map hash ;
    /** Reference queue for cleared SoftReference objects. */
    private final ReferenceQueue queue;

    public SoftHashMap()
    {
        this(16);
    }

    public SoftHashMap(int initialSize)
    {
        super();
        hash = new HashMap(initialSize);
        queue = new ReferenceQueue();
    }

    public Object get(Object key)
    {
        Object result = null;
        // We get the SoftReference represented by that key
        SoftReference softRef = (SoftReference) hash.get(key);
        if (softRef != null)
        {
            // From the SoftReference we get the value, which can be
            // null if it was not in the map, or it was removed in
            // the processQueue() method defined below
            result = softRef.get();
            if (result == null)
            {
                // If the value has been garbage collected, remove the
                // entry from the HashMap.
                hash.remove(key);
            }
        }
        return result;
    }

    /**
     * Put the key, value pair into the HashMap using
     * a SoftValue object.
     */
    public Object put(Object key, Object value)
    {
        processQueue(); // throw out garbage collected values first
        return hash.put(key, new SoftValue(value, key, queue));
    }

    public Object remove(Object key)
    {
        processQueue(); // throw out garbage collected values first
        return hash.remove(key);
    }

    public void clear()
    {
        processQueue(); // throw out garbage collected values
        hash.clear();
    }

    public int size()
    {
        processQueue(); // throw out garbage collected values first
        return hash.size();
    }

    /**
     * Go through the ReferenceQueue and remove garbage
     * collected SoftValue objects from the HashMap by looking them
     * up using the SoftValue.key data member.
     */
    private void processQueue()
    {
        SoftValue softValue;
        while ((softValue = (SoftValue) queue.poll()) != null)
        {
            hash.remove(softValue.getKey());
        }
    }


    public Set entrySet()
    {
        throw new UnsupportedOperationException("SoftHashMap does not allow retrieving the entry set.");
    }

}
