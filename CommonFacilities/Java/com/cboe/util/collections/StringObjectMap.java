package com.cboe.util.collections;

/**
 * StringObjectMap.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueMap.template where keytype = (String) and valtype = (Object)
 *
 */

public class StringObjectMap
{
    protected final    Bucket[]           buckets;
    protected final    int                highestBucketIndex;
    protected volatile int                mapSize;
    protected final    MapInstrumentation mapInstrumentation;

    public static final Object INVALID_VALUE = null;

    public Object ValueNotFound = INVALID_VALUE;

    public static final int DEFAULT_MODE                      = 0;
    public static final int MULTITHREAD_MODE_ON               = 1 << 1;
    public static final int INSTRUMENTATION_MODE_ON           = 1 << 2;

    public static final int DEFAULT_SMALL_MAP_NUMBER_BUCKETS  = 16;
    public static final int DEFAULT_MEDIUM_MAP_NUMBER_BUCKETS = 64;
    public static final int DEFAULT_LARGE_MAP_NUMBER_BUCKETS  = 256;
    public static final int DEFAULT_HUGE_MAP_NUMBER_BUCKETS   = 1024;
    public static final int DEFAULT_NUMBER_BUCKETS            = DEFAULT_LARGE_MAP_NUMBER_BUCKETS;
    public static final int MAXIMUM_NUMBER_BUCKETS            = 1024 * 16; // must be a power of ^2
    public static final int MINIMUM_NUMBER_BUCKETS            = 16;        // must be a power of ^2
    public static final int DEFAULT_ENTRY_LIST_CAPACITY       = 4;
    public static final int DEFAULT_ENTRY_LIST_GROWTH         = 4;

    public StringObjectMap()
    {
        this(DEFAULT_NUMBER_BUCKETS, DEFAULT_MODE);
    }

    public StringObjectMap(int numberBuckets)
    {
        this(numberBuckets, DEFAULT_MODE);
    }

    public StringObjectMap(int numberBuckets, int mapParameters)
    {
        if ((mapParameters & INSTRUMENTATION_MODE_ON) == INSTRUMENTATION_MODE_ON)
        {
            mapInstrumentation = new MapInstrumentation();
        }
        else
        {
            mapInstrumentation = MapInstrumentation.NopMapInstrumentation;
        }

        if (numberBuckets < 2)
        {
            numberBuckets = DEFAULT_NUMBER_BUCKETS;
        }
        else if (numberBuckets > MAXIMUM_NUMBER_BUCKETS)
        {
            numberBuckets = MAXIMUM_NUMBER_BUCKETS;
        }

        int powerOf2;

        for (powerOf2 = MINIMUM_NUMBER_BUCKETS; powerOf2 < numberBuckets; powerOf2 <<= 1)
        {

        }

        numberBuckets = powerOf2;

        highestBucketIndex = numberBuckets - 1;

        buckets = new Bucket[numberBuckets];

        if ((mapParameters & MULTITHREAD_MODE_ON) == MULTITHREAD_MODE_ON)
        {
            for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
            {
                buckets[bucketIndex] = new SynchronizedBucket();
            }
        }
        else
        {
            for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
            {
                buckets[bucketIndex] = new UnsynchronizedBucket();
            }
        }
    }

    /**
     * This allows to switch internally from the synchronized map to an unsynchronized, as many times as you want.
     *
     * It is a moderately cheap operation IFF you are SURE that nobody is modifying the map while you are doing this.
     *
     * This is mostly used for cases where you want to add a number of items up front unsynchronized, and then make the
     * map synchronized for subsequent lookups/additions
     *
     */
    public boolean switchSynchronization(int mapParameters)
    {
        if ((mapParameters & MULTITHREAD_MODE_ON) == MULTITHREAD_MODE_ON)
        {
            if (buckets[0] instanceof SynchronizedBucket)
            {
                return false;
            }

            for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
            {
                buckets[bucketIndex] = new UnsynchronizedBucket(buckets[bucketIndex]);
            }

            return true;
        }
        else
        {
            if (buckets[0] instanceof UnsynchronizedBucket)
            {
                return false;
            }

            for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
            {
                buckets[bucketIndex] = new SynchronizedBucket(buckets[bucketIndex]);
            }

            return true;
        }
    }

    public MapInstrumentation getMapInstrumentation()
    {
        return mapInstrumentation;
    }

