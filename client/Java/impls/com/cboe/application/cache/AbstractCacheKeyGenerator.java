package com.cboe.application.cache;

import com.cboe.interfaces.application.*;
import java.util.*;

/**
 * This class implements the basics of a {@link CacheKeyGenerator}.
 * <p>
 * It enforces the rule that multiple instances of the same key generator should return identical
 * hashcodes and match on an equals() call. It provides default non-grouping and automatic updating logic.
 * <p>
 * @author Brian Erst
 * @see CacheKeyGenerator
 * @version 2001.1.12
 */

public abstract class AbstractCacheKeyGenerator implements CacheKeyGenerator
{
    /**
     * Generates hash code based on class name, enforcing a rule that all instances of a cache
     * key generator should return identical hash codes.
     * @return hash code unique to class, not instance
     */
    public int hashCode()
    {
        // All instances of a given key generator should be added to a HashMap the same way
        return this.getClass().getName().hashCode();
    }

    /**
     * Compares two cache key generators based on hash code, enforcing a rule that all instances
     * of a given cache key generator class should match on an equals() call.
     * @param generator an instance of a cache key generator to compare against
     * @return true if the cache key generators are of the same class
     */
    public boolean equals(Object generator)
    {
        if (generator instanceof AbstractCacheKeyGenerator)
            return ((AbstractCacheKeyGenerator)generator).hashCode() == hashCode();
        else
            return false;
    }

    /**
     * Provides subclasses a default of non-grouping keys.
     * @param fromObject object to generate a grouping key from. Ignored.
     * @return <code>null</code>, which indicates non-grouping.
     */
    public Iterator groupIterator(Object fromObject)
    {
        return new Iterator()
               {
                    public boolean hasNext() {return false;}
                    public Object next() {throw new NoSuchElementException();}
                    public void remove() {throw new UnsupportedOperationException();}
               };
    }

    /**
     * Provides subclasses a default of non-grouping keys.
     * @return <code>false</code>, which indicates non-grouping.
     */
    public boolean doesGroup() {return false;}
}
