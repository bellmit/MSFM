package com.cboe.interfaces.application;

/**
 * Allows a {@link Cache} to query elements within the cache.
 * <p>
 * The CacheElementQuery interface is designed to allow a cache to query elements within it
 * for certain information that all such elements are expected to be able to provide. Currently,
 * the only thing required by Cache is a synchonization ID, which will be used to allow a synchronization
 * layer to query the cache (via the {@link Synchable} interface) to retrieve the greatest (most up-to-date)
 * synchronization ID contained by any of its elements.
 * <p>
 * @author Brian Erst
 * @see Cache
 * @see Synchable
 * @version 2001.1.12
 */
public interface CacheElementQuery
{
    public long getSyncIDFromObject(Object obj);
}