    public Object getValueNotFound()
    {
        return ValueNotFound;
    }

    public StringObjectMap setValueNotFound(Object ValueNotFound)
    {
        this.ValueNotFound = ValueNotFound;

        return this;
    }

    public int size()
    {
        return mapSize;
    }

    public boolean isEmpty()
    {
        return mapSize == 0;
    }

    public boolean containsKey(String key)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].containsKey(key);
    }

    public boolean containsKey(String key, StringObjectMapPolicy policy)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].containsKey(key, policy);
    }

    public boolean containsValue(Object value)
    {
        boolean contains = false;

        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex && !contains; bucketIndex++)
        {
            contains = buckets[bucketIndex].containsValue(value);
        }

        return contains;
    }

    public boolean containsValue(Object value, StringObjectMapPolicy policy)
    {
        boolean contains = false;

        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex && !contains; bucketIndex++)
        {
            contains = buckets[bucketIndex].containsValue(value, policy);
        }

        return contains;
    }

    public StringArrayHolder getKeysForValue(Object value, StringArrayHolder arrayHolder)
    {
        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
        {
            buckets[bucketIndex].getKeysForValue(value, arrayHolder);
        }

        return arrayHolder;
    }

    public StringArrayHolder getKeysForValue(Object value, StringArrayHolder arrayHolder, StringObjectMapPolicy policy)
    {
        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
        {
            buckets[bucketIndex].getKeysForValue(value, arrayHolder, policy);
        }

        return arrayHolder;
    }

    public Object get(String key)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].get(key);
    }

    public Object get(String key, StringObjectMapPolicy policy)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].get(key, policy);
    }

    public Object put(String key, Object value)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].put(key, value);
    }

    public Object put(String key, Object value, StringObjectMapPolicy policy)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].put(key, value, policy);
    }

    public Object put(String key, Object value, StringObjectMapModifyPolicy policy)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].put(key, value, policy);
    }

    public Object remove(String key)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].remove(key);
    }

    public Object remove(String key, StringObjectMapPolicy policy)
    {
        return buckets[(int) (key.hashCode() & highestBucketIndex)].remove(key, policy);
    }

    public void remove(StringObjectMapPolicy policy)
    {
        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
        {
            buckets[bucketIndex].remove(policy);
        }
    }

    public void remove(StringObjectMapArrayHolder arrayHolder, StringObjectMapPolicy policy)
    {
        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
        {
            buckets[bucketIndex].remove(arrayHolder, policy);
        }
    }

    public void find(StringObjectMapArrayHolder arrayHolder)
    {
        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
        {
            buckets[bucketIndex].find(arrayHolder);
        }
    }

    public void find(StringObjectMapArrayHolder arrayHolder, StringObjectMapPolicy policy)
    {
        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
        {
            buckets[bucketIndex].find(arrayHolder, policy);
        }
    }

    public StringObjectMap clear()
    {
        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
        {
            buckets[bucketIndex].clear();
        }

        mapSize = 0;

        return this;
    }

    public StringObjectMapVisitor acceptVisitor(StringObjectMapVisitor visitor)
    {
        for (int bucketIndex = 0; bucketIndex <= highestBucketIndex; bucketIndex++)
        {
            buckets[bucketIndex].acceptVisitor(visitor);
        }

        return visitor;
    }

// bucket interface
    public interface Bucket
    {
        public boolean containsKey(String key);
        public boolean containsKey(String key, StringObjectMapPolicy policy);
        public boolean containsValue(Object value);
        public boolean containsValue(Object value, StringObjectMapPolicy policy);
        public Object get(String key);
        public Object get(String key, StringObjectMapPolicy policy);
        public void getKeysForValue(Object value, StringArrayHolder arrayHolder);
        public void getKeysForValue(Object value, StringArrayHolder arrayHolder, StringObjectMapPolicy policy);
        public Object put(String key, Object value);
        public Object put(String key, Object value, StringObjectMapPolicy policy);
        public Object put(String key, Object value, StringObjectMapModifyPolicy policy);
        public Object remove(String key);
        public Object remove(String key, StringObjectMapPolicy policy);
        public void remove(StringObjectMapPolicy policy);
        public void remove(StringObjectMapArrayHolder arrayHolder, StringObjectMapPolicy policy);
        public void acceptVisitor(StringObjectMapVisitor visitor);
        public void clear();
        public void find(StringObjectMapArrayHolder arrayHolder);
        public void find(StringObjectMapArrayHolder arrayHolder, StringObjectMapPolicy policy);
    }
