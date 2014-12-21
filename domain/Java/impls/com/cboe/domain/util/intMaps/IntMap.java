package com.cboe.domain.util.intMaps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface IntMap<V>
{

    public abstract void putAll(IntMap<? extends V> m);
    public abstract void putAll(Map<Integer, ? extends V> m);
    
    public abstract boolean containsKey(int key);

    /**
     * Returns the number of key-value mappings in this map.
     * 
     * @return the number of key-value mappings in this map
     */
    public abstract int size();

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     * 
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public abstract boolean isEmpty();

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     * 
     * <p>
     * More formally, if this map contains a mapping from a key {@code k} to a
     * value {@code v} such that {@code (key==null ? k==null : key.equals(k))},
     * then this method returns {@code v}; otherwise it returns {@code null}.
     * (There can be at most one such mapping.)
     * 
     * <p>
     * A return value of {@code null} does not <i>necessarily</i> indicate that
     * the map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to {@code null}. The
     * {@link #containsKey containsKey} operation may be used to distinguish
     * these two cases.
     * 
     * @see #put(Object, Object)
     */
    public abstract V get(int key);

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is
     * replaced.
     * 
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>. (A
     *         <tt>null</tt> return can also indicate that the map previously
     *         associated <tt>null</tt> with <tt>key</tt>.)
     */
    public abstract V put(int key, V value);

    /**
     * Removes the mapping for the specified key from this map if present.
     * 
     * @param key
     *            key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>. (A
     *         <tt>null</tt> return can also indicate that the map previously
     *         associated <tt>null</tt> with <tt>key</tt>.)
     */
    public abstract V remove(int key);

    /**
     * Removes all of the mappings from this map. The map will be empty after
     * this call returns.
     */
    public abstract void clear();

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     * 
     * @param value
     *            value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    public abstract boolean containsValue(Object value);

    /**
     * Returns a {@link Set} view of the keys contained in this map. The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. If the map is modified while an iteration over the set is in
     * progress (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined. The set supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     */
    public abstract Set<Integer> keySet();

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are reflected
     * in the collection, and vice-versa. If the map is modified while an
     * iteration over the collection is in progress (except through the
     * iterator's own <tt>remove</tt> operation), the results of the iteration
     * are undefined. The collection supports element removal, which removes the
     * corresponding mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>
     * and <tt>clear</tt> operations. It does not support the <tt>add</tt>
     * or <tt>addAll</tt> operations.
     */
    public abstract Collection<V> values();

    /**
     * Returns a {@link Set} view of the mappings contained in this map. The set
     * is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. If the map is modified while an iteration over the set is in
     * progress (except through the iterator's own <tt>remove</tt> operation,
     * or through the <tt>setValue</tt> operation on a map entry returned by
     * the iterator) the results of the iteration are undefined. The set
     * supports element removal, which removes the corresponding mapping from
     * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt>
     * operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     * 
     * @return a set view of the mappings contained in this map
     */
    public abstract Set<IntMapEntry<V>> entrySet();
}