// unsynchronized bucket 
    protected class UnsynchronizedBucket implements Bucket
    {
        protected String[] keys;
        protected Object[] values;
        protected int       keyCount;

        public UnsynchronizedBucket()
        {

        }

        public UnsynchronizedBucket(Bucket bucket)
        {
            if (bucket instanceof SynchronizedBucket)
            {
                this.keys     = ((SynchronizedBucket) bucket).keys;
                this.values   = ((SynchronizedBucket) bucket).values;
                this.keyCount = ((SynchronizedBucket) bucket).keyCount;
            }
            else
            {
                this.keys     = ((UnsynchronizedBucket) bucket).keys;
                this.values   = ((UnsynchronizedBucket) bucket).values;
                this.keyCount = ((UnsynchronizedBucket) bucket).keyCount;
            }
        }

        public boolean containsKey(String key)
        {
            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                mapInstrumentation.incHit();

                return true;
            }

            mapInstrumentation.incMiss();

            return false;
        }

        public boolean containsKey(String key, StringObjectMapPolicy policy)
        {
            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                mapInstrumentation.incHit();

                return policy.canRetrieve(keys[keyIndex], values[keyIndex]);
            }

            mapInstrumentation.incMiss();

            return false;
        }

        public boolean containsValue(Object value)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (value != null && value.equals(values[keyIndex]))
                {
                    return true;
                }
            }

            return false;
        }

        public boolean containsValue(Object value, StringObjectMapPolicy policy)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (value != null && value.equals(values[keyIndex]))
                {
                    return policy.canRetrieve(keys[keyIndex], value);
                }
            }

            return false;
        }

        public Object get(String key)
        {
            if (keyCount > 0)
            {
                int keyIndex = keyFind(key);
                if (keyIndex >= 0)
                {
                    mapInstrumentation.incHit();

                    return values[keyIndex];
                }
            }

            mapInstrumentation.incMiss();

            return ValueNotFound;
        }

        public Object get(String key, StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                int keyIndex = keyFind(key);
                if (keyIndex >= 0)
                {
                    Object value = values[keyIndex];
                    if (policy.canRetrieve(key, value))
                    {
                        mapInstrumentation.incHit();

                        return value;
                    }
                }
            }

            mapInstrumentation.incMiss();

            return ValueNotFound;
        }

        public void getKeysForValue(Object value, StringArrayHolder arrayHolder)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (value != null && value.equals(values[keyIndex]))
                {
                    arrayHolder.add(keys[keyIndex]);
                }
            }
        }

        public void getKeysForValue(Object value, StringArrayHolder arrayHolder, StringObjectMapPolicy policy)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (value != null && value.equals(values[keyIndex]))
                {
                    String key = keys[keyIndex];

                    if (policy.canRetrieve(key, value))
                    {
                        arrayHolder.add(key);
                    }
                }
            }
        }

        public Object put(String key, Object value)
        {
            if (keyCount == 0)
            {
                if (keys == null)
                {
                    keys   = new String[DEFAULT_ENTRY_LIST_CAPACITY];
                    values = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
                }

                keys[0]   = key;
                values[0] = value;
                keyCount  = 1;

                mapSize++;

                mapInstrumentation.incAdded();

                return value;
            }

            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                values[keyIndex] = value;

                mapInstrumentation.incReplaced();

                return value;
	        }

            // if we got here, this is completely new key, we let's add it
            keyIndex = MapHelper.normalizeBinarySearchPosition(keyIndex);
            if (keyCount >= keys.length) // need to expand
            {
                if (keyIndex == keyCount) // if at end, then just arrayclone, and append later
                {
                    keys   = arrayclone(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH);
                    values = arrayclone(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH);
                }
                else
                {
                    keys   = arraycloneExpandGap(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                    values = arraycloneExpandGap(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                }
            }
            else
            {
                if (keyIndex < keyCount)
                {
                    System.arraycopy(values, keyIndex, values, keyIndex + 1, keyCount - keyIndex);
                    System.arraycopy(keys,   keyIndex, keys,   keyIndex + 1, keyCount - keyIndex);
                }
            }

            keys[keyIndex]   = key;
            values[keyIndex] = value;

            keyCount++;
            mapSize++;

            mapInstrumentation.incAdded();

            return value;
        }

        public Object put(String key, Object value, StringObjectMapPolicy policy)
        {
            if (keyCount == 0)
            {
                if (!policy.canInsert(key, value))
                {
                    return ValueNotFound;
                }

                if (keys == null)
                {
                    keys   = new String[DEFAULT_ENTRY_LIST_CAPACITY];
                    values = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
                }

                keys[0]   = key;
                values[0] = value;
                keyCount  = 1;

                mapSize++;

                mapInstrumentation.incAdded();

                return value;
            }

            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                if (policy.canUpdate(keys[keyIndex], values[keyIndex], value))
                {
                    values[keyIndex] = value;

                    mapInstrumentation.incReplaced();
                }

                return values[keyIndex];
	        }

            // if we got here, this is completely new key, we let's add it
            if (!policy.canInsert(key, value))
            {
                return ValueNotFound;
            }

            keyIndex = MapHelper.normalizeBinarySearchPosition(keyIndex);
            if (keyCount >= keys.length) // need to expand
            {
                if (keyIndex == keyCount) // if at end, then just arrayclone, and append later
                {
                    keys   = arrayclone(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH);
                    values = arrayclone(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH);
                }
                else
                {
                    keys   = arraycloneExpandGap(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                    values = arraycloneExpandGap(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                }
            }
            else
            {
                if (keyIndex < keyCount)
                {
                    System.arraycopy(values, keyIndex, values, keyIndex + 1, keyCount - keyIndex);
                    System.arraycopy(keys,   keyIndex, keys,   keyIndex + 1, keyCount - keyIndex);
                }
            }

            keys[keyIndex]   = key;
            values[keyIndex] = value;

            keyCount++;
            mapSize++;

            mapInstrumentation.incAdded();

            return value;
        }

        public Object put(String key, Object value, StringObjectMapModifyPolicy policy)
        {
            if (keyCount == 0)
            {
                if (!policy.canInsert(key, value))
                {
                    return ValueNotFound;
                }

                if (keys == null)
                {
                    keys   = new String[DEFAULT_ENTRY_LIST_CAPACITY];
                    values = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
                }

                keys[0]   = key;
                values[0] = policy.valueToInsert(key, value);
                keyCount  = 1;

                mapSize++;

                mapInstrumentation.incAdded();

                return value;
            }

            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                if (policy.canUpdate(keys[keyIndex], values[keyIndex], value))
                {
                    values[keyIndex] = policy.valueToUpdate(key, values[keyIndex], value);

                    mapInstrumentation.incReplaced();
                }

                return values[keyIndex];
	        }

            // if we got here, this is completely new key, we let's add it
            if (!policy.canInsert(key, value))
            {
                return ValueNotFound;
            }

            keyIndex = MapHelper.normalizeBinarySearchPosition(keyIndex);
            if (keyCount >= keys.length) // need to expand
            {
                if (keyIndex == keyCount) // if at end, then just arrayclone, and append later
                {
                    keys   = arrayclone(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH);
                    values = arrayclone(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH);
                }
                else
                {
                    keys   = arraycloneExpandGap(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                    values = arraycloneExpandGap(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                }
            }
            else
            {
                if (keyIndex < keyCount)
                {
                    System.arraycopy(values, keyIndex, values, keyIndex + 1, keyCount - keyIndex);
                    System.arraycopy(keys,   keyIndex, keys,   keyIndex + 1, keyCount - keyIndex);
                }
            }

            keys[keyIndex]   = key;
            values[keyIndex] = policy.valueToInsert(key, value);

            keyCount++;
            mapSize++;

            mapInstrumentation.incAdded();

            return value;
        }

        public Object remove(String key)
        {
            if (keyCount > 0)
            {
                int keyIndex = keyFind(key);
                if (keyIndex >= 0)
                {
                    Object oldValue = values[keyIndex];

                    int highest_index = keys.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(values, keyIndex + 1, values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(keys,   keyIndex + 1, keys,   keyIndex, highest_index - keyIndex);
                    }

                    keys[highest_index]   = null;
                    values[highest_index] = null;

                    keyCount--;
                    mapSize--;

                    mapInstrumentation.incRemoved();

                    return oldValue;
	            }
            }

            mapInstrumentation.incMiss();

            return ValueNotFound;
        }

        public Object remove(String key, StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                int keyIndex = keyFind(key);
                if (keyIndex >= 0)
                {
                    Object oldValue = values[keyIndex];

                    if (!policy.canRemove(key, oldValue))
                    {
                        mapInstrumentation.incMiss();

                        return ValueNotFound;
                    }

                    int highest_index = keys.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(values, keyIndex + 1, values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(keys,   keyIndex + 1, keys,   keyIndex, highest_index - keyIndex);
                    }

                    keys[highest_index]   = null;
                    values[highest_index] = null;

                    keyCount--;
                    mapSize--;

                    mapInstrumentation.incRemoved();

                    return oldValue;
	            }
            }

            mapInstrumentation.incMiss();

            return ValueNotFound;
        }

        public void remove(StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                int startKeyCount = keyCount;

                for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
                {
                    String key      = keys[keyIndex];
                    Object oldValue = values[keyIndex];

                    if (!policy.canRemove(key, oldValue))
                    {
                        mapInstrumentation.incMiss();

                        continue;
                    }

                    int highest_index = keys.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(values, keyIndex + 1, values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(keys,   keyIndex + 1, keys,   keyIndex, highest_index - keyIndex);
                    }

                    keys[highest_index]   = null;
                    values[highest_index] = null;

                    keyCount--;
                    mapSize--;
                }

                mapInstrumentation.incRemoved(startKeyCount - keyCount);
            }
        }

        public void remove(StringObjectMapArrayHolder arrayHolder, StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                int startKeyCount = keyCount;

                for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
                {
                    String key      = keys[keyIndex];
                    Object oldValue = values[keyIndex];

                    if (!policy.canRemove(key, oldValue))
                    {
                        mapInstrumentation.incMiss();

                        continue;
                    }

                    arrayHolder.add(key, oldValue);

                    int highest_index = keys.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(values, keyIndex + 1, values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(keys,   keyIndex + 1, keys,   keyIndex, highest_index - keyIndex);
                    }

                    keys[highest_index]   = null;
                    values[highest_index] = null;

                    keyCount--;
                    mapSize--;
                }

                mapInstrumentation.incRemoved(startKeyCount - keyCount);
            }
        }

        public void acceptVisitor(StringObjectMapVisitor visitor)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (visitor.visit(keys[keyIndex], values[keyIndex]) == StringObjectMapVisitor.ABORT)
                {
                    break;
                }
            }
        }

        public void clear()
        {
            keys     = null;
            values   = null;
            keyCount = 0;
        }

        public void find(StringObjectMapArrayHolder arrayHolder)
        {
            if (keyCount > 0)
            {
                arrayHolder.add(keys, values, keyCount);
            }
        }

        public void find(StringObjectMapArrayHolder arrayHolder, StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                String key;
                Object value;

                for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
                {
                    key   = keys[keyIndex];
                    value = values[keyIndex];

                    if (policy.canRetrieve(key, value))
                    {
                        arrayHolder.add(key, value);
                    }
                }
            }
        }

        private int keyFind(String key)
        {
            return MapHelper.binarySearch(keys, key, keyCount);
        }


        private String[] arrayclone(String from)
        {
            String[] to = new String[1];

            to[0] = from;

            return to;
        }

        private String[] arrayclone(String[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (String[]) from.clone();
        }

        private String[] arrayclone(String[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private String[] arrayclone(String[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private String[] arraycloneCombine(String[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private String[] arraycloneExpandGap(String[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            int gap = gapOffset + gapLength;
            System.arraycopy(from, fromOffset, to, 0,   gapOffset);
            System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
            return to;
        }

        private Object[] arrayclone(Object from)
        {
            Object[] to = new Object[1];

            to[0] = from;

            return to;
        }

        private Object[] arrayclone(Object[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (Object[]) from.clone();
        }

        private Object[] arrayclone(Object[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private Object[] arrayclone(Object[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private Object[] arraycloneCombine(Object[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private Object[] arraycloneExpandGap(Object[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            int gap = gapOffset + gapLength;
            System.arraycopy(from, fromOffset, to, 0,   gapOffset);
            System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
            return to;
        }
    }
// synchronized bucket
    protected class SynchronizedBucket implements Bucket
    {
        protected String[] keys;
        protected Object[] values;
        protected int       keyCount;

        public SynchronizedBucket()
        {

        }

        public SynchronizedBucket(Bucket bucket)
        {
            if (bucket instanceof SynchronizedBucket)
            {
                this.keys     = ((SynchronizedBucket) bucket).keys;
                this.values   = ((SynchronizedBucket) bucket).values;
                this.keyCount = ((SynchronizedBucket) bucket).keyCount;
            }
            else
            {
                this.keys     = ((UnsynchronizedBucket) bucket).keys;
                this.values   = ((UnsynchronizedBucket) bucket).values;
                this.keyCount = ((UnsynchronizedBucket) bucket).keyCount;
            }
        }

        public synchronized boolean containsKey(String key)
        {
            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                mapInstrumentation.incHit();

                return true;
            }

            mapInstrumentation.incMiss();

            return false;
        }

        public synchronized boolean containsKey(String key, StringObjectMapPolicy policy)
        {
            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                mapInstrumentation.incHit();

                return policy.canRetrieve(keys[keyIndex], values[keyIndex]);
            }

            mapInstrumentation.incMiss();

            return false;
        }

        public synchronized boolean containsValue(Object value)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (value != null && value.equals(values[keyIndex]))
                {
                    return true;
                }
            }

            return false;
        }

        public synchronized boolean containsValue(Object value, StringObjectMapPolicy policy)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (value != null && value.equals(values[keyIndex]))
                {
                    return policy.canRetrieve(keys[keyIndex], value);
                }
            }

            return false;
        }

        public synchronized Object get(String key)
        {
            if (keyCount > 0)
            {
                int keyIndex = keyFind(key);
                if (keyIndex >= 0)
                {
                    mapInstrumentation.incHit();

                    return values[keyIndex];
                }
            }

            mapInstrumentation.incMiss();

            return ValueNotFound;
        }

        public synchronized Object get(String key, StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                int keyIndex = keyFind(key);
                if (keyIndex >= 0)
                {
                    Object value = values[keyIndex];
                    if (policy.canRetrieve(key, value))
                    {
                        mapInstrumentation.incHit();

                        return value;
                    }
                }
            }

            mapInstrumentation.incMiss();

            return ValueNotFound;
        }

        public synchronized void getKeysForValue(Object value, StringArrayHolder arrayHolder)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (value != null && value.equals(values[keyIndex]))
                {
                    arrayHolder.add(keys[keyIndex]);
                }
            }
        }

        public synchronized void getKeysForValue(Object value, StringArrayHolder arrayHolder, StringObjectMapPolicy policy)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (value != null && value.equals(values[keyIndex]))
                {
                    String key = keys[keyIndex];

                    if (policy.canRetrieve(key, value))
                    {
                        arrayHolder.add(key);
                    }
                }
            }
        }

        public synchronized Object put(String key, Object value)
        {
            if (keyCount == 0)
            {
                if (keys == null)
                {
                    keys   = new String[DEFAULT_ENTRY_LIST_CAPACITY];
                    values = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
                }

                keys[0]   = key;
                values[0] = value;
                keyCount  = 1;

                mapSize++;

                mapInstrumentation.incAdded();

                return value;
            }

            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                values[keyIndex] = value;

                mapInstrumentation.incReplaced();

                return value;
	        }

            // if we got here, this is completely new key, we let's add it
            keyIndex = MapHelper.normalizeBinarySearchPosition(keyIndex);
            if (keyCount >= keys.length) // need to expand
            {
                if (keyIndex == keyCount) // if at end, then just arrayclone, and append later
                {
                    keys   = arrayclone(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH);
                    values = arrayclone(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH);
                }
                else
                {
                    keys   = arraycloneExpandGap(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                    values = arraycloneExpandGap(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                }
            }
            else
            {
                if (keyIndex < keyCount)
                {
                    System.arraycopy(values, keyIndex, values, keyIndex + 1, keyCount - keyIndex);
                    System.arraycopy(keys,   keyIndex, keys,   keyIndex + 1, keyCount - keyIndex);
                }
            }

            keys[keyIndex]   = key;
            values[keyIndex] = value;

            keyCount++;
            mapSize++;

            mapInstrumentation.incAdded();

            return value;
        }

        public synchronized Object put(String key, Object value, StringObjectMapPolicy policy)
        {
            if (keyCount == 0)
            {
                if (!policy.canInsert(key, value))
                {
                    return ValueNotFound;
                }

                if (keys == null)
                {
                    keys   = new String[DEFAULT_ENTRY_LIST_CAPACITY];
                    values = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
                }

                keys[0]   = key;
                values[0] = value;
                keyCount  = 1;

                mapSize++;

                mapInstrumentation.incAdded();

                return value;
            }

            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                if (policy.canUpdate(keys[keyIndex], values[keyIndex], value))
                {
                    values[keyIndex] = value;

                    mapInstrumentation.incReplaced();
                }

                return values[keyIndex];
	        }

            // if we got here, this is completely new key, we let's add it
            if (!policy.canInsert(key, value))
            {
                return ValueNotFound;
            }

            keyIndex = MapHelper.normalizeBinarySearchPosition(keyIndex);
            if (keyCount >= keys.length) // need to expand
            {
                if (keyIndex == keyCount) // if at end, then just arrayclone, and append later
                {
                    keys   = arrayclone(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH);
                    values = arrayclone(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH);
                }
                else
                {
                    keys   = arraycloneExpandGap(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                    values = arraycloneExpandGap(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                }
            }
            else
            {
                if (keyIndex < keyCount)
                {
                    System.arraycopy(values, keyIndex, values, keyIndex + 1, keyCount - keyIndex);
                    System.arraycopy(keys,   keyIndex, keys,   keyIndex + 1, keyCount - keyIndex);
                }
            }

            keys[keyIndex]   = key;
            values[keyIndex] = value;

            keyCount++;
            mapSize++;

            mapInstrumentation.incAdded();

            return value;
        }

        public synchronized Object put(String key, Object value, StringObjectMapModifyPolicy policy)
        {
            if (keyCount == 0)
            {
                if (!policy.canInsert(key, value))
                {
                    return ValueNotFound;
                }

                if (keys == null)
                {
                    keys   = new String[DEFAULT_ENTRY_LIST_CAPACITY];
                    values = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
                }

                keys[0]   = key;
                values[0] = policy.valueToInsert(key, value);
                keyCount  = 1;

                mapSize++;

                mapInstrumentation.incAdded();

                return value;
            }

            int keyIndex = keyFind(key);
            if (keyIndex >= 0)
            {
                if (policy.canUpdate(keys[keyIndex], values[keyIndex], value))
                {
                    values[keyIndex] = policy.valueToUpdate(key, values[keyIndex], value);

                    mapInstrumentation.incReplaced();
                }

                return values[keyIndex];
	        }

            // if we got here, this is completely new key, we let's add it
            if (!policy.canInsert(key, value))
            {
                return ValueNotFound;
            }

            keyIndex = MapHelper.normalizeBinarySearchPosition(keyIndex);
            if (keyCount >= keys.length) // need to expand
            {
                if (keyIndex == keyCount) // if at end, then just arrayclone, and append later
                {
                    keys   = arrayclone(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH);
                    values = arrayclone(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH);
                }
                else
                {
                    keys   = arraycloneExpandGap(keys,   0, keys.length,   keys.length   + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                    values = arraycloneExpandGap(values, 0, values.length, values.length + DEFAULT_ENTRY_LIST_GROWTH, keyIndex, 1);
                }
            }
            else
            {
                if (keyIndex < keyCount)
                {
                    System.arraycopy(values, keyIndex, values, keyIndex + 1, keyCount - keyIndex);
                    System.arraycopy(keys,   keyIndex, keys,   keyIndex + 1, keyCount - keyIndex);
                }
            }

            keys[keyIndex]   = key;
            values[keyIndex] = policy.valueToInsert(key, value);

            keyCount++;
            mapSize++;

            mapInstrumentation.incAdded();

            return value;
        }

        public synchronized Object remove(String key)
        {
            if (keyCount > 0)
            {
                int keyIndex = keyFind(key);
                if (keyIndex >= 0)
                {
                    Object oldValue = values[keyIndex];

                    int highest_index = keys.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(values, keyIndex + 1, values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(keys,   keyIndex + 1, keys,   keyIndex, highest_index - keyIndex);
                    }

                    keys[highest_index]   = null;
                    values[highest_index] = null;

                    keyCount--;
                    mapSize--;

                    mapInstrumentation.incRemoved();

                    return oldValue;
	            }
            }

            mapInstrumentation.incMiss();

            return ValueNotFound;
        }

        public synchronized Object remove(String key, StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                int keyIndex = keyFind(key);
                if (keyIndex >= 0)
                {
                    Object oldValue = values[keyIndex];

                    if (!policy.canRemove(key, oldValue))
                    {
                        mapInstrumentation.incMiss();

                        return ValueNotFound;
                    }

                    int highest_index = keys.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(values, keyIndex + 1, values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(keys,   keyIndex + 1, keys,   keyIndex, highest_index - keyIndex);
                    }

                    keys[highest_index]   = null;
                    values[highest_index] = null;

                    keyCount--;
                    mapSize--;

                    mapInstrumentation.incRemoved();

                    return oldValue;
	            }
            }

            mapInstrumentation.incMiss();

            return ValueNotFound;
        }

        public synchronized void remove(StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                int startKeyCount = keyCount;

                for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
                {
                    String key      = keys[keyIndex];
                    Object oldValue = values[keyIndex];

                    if (!policy.canRemove(key, oldValue))
                    {
                        mapInstrumentation.incMiss();

                        continue;
                    }

                    int highest_index = keys.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(values, keyIndex + 1, values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(keys,   keyIndex + 1, keys,   keyIndex, highest_index - keyIndex);
                    }

                    keys[highest_index]   = null;
                    values[highest_index] = null;

                    keyCount--;
                    mapSize--;
                }

                mapInstrumentation.incRemoved(startKeyCount - keyCount);
            }
        }

        public synchronized void remove(StringObjectMapArrayHolder arrayHolder, StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                int startKeyCount = keyCount;

                for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
                {
                    String key      = keys[keyIndex];
                    Object oldValue = values[keyIndex];

                    if (!policy.canRemove(key, oldValue))
                    {
                        mapInstrumentation.incMiss();

                        continue;
                    }

                    arrayHolder.add(key, oldValue);

                    int highest_index = keys.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(values, keyIndex + 1, values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(keys,   keyIndex + 1, keys,   keyIndex, highest_index - keyIndex);
                    }

                    keys[highest_index]   = null;
                    values[highest_index] = null;

                    keyCount--;
                    mapSize--;
                }

                mapInstrumentation.incRemoved(startKeyCount - keyCount);
            }
        }

        public synchronized void acceptVisitor(StringObjectMapVisitor visitor)
        {
            for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
            {
                if (visitor.visit(keys[keyIndex], values[keyIndex]) == StringObjectMapVisitor.ABORT)
                {
                    break;
                }
            }
        }

        public synchronized void clear()
        {
            keys     = null;
            values   = null;
            keyCount = 0;
        }

        public synchronized void find(StringObjectMapArrayHolder arrayHolder)
        {
            if (keyCount > 0)
            {
                arrayHolder.add(keys, values, keyCount);
            }
        }

        public synchronized void find(StringObjectMapArrayHolder arrayHolder, StringObjectMapPolicy policy)
        {
            if (keyCount > 0)
            {
                String key;
                Object value;

                for (int keyIndex = 0; keyIndex < keyCount; keyIndex++)
                {
                    key   = keys[keyIndex];
                    value = values[keyIndex];

                    if (policy.canRetrieve(key, value))
                    {
                        arrayHolder.add(key, value);
                    }
                }
            }
        }

        private int keyFind(String key)
        {
            return MapHelper.binarySearch(keys, key, keyCount);
        }


        private String[] arrayclone(String from)
        {
            String[] to = new String[1];

            to[0] = from;

            return to;
        }

        private String[] arrayclone(String[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (String[]) from.clone();
        }

        private String[] arrayclone(String[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private String[] arrayclone(String[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private String[] arraycloneCombine(String[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private String[] arraycloneExpandGap(String[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            int gap = gapOffset + gapLength;
            System.arraycopy(from, fromOffset, to, 0,   gapOffset);
            System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
            return to;
        }

        private Object[] arrayclone(Object from)
        {
            Object[] to = new Object[1];

            to[0] = from;

            return to;
        }

        private Object[] arrayclone(Object[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (Object[]) from.clone();
        }

        private Object[] arrayclone(Object[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private Object[] arrayclone(Object[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private Object[] arraycloneCombine(Object[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private Object[] arraycloneExpandGap(Object[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            int gap = gapOffset + gapLength;
            System.arraycopy(from, fromOffset, to, 0,   gapOffset);
            System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
            return to;
        }
    }
}